# 📈 Cobertura de Código con JaCoCo y Spring Boot

---

### 🧩 Proyecto base

Este módulo parte del proyecto `spring-rest-api`, desarrollado durante el curso de `Andrés Guzmán`, alojado en el
repositorio `java-spring-test-suite`.

La integración de `JaCoCo` y los ejemplos aquí presentados son fruto de una investigación técnica complementaria,
orientada a entender cómo se aplica la cobertura de código en proyectos reales con `Spring Boot`.

### 📚 Fuentes consultadas

- [JaCoCo Code Coverage with Spring Boot (Truong Bui - medium)](https://medium.com/@truongbui95/jacoco-code-coverage-with-spring-boot-835af8debc68)
- [Intro to JaCoCo (Baeldung)](https://www.baeldung.com/jacoco)

> 💡 `Nota`. Siempre es recomendable verificar documentación oficial y ejemplos aplicados a `Spring Boot 3+`
> porque hubo cambios al plugin y el manejo del reporte.

---

## ⚙️ Dependencias iniciales del proyecto

Antes de integrar `JaCoCo`, se presenta el `pom.xml` base del proyecto. Este incluye dependencias comunes para una
API REST con Spring Boot, MapStruct, OpenAPI y pruebas unitarias/integración.

> 💡 `Recomendación`: Mantener el `pom.xml` modular y ordenado. Agrupa dependencias por propósito
> (core, documentación, testing, etc.) y usa propiedades para versiones.

````xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.6</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>dev.magadiflo</groupId>
    <artifactId>spring-jacoco</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-jacoco</name>
    <description>Demo project for Spring Boot</description>
    <properties>
        <java.version>21</java.version>
        <org.mapstruct.version>1.6.3</org.mapstruct.version>
        <lombok-mapstruct-binding.version>0.2.0</lombok-mapstruct-binding.version>
        <openapi.version>2.8.13</openapi.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!--Agregado manualmente-->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${openapi.version}</version>
        </dependency>
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>${org.mapstruct.version}</version>
        </dependency>
        <!--/Agregado manualmente-->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!--MapStruct-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>${maven-compiler-plugin.version}</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>${org.mapstruct.version}</version>
                        </path>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok-mapstruct-binding</artifactId>
                            <version>${lombok-mapstruct-binding.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            <!--/MapStruct-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
````

## 🧪 Estructura de pruebas en el proyecto

El proyecto cuenta con una base sólida de pruebas, divididas en dos tipos:

### ✅ Pruebas unitarias

Simulan componentes individuales usando mocks, sin bases de datos reales.

| Clase de test                           | Herramienta utilizada |
|:----------------------------------------|-----------------------|
| AccountControllerMockMvcTest            | `MockMvc`             |
| AccountServiceImplMockitoAnnotationTest | `Mockito`             |

### 🔁 Pruebas de integración

Se prueban flujos completos, conectando con la base de datos (MySQL para entorno de desarrollo).

| Clase de test                      | Herramienta utilizada |
|:-----------------------------------|-----------------------|
| AccountControllerWebTestClientTest | `WebTestClient`       |
| AccountRepositoryMySQLTest         | `MySQL` real          |

> 🧠 `Nota`: No repetimos el código fuente de los tests porque ya fue documentado en `spring-rest-api`.
> Esta guía se enfoca de lleno en medición de cobertura y calidad de testing.
