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

## ♻️ Refactorizando nuestra clase ExamServiceImpl

Al analizar nuestros tests iniciales, notamos que hay código repetido en varios métodos, específicamente:

- La creación del mock del repositorio:
    ````java
    ExamRepository examRepository = Mockito.mock(ExamRepository.class); 
    ````

- La creación de la instancia del servicio que depende de ese repositorio:
    ````java
    ExamService examService = new ExamServiceImpl(examRepository);
    ````

Como recordamos de `JUnit 5`, podemos usar el ciclo de vida de pruebas con `@BeforeEach` para inicializar los objetos
necesarios antes de cada test.

Esto es ideal porque:

- Evitamos repetición de código.
- Mantenemos nuestros tests más claros y concisos.
- Reutilizamos las dependencias en todos los métodos de prueba.

### 📝 Refactor aplicado

````java
class ExamServiceImplTest {

    private ExamRepository examRepository;
    private ExamService examService;

    @BeforeEach
    void setUp() {
        this.examRepository = Mockito.mock(ExamRepository.class);
        this.examService = new ExamServiceImpl(this.examRepository);
    }

    @Test
    void shouldReturnOptionalExamWithCorrectIdAndNameWhenRepositoryIsMocked() {
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

        Mockito.when(this.examRepository.findAll()).thenReturn(exams);

        Exam exam = this.examService.findExamByName("Aritmética");

        assertThat(exam)
                .isNotNull()
                .extracting(Exam::getId, Exam::getName)
                .containsExactly(1L, "Aritmética");
    }

