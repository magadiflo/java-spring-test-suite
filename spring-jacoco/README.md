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

Para este proyecto, `las métricas de cobertura de código estarán basadas únicamente en pruebas unitarias`.
Esto se alinea con el estándar corporativo utilizado junto con `JaCoCo` + `SonarQube`, donde:

> 🚫 Las pruebas de integración `no participan en el cálculo de cobertura`.

Aunque en este proyecto `únicamente utilizamos las pruebas unitarias` para el cálculo de cobertura, mantenemos las
pruebas de integración porque forman parte esencial de un entorno empresarial real. Su presencia nos permite simular un
escenario donde coexisten distintos niveles de pruebas y evaluar cómo interactúan al momento de ejecutar los procesos
de validación. Esto refleja de manera más precisa el contexto en el que normalmente se desarrollan y despliegan
aplicaciones en producción.

### ✅ Pruebas Unitarias (sí contribuyen a cobertura)

Evalúan la lógica de negocio de manera aislada. Se simulan dependencias externas como repositorios o llamadas HTTP para
evitar efectos colaterales y mantener ejecuciones rápidas.

| Clase de test         | Sufijo distintivo | `@Tag` asociado | Herramienta principal |
|:----------------------|-------------------|-----------------|----------------------:|
| AccountControllerTest | `Test`            | `unit`          |             `MockMvc` |
| AccountServiceTest    | `Test`            | `unit`          |             `Mockito` |

- 🛠️ Ejecutan solo JUnit y mocks.
- ⚡ Son rápidas y determinísticas.
- 📈 Alimentan las métricas de cobertura con JaCoCo.

### ❌ Pruebas de Integración (no cuentan para cobertura)

Ejecutan componentes reales: base de datos, capa web, repositorio. Verifican el comportamiento end-to-end.

| Clase de test       | Sufijo distintivo | `@Tag` asociado | Infraestructura |
|:--------------------|-------------------|-----------------|----------------:|
| AccountControllerIT | `IT`              | `integration`   | `WebTestClient` |
| AccountRepositoryIT | `IT`              | `integration`   |    `MySQL` real |

- 🧩 Validan funcionalidad completa.
- 🚀 Se ejecutan en pipelines CI/CD como etapa separada.
- 🛑 Si fallan, pueden bloquear un despliegue.

Aunque son críticas en producción, `no se usan para cobertura`, ya que pueden dar una falsa sensación de calidad al
ejecutar código que no está correctamente validado con asserts.

#### 🎛️ Ejecución selectiva con Maven

La separación por `@Tags` y sufijos nos permite filtrar qué tipo de pruebas ejecutar:

````bash
# Ejecutar solo pruebas unitarias
mvn test -Dgroups="unit"

# Ejecutar solo pruebas de integración
mvn test -Dgroups="integration"

# Ejecutar ambos tipos
mvn test -Dgroups="unit,integration"

# Excluir integración
mvn test -DexcludedGroups="integration"
````

Esto es exactamente como se organizan los pipelines corporativos en `GitHub Actions`, `GitLab CI` o `Jenkins`.

### ✅ En esta guía

Solo se utilizarán las `pruebas unitarias` para:

| Objetivo                    | Herramienta |
|-----------------------------|-------------|
| Medir cobertura             | ✅ JaCoCo    |
| Analizar calidad del código | ✅ SonarQube |
| Cumplir Quality Gates       | ✅ CI/CD     |

> 📍 Esta guía se enfoca exclusivamente en la `medición de cobertura de código`, por lo tanto, hacemos uso
> exclusívamente de las `Pruebas Unitarias`, que es lo que evalúa `JaCoCo` y herramientas como `SonarQube`.

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

Las siguientes clases de prueba, son pruebas de integración que trabajamos en el proyecto `spring-rest-api`
(aunque aquí están renombradas siguiendo una convención de nombres) y que en este proyecto de JaCoCo no las vamos a
considerar:

| Clase no considerada |                  Motivo |
|:---------------------|------------------------:|
| AccountControllerIT  | `Prueba de Integración` |
| AccountRepositoryIT  | `Prueba de Integración` |

Estas pruebas son valiosas para validar funcionalidad `end-to-end`, pero
`no se consideran para métricas de cobertura` en pipelines corporativos. Se recomienda ejecutarlas en etapas
separadas del `CI/CD`, con herramientas como `Jenkins`, `GitHub Actions` o `GitLab CI`.

### 🏢 Flujo típico en empresas

````scss
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

## 📌 ¿Qué es JaCoCo?

Las `pruebas unitarias` juegan un rol clave en el aseguramiento de calidad. Nos permiten validar la lógica de cada
método y componente sin depender de infraestructura externa. Una vez implementadas, surge la gran pregunta:

> **¿Cuánto del código de producción está siendo realmente probado?**

