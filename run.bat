@echo off
echo Starting Pathshala Connect...
where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven was not found on PATH.
  echo Install Maven 3.9+ and make sure "mvn" works in a new terminal.
  pause
  exit /b 1
)
mvn spring-boot:run
