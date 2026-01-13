@echo off
echo ========================================
echo Stone Sales Management System
echo Running JavaFX Application...
echo ========================================
echo.

cd /d "%~dp0"
mvn clean javafx:run

echo.
echo ========================================
echo Application closed
echo ========================================
pause
