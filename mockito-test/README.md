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

## 🗂️ Creando la estructura de directorios y archivos para las pruebas

Para organizar nuestro proyecto de pruebas con `Mockito`, definiremos una estructura básica de paquetes:

- 📦 `model` → Contendrá las entidades o clases de dominio (en este caso, `Exam`).
- 📦 `repository` → Contendrá las interfaces que simulan la capa de acceso a datos.
- 📦 `service` → Contendrá las interfaces de lógica de negocio que utilizaremos en nuestras pruebas.

### 📝 Clase de dominio: Exam

La clase `Exam` representa un examen con un id, un nombre y una lista de preguntas.

````java
public class Exam {
    private Long id;
    private String name;
    private List<String> questions = new ArrayList<>();

    public Exam(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Exam{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", questions=").append(questions);
        sb.append('}');
        return sb.toString();
    }
}
````

📌 `Nota`: La lista de preguntas se inicializa directamente como un `ArrayList<>` para evitar problemas de
`NullPointerException`.

### 📂 Capa Repository

En este punto, solo necesitamos una interfaz que defina el contrato de acceso a los datos.

````java
public interface ExamRepository {
    List<Exam> findAll();
}
````

Más adelante, en las pruebas, `mockearemos` esta interfaz con `Mockito` para simular el acceso a la base de datos.

### 📂 Capa Service

La capa de servicio contendrá la lógica de negocio. Por ahora solo definimos un método para buscar un examen por nombre.

````java
public interface ExamService {
    Exam findExamByName(String name);
}
````

🔑 Resumen hasta aquí:
> Hemos creado la base de nuestro proyecto con una entidad (`Exam`), un repositorio (`ExamRepository`) y un servicio
> (`ExamService`). El siguiente paso será implementar estas interfaces y empezar a probarlas con Mockito 🧪.

## ⚙️ Implementando la capa Service

Para poder realizar nuestras pruebas con `Mockito`, necesitamos una implementación concreta tanto del `ExamRepository`
como del `ExamService`.

### 📂 Implementación del ExamRepository

El repositorio será nuestra fuente de datos simulada. En lugar de conectarnos a una base de datos real, devolveremos
una lista de exámenes `hardcodeada`.

````java
public class ExamRepositoryImpl implements ExamRepository {
    @Override
    public List<Exam> findAll() {
        return List.of(
                new Exam(1L, "Aritmética"),
                new Exam(2L, "Geometría"),
                new Exam(3L, "Álgebra"),
                new Exam(4L, "Trigonometría"),
                new Exam(5L, "Programación"),
                new Exam(6L, "Bases de Datos"),
                new Exam(7L, "Estructura de datos"),
                new Exam(8L, "Java 17")
        );
    }
}
````

📌 Nota:
> En un proyecto real, `ExamRepository` se conectaría a una base de datos mediante `JDBC`, `JPA`, `Hibernate`, etc.
> Aquí lo implementamos de forma estática para centrarnos en las pruebas con `Mockito`.

### 📂 Implementación del ExamService

El servicio se encarga de la lógica de negocio. En este caso, implementaremos el método `findExamByName(String name)`
que busca un examen dentro de la lista proporcionada por el repositorio.

