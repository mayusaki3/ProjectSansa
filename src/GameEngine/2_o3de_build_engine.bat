chcp 65001
%~d0
cd %~dp0
rmdir /q /s o3de\3rdparty
rmdir /q /s o3de\build
mkdir o3de\3rdparty
mkdir o3de\build
cd o3de\build

cmd /c ..\python\get_python.bat

cmake -S ../ -G "Visual Studio 17 2022" -DLY_3RDPARTY_PATH=../3rdparty

cd ..\..
rem ■■■  2_終了しました  ■■■
pause
