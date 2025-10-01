# 🧪 Mockito

---

## ⚙️ Configuración del proyecto con JUnit 5 y Mockito

Crearemos un proyecto de Java puro con Maven llamado `mockito-test`. Este proyecto contará con las mismas dependencias
que se trabajó en el proyecto de `junit5-test`, con la diferencia de que aquí agregaremos las dependencias de `Mockito`.

📂 Dependencias en pom.xml

````xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>dev.magadiflo</groupId>
    <artifactId>mockito-test</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    <dependencies>
        <!-- JUnit 5 -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.13.4</version>
            <scope>test</scope>
        </dependency>
        <!-- Mockito Core -->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>5.20.0</version>
            <scope>test</scope>
        </dependency>
        <!-- Extensión para integración con JUnit 5 -->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <version>5.20.0</version>
            <scope>test</scope>
        </dependency>

        <!-- AssertJ (aserciones más expresivas y legibles) -->
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <version>3.25.3</version>
            <scope>test</scope>
        </dependency>
        <!-- Logging con SLF4J + Logback -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.17</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.5.18</version>
            <scope>compile</scope>
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

### 📌 Explicación de las dependencias principales

- `JUnit 5 (junit-jupiter)` → Framework de pruebas unitarias moderno en Java.
- `Mockito Core (mockito-core)` → Librería base de `Mockito`, permite crear mocks, stubs y espías.
- `Mockito + JUnit 5 (mockito-junit-jupiter)` → Extensión que facilita la integración entre `Mockito` y `JUnit 5`.
- `AssertJ (assertj-core)` → Librería de aserciones con una sintaxis más fluida y legible.
- `SLF4J + Logback` → Sistema de logging para imprimir mensajes durante la ejecución.

👉 Con esta configuración ya podemos comenzar a escribir nuestras pruebas unitarias con `Mockito`.

