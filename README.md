# Puka

Utilidad de servicio de impresión, para puntos de venta.

## Indice

1. [Estructura del proyecto](#estructura-del-proyecto)
2. [Construir el proyecto](#construir-el-proyecto)
3. [Generación de instaladores](#generación-de-instaladores)

## Estructura del proyecto

- PukaFx
  - installers (Scripts de generación de instaladores)
  - src (Código fuente)
    - main
      - java
        - pe.puyu.pukafx
          - app (Clase Aplicacion JavaFX)
          - model (JavaFX beans)
          - services (Servicios en puka)
            - bifrost (Conexion websocket con bifrost)
            - printer (Servicio de impresión)
            - printingtest (Servicio de pruebas de impresión)
            - trayicon (Servicio trayicon)
          - util (helpers)
          - validations (Validador parametros de conexión a bifrost)
          - views (Controladores JavaFX)
          - AppLauncher.java (Clase principal)
      - resources
        - pe.puyu.pukafx
          - assets (Iconos)
          - fonts (Fuentes de texto)
          - styles (Estilos css javafx)
          - testPrinter (Modelos json pruebas de impresión)
          - views (Vistas FXML JavaFX)
  - .env.example (Ejemplo de configuración .env)
  - pom.xml (Declaración de dependencias y plugins compilación)
  - README.md (Instruccion sobre PukaFX)

## Construir el proyecto

* Prerequisitos:
  * [JAVA openjdk 17 o superior](https://ed.team/blog/instalar-openjdk-en-linux).
  * [Apache Maven](https://ubunlog.com/apache-maven-instalacion-ubuntu/) , algunos IDE's ya tren maven incluido, ejm.
    Intellij IDEA.

1. Clonar el repositorio<br>
   Utilizando su IDE favorito o por medio de linea de comandos.

```
git clone git@github.com:puyu-pe/puka.git
```

2. Ejecutar proyecto<br>
   Utilando su IDE favorito debe ejecutar la acciones maven clean
   compile y javafx:run, o por medio de linea de comandos:

```
mvn clean compile javafx:run
```

## Generación de instaladores

Los generadores de instaladores son scripts que se tienen que ejecutar
en una maquina con el sistema operativo objetivo, por ejemplo si se quiere
un instalador msi para dispositivos windows, se tiene que ejecutar el script
en una pc con windows 7 o superior.<br>

Los scripts utilizan jpackage , jdeps y jlink.
[Ver el proyecto JPackageScriptFX](https://github.com/dlemmermann/JPackageScriptFX.git)
para mas información.

> NOTA: **JAVA_HOME**, variable de entorno, tiene que estar correctamente configurado.
> ``` 
> $ echo $JAVA_HOME                                                                                                                            ─╯
>/usr/lib/jvm/java-17-openjdk 
> ```

### Preparar el proyecto

Según su IDE de turno, se tiene que ejecutar las acciones maven
clean, compile y package.

```bash
mvn clean compile package
```

### Preparar entorno

#### 1. Crear el archivo .env

```
cp .env.example .env
```

#### 2. Configurar archivo .env

* APP_VERSION: Version de la aplicacion a generar, según el archivo
  VERSION en la raiz del proyecto.
* PROJECT_VERSION: Version del proyecto,
  **OJO: Tiene que ser la misma version que el pom.xml.**

```xml

<project>
  <!-- ... demas configuraciones -->
  <groupId>pe.puyu</groupId>
  <artifactId>PukaFX</artifactId>
  <version>0.3.0</version> <!-- PROJECT_VERSION -->
  <!-- ... demas configuraciones -->
</project>
```

> WARNING: si no se especifica la misma version del pom.xml, fallara el script generador de instaladores

* JAVA_VERSION: Version de java con la que se tiene que generar
  el instalador. (17 o superior).
* INSTALLER_TYPE: Tipo de instalador soportado: msi, exe, app-image, deb, rpm.

### Generar instalador para Windows

* Tipos de instaladores soportados
  * msi (Requisito [WIX toolset ](https://wixtoolset.org))
  * exe (Requisito [WIX toolset ](https://wixtoolset.org))
  * app-image (No probado en la maquina del autor)

#### 1. Establecer las variables de entorno

El siguiente comando recorrera cada una de las variables especificadas
en el .env y las establecera para que sean visibles para el script.

```shell
Get-Content .env | ForEach-Object {
 if ($_ -match '^(.*?)=(.*)$'){
   Set-Item -Path "Env:\$($Matches[1])" -Value $Matches[2]
 }
}
```

#### 2. Ejecutar script, para generar el instaldor

```bash
./installers/installer-windows.bat
```

### Generar instalador para Linux

* Tipos de instaladores soportados
  * deb (Una distribución debian)
  * rpm (Una distribución redhat)
  * app-image (Probado solo en archlinux)

#### 1. Establecer las variables de entorno

El siguiente comando recorrera cada una de las variables especificadas
en el .env y las establecera para que sean visibles para el script.

```bash
export $(cat .env | xargs)
```

#### 2. Ejecutar script, para generar instalador

```bash
./installers/installer-linux.sh
```

## ¿ Problemas con el empaquetado final del instalador ?
Si la aplicación final generado con el instalador, falla de manera extraña
sin razón alguna, puede deberse a que falta un modulo en el empaquetado final.

Modificar el bloque de codigo jlink --add-modules:
```bash
# ...

echo "creating java runtime image"
$JAVA_HOME/bin/jlink \
  --strip-native-commands \
  --no-header-files \
  --no-man-pages  \
  --compress=2  \
  --strip-debug \
  --add-modules "${detected_modules}${manual_modules}" \
  --output target/java-runtime
  
# ...
```
A lo siguiente:
```bash
# ...

echo "creating java runtime image"
$JAVA_HOME/bin/jlink \
  --strip-native-commands \
  --no-header-files \
  --no-man-pages  \
  --compress=2  \
  --strip-debug \
  --add-modules ALL-MODULE-PATH \
  --output target/java-runtime
# solo modificar --add-modules 
# ...
```

> Nota: Puede listar todos los modulos disponibles en java
> ``` 
> $ java --list-modules 
> ```