    @Test
    void shouldThrowNoSuchElementExceptionWithCorrectMessageWhenExamIsNotFound() {
        Mockito.when(this.examRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> this.examService.findExamByName("Aritmética"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No existe el examen Aritmética");

    }
}
````

🔎 Observaciones clave

- Uso de `@BeforeEach`: Se ejecuta antes de cada método de prueba, garantizando que los mocks y la instancia del
  servicio estén listos y limpios en cada ejecución.


- `Variables de clase`: Declaramos `examRepository` y `examService` como atributos privados globales en el test,
  de modo que cada método pueda reutilizarlos.


- `Ventaja adicional`: Si en el futuro el `ExamServiceImpl` tuviera más dependencias, solo tendríamos que configurarlas
  una vez dentro de `setUp()` en lugar de repetirlo en cada test.

## ➕ Agregando nuevas dependencias mock

Hasta ahora nuestro servicio `ExamServiceImpl` dependía únicamente de `ExamRepository`. Sin embargo, en un escenario
más realista, un examen debe estar asociado a un conjunto de preguntas. Para manejar esta nueva relación, crearemos un
nuevo repositorio y actualizaremos la lógica del servicio.

### 🗂️ 1. Creación del repositorio de preguntas

Definimos una nueva interfaz `QuestionRepository` con un método que permitirá obtener las preguntas de un examen a
partir de su identificador:

````java
public interface QuestionRepository {
    List<String> findQuestionByExamId(Long examId);
}
````

### 🗂️ 2. Extensión del contrato del servicio

Ampliamos la interfaz ExamService para que ahora, además de buscar un examen por nombre, podamos obtener también sus
preguntas:

````java
public interface ExamService {
    Exam findExamByName(String name);

    Exam findExamByNameWithQuestions(String name);
}
````

### 🗂️ 3. Implementación en ExamServiceImpl

En la clase de implementación debemos:

- Agregar una nueva dependencia: `QuestionRepository`.
- Modificar el constructor para inyectar esa dependencia.
- Implementar el nuevo método `findExamByNameWithQuestions(String name)`.

````java
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;

    public ExamServiceImpl(ExamRepository examRepository, QuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Exam findExamByName(String name) {
        return this.examRepository.findAll().stream()
                .filter(exam -> exam.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No existe el examen " + name));
    }

    @Override
    public Exam findExamByNameWithQuestions(String name) {
        Exam exam = this.findExamByName(name);
        List<String> questions = this.questionRepository.findQuestionByExamId(exam.getId());
        exam.setQuestions(questions);
        return exam;
    }
}
````

📌 Claves del nuevo método:

- Primero reutiliza `findExamByName()` para localizar el examen.
- Luego consulta el `QuestionRepository` para obtener sus preguntas.
- Finalmente, retorna el examen con la lista de preguntas asociadas.
- Si el examen no existe, lanza una `NoSuchElementException` como antes.

````java
class ExamServiceImplTest {

    private ExamRepository examRepository;
    private QuestionRepository questionRepository;
    private ExamService examService;

    @BeforeEach
    void setUp() {
        this.examRepository = Mockito.mock(ExamRepository.class);
        this.questionRepository = Mockito.mock(QuestionRepository.class);
        this.examService = new ExamServiceImpl(this.examRepository, this.questionRepository);
    }
    //...
}
````

### 🚀 Conclusión

Con este cambio hemos:

- Introducido un nuevo repositorio para manejar preguntas.
- Ampliado el contrato del servicio (`ExamService`).
- Adaptado la implementación para trabajar con múltiples dependencias.
- Preparado la clase de pruebas para simular ambas dependencias con `Mockito`.

Esto sienta las bases para los próximos tests, donde ya no solo validaremos la existencia del examen, sino también la
correcta asociación de preguntas.

## 🛠️ Refactorizando ExamServiceImplTest

En nuestra clase de pruebas `ExamServiceImplTest` notamos que repetimos constantemente la creación de listas de
exámenes y preguntas.

Esto viola el principio `DRY (Don't Repeat Yourself)`, ya que duplicamos código de preparación de datos y, en
consecuencia, dificultamos el mantenimiento.

### 💡 Solución: Patrón Test Fixtures

Un `Test Fixture` es un conjunto de datos predefinidos y reutilizables que facilita la escritura de pruebas limpias y
consistentes. La idea es centralizar la creación de datos de prueba en una sola clase para que todos los tests puedan
reutilizarlos.

Ubicación sugerida para la clase `ExamFixtures`:

````bash
src/test/java/dev/magadiflo/mockito/app/fixtures/ExamFixtures.java 
````

### 📝 Implementación de ExamFixtures

````java

public class ExamFixtures {
    public static List<Exam> getAllExams() {
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

    public static List<Exam> getEmptyExams() {
        return List.of();
    }

    public static List<Exam> getExamsWithNegativeIds() {
        return List.of(
                new Exam(-1L, "Aritmética"),
                new Exam(-2L, "Geometría"),
                new Exam(-3L, "Álgebra")
        );
    }

    public static List<Exam> getExamsWithNullIds() {
        return List.of(
                new Exam(null, "Aritmética"),
                new Exam(null, "Geometría"),
                new Exam(null, "Álgebra")
        );
    }

    public static Exam getValidExam() {
        return new Exam(9L, "Docker");
    }

    public static Exam getNewExam() {
        return new Exam(null, "Kubernetes");
    }

    public static Exam getDefaultExam() {
        return new Exam(1L, "Aritmética");
    }

    public static List<String> getQuestions() {
        return List.of(
                "Pregunta 1", "Pregunta 2", "Pregunta 3",
                "Pregunta 4", "Pregunta 5", "Pregunta 6",
                "Pregunta 7", "Pregunta 8", "Pregunta 9",
                "Pregunta 10"
        );
    }

    public static List<String> getFewQuestions() {
        return List.of(
                "Pregunta 1", "Pregunta 2", "Pregunta 3",
                "Pregunta 4", "Pregunta 5"
        );
    }

    public static List<String> getEmptyQuestions() {
        return List.of();
    }
}
````

### 🔒 Inmutabilidad de los datos

Las listas retornadas por `List.of()` son `inmutables`. Esto significa que cualquier intento de modificarlas lanzará
una excepción `UnsupportedOperationException`.

Este comportamiento es intencional:

- ✔️ Previene efectos secundarios entre pruebas.
- ✔️ Nos asegura que los datos se mantengan consistentes.

📌 Si en algún caso necesitamos una lista mutable, basta con crear una copia:

````java
List<Exam> mutableExams = new ArrayList<>(getAllExams());
````

### 🧪 Uso en los tests

Ahora, en lugar de duplicar la creación de listas en cada método de prueba, simplemente reutilizamos los `Fixtures`:

````java
class ExamenServiceImplTest {
    @Test
    void shouldReturnOptionalExamWithCorrectIdAndNameWhenRepositoryIsMocked() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());
        //...
    }

    @Test
    void shouldThrowNoSuchElementExceptionWithCorrectMessageWhenExamIsNotFound() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getEmptyExams());
        //...
    }
}
````

🚀 Beneficios de usar Fixtures

- ✅ Reutilización de datos → menos código repetido.
- ✅ Mantenimiento sencillo → si cambian los datos, se actualiza en un solo lugar.
- ✅ Claridad → los tests se enfocan en la lógica, no en la preparación de datos.
- ✅ Consistencia → todos los tests usan los mismos datos base.
