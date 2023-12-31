chcp 65001
%~d0
cd %~dp0
c:
cd \o3de
rmdir /q /s o3de-packages
rmdir /q /s o3de\build
mkdir o3de-packages
mkdir o3de\build
cd o3de

cmd /c python\get_python.bat

cmake -B build/windows_vs2019 -S . -G "Visual Studio 16 2019" -DLY_3RDPARTY_PATH=../o3de-packages
cmake --build build/windows_vs2019 --target AssetProcessor Editor --config profile -- -m

%~d0
cd %~dp0
rem ■■■  2_終了しました  ■■■
pause
