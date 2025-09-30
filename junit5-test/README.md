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

        <!-- SLF4J (API de logging independiente de la implementación) -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.17</version>
        </dependency>

        <!-- Logback (implementación de logging basada en SLF4J, sucesor de Log4j) -->
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

### 📝 Logging en el proyecto: SLF4J + Logback

En pruebas unitarias es común imprimir valores con `System.out.println(...)` para depurar, pero esto no es una buena
práctica en proyectos reales. La forma recomendada es usar un framework de `logging` que sea:

- 📊 Consistente en todo el proyecto.
- ⚙️ Configurable según el entorno (desarrollo, pruebas, producción).
- 🔍 Filtrable por niveles (DEBUG, INFO, WARN, ERROR).
- 🔌 Extensible (escribir logs en archivos, consola, sistemas externos, etc.).

Por eso agregamos las dependencias al `pom.xml`: `slf4j-api` y el `logback-classic`.

#### 📘 `¿Qué es SLF4J?`

- `SLF4J` (Simple Logging Facade for Java) es una fachada de logging.
- Define una API genérica para escribir logs, pero no implementa el almacenamiento de los mismos.
- Permite que el código de la aplicación sea independiente de la librería de logging concreta.
  > 👉 Con `SLF4J` escribes tus logs siempre igual (`logger.info(...)`, `logger.error(...)`), y detrás puedes cambiar de
  > implementación (`Logback`, `Log4j2`, `JUL`, etc.) sin modificar tu código.

#### 📘 `¿Qué es Logback?`

- `Logback` es una de las implementaciones más populares de `SLF4J`.
- Es el sucesor de `Log4j` y está diseñado para ser más rápido y flexible.
- Permite definir configuraciones avanzadas en el archivo `logback.xml` (nivel de logs, formato, appender a archivos,
  etc.).

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

---

## 🏦 Creando la clase Account (Cuenta)

Antes de escribir pruebas unitarias, necesitamos una clase sencilla que represente un objeto de negocio.
En este caso, modelaremos una Cuenta bancaria (`Account`) con dos atributos principales:

- 👤 `person` → nombre del titular de la cuenta.
- 💰 `balance` → saldo de la cuenta, representado con `BigDecimal`.

````java
public class Account {
    private String person;
    private BigDecimal balance;

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
````

### 📘 Notas de diseño

- Usamos `BigDecimal` en lugar de double o float para representar dinero, ya que evita problemas de precisión en
  cálculos financieros.
- La clase está en su forma más simple (`POJO: Plain Old Java Object`), con atributos privados y getters/setters
  públicos.
- Al inicio solo contiene estado (datos), pero más adelante podemos extenderla con comportamiento (métodos) como
  `debit()`, `credit()`, etc. para enriquecer las pruebas.

## 🧪 Escribiendo y ejecutando nuestras primeras pruebas unitarias

Ahora que ya tenemos nuestra clase `Account`, vamos a dar el siguiente paso: crear su primera clase de pruebas unitarias
con `JUnit 5`.

### ⚡ Creando la clase de prueba desde IntelliJ IDEA

Podríamos crear la clase de prueba manualmente, pero `IntelliJ` nos facilita esta tarea con el atajo:

````bash
Ctrl + Shift + T
````

Este atajo puede hacer lo siguiente según el contexto:

- ✨ `Crear automáticamente una clase de prueba` a partir de la clase base (si el cursor está dentro de `Account`).
- 🔄 `Navegar entre clase y test`: si ya existe la clase de prueba, podemos saltar de la clase `Account` a `AccountTest`
  y viceversa.
- ➕` Agregar nuevos métodos de test` a la clase ya existente.

Al generarse automáticamente, la clase de prueba se verá así:

````java
package dev.magadiflo.junit5.app.model;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

}
````

Fijémonos en la línea:

````java
import static org.junit.jupiter.api.Assertions.*; 
````

- Esta importación estática corresponde a `JUnit 5` y nos permite usar directamente sus métodos de aserción clásicos
  como: `assertEquals(...)`, `assertTrue(...)`, `assertThrows(...)`, etc.

Sin embargo, en este proyecto también incluimos `AssertJ`, que nos da un estilo de aserciones mucho más expresivo.
Para usarlo necesitamos añadir:

````java
import static org.assertj.core.api.Assertions.assertThat;
````

#### 🎯 Estrategia que seguiremos

- Mantendremos ambos estilos de aserciones (`JUnit` y `AssertJ`) dentro del proyecto.
- La idea es comparar las diferencias en expresividad y legibilidad.
- A medida que avancemos, verás cómo `AssertJ` se vuelve más natural en casos complejos (listas, excepciones, objetos
  anidados, etc.).

