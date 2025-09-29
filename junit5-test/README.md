# 🧪 JUnit 5

---

## 📦 Creando y configurando el proyecto con JUnit 5

El primer paso para trabajar con `JUnit 5` en un proyecto de `Java puro` con `Maven` es declarar las dependencias y
configurar el plugin que permitirá ejecutar las pruebas.

````xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>dev.magadiflo</groupId>
    <artifactId>junit5-test</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    <dependencies>
        <!-- JUnit 5 (Jupiter) -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.13.4</version>
            <scope>test</scope>
        </dependency>

        <!-- AssertJ para aserciones modernas y expresivas -->
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <version>3.25.3</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <!-- Plugin para ejecutar pruebas desde consola -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.4</version>
            </plugin>
        </plugins>
    </build>

</project>
````

### 📘 Sobre la dependencia `junit-jupiter`

La dependencia `junit-jupiter` es un "paquete todo en uno" que agrupa tres componentes principales:

- `junit-jupiter-api`: API principal para escribir pruebas con anotaciones como `@Test`, `@BeforeEach`, `@DisplayName`,
  etc.
- `junit-jupiter-engine`: motor de ejecución que permite correr las pruebas escritas con la API.
- `junit-jupiter-params`: módulo adicional para pruebas parametrizadas con `@ParameterizedTest`, `@ValueSource`,
  `@CsvSource`, entre otros.

El `scope` definido como `test` indica que estas dependencias se usan exclusivamente en el entorno de pruebas, y no se
incluyen en el empaquetado final de la aplicación. Esto es común en bibliotecas como `JUnit`, `Mockito` o `AssertJ`.

### 📘 Sobre la dependencia `assertj-core` (opcional, pero recomendable)

`AssertJ` es una librería que extiende las capacidades de `JUnit` y ofrece un estilo moderno para escribir aserciones
basado en el método `assertThat(...)`, ampliamente recomendado por su expresividad, legibilidad y capacidad de
encadenamiento.

Esta biblioteca se integra perfectamente con `JUnit 5` y ofrece una sintaxis fluida para validar resultados,
colecciones, excepciones, fechas, objetos personalizados, entre otros.

> `Nota`: Esta dependencia es opcional. Si solo se desea ejecutar pruebas básicas, basta con incluir `junit-jupiter`.
> Sin embargo, en este proyecto se ha agregado `assertj-core` para beneficiarnos de un estilo de aserción más legible,
> expresivo y alineado con buenas prácticas modernas.

Ventajas principales:

- ✅ Aserciones más legibles y naturales: `assertThat(valor).isEqualTo(...)`.
- ✅ Encadenamiento de condiciones en una sola línea.
- ✅ Mensajes de error más claros y detallados.
- ✅ Soporte para estructuras complejas como listas, mapas, streams, etc..

> En proyectos `Spring Boot`, `AssertJ` ya viene incluido de forma transitiva al usar `spring-boot-starter-test`. Sin
> embargo, en proyectos `Java puro` como este módulo, es necesario agregarlo explícitamente.

### ⚙️ Sobre el plugin `maven-surefire-plugin`

El plugin `surefire` permite ejecutar pruebas desde la terminal usando `Maven`, sin necesidad de un entorno gráfico.
Aunque durante el desarrollo solemos usar el IDE (por ejemplo, `IntelliJ IDEA`, con `Ctrl + Shift + F10` para ejecutar
una clase o método de prueba), existen escenarios donde esto no es posible:

- Ejecución remota en servidores o entornos de integración continua `CI/CD`.
- Máquinas sin entorno gráfico o sin IDE instalado.
- Automatización de pruebas como parte de un pipeline.

En esos casos, este plugin permite ejecutar las pruebas con un simple comando:

````bash
$ mvn test
````

#### 💡 Buenas prácticas iniciales

- Usa nombres descriptivos en las pruebas (`shouldCalculateTotalCorrectly()` en vez de `test1()`).
- Aplica la convención `Given-When-Then` para estructurar el cuerpo de la prueba.
- Prefiere `AssertJ` frente a `Assertions.assertEquals(...)` por legibilidad.
- Mantén tus clases de prueba en un paquete paralelo a la lógica de negocio (ej: `src/test/java/dev/magadiflo/...`).

## ⚙️ Configuración de Maven y variables de entorno

Para ejecutar pruebas desde consola con `maven`, primero debemos instalarlo en la máquina local y configurar sus
variables de entorno.

### 📥 Instalación de Maven

