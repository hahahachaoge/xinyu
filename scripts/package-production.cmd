@echo off
setlocal EnableExtensions
pushd "%~dp0.."
where git >nul 2>nul
if errorlevel 1 goto missingTool
where tar >nul 2>nul
if errorlevel 1 goto missingTool
if not exist "QaMentalHealthKnowledgeServicesAdolescents.springboot\target\web-0.0.1-SNAPSHOT.jar" goto missingBuild
if not exist "QaMentalHealthKnowledgeServicesAdolescents.elementui\dist\index.html" goto missingBuild
set "OUT=release\build-%RANDOM%-%RANDOM%"
if exist "%OUT%" goto failed
mkdir "%OUT%\backend"
if errorlevel 1 goto failed
copy /y "QaMentalHealthKnowledgeServicesAdolescents.springboot\target\web-0.0.1-SNAPSHOT.jar" "%OUT%\backend\app.jar" >nul
if errorlevel 1 goto failed
xcopy "QaMentalHealthKnowledgeServicesAdolescents.elementui\dist" "%OUT%\html\" /e /i /q /y >nul
if errorlevel 1 goto failed
rem Export only committed demo resources. Never package local uploads or credentials.
git archive --format=zip --output="%OUT%\demo-resources.zip" HEAD QaMentalHealthKnowledgeServicesAdolescents.springboot/external-resources
if errorlevel 1 goto failed
tar -xf "%OUT%\demo-resources.zip" --strip-components=1 -C "%OUT%\backend"
if errorlevel 1 goto failed
del "%OUT%\demo-resources.zip"
copy /y deploy\start-prod.cmd "%OUT%\backend\" >nul
if errorlevel 1 goto failed
copy /y deploy\start-prod.sh "%OUT%\backend\" >nul
if errorlevel 1 goto failed
copy /y deploy\nginx.conf "%OUT%\nginx.conf" >nul
if errorlevel 1 goto failed
copy /y .env.example "%OUT%\.env.example" >nul
if errorlevel 1 goto failed
copy /y qa_mental_health_knowledge_services_adolescents.sql "%OUT%\" >nul
if errorlevel 1 goto failed
copy /y docs\deployment-step-3.md "%OUT%\README.md" >nul
if errorlevel 1 goto failed
echo Deployment files: %CD%\%OUT%
echo Copy .env.example to .env.properties and configure it privately on the server.
echo See README.md in the output directory before starting services.
popd
exit /b 0
:missingTool
echo ERROR: Git and Windows tar.exe are required.
goto failed
:missingBuild
echo ERROR: Build the backend JAR and frontend dist first. See docs\deployment-step-3.md.
:failed
echo ERROR: Packaging did not complete. Existing releases were preserved.
popd
exit /b 1
