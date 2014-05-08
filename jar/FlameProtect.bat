
cd "E:\Informatik\NetBeans\forge\mcp\"
call recompile.bat < nul
call reobfuscate.bat < nul

cd E:\Informatik\NetBeans\forge\mcp\reobf\minecraft
jar cvf %~dp0\FlameProtect.jar flamefeed\FlameProtect\src
