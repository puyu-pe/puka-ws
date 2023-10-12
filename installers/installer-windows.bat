@ECHO OFF
for /f "delims=" %%x in (.env) do (set "%%x")

rem ------ ENVIRONMENT --------------------------------------------------------
rem El script depende de cuatro variables de entorno establecidas  
rem JAVA_VERSION: Definido en un archivo .env, (vea .env.example)
rem PROJECT_VERSION: Version del proyecto, segun el pom.xml ,Definido en un archivo .env, (vea .env.example) 
rem APP_VERSION: Version de al app, Definido en un archivo .env, (vea .env.example) 
rem MAIN_JAR: el jar generado por el plugin maven-dependency-plugin, Definido en un archivo .env, (vea .env.example)

rem 3 de las cuatro variables tiene que estar presentes en un archivo .env. establecerlas con el siguiente script:

rem Get-Content .env | ForEach-Object {
rem  if ($_ -match '^(.*?)=(.*)$') {
rem    Set-Item -Path "Env:\$($Matches[1])" -Value $Matches[2]
rem  }
rem}
rem Esto recorre las variables definidas en el archivo .env y los establece para que estos sean visibles
rem por el script. Lo mas probable es que este comando este en el archivo README.md. (vea README.md)


rem MAIN_JAR depende de PROJECT_VERSION y tiene que ser el jar que se encuentra en el la carpeta target
rem este jar es generado automaticamente por el maven-dependency-plugin , (vea pom.xml)
set MAIN_JAR="puka-%PROJECT_VERSION%.jar"

rem INSTALLER_TYPE Se puede establecer los siguiente "app-image", "exe", "msi"
rem Solo se probaron "exe" y "msi". En cambio "app-image", no funciono en la maquina del autor
rem para msi se necesita tener instalado previamente WIX toolset, (https://wixtoolset.org)
set INSTALLER_TYPE=msi

rem ------ Configuracion de directorios y archivos ----------------------------------------
rem El script crea los directorios necesarios para la instalacion 
rem copia el directorio target/libs en target/intaller/input/libs
rem el directorio target/libs es generado por el plugin maven-dependency-plugin

IF EXIST target\java-runtime rmdir /S /Q  .\target\java-runtime
IF EXIST target\installer rmdir /S /Q target\installer

xcopy /S /Q target\libs\* target\installer\input\libs\
copy target\%MAIN_JAR% target\installer\input\libs\

rem ------ Modulos requeridos ---------------------------------------------------
rem Se usa jlink para detectar todos los modulos requeridos.
rem Esta seccion inicia con jdeps que analiza que modulos son requeridos por la aplicacion 

echo detecting required modules

"%JAVA_HOME%\bin\jdeps" ^
  -q ^
  --multi-release %JAVA_VERSION% ^
  --ignore-missing-deps ^
  --class-path "target\installer\input\libs\*" ^
  --print-module-deps target/classes/pe/puyu/app/App.class > temp.txt

set /p detected_modules=<temp.txt

echo detected modules: %detected_modules%

rem ------ MANUAL MODULES -----------------------------------------------------
rem jdeps no detecta todos los modulos faltantes, asi que se puede agrega los que faltan
rem en manual_modules, para este proyecto se detectaron 3 modulos que necesita la aplicacion
rem ,jdk.crypto.ec,jdk.zipfs,jdk.charsets
rem crypto para las conexiones http, zipfs para el sistema de ficheros y chartsets,
rem este ultimo es requerido por la libreria JTicketDesing vea issue #48 en el repositorio del proyecto
rem en caso que en el futuro se vea que faltan modulos se puede probar con todos los modulos 
rem se puede ver todos los modulos con el comando java --list-modules

rem !! No quites la coma al inicio ','!
set manual_modules=,jdk.crypto.ec,jdk.zipfs,jdk.charsets
echo manual modules: %manual_modules%

rem ------ RUNTIME IMAGE ------------------------------------------------------
rem Se usa  jlink para crear una imagen en tiempo de ejecucion
rem sin en caso la aplicacion resultante no se comporta como debe ser
rem puede deberse a que falte algun modulo pruebe reemplazando lo siguiente:
rem --add-modules %detected_modules%%manual_modules% ^
rem por:
rem --add-modules ALL-MODULE-PATH ^
rem en el siguiente codigo de jlink

echo creating java runtime image

call "%JAVA_HOME%\bin\jlink" ^
  --strip-native-commands ^
  --no-header-files ^
  --no-man-pages ^
  --compress=2 ^
  --strip-debug ^
  --add-modules %detected_modules%%manual_modules% ^
  --output target/java-runtime
  

rem ------ PACKAGING ----------------------------------------------------------
rem Finalmente lo mas ansiado por los mortales, nuestro instalador
rem si todo sale bien se deberia ver en consola algo parecido a lo siguiente:
rem " 31 archivo(s) copiado(s)
rem        1 archivo(s) copiado(s).
rem detecting required modules
rem detected modules: java.base,java.naming,java.scripting,java.sql,jdk.jfr,jdk.unsupported,jdk.unsupported.desktop
rem manual modules: ,jdk.crypto.ec,jdk.zipfs,jdk.charsets
rem creating java runtime image ".

rem y el instalador estara en el directorio target/installer

call "%JAVA_HOME%\bin\jpackage" ^
  --type %INSTALLER_TYPE% ^
  --dest target/installer ^
  --input target/installer/input/libs ^
  --name PUKA ^
  --main-class pe.puyu.AppLauncher ^
  --main-jar %MAIN_JAR% ^
  --java-options -Xmx2048m ^
  --runtime-image target/java-runtime ^
  --icon src/main/resources/assets/icon.ico ^
  --app-version %APP_VERSION% ^
  --vendor "PUKA SRL." ^
  --copyright "Copyright © 2023 PUYU SRL." ^
  --win-dir-chooser ^
  --win-shortcut ^
  --win-per-user-install ^
  --win-menu

del temp.txt