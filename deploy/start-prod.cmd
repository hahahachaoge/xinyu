@echo off
setlocal
cd /d "%~dp0"
java -jar app.jar --spring.profiles.active=prod %*
