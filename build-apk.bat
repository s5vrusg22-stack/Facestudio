@echo off
where gradle >nul 2>nul || (echo Gradle 9.6.0이 필요합니다. & exit /b 1)
if "%ANDROID_HOME%"=="" if "%ANDROID_SDK_ROOT%"=="" (echo ANDROID_HOME 또는 ANDROID_SDK_ROOT를 설정하세요. & exit /b 1)
gradle :app:assembleDebug
if errorlevel 1 exit /b %errorlevel%
echo APK: app\build\outputs\apk\debug\app-debug.apk
