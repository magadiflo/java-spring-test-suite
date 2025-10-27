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
- ChatGPT, ClaudeAI, Copilot

> 💡 `Nota`. Siempre es recomendable verificar documentación oficial y ejemplos aplicados a `Spring Boot 3+`
> porque hubo cambios al plugin y el manejo del reporte.

---

## ⚙️ Dependencias iniciales del proyecto

Antes de integrar `JaCoCo`, se presenta el `pom.xml` base del proyecto. Este incluye dependencias comunes para una
API REST con Spring Boot, MapStruct, OpenAPI, etc.

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

Para este proyecto, las `métricas de cobertura de código` estarán basadas únicamente en `pruebas unitarias`.
Esto se alinea con el estándar corporativo utilizado junto con `JaCoCo` + `SonarQube`, donde
`las pruebas de integración no se consideran para el cálculo de cobertura`.

Esto permite obtener métricas realistas y comparables con pipelines profesionales que aplican `Quality Gates`.

### ✅ Pruebas Unitarias

Aíslan y prueban la lógica de negocio sin dependencias externas. Usan mocks para simular componentes como repositorios
o clientes HTTP. En este proyecto tenemos las siguientes pruebas unitarias ya implementadas:

| Clase de test                           | Herramienta utilizada |
|:----------------------------------------|----------------------:|
| AccountControllerMockMvcTest            |             `MockMvc` |
| AccountServiceImplMockitoAnnotationTest |             `Mockito` |

> 📍 Esta guía se enfoca exclusivamente en la `medición de cobertura de código`, que es lo que evalúa `JaCoCo` y
> herramientas como `SonarQube`.

### 📌 ¿Por qué solo pruebas unitarias para cobertura?

Aunque `JaCoCo` **técnicamente puede medir cualquier tipo de prueba** que se ejecute en la JVM (unitarias, integración,
E2E), en entornos corporativos se mide exclusivamente la `cobertura de pruebas unitarias`.

### 🎯 Razones fundamentales:

#### 1️⃣ Estándar de la industria

- Herramientas como `SonarQube` (líder en análisis de código estático) `solo consideran` `pruebas unitarias` para
  `métricas de cobertura` por defecto.
- Los `Quality Gates` corporativos establecen umbrales basados en `cobertura unitaria` (ej: `80% mínimo`).

#### 2️⃣ Velocidad y eficiencia en CI/CD

- Las pruebas unitarias son `rápidas` (milisegundos) y se ejecutan en cada commit.
- Las pruebas de integración son `lentas` (segundos/minutos) y requieren infraestructura (BD, servicios externos).
- Medir cobertura con tests lentos haría inviable el feedback rápido en pipelines.

#### 3️⃣ Propósitos diferentes

- `Cobertura unitaria` → Mide `calidad del código` y diseño testeable.
- `Pruebas de integración` → Validan `funcionalidad completa` del sistema.
- Mezclar ambas distorsiona la métrica: alta cobertura podría venir solo de tests de integración, ocultando código no
  testeado unitariamente.

#### 4️⃣ Aislamiento y mantenibilidad

- Las pruebas unitarias verifican `lógica de negocio pura`, sin dependencias externas.
- Medir cobertura aquí incentiva código `desacoplado, SOLID y mantenible`.

### 📊 Flujo corporativo estándar

```
Cobertura de Código (JaCoCo/SonarQube):
└── Pruebas Unitarias ✅ (se miden)

Validación de Funcionalidad (CI/CD Pipeline):
├── Pruebas Unitarias ✅
└── Pruebas de Integración ✅ (importantes pero no se miden para cobertura)
```

> 💡 **En resumen:** Las pruebas de integración son **críticas para validar funcionalidad**, pero no se usan para
> métricas de cobertura porque tienen un propósito distinto y ralentizarían el proceso de análisis de calidad de código.

### 🧹 ¿Qué pasa con las pruebas de integración?

Las siguientes clases de prueba, son pruebas de integración que trabajamos en el proyecto `spring-rest-api` y que en
este proyecto de JaCoCo no las vamos a considerar:

| Clase no considerada               |                  Motivo |
|:-----------------------------------|------------------------:|
| AccountControllerWebTestClientTest | `Prueba de Integración` |
| AccountRepositoryMySQLTest         | `Prueba de Integración` |

Estas pruebas son valiosas para validar funcionalidad `end-to-end`, pero
`no se consideran para métricas de cobertura` en pipelines corporativos. Se recomienda ejecutarlas en etapas
separadas del `CI/CD`, con herramientas como `Jenkins`, `GitHub Actions` o `GitLab CI`.

### 🏢 Flujo típico en empresas

````
1. Commit código
2. Pipeline CI/CD ejecuta:
   ├─ Pruebas Unitarias → JaCoCo genera reporte → SonarQube valida umbral
   └─ Pruebas Integración → Validan funcionalidad completa
3. Si AMBAS pasan → Deploy ✅
4. Si alguna falla → Deploy bloqueado ❌
````

### 🧭 Ruta de aprendizaje alineada al mundo real

#### 1. 🧪 Proyecto actual: `JaCoCo`

- Solo `Pruebas Unitarias`.
- Configurar reportes de cobertura.

#### 2. 📊 Siguiente proyecto: `SonarQube`

- Solo `Pruebas Unitarias`.
- Integrar reportes de `JaCoCo` con SonarQube.
- Configurar Quality Gates (umbrales de cobertura).

#### 3. 🚀 Proyecto Futuro: `CI/CD (Jenkins/GitHub Actions)`

- Pruebas unitarias (para cobertura)
- Pruebas de integración (para validación funcional)
- Pipeline completo que ejecuta ambas en stages separados

