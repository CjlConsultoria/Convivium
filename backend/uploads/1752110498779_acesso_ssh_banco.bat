@echo off

rem Configurações do servidor SSH
set usuario=arcgis
rem set servidor=10.153.18.83
set servidor=10.5.19.159
set porta=22
rem set servidor_remoto=10.153.18.82
set servidor_remoto=10.5.19.162
set porta_remota=5432
set senha=oPSEeqC0

rem Caminho para o executável Plink
set plink=C:\Program Files\PuTTY\plink.exe

rem Comando para fazer login SSH com encaminhamento de porta
"%plink%" -ssh -L 63333:%servidor_remoto%:%porta_remota% -P %porta% %usuario%@%servidor% -pw %senha%