````java
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;

    public ExamServiceImpl(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public Exam findExamByName(String name) {
        return this.examRepository.findAll().stream()
                .filter(exam -> exam.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No existe el examen " + name));
    }
}
````

🔑 Puntos importantes a destacar

- `Inyección de dependencias`: `ExamServiceImpl` recibe un `ExamRepository` en su constructor. Esto nos permitirá
  mockear el repositorio en las pruebas unitarias, aislando la lógica del servicio.
- `Uso de Streams en Java`: `findExamByName` filtra la lista de exámenes y devuelve el primero que coincida con el
  nombre. Si no encuentra ninguno, lanza una excepción `NoSuchElementException`.
- `Buena práctica para pruebas`: Al no depender de una BD real, nuestras pruebas serán rápidas y fáciles de ejecutar.

## 🧪 Realizando primeras pruebas con Mockito

Nuestro objetivo es probar el método `findExamByName(String name)` de la clase `ExamServiceImpl`. Para ello,
necesitamos crear su clase de test unitario.

Podemos hacerlo manualmente o (más rápido 🚀) desde `IntelliJ IDEA`:

1. Abrimos la clase `ExamServiceImpl`.
2. Presionamos `Ctrl` + `Shift` + `T` → `Create New Test....`
3. Aceptamos, y automáticamente tendremos creada la clase de prueba en el directorio `/test`

````java
package dev.magadiflo.mockito.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;

class ExamServiceImplTest {

}
````

### 🔍 Prueba inicial con JUnit + AssertJ (sin Mockito)

Antes de usar Mockito, probemos con `JUnit + AssertJ`.

> 📌 `Importante`: A partir de aquí usaremos `AssertJ` siempre que sea posible por su sintaxis más fluida. En los casos
> donde `AssertJ` no sea suficiente, combinaremos con el assert de `JUnit`.

````java
class ExamServiceImplTest {
    @Test
    void shouldReturnExamWithCorrectIdAndNameWhenSearchingByName() {
        ExamRepository examRepository = new ExamRepositoryImpl();
        ExamService examService = new ExamServiceImpl(examRepository);

        Exam exam = examService.findExamByName("Aritmética");

        assertThat(exam)
                .isNotNull()
                .extracting(Exam::getId, Exam::getName)
                .containsExactly(1L, "Aritmética");
    }
}
````

- ✅ Aquí usamos la implementación real del repositorio.
- ❌ El problema es que si queremos probar escenarios diferentes (ej: lista vacía), tendríamos que modificar la
  implementación real del repositorio, lo cual no es correcto en pruebas unitarias.

### 🎭 ¿Por qué usar Mockito?

- Necesitamos probar la clase `ExamServiceImpl` en aislamiento.
- Si dependemos de la implementación real de `ExamRepository`, nuestras pruebas dejan de ser unitarias.
- Con `Mockito` podemos simular (`mockear`) el comportamiento del repositorio:
    - Qué retorna `findAll()`.
    - Qué pasa cuando la lista está vacía, etc.

> Así mantenemos la prueba enfocada en el servicio y no en la implementación real del repositorio.

### 🛠️ Primer Test Unitario con Mockito

````java
public class ExamRepositoryImpl implements ExamRepository {
    @Override
    public List<Exam> findAll() {
        return List.of();
    }
}
````

Ahora sí, llegó el momento de crear nuestro **primer Test Unitario usando Mockito**:

````java

class ExamServiceImplTest {
    @Test
    void shouldReturnOptionalExamWithCorrectIdAndNameWhenRepositoryIsMocked() {
        // (1) Creamos el mock del repositorio
        ExamRepository examRepository = Mockito.mock(ExamRepository.class);
        ExamService examService = new ExamServiceImpl(examRepository);

        // Datos simulados
        List<Exam> exams = List.of(
                new Exam(1L, "Aritmética"),
                new Exam(2L, "Geometría"),
                new Exam(3L, "Álgebra"),
                new Exam(4L, "Trigonometría"),
                new Exam(5L, "Programación"),
                new Exam(6L, "Bases de Datos"),
                new Exam(7L, "Estructura de datos"),
                new Exam(8L, "Java 17")
        );

        // (2) Definimos el comportamiento del mock
        Mockito.when(examRepository.findAll()).thenReturn(exams); //(2)

        // Ejecutamos el método a probar
        Exam exam = examService.findExamByName("Aritmética");

        // Verificamos el resultado
        assertThat(exam)
                .isNotNull()
                .extracting(Exam::getId, Exam::getName)
                .containsExactly(1L, "Aritmética");
    }
}
````

Explicación:

- `(1)` → `Mockito.mock(ExamRepository.class)` crea una implementación simulada de `ExamRepository`.
- `(2)` → `when(...).thenReturn(...)` define el comportamiento esperado: cuando se invoque `findAll()`, devolverá
  nuestra lista predefinida.

📌 Nota:
> - `Mockito` solo `puede mockear` métodos `públicos` o `default`.
> - `No funciona` con métodos `privados`, `estáticos` o `finales`.

### 🔄 Mockeando con la implementación concreta

Incluso podemos mockear una clase concreta (`ExamRepositoryImpl`) en lugar de la interfaz.

````java
class ExamServiceImplTest {
    @Test
    void shouldReturnOptionalExamWithCorrectIdAndNameWhenRepositoryIsMocked() {
        ExamRepository examRepository = Mockito.mock(ExamRepositoryImpl.class);
    }
}
````

> 👉 Sin embargo, `es mejor práctica mockear interfaces`. Esto mantiene las pruebas más limpias y desacopladas de
> implementaciones específicas.

### ⚠️ Segundo Test: lista vacía en el repositorio

¿Qué pasa si el repositorio devuelve una lista vacía?

````java
class ExamenServiceImplTest {
    @Test
    void shouldThrowNoSuchElementExceptionWithCorrectMessageWhenExamIsNotFound() {
        ExamRepository examRepository = Mockito.mock(ExamRepository.class);
        ExamService examService = new ExamServiceImpl(examRepository);

        // Simulamos que el repositorio no tiene exámenes
        Mockito.when(examRepository.findAll()).thenReturn(List.of());

        // Verificamos que el servicio lanza la excepción esperada
        assertThatThrownBy(() -> examService.findExamByName("Aritmética"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No existe el examen Aritmética");

    }
}
````

- Aquí ya no necesitamos modificar `ExamRepositoryImpl`.
- Simplemente, decimos con `Mockito` qué debe pasar cuando `findAll()` devuelva una lista vacía.

### 🔑 Conclusión

- Las pruebas unitarias deben enfocarse en la clase bajo prueba (`SUT: System Under Test`).
- Con `Mockito` podemos aislar dependencias y simular distintos escenarios.
- Esto nos da:
    - Pruebas más rápidas ⚡
    - Código desacoplado 🧩
    - Escenarios flexibles 🎭
