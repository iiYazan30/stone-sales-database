@echo off
echo ========================================
echo Cleaning and Rebuilding Project
echo ========================================
echo.

cd /d "%~dp0"

echo Deleting target directory...
if exist target rmdir /S /Q target

echo.
echo Running Maven clean compile...
mvn clean compile

echo.
echo ========================================
echo Build Complete
echo ========================================
echo.
echo Now run run.bat to start the application
pause
