@echo off
title LECTURA - Java Swing Desktop App
cd /d "%~dp0"
if not exist bin mkdir bin
echo Mengompilasi kode sumber Java...
javac -encoding UTF-8 -d bin src\id\ac\lectura\*.java src\id\ac\lectura\exception\*.java src\id\ac\lectura\model\*.java src\id\ac\lectura\util\*.java src\id\ac\lectura\dao\*.java src\id\ac\lectura\controller\*.java src\id\ac\lectura\view\*.java
if %errorlevel% neq 0 (
    echo Kompilasi gagal!
    pause
    exit /b 1
)
echo Menjalankan aplikasi LECTURA...
java -cp bin id.ac.lectura.Main
pause
