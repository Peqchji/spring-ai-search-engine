@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars:
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a key stroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_ARG0_DIR__=%~dp0
@SET __MVNW_ARG0_DIR__=%__MVNW_ARG0_DIR__:~0,-1%

@IF "%MAVEN_BATCH_ECHO%" == "on"  ECHO %MAVEN_BATCH_ECHO%

@PAUSE >NUL 2>&1
@IF ERRORLEVEL 1 (
    @REM MAVEN_BATCH_PAUSE is not supported on Windows 9x/Me
    EXIT /B
)

@REM ----------------------------------------------------------------------------
@REM Maven Wrapper specific commands
@REM ----------------------------------------------------------------------------

@SET "WRAPPER_JAR=%__MVNW_ARG0_DIR__%\.mvn\wrapper\maven-wrapper.jar"
@SET "WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain"
@SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"

@IF EXIST "%JAVA_EXE%" GOTO Execute

@SET "JAVA_EXE=java"
"%JAVA_EXE%" -version >NUL 2>&1
@IF ERRORLEVEL 1 (
    ECHO Error: JAVA_HOME is not defined correctly.
    ECHO   We cannot execute %JAVA_EXE%
    GOTO :EOF
)

:Execute
@SET "MAVEN_JAVA_EXE=%JAVA_EXE%"
@SET "WRAPPER_JAR=\"%WRAPPER_JAR%\""

"%MAVEN_JAVA_EXE%" %MAVEN_OPTS% -classpath %WRAPPER_JAR% %WRAPPER_LAUNCHER% %*

@IF ERRORLEVEL 1 GOTO Error
@GOTO :EOF

:Error
@ECHO.
@ECHO Exception in thread "main" java.lang.RuntimeException: Wrapper JAR not found.
@ECHO.
@EXIT /B %ERRORLEVEL%
