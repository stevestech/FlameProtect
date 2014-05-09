REM dir: \forge\mcp\src\minecraft\flamefeed\FlameLoader\jar

cd ..\..\..\..\..
REM dir: \forge\mcp\
call recompile.bat < nul
call reobfuscate.bat < nul

cd reobf\minecraft
REM dir: \forge\mcp\reobf\minecraft
jar cvf %~dp0\FlameProtect.jar flamefeed\FlameProtect\src