- Ir a la página de maven y descargar el binario [Binary zip archive](https://maven.apache.org/download.cgi).
- En mi caso descargué el archivo: `apache-maven-3.9.9-bin.zip`.
- Descomprimimos el archivo en un directorio de nuestra preferencia, por ejemplo:
  `C:\Program Files\maven\apache-maven-3.9.9`.

### 🖥️ Configuración de variables de entorno (Windows)

- Ir a las variables de entorno del sistema en windows.
- En `System variables` creamos la variable `MAVEN_HOME`, similar a cómo creamos la variable `JAVA_HOME` para
  java.
- El valor de la variable `MAVEN_HOME` será la ruta de nuestro binario descargado:
  `MAVEN_HOME=C:\Program Files\maven\apache-maven-3.9.9`.
- En `System variables` editamos la variable `Path` la parte faltante a nuestra ruta de maven:
  `%MAVEN_HOME%\bin`.

Al final deberíamos tener algo como esto:

![01.png](assets/01.png)

### ✅ Verificación de instalación

Cierra cualquier terminal abierta, abre una nueva ventana de cmd o PowerShell y ejecutamos `mvn -version`. Si todo está
configurado correctamente, deberíamos obtener una salida similar a:

````bash
$ mvn -version
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: C:\Program Files\maven\apache-maven-3.9.9
Java version: 21.0.6, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-21.0.6
Default locale: en_US, platform encoding: UTF-8
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
````

## ▶️ Ejecutar pruebas: IDE vs Consola

En proyectos de `Java` con `Maven`, podemos ejecutar las pruebas de dos formas principales:

### 1️⃣ Desde el IDE (IntelliJ IDEA)

La opción más sencilla durante el desarrollo es ejecutar pruebas directamente desde el IDE:

- En `IntelliJ IDEA`, ubica el proyecto en el panel de `Maven`:
    ````bash
    $ Maven/junit5-test/Lifecycle/test
    ````
- Haz clic derecho en `test` y selecciona `Run junit5-test [test]`.

📌 Ventaja:

- Ideal para desarrollo rápido y depuración.
- Permite correr pruebas individuales, con cobertura y con depurador.

### 2️⃣ Desde la consola con Maven

También podemos ejecutar las pruebas sin depender de un IDE usando el comando básico `mvn test`. Esto es útil en:

- 🖥️ Servidores sin entorno gráfico.
- 🤖 Pipelines de CI/CD (GitHub Actions, Jenkins, GitLab CI, etc.).
- 📦 Automatización de validaciones antes de desplegar.

Ejemplo de ejecución (sin tests implementados aún):

````bash
D:\programming\spring\01.udemy\02.andres_guzman\03.junit_y_mockito_2023\java-spring-test-suite\junit5-test (feature/junit5)
$ mvn test
[INFO] Scanning for projects...
[INFO]
[INFO] ---------------------< dev.magadiflo:junit5-test >----------------------
[INFO] Building junit5-test 1.0-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- resources:3.3.1:resources (default-resources) @ junit5-test ---
[INFO] Copying 0 resource from src\main\resources to target\classes
[INFO]
[INFO] --- compiler:3.13.0:compile (default-compile) @ junit5-test ---
[INFO] Nothing to compile - all classes are up to date.
[INFO]
[INFO] --- resources:3.3.1:testResources (default-testResources) @ junit5-test ---
[INFO] skip non existing resourceDirectory D:\programming\spring\01.udemy\02.andres_guzman\03.junit_y_mockito_2023\java-spring-test-suite\junit5-test\src\test\resources
[INFO]
[INFO] --- compiler:3.13.0:testCompile (default-testCompile) @ junit5-test ---
[INFO] Nothing to compile - all classes are up to date.
[INFO]
[INFO] --- surefire:3.5.4:test (default-test) @ junit5-test ---
[INFO] No tests to run.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.345 s
[INFO] Finished at: 2025-09-29T16:05:28-05:00
[INFO] ------------------------------------------------------------------------
````

📌 Ventaja:

- Estándar en automatización y despliegues.
- Permite ejecutar todas las pruebas o un subconjunto (según configuración).

### 3️⃣ Ejecutar tests específicos con `@Tag`

`JUnit 5` permite categorizar pruebas con la anotación `@Tag`. Esto es útil para agrupar tests
(por ejemplo: `unit`, `integration`, `slow`, `account`).

Ejemplo de test con un `@Tag`:

````java
class AccountServiceTest {
    @Test
    @Tag("account")
    void shouldCreateAccountSuccessfully() {
        // lógica de prueba
    }
}
````

Para ejecutar solo los tests con un `tag específico`, debemos configurarlo en el plugin `maven-surefire-plugin`:

````xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.5.4</version>
    <!-- Ejecutar un tag en específico -->
    <configuration>
        <groups>account</groups>
    </configuration>
</plugin>
````

Ahora, al ejecutar:

````bash
$ mvn test
````

Solo se ejecutarán los tests anotados con `@Tag("account")`.

📌 Nota importante:

- Si no configuramos `groups`, `Maven` ejecutará todas las pruebas.
- Podemos especificar múltiples grupos separados por comas: `<groups>unit,integration</groups>`
