chcp 65001
%~d0
cd %~dp0

rem rmdir /q /s o3de_manager
rem mkdir o3de_manager
rem cd o3de_manager

cd o3de\build

rem cmd /c ..\o3de\scripts\o3de.bat create-project --project-path .
rem cmd /c ..\o3de\scripts\o3de.bat register --project-path .

cmake -S ..\o3de -G "Visual Studio 17 2022" -DLY_3RDPARTY_PATH=../o3de/3rdparty

cmake --build . --target AssetProcessor Editor --config profile -- /m

cd ..\..
rem ■■■  4_終了しました  ■■■
pause
