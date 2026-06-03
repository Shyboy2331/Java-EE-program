@echo off
setlocal enableextensions

cd /d "%~dp0"

set MAVEN_VERSION=3.9.6
set USER_HOME_DIR=%USERPROFILE%
set WRAPPER_URI=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
set WRAPPER_JAR=%MAVEN_BASEDIR%\.mvn\wrapper\maven-wrapper.jar

if not exist "%MAVEN_BASEDIR%\.mvn\wrapper" mkdir "%MAVEN_BASEDIR%\.mvn\wrapper"

if not exist "%WRAPPER_JAR%" (
    echo Downloading Maven Wrapper...
    powershell -Command "(New-Object Net.WebClient).DownloadFile('%WRAPPER_URI%', '%WRAPPER_JAR%')"
)

java -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
