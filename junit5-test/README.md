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
