chcp 65001
%~d0
cd %~dp0
cd o3de

cmd /c scripts\o3de.bat register --this-engine

cd ..
rem ■■■  3_終了しました  ■■■
pause