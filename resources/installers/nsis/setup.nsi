# TimeSlotTracker Setup

# sets best compression
SetCompress Auto
SetCompressor /SOLID lzma
SetCompressorDictSize 32
SetDatablockOptimize On

# sets some stuff
XPStyle on
CRCCheck On
!include MUI2.nsh
;Request application privileges for Windows Vista
RequestExecutionLevel user

# main settings
!define PRODUCT "TimeSlotTracker"
Name    "${PRODUCT} ${PRODUCT_VERSION}"
OutFile "..\..\..\target\${PRODUCT}-${PRODUCT_VERSION}-setup.exe"
InstallDir "$PROGRAMFILES\TimeSlotTracker"

# modern UI Configuration
!define MUI_WELCOMEPAGE_TITLE  "Welcome to the ${PRODUCT} ${PRODUCT_VERSION} Setup Wizard"
!define MUI_WELCOMEPAGE_TEXT "This wizard will guide you through the installation of ${PRODUCT} ${PRODUCT_VERSION}, simple and useful time tracking tool written in java.$\n$\nClose any running instances of ${PRODUCT} before installation.$\n$\nClick Next to continue."
!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP "..\..\icons\users\clock48-logo.bmp"
!define MUI_HEADERIMAGE_BITMAP_NOSTRETCH
!define MUI_ABORTWARNING
!define MUI_ICON "..\..\icons\users\clock16.ico"

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "..\..\libs\tst-license.txt"
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES

!insertmacro MUI_LANGUAGE "English"

# install section
section

    # define output path
    setOutPath $INSTDIR

    # define unstaller name
    writeUninstaller "$INSTDIR\uninstaller.exe"

    # copy apps files
    File "..\..\..\target\tst.jar"
    File "..\..\..\target\TimeSlotTracker.exe"

    # make user directory
    createDirectory "$PROFILE\.tst"

    # make data directory
    #createDirectory "$PROFILE\.tst\data"

    # make backup directory
    #createDirectory "$PROFILE\.tst\backup"

    # make log directory
    createDirectory "$PROFILE\.tst\log"

    # copy config files
    createDirectory "$PROFILE\.tst\config"
    File "/oname=$PROFILE\.tst\config\logging.properties" logging.properties 
    File "/oname=$PROFILE\.tst\config\jvm_args" jvm_args

    #FileOpen $9 "$PROFILE\.tst\config\timeslottracker.properties" w
    #FileWrite $9 "dataSource.data.directory=$PROFILE\\.tst\\data"
    #FileWrite $9 "$\r$\n"
    #FileWrite $9 "backup.directory=$PROFILE\\.tst\\backup"
    #FileWrite $9 "$\r$\n"
    #FileClose $9

    # create shortcuts
    createDirectory "$SMPROGRAMS\TimeSlotTracker"
    createShortCut "$SMPROGRAMS\TimeSlotTracker\TimeSlotTracker.lnk" "$INSTDIR\TimeSlotTracker.exe"
    createShortCut "$SMPROGRAMS\TimeSlotTracker\Uninstall.lnk" "$INSTDIR\uninstaller.exe"

sectionEnd

# uninstall section
section "Uninstall"

    # deletes uninstaller and apps files
    delete $INSTDIR\*.*

    # remove the links from the start menu
    delete "$SMPROGRAMS\TimeSlotTracker\*.*"
    rmdir /r "$SMPROGRAMS\TimeSlotTracker"

    # remove install dir
    rmdir /r $INSTDIR

sectionEnd
