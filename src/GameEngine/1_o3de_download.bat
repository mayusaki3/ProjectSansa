chcp 65001
%~d0
cd %~dp0
if EXIST c:\o3de\o3de GOTO PULL
:CLONE
	c:
	cd \
	mkdir o3de
	cd o3de
	git clone -b main https://github.com/o3de/o3de.git
rem	GOTO END
:PULL
	c:
	cd \o3de\o3de
	git lfs pull origin main
:END
%~d0
cd %~dp0
rem ■■■  1_終了しました  ■■■
pause
