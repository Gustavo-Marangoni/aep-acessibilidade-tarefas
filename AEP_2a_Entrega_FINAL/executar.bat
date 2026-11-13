@echo off
setlocal
chcp 65001 > nul
cd /d "%~dp0"

echo ==========================================
echo       TAREFAS ACESSIVEIS - AEP
echo ==========================================

where mvn >nul 2>nul
if errorlevel 1 (
    echo [ERRO] Maven nao encontrado.
    echo Instale o Apache Maven e configure a variavel PATH.
    echo Consulte a secao Como executar do README.md.
    pause
    exit /b 1
)

if not exist "db.properties" (
    echo [AVISO] Arquivo db.properties nao encontrado.
    echo Copie db.properties.example para db.properties e informe a senha do MySQL.
    pause
    exit /b 1
)

if "%~1"=="--voz" (
    call mvn -q compile exec:java -Dexec.args="--voz"
) else (
    call mvn -q compile exec:java
)

if errorlevel 1 (
    echo.
    echo [ERRO] A aplicacao nao conseguiu iniciar. Confira MySQL, senha, porta e banco.
    pause
    exit /b 1
)
endlocal