### 📝 Creando el primer método de prueba

Sigamos con el atajo para generar un método de prueba:

````bash
Alt + insert
Seleccionamos Test Method
````

Y seleccionamos `Test Method`. Esto generará un método vacío con la anotación `@Test`:

````java
class AccountTest {
    @Test
    void shouldHaveAvailableBalanceAfterDeposit() {
        // Aquí irá nuestra lógica de prueba
    }
}
````

💡 Buenas prácticas sobre visibilidad en pruebas

Por convención:

- La clase de prueba debería declararse con acceso por `default (package-private)`, es decir, `class AccountTest {}`,
  y no `public class AccountTest {}`.
- Lo mismo aplica a los métodos de prueba: `void shouldHaveAvailableBalanceAfterDeposit() {}` en lugar de
  `public void ....`

Esto se hace porque las pruebas son internas al módulo y no deberían estar expuestas públicamente fuera del contexto
de ejecución de los tests.

En otras palabras: las pruebas `no forman parte del API de tu aplicación, sino de su suite de verificación`.

## Creando nuestra primera prueba

### 1. Preparando la clase Account

Vamos a darle un constructor a nuestra clase `Account` que reciba dos parámetros: `person` y `balance`.

Pero de manera `intencional`, cometeremos un error: asignaremos solo el balance al atributo, dejando el person sin
asignar.

````java

public class Account {
    private String person;
    private BigDecimal balance;

    public Account(String person, BigDecimal balance) {
        this.balance = balance; // 👈 intencionalmente olvidamos asignar 'person'
    }

    /* getters and setters */
}
````

### 2. Escribiendo la prueba

Ahora, desde la clase de prueba, creamos un objeto con datos conocidos y validamos que el nombre de la persona sea
correctamente retornado:

````java
class AccountTest {
    @Test
    void shouldReturnCorrectPersonNameWhenAccountIsCreated() {
        Account account = new Account("Martín", new BigDecimal("2000"));

        String real = account.getPerson();

        // JUnit 5
        assertEquals("Martín", real);

        // AssertJ
        assertThat(real).isEqualTo("Martín");
    }
}
````

### 3. Ejecutando la prueba

Para correrla desde `IntelliJ` presionamos: `Ctrl + Shift + F10`. El resultado esperado es un fallo, ya que olvidamos
asignar el parámetro person en el constructor:

````bash
org.opentest4j.AssertionFailedError: 
Expected :Martín
Actual   :null
````

### 4. Corrigiendo la clase base

Al revisar el constructor notamos el problema: `faltó asignar person`. Lo corregimos así:

````java
public class Account {
    private String person;
    private BigDecimal balance;

    public Account(String person, BigDecimal balance) {
        this.person = person;   // ✅ corregido
        this.balance = balance;
    }
    /* Getters and Setters */
}
````

### 5. Volvemos a ejecutar

Ejecutamos otra vez la prueba y ahora sí:

> ✅ la prueba pasa exitosamente.

## 💰 Escribiendo pruebas para el balance (saldo)

Ahora validaremos que, al crear una cuenta con un saldo inicial, este sea positivo.

````java
class AccountTest {
    @Test
    void shouldHavePositiveBalanceWhenAccountIsCreated() {
        Account account = new Account("Martín", new BigDecimal("2000"));

        // JUnit 5
        assertEquals(2000D, account.getBalance().doubleValue());
        assertNotEquals(-1, account.getBalance().compareTo(BigDecimal.ZERO));
        assertEquals(1, account.getBalance().compareTo(BigDecimal.ZERO));

        // AssertJ
        assertThat(account.getBalance()).isEqualByComparingTo("2000");
        assertThat(account.getBalance().compareTo(BigDecimal.ZERO)).isNotEqualTo(-1);
        assertThat(account.getBalance().compareTo(BigDecimal.ZERO)).isGreaterThan(0);
    }
}
````

### 1. Comparación directa del saldo

Sobre: `assertEquals(2000D, account.getBalance().doubleValue())`

- Convertimos el `BigDecimal` en `double` para hacer la comparación numérica.
- Es una forma rápida, pero puede no ser la más precisa debido a las particularidades de los decimales en coma
  flotante.

### 2. Verificando que el saldo no sea negativo

Sobre: `assertNotEquals(-1, account.getBalance().compareTo(BigDecimal.ZERO))`

- `compareTo(BigDecimal.ZERO)` devuelve:
    - `-1` si es menor que 0
    - `0` si es igual a 0
    - `1` si es mayor que 0
- En este caso, aseguramos que el resultado `no sea -1`, es decir, que no sea negativo.

### 3. Verificando que el saldo es mayor que 0

