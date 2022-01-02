chcp 65001
%~d0
cd %~dp0
if EXIST %~dp0\o3de GOTO PULL
:CLONE
	git clone https://github.com/o3de/o3de.git
	GOTO END
:PULL
	git pull https://github.com/o3de/o3de.git
:END
rem ■■■  1_終了しました  ■■■
pause
