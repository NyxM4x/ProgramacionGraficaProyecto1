@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

set "OPCION=%~1"
if "%OPCION%"=="" set "OPCION=6"

where mvn >nul 2>nul
if errorlevel 1 (
    echo No se encontró Maven. Instálalo y verifica con: mvn -version
    pause
    exit /b 1
)

if /i "%OPCION%"=="test" (
    call mvn -q test
    if errorlevel 1 (
        echo Las pruebas fallaron.
        pause
        exit /b 1
    )
    echo Pruebas correctas.
    exit /b 0
)

for %%N in (1 2 3 4 5 6) do (
    if "%OPCION%"=="%%N" goto ejecutar
)

echo Uso: ejecutar.bat [1-6 ^| test]
echo   sin argumento  abre clase6 (desgaste y taller)
echo   1..6           abre esa etapa
echo   test           ejecuta las pruebas
exit /b 1

:ejecutar
echo Compilando y abriendo clase%OPCION%... (ESC cierra la ventana)
call mvn -q compile exec:exec -DmainClass=com.graphics.clase%OPCION%
if errorlevel 1 (
    echo Ocurrió un error al compilar o ejecutar.
    pause
    exit /b 1
)
