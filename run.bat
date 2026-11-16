@echo off
title LECTURA - Sistem Penilaian Akademik Dosen
echo ===================================================================
echo     LECTURA - Sistem Penilaian Akademik Dosen
echo     Dra. Rina Puspita, M.Kom - SIPENA Portal
echo ===================================================================
echo.
echo Menjalankan server aplikasi LECTURA...
echo Buka browser pada alamat: http://localhost:3000
echo.
start http://localhost:3000
call npm run dev -- --port 3000
pause
