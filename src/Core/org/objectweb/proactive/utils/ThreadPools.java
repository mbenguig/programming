/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $ACTIVEEON_INITIAL_DEV$
 */
package org.objectweb.proactive.utils;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Factory and utility methods to create thread pools.
 * 
 * <p>It is a counterpart of the {@link Executors} class.</p>
 * 
 * @see Executors
 * @author ProActive team
 * @since  5.1.0
 */
final public class ThreadPools {

    /**
     * Creates a thread pool that creates up to maxThreads thread.
     * 
     * <p>On Java 6 and later the thread pool creates new threads as needed, 
     * but will reuse previously constructed threads when they are
     * available.  These pools will typically improve the performance
     * of programs that execute many short-lived asynchronous tasks.
     * Calls to <tt>execute</tt> will reuse previously constructed
     * threads if available. If no existing thread is available, a new
     * thread will be created and added to the pool until the pool reach its
     * maximum size. Threads that have not been used for sixty seconds are 
     * terminated and removed from the cache. Thus, a pool that remains idle 
     * for long enough will not consume any resources. Note that pools with similar
     * properties but different details (for example, timeout parameters)
     * may be created using {@link ThreadPoolExecutor} constructors.</p>
     *
     * <p><b>Warning</b>Java 5 does not allow to create a such thread pool. As a fall-back
     * a thread with fixed size of maxThreads is created.</p> 
     * 
     * @param maxThreads
     *    the maximum number of thread to create
     * @return
     *    the newly created thread pool
     */
    public static ExecutorService newBoundedThreadPool(int maxThreads) {
        return newBoundedThreadPool(maxThreads, Executors.defaultThreadFactory());
    }

    /**
     * Creates a thread pool that creates up to maxThreads thread.
     * 
     * <p>On Java 6 and later the thread pool creates new threads as needed, 
     * but will reuse previously constructed threads when they are
     * available.  These pools will typically improve the performance
     * of programs that execute many short-lived asynchronous tasks.
     * Calls to <tt>execute</tt> will reuse previously constructed
     * threads if available. If no existing thread is available, a new
     * thread will be created and added to the pool until the pool reach its
     * maximum size. Threads that have not been used for sixty seconds are 
     * terminated and removed from the cache. Thus, a pool that remains idle 
     * for long enough will not consume any resources. Note that pools with similar
     * properties but different details (for example, timeout parameters)
     * may be created using {@link ThreadPoolExecutor} constructors.</p>
     * 
     * <p><b>Warning</b>Java 5 does not allow to create a such thread pool. As a fall-back
     * a thread with fixed size of maxThreads is created.</p>
     * 
     * @param maxThreads
     *    the maximum number of thread to create
     * @param threadFactory
     *    the factory to use when creating new threads
     * @return
     *    the newly created thread pool
     */
    public static ExecutorService newBoundedThreadPool(int maxThreads, ThreadFactory threadFactory) {
        return newBoundedThreadPool(maxThreads, 60L, TimeUnit.SECONDS, threadFactory);
    }

    /**
     * Creates a thread pool that creates up to maxThreads thread.
     * 
     * <p>On Java 6 and later the thread pool creates new threads as needed, 
     * but will reuse previously constructed threads when they are
     * available.  These pools will typically improve the performance
     * of programs that execute many short-lived asynchronous tasks.
     * Calls to <tt>execute</tt> will reuse previously constructed
     * threads if available. If no existing thread is available, a new
     * thread will be created and added to the pool until the pool reach its
     * maximum size. Threads that have not been used for a given amount of time are 
     * terminated and removed from the cache. Thus, a pool that remains idle 
     * for long enough will not consume any resources. Note that pools with similar
     * properties but different details (for example, timeout parameters)
     * may be created using {@link ThreadPoolExecutor} constructors.</p>
     * 
     * <p><b>Warning</b>Java 5 does not allow to create a such thread pool. As a fall-back
     * a thread with fixed size of maxThreads is created.</p>
     * 
     * @param maxThreads
     *    the maximum number of thread to create
     * @param threadFactory
     *    the factory to use when creating new threads
     * @param keepAliveTime 
     *    this is the maximum time that idle threads
     *    will wait for new tasks before terminating.
     * @param unit 
     *    the time unit for the {@code keepAliveTime} argument
     * @return
     *    the newly created thread pool
     */
    public static ExecutorService newBoundedThreadPool(int maxThreads, long keepAliveTime, TimeUnit unit,
            ThreadFactory threadFactory) {
        final ThreadPoolExecutor tpe;
        tpe = new ThreadPoolExecutor(maxThreads, maxThreads, keepAliveTime, unit,
            new LinkedBlockingQueue<Runnable>());

        try {
            // Enable dynamic sizing by allowing core threads to terminate on timeout
            // This method has been added by Java 6. 
            // With Java 5 the thread pool has a fixed size
            @RefactorWhenDroppingJava5
            Method m = tpe.getClass().getDeclaredMethod("allowCoreThreadTimeOut", boolean.class);
            m.invoke(tpe, true);
        } catch (Throwable t) {
            // Ok Java 5
        }

        return tpe;
    }

    private ThreadPools() {

    }
}
