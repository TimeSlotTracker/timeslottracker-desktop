# TimeSlotTracker Launcher

# sets best compression
SetCompress Auto
SetCompressor /SOLID lzma
SetCompressorDictSize 32
SetDatablockOptimize On

# params
Name "TimeSlotTracker"
Caption "TimeSlotTracker"
Icon "..\..\icons\users\clock16.ico"
OutFile "..\..\..\target\TimeSlotTracker.exe"
!define JARFILE ".\tst.jar"

;Request application privileges for Windows Vista
RequestExecutionLevel user
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow

# run
Section ""
  Call GetJRE
  Pop $R0

  FileOpen $9 "$PROFILE\.tst\config\jvm_args" r
  FileRead $9 $8
  FileClose $9

#  StrCpy $0 '"$R0" $8 "-Djava.util.logging.config.file=$PROFILE\.tst\config\logging.properties" "-Dprop.directory=$PROFILE\.tst\config" "-Dtst.directory=$PROFILE\.tst\data" "-Ddtd.directory=$PROFILE\.tst\data" -jar "${JARFILE}"'
  StrCpy $0 '"$R0" $8 "-Djava.util.logging.config.file=$PROFILE\.tst\config\logging.properties" -jar "${JARFILE}"'

  SetOutPath $EXEDIR
  ExecWait $0
SectionEnd

Function GetJRE
;
;  Find JRE (javaw.exe)
;  1 - in .\jre directory (JRE Installed with application)
;  2 - in JAVA_HOME environment variable
;  3 - in the registry
;  4 - assume javaw.exe in current dir or PATH

  Push $R0
  Push $R1

  ClearErrors
  StrCpy $R0 "$EXEDIR\jre\bin\javaw.exe"
  IfFileExists $R0 JreFound
  StrCpy $R0 ""

  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\javaw.exe"
  IfErrors 0 JreFound

  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\javaw.exe"

  IfErrors 0 JreFound
  StrCpy $R0 "javaw.exe"

 JreFound:
  Pop $R1
  Exch $R0
FunctionEnd
