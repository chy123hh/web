@echo off
cd /d "d:\gongju\code\AICursor\web\java\jar"
java -jar -Dserver.port=9090 -Dserver.servlet.contextpath=/ sentinel-dashboard.jar
pause
