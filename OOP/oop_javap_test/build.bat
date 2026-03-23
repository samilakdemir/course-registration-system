@echo off
echo === Course Registration System Build Script ===
echo Group: [Replace with your group number]
echo Iteration: 1
echo Project Location: C:\oop_javaproject\oop_javap_test
echo.

:: Set classpath for Windows
set CLASSPATH=lib\gson-2.8.9.jar;lib\junit-platform-console-standalone-1.8.2.jar

:: Clean previous builds
echo Cleaning previous builds...
if exist build rmdir /s /q build
mkdir build
mkdir build\classes
mkdir build\test-classes

:: Compile main source files
echo Compiling main source files...
javac -cp "%CLASSPATH%" -d build\classes src\*.java

if %ERRORLEVEL% EQU 0 (
    echo [32m✓ Main compilation successful[0m
) else (
    echo [31m✗ Main compilation failed[0m
    exit /b 1
)

:: Compile test files
echo Compiling test files...
javac -cp "%CLASSPATH%;build\classes" -d build\test-classes test\*.java

if %ERRORLEVEL% EQU 0 (
    echo [32m✓ Test compilation successful[0m
) else (
    echo [31m✗ Test compilation failed[0m
    exit /b 1
)

:: Generate sample data
echo Generating sample data...
java -cp "%CLASSPATH%;build\classes" DataGenerator

if %ERRORLEVEL% EQU 0 (
    echo [32m✓ Sample data generation successful[0m
) else (
    echo [31m✗ Sample data generation failed[0m
    exit /b 1
)

:: Run unit tests
echo Running unit tests...
java -jar lib\junit-platform-console-standalone-1.8.2.jar --class-path "build\classes;build\test-classes;%CLASSPATH%" --scan-classpath

echo.
echo === Build Summary ===
echo ✓ Main classes compiled
echo ✓ Test classes compiled
echo ✓ Sample data generated
echo ✓ Unit tests executed
echo.
echo To run the application:
echo java -cp "%CLASSPATH%;build\classes" CourseRegistrationApp