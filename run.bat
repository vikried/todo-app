@echo off

:: ==============================================
:: Docker Compose Build & Run Script (Windows CMD)
:: ==============================================

set MODE=%1
if "%MODE%"=="" set MODE=dev

set CLEAN=%2

echo.
echo ======================================================
echo   Docker Compose Build & Run Script for TODO-App
echo   Mode: %MODE%
echo ======================================================
echo.

:: Prüfen, ob Docker läuft
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker scheint nicht zu laufen. Bitte starte Docker Desktop zuerst.
    exit /b 1
)

:: Alte Container entfernen
echo [INFO] Entferne alte Container und Volumes...
docker-compose down --volumes --remove-orphans

:: Optional: Clean-Option prüfen
if "%CLEAN%"=="--clean" (
    echo [INFO] Lösche Build-Cache...
    docker system prune -af
)

:: Compose je nach Modus starten
if "%MODE%"=="prod" (
    echo [INFO] Baue Produktions-Images...
    docker-compose build
    echo [INFO] Starte App im Produktionsmodus (detached)...
    docker-compose up -d
) else (
    echo [INFO] Baue Entwicklungs-Images...
    docker-compose build
    echo [INFO] Starte App im Entwicklungsmodus (mit Logs)...
    docker-compose up
)

echo.
echo [SUCCESS] Fertig! App sollte nun laufen.
echo.
pause