Sobre: `assertEquals(1, account.getBalance().compareTo(BigDecimal.ZERO))`

- Aquí confirmamos explícitamente que el resultado sea `1`, o sea, que el saldo es estrictamente mayor que cero.

### ✨ Diferencias con AssertJ

- `isEqualByComparingTo("2000")` → compara directamente valores de tipo `BigDecimal` de manera precisa
  (mejor que convertir a double).
- `isGreaterThan(0)` → mucho más expresivo que `assertEquals(1, compareTo(...))`.
- El código resulta más legible y cercano al lenguaje natural.

### 📌 Conclusión

> Con `JUnit 5` puedes lograr las validaciones, pero `AssertJ` te permite escribir pruebas más expresivas y fáciles
> de leer, especialmente cuando trabajas con objetos como `BigDecimal`.

## 🧪 Test Driven Development (TDD)

Con TDD primero escribimos la prueba, luego implementamos la solución. Esto nos permite guiar el diseño del código a
través de los tests.

En este ejemplo trabajaremos con la clase `Account`. Queremos comprobar la igualdad de objetos:

### Caso 1: Comparación por referencia (default en Java)

Creamos dos cuentas distintas pero con los mismos valores:

````java
class AccountTest {
    @Test
    void shouldNotBeSameReferenceWhenAccountAreCreatedSeparately() {
        Account account1 = new Account("Liz Gonzales", new BigDecimal("2500.00"));
        Account account2 = new Account("Liz Gonzales", new BigDecimal("2500.00"));

        // JUnit 5
        assertNotEquals(account1, account2);

        // AssertJ
        assertThat(account1).isNotSameAs(account2);
    }
}
````

✅ Este test pasará porque, aunque los atributos sean iguales, las referencias de memoria son distintas.
Java, por defecto, compara los objetos por referencia.

### Caso 2: Nueva regla de negocio → Comparar por valor

El negocio ahora exige que dos cuentas con mismos datos deben ser consideradas iguales, aunque se hayan creado por
separado.

````java
class AccountTest {
    @Test
    void shouldBeEqualWhenAccountsHaveSameValues() {
        Account account1 = new Account("Liz Gonzales", new BigDecimal("2500.00"));
        Account account2 = new Account("Liz Gonzales", new BigDecimal("2500.00"));

        // JUnit 5
        assertEquals(account1, account2);

        // AssertJ
        assertThat(account1).isEqualTo(account2);
    }
}
````

🚨 Este test falla, porque aún seguimos comparando referencias de memoria. El mensaje de error será algo así:

````bash
org.opentest4j.AssertionFailedError: 
Expected :dev.magadiflo.junit5.app.model.Account@55634720
Actual   :dev.magadiflo.junit5.app.model.Account@4b0d79fc
````

### Caso 3: Implementamos equals() (y hashCode())

Para que la comparación se haga por valor, sobrescribimos el método `equals()` en la clase `Account`.

````java
public class Account {
    /* omitted code */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(person, account.person) && Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person, balance);
    }
}
````

### Resultado final

Ahora, al ejecutar el test `shouldBeEqualWhenAccountsHaveSameValues()`, este pasa correctamente, porque la igualdad se
hace por valor.

> 📌 `Importante`: mantener también el test de referencia (`shouldNotBeSameReferenceWhenAccountsAreCreatedSeparately`)
> es útil para mostrar la diferencia entre referencia vs valor, reforzando el aprendizaje.

## 🧪 TDD para Débito y Crédito

Aplicando `Test Driven Development (TDD)` seguimos el ciclo clásico:

- ✍️ Escribir la prueba (rojo 🔴, porque fallará).
- 🔨 Implementar lo mínimo necesario para que la prueba pase.
- ✅ Refactorizar si es necesario, manteniendo todas las pruebas en verde.

### Paso 1: Definimos los métodos (aún sin lógica)

Primero declaramos los métodos en la clase `Account`, pero sin implementación:

````java
public class Account {
    /* omitted code */

    public void debit(BigDecimal amount) {
        // pendiente de implementación
    }

    public void credit(BigDecimal amount) {
        // pendiente de implementación
    }
    /* omitted code */
}
````

### Paso 2: Creamos las pruebas unitarias

Ahora escribimos dos tests:

- `shouldReduceBalanceWhenDebitIsApplied()` → valida que al debitar, el saldo disminuye.
- `shouldIncreaseBalanceWhenCreditIsApplied()` → valida que al acreditar, el saldo aumenta.

