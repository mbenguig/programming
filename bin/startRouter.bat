@echo off
echo.
echo --- StartRouter----------------------------------------

SETLOCAL ENABLEDELAYEDEXPANSION
call init.bat
%JAVA_CMD%  org.objectweb.proactive.extra.messagerouting.router.Main %*
ENDLOCAL

:end
echo.
echo ---------------------------------------------------------