Ahí aparece `JaCoCo (Java Code Coverage)`. Es la herramienta estándar en el ecosistema Java para
`medir la cobertura de código ejecutado por pruebas unitarias`. Gracias a esta métrica se pueden detectar:

- Código no ejecutado por las pruebas.
- Ramas lógicas sin validar.
- Secciones con potenciales defectos ocultos.

### 🧠 ¿Qué es la cobertura de código?

La `cobertura de código` es una métrica que indica qué porcentaje del código fuente ha sido ejecutado durante las
pruebas automatizadas. Nos ayuda a:

- Identificar áreas no testeadas.
- Detectar código muerto o innecesario.
- Priorizar mejoras en la suite de pruebas.
- Aumentar la confianza antes de un despliegue.

### 🔍 ¿Qué cubre exactamente JaCoCo?

`JaCoCo` genera métricas detalladas sobre:

| Métrica | Qué mide                        | Relevancia                             |
|---------|---------------------------------|----------------------------------------|
| Líneas  | Líneas ejecutadas               | Visión general del alcance del testing |
| Ramas   | Condicionales y flujos alternos | Detecta lógicas no ejercitadas         |
| Métodos | Métodos testeados               | Indica cobertura funcional             |
| Clases  | Código de producción alcanzado  | Nivel global de la app                 |

### 🎯 Objetivo corporativo típico

> Al menos `80%+` `de cobertura unitaria` en proyectos críticos.

### 🏢 Estándar corporativo actual

En empresas que integran `JaCoCo` + `SonarQube` + `CI/CD`, la `cobertura` se calcula exclusivamente con
`pruebas unitarias`. Razones:

- ✅ Se ejecutan rápido y en cada commit.
- ✅ `SonarQube` solo considera cobertura unitaria por defecto.
- ✅ Dan métricas estables y comparables.
- ⚠️ Pruebas de integración no reflejan calidad del código línea a línea.

````scss
Cobertura de Código (JaCoCo/SonarQube)
└── ✅ Pruebas Unitarias

Validación Funcional
├── ✅ Pruebas Unitarias
└── ✅ Pruebas de Integración (pipelines separados)
````

### 🏁 Conclusión

`JaCoCo` es una herramienta poderosa para medir la calidad de nuestras `Pruebas Unitarias`. En esta guía nos
enfocaremos exclusivamente en `Pruebas Unitarias`, que son las que realmente se consideran para métricas de cobertura
en entornos empresariales.

Las pruebas de integración siguen siendo importantes, pero se ejecutan en etapas separadas del pipeline y no se
incluyen en los reportes de cobertura.

- ✅ **Cobertura de código = Pruebas unitarias**
- 🔁 **Validación funcional = Pruebas de integración**

## 🔧 Configuración de JaCoCo en el proyecto

En proyectos Java con Maven, `JaCoCo se integra como plugin`, no como dependencia. Esto significa que
`no necesitamos agregar nada` en `<dependencies>`, solo configurar el plugin en la sección `<build>` del `pom.xml`.

### ⚙️ Plugin básico de JaCoCo

````xml

<project>
    <properties>
        <!-- JaCoCo Properties -->
        <jacoco.version>0.8.12</jacoco.version>
    </properties>
    <build>
        <plugins>
            <!--JaCoCo Maven Plugin-->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>${jacoco.version}</version>
                <executions>
                    <!--Preparar agente de JaCoCo antes de ejecutar tests-->
                    <execution>
                        <id>prepare-agent</id>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>

                    <!--Generar reporte después de ejecutar tests-->
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <!--/JaCoCo Maven Plugin-->
        </plugins>
    </build>
</project>
````

### 🧩 Explicación técnica

| Elemento                     | Qué hace                                                                                |
|------------------------------|-----------------------------------------------------------------------------------------|
| `<executions>`               | Define las fases del ciclo de vida Maven donde se ejecutará el plugin.                  |
| `<execution>`                | Cada ejecución tiene un propósito: preparar el agente o generar el reporte.             |
| `<goal>prepare-agent</goal>` | Instrumenta el código antes de ejecutar los tests para rastrear qué líneas se ejecutan. |
| `<goal>report</goal>`        | Genera el reporte de cobertura en `target/site/jacoco/index.html` (HTML, XML).          |
| `<phase>test</phase>`        | Ejecuta el reporte justo después de las `pruebas unitarias`.                            |

### 🧪 ¿Qué pruebas se miden con esta configuración?

Esta configuración está diseñada para medir `únicamente pruebas unitarias`, siguiendo el enfoque corporativo:

- ✅ `Pruebas unitarias` → Se ejecutan en la fase `test` → JaCoCo las mide.
- ❌ `Pruebas de integración` → No se ejecutan en esta fase → No se miden.

> 📌 `Importante`: En entornos empresariales, `SonarQube` solo analiza cobertura generada por `pruebas unitarias`.
> Por eso, esta configuración excluye pruebas de integración y se alinea con los `Quality Gates` corporativos.