````java
class AccountTest {
    @Test
    void shouldReduceBalanceWhenDebitIsApplied() {
        Account account = new Account("Martín", new BigDecimal("2000"));
        account.debit(new BigDecimal("100")); // ejecutamos el método a probar

        // JUnit 5
        assertNotNull(account.getBalance());
        assertEquals(1900D, account.getBalance().doubleValue());
        assertEquals("1900", account.getBalance().toPlainString());

        // AssertJ
        assertThat(account.getBalance())
                .isNotNull()
                .isEqualByComparingTo("1900");
    }

    @Test
    void shouldIncreaseBalanceWhenCreditIsApplied() {
        Account account = new Account("Martín", new BigDecimal("2000"));
        account.credit(new BigDecimal("100")); // ejecutamos el método a probar

        // JUnit 5
        assertNotNull(account.getBalance());
        assertEquals(2100D, account.getBalance().doubleValue());
        assertEquals("2100", account.getBalance().toPlainString());

        // AssertJ
        assertThat(account.getBalance())
                .isNotNull()
                .isEqualByComparingTo("2100");
    }
}
````

🚨 Ambos tests fallarán, porque aún no hemos implementado la lógica de negocio:

````bash
org.opentest4j.AssertionFailedError: 
Expected :1900.0
Actual   :2000.0
````

````bash
org.opentest4j.AssertionFailedError: 
Expected :2100.0
Actual   :2000.0
````

### Paso 3: Implementamos la lógica

Ahora completamos los métodos en la clase Account:

````java
public class Account {
    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
````

### ✅ Resultado Final

Al ejecutar nuevamente los tests, ahora sí pasan en verde 🎉, porque ya se actualiza correctamente el atributo
`balance`:

- `debit` → resta el monto al saldo.
- `credit` → suma el monto al saldo.

## ⚠️ Probando y afirmando excepciones

En este paso simularemos un `escenario de error controlado`: cuando se intente debitar más dinero del que la cuenta
posee. Para manejar este caso, crearemos una excepción personalizada llamada `InsufficientMoneyException`.

### 🛠️ Creando la excepción personalizada

````java
public class InsufficientMoneyException extends RuntimeException {
    public InsufficientMoneyException(String message) {
        super(message);
    }
}
````

Al extender de `RuntimeException`, no será obligatorio declararla en la firma del método
(`checked` vs `unchecked exception`).

### 🧪 TDD → Primero escribimos la prueba

Queremos que el método `debit()` lance la excepción si el monto (`amount`) excede al `balance` de la cuenta:

````java
class AccountTest {
    @Test
    void shouldThrowInsufficientMoneyExceptionWhenDebitExceedsBalance() {
        Account account = new Account("Martín", new BigDecimal("2000"));

        // JUnit 5
        InsufficientMoneyException exception = assertThrows(InsufficientMoneyException.class, () -> {
            account.debit(new BigDecimal("5000")); // acción que dispara la excepción
        });
        assertEquals(InsufficientMoneyException.class, exception.getClass());
        assertEquals("Dinero insuficiente", exception.getMessage());

        // AssertJ
        assertThatThrownBy(() -> account.debit(new BigDecimal("5000")))
                .isInstanceOf(InsufficientMoneyException.class)
                .hasMessage("Dinero insuficiente");
    }
}
````

🔎 Aquí estamos usando:

- `JUnit 5` → `assertThrows()`: captura y nos permite inspeccionar la excepción.
- `AssertJ` → `assertThatThrownBy()`: ofrece una API más expresiva y fluida.

### ❌ Fallo esperado antes de la implementación

Como aún no hemos implementado la lógica en `debit()`, el test fallará:

````bash
org.opentest4j.AssertionFailedError: Expected dev.magadiflo.junit5.app.exception.InsufficientMoneyException to be thrown, but nothing was thrown.
````

### ✅ Implementación mínima para pasar el test

````java
public class Account {
    /* omitted code */
    public void debit(BigDecimal amount) {
        if (amount.compareTo(this.balance) > 0) {
            throw new InsufficientMoneyException("Dinero insuficiente");
        }
        this.balance = this.balance.subtract(amount);
    }
    /* omitted code */
}
````

Con esta lógica:

- Si el `amount` es mayor que el `balance` → se lanza `InsufficientMoneyException`.
- Caso contrario → se descuenta el saldo normalmente.

### 🟢 Ejecución final

Tras implementar la lógica, volvemos a correr las pruebas y:

- El test de `JUnit 5` pasa ✅
- El test de `AssertJ` también pasa ✅

Esto valida que la excepción se lanzó correctamente solo en el caso esperado.

💡 Tip práctico:
> En `TDD`, las excepciones suelen ser una de las primeras reglas de negocio críticas que se prueban. Validar errores
> esperados no solo ayuda a robustecer la lógica, sino también a documentar qué casos no están permitidos en el dominio.
