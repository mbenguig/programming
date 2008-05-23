package org.objectweb.proactive.ic2d.chartit.data;

import java.util.Iterator;
import java.util.List;

import org.objectweb.proactive.ic2d.chartit.Activator;
import org.objectweb.proactive.ic2d.chartit.data.provider.IDataProvider;
import org.objectweb.proactive.ic2d.chartit.util.Utils;
import org.objectweb.proactive.ic2d.console.Console;


public class ChartModelValidator {

    /**
     * The types associated to this class of model
     */
    public static final String[] NUMBER_TYPE = new String[] { "byte", "java.lang.Byte", "short",
            "java.lang.Short", "int", "java.lang.Integer", "float", "java.lang.Float", "double",
            "java.lang.Double", "long", "java.lang.Long" };

    /**
     * The string array type name
     */
    public static final String[] STRING_ARRAY_TYPE = new String[] { "[Ljava.lang.String;" };

    /**
     * The number array type name
     */
    public static final String[] NUMBER_ARRAY_TYPE = new String[] { "[B", "[Ljava.lang.Byte;", "[S",
            "[Ljava.lang.Short;", "[I", "[Ljava.lang.Integer;", "[F", "[Ljava.lang.Float;", "[D",
            "[Ljava.lang.Double;", "[J", "[Ljava.lang.Long;" };

    /**
     * @param model
     * @return
     */
    public static final boolean validate(final ChartModel model) {
        List<IDataProvider> providers = model.getProviders();
        // At least 1 provider is needed
        if (providers.size() < 1) {
            return false;
        }
        // Get the iterator
        Iterator<IDataProvider> it = providers.iterator();
        // Get first element
        IDataProvider firstElement = it.next();
        // If string array
        if (Utils.contains(STRING_ARRAY_TYPE, firstElement.getType())) {
            // size must not exceed 2 and the next element must be an array of
            // number
            if (it.hasNext()) {
                final IDataProvider next = it.next();
                if (Utils.contains(NUMBER_ARRAY_TYPE, next.getType())) {
                    initModel(model, firstElement, next);
                    return true;
                }
                return false;
            }
            return false;
            // If array of number
        } else if (Utils.contains(NUMBER_ARRAY_TYPE, firstElement.getType())) {
            // size must not exceed 2 and the next element must be a string
            // array
            if (it.hasNext()) {
                final IDataProvider next = it.next();
                if (Utils.contains(STRING_ARRAY_TYPE, next.getType())) {
                    initModel(model, next, firstElement);
                    return true;
                }
                return false;
            }
            return false;
            // If number type
        } else if (Utils.contains(NUMBER_TYPE, firstElement.getType())) {
            // Check that all other elements are of the same type
            if (providers.size() > 1) {
                while (it.hasNext()) {
                    if (!Utils.contains(NUMBER_TYPE, it.next().getType())) {
                        return false;
                    }
                }
            }
            initModel(model, providers);
            return true;
        } else {
            return false;
        }

    }

    private static final void initModel(final ChartModel model, final IDataProvider namesProvider,
            final IDataProvider valuesProvider) {
        model.runtimeNames = (String[]) namesProvider.provideValue();

        // Check and adapt names
        checkAndAdaptNamesLength(model);

        model.runtimeValues = new double[model.runtimeNames.length];
        model.runtimeValuesUpdater = new IRuntimeValuesUpdater() {
            public final void updateValues(final double[] valuesToUpdate) {
                final Object values = valuesProvider.provideValue();
                final int length = java.lang.reflect.Array.getLength(values);
                // If values array length differs from names array length log a
                // message and return
                if (length != model.runtimeNames.length) {
                    Console.getInstance(Activator.CONSOLE_NAME).log(
                            "Cannot refresh the chart " + model.name +
                                " because its values length differs from names length.");
                    return;
                }
                for (int i = 0; i < length; i++) {
                    valuesToUpdate[i] = ((Number) java.lang.reflect.Array.get(values, i)).doubleValue();
                }
            }
        };
        model.runtimeValuesUpdater.updateValues(model.runtimeValues);
    }

    private static final void initModel(final ChartModel model, final List<IDataProvider> providers) {
        final String[] names = new String[providers.size()];
        int i = 0;
        for (final IDataProvider provider : providers) {
            names[i++] = provider.getName();
        }
        model.runtimeNames = names;

        // Check and adapt names
        checkAndAdaptNamesLength(model);

        model.runtimeValues = new double[names.length];
        model.runtimeValuesUpdater = new IRuntimeValuesUpdater() {
            public final void updateValues(final double[] valuesToUpdate) {
                int i = 0;
                for (final IDataProvider provider : providers) {
                    valuesToUpdate[i++] = ((Number) provider.provideValue()).doubleValue();
                }

            }
        };

        model.runtimeValuesUpdater.updateValues(model.runtimeValues);
    }

    private static final void checkAndAdaptNamesLength(final ChartModel model) {
        // If RRD4J chart type Datasource name must be less than 20 chars
        if (model.isChronological()) {
            for (int i = 0; i < model.runtimeNames.length; i++) {
                String name = model.runtimeNames[i];
                if (name.length() > 20) {
                    model.runtimeNames[i] = name.substring(0, 19);
                }
            }
        }
    }
}