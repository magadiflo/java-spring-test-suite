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

## 🧪 Probando nuevas dependencias mock

En esta sección vamos a probar el método `findExamByNameWithQuestions(String name)`, que ahora depende de dos
repositorios:

- `ExamRepository` → Obtiene el examen por nombre.
- `QuestionRepository` → Busca las preguntas asociadas al examen.

### ✅ Caso 1: Encontrar examen con preguntas

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

    @Test
    void shouldReturnExamWithQuestionsWhenSearchingByName() {
        // (1) Stub del repositorio de exámenes
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());

        // (2) Stub del repositorio de preguntas (para cualquier ID)
        Mockito.when(this.questionRepository.findQuestionByExamId(Mockito.anyLong())).thenReturn(ExamFixtures.getQuestions());

        // (3) Ejecución del método a probar
        Exam exam = this.examService.findExamByNameWithQuestions("Geometría");

        // Verificación
        assertThat(exam.getQuestions())
                .hasSize(10)
                .contains("Pregunta 10");

    }
}
````

📌 Explicación paso a paso:

1. `(1) Stub del examRepository.findAll()` → simulamos que devuelve todos los exámenes (`ExamFixtures.getAllExams()`).
2. `(2) Stub del questionRepository.findQuestionByExamId(anyLong())` → simulamos que devuelve siempre una lista de 10
   preguntas (`ExamFixtures.getQuestions()`).
3. `(3) Ejecutamos el método real findExamByNameWithQuestions("Geometría")`
    - Internamente:
        - Busca el examen en el repositorio de exámenes.
        - Si existe, consulta sus preguntas en questionRepository.
        - Retorna un objeto Exam completo con preguntas.

### Diferencias entre Stub, Mock y Spy en Mockito

- `Stub`: Configurar la salida de un mock (qué debe devolver). Objeto que simula una respuesta predefinida.
- `Mock`: Objeto creado por Mockito. Lo usamos tanto para stubbing como para verificación.
- `Spy`: Es un objeto real "parcialmente espiado". Ejecuta el código verdadero pero podemos sobreescribir ciertos
  métodos.

### ¿Qué significa stubear?

`Stubear` viene de `stub`, que en inglés significa “suplente”, “pieza de relleno”. En pruebas unitarias, stubear un
método es:

> Configurar de antemano qué valor debe devolver un método de un mock cuando se le invoque con ciertos parámetros.

En `Mockito` se hace con construcciones como:

````bash
when(repo.findAll()).thenReturn(List.of(...));
when(service.doSomething("x")).thenThrow(new RuntimeException());
````

Aquí no nos interesa el comportamiento real, sino que el mock “supla” con una respuesta predefinida.

### ¿Por qué muchos dicen "mockear" en lugar de "stubear"?

Porque `Mockito` crea `mocks`, y sobre esos mocks nosotros:

1. `Stubbeamos` → configuramos qué debe devolver.
2. `Verificamos` → comprobamos que se llamaron con ciertos argumentos.

En la jerga diaria, la gente suele decir `“mockear el repo”` tanto para `crear` el mock como para `stubear` un método,
aunque no es lo más preciso.

✅ Definición resumida:
> - `Stubear` = decirle a un método de un mock qué debe devolver cuando se invoque.
> - `Mockear` = crear el objeto falso.
> - `Spy` = envolver un objeto real para espiar llamadas (y a veces stubear parcialmente).

### ❌ Caso 2: No se encuentra el examen

Ahora probemos el escenario en que no existen exámenes en el repositorio. En este caso, el método debe lanzar una
excepción `NoSuchElementException`.

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

    @Test
    void shouldFailToFindExamByNameAndThrowExceptionWhenRepositoryIsEmpty() {
        // Stub: repositorio de exámenes vacío
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getEmptyExams());

        // 🚨 No es necesario stubear questionRepository en este caso

        // Verificación de excepción
        assertThatThrownBy(() -> this.examService.findExamByNameWithQuestions("Aritmética"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No existe el examen Aritmética");
    }
}
````

💡 Observa que aquí no necesitamos stubear `questionRepository`. ¿Por qué? Porque la excepción ocurre antes de llegar a
la consulta de preguntas.

````bash
Mockito.when(this.questionRepository.findQuestionByExamId(Mockito.anyLong())).thenReturn(ExamFixtures.getQuestions());
````

🎯 Conclusiones

- Con múltiples mocks, podemos simular el comportamiento de diferentes repositorios de los que depende nuestro servicio.
- Solo se mockean las dependencias necesarias para el flujo que se está probando.
- Los `Fixtures (ExamFixtures)` reducen la duplicación de datos y hacen los tests más claros.

## Probando con verify de Mockito

`Mockito` no solo nos permite `stubear` (definir respuestas de métodos mockeados), sino también verificar si dichos
métodos fueron invocados y cuántas veces.

### Caso 1: Verificar llamadas exitosas

````java
class ExamServiceImplTest {
    @Test
    void shouldReturnExamWithQuestionsWhenSearchingByName() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());
        Mockito.when(this.questionRepository.findQuestionByExamId(Mockito.anyLong())).thenReturn(ExamFixtures.getQuestions());

        Exam exam = this.examService.findExamByNameWithQuestions("Geometría");

        assertThat(exam.getQuestions())
                .hasSize(10)
                .contains("Pregunta 10");

        // Verificaciones
        Mockito.verify(this.examRepository).findAll(); //(1)
        Mockito.verify(this.questionRepository).findQuestionByExamId(Mockito.anyLong()); //(2)
    }
}
````

- `(1)` Se verifica que `findAll()` fue invocado exactamente una vez (`valor por defecto`).
- `(2)` Se verifica que `findQuestionByExamId(anyLong())` también fue invocado exactamente una vez.

### Caso 2: Verificar que un método NO fue llamado

````java
class ExamServiceImplTest {
    @Test
    void shouldThrowExceptionAndSkipQuestionLookupWhenExamIsNotFound() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());

        assertThatThrownBy(() -> this.examService.findExamByNameWithQuestions("Lenguaje"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No existe el examen Lenguaje");

        // Verificaciones
        Mockito.verify(this.examRepository).findAll(); //(1)
        Mockito.verify(this.questionRepository, Mockito.never()).findQuestionByExamId(Mockito.anyLong()); //(2)
    }
}
````

- `(1)` Se verifica que `findAll()` fue invocado una vez.
- `(2)` Se verifica que `findQuestionByExamId(anyLong())` nunca fue invocado gracias a `Mockito.never()`.

## Inyección de dependencias con @Mock, @InjectMocks y @ExtendWith

Hasta ahora, nosotros mismos creábamos manualmente los mocks de los repositorios y los inyectábamos a la clase
bajo prueba en un `@BeforeEach`. Ejemplo:

````java
class ExamServiceImplTest {

    // Todos son interfaces
    private ExamRepository examRepository;
    private QuestionRepository questionRepository;
    private ExamService examService;

    @BeforeEach
    void setUp() {
        this.examRepository = Mockito.mock(ExamRepository.class);
        this.questionRepository = Mockito.mock(QuestionRepository.class);
        this.examService = new ExamServiceImpl(this.examRepository, this.questionRepository);
    }
}
````

Esto funciona, pero `Mockito` ofrece anotaciones que simplifican este proceso.

### Usando anotaciones de Mockito

Con `@Mock` y `@InjectMocks`, la configuración se vuelve mucho más limpia:

````java
class ExamServiceImplOpenMocksTest {
    @Mock
    private ExamRepository examRepository;          // Interfaz
    @Mock
    private QuestionRepository questionRepository;  // Interfaz
    @InjectMocks
    private ExamServiceImpl examService;            // Implementación Concreta
}
````

📌 Importante:

- `@Mock` crea un mock de la dependencia.
- `@InjectMocks` crea una instancia real de la clase y automáticamente le inyecta las dependencias anotadas con `@Mock`.
- Debemos usar la implementación concreta (`ExamServiceImpl`) y no la interfaz (`ExamService`) que implementa, ya que
  `Mockito` necesita saber en qué constructor inyectar los mocks.

### Habilitando las anotaciones de Mockito

Para que `@Mock` y `@InjectMocks` funcionen, hay que habilitar el soporte de anotaciones. Tenemos dos formas:

1. Con `MockitoAnnotations.openMocks(this)`

````java
class ExamServiceImplOpenMocksTest {
    @Mock
    private ExamRepository examRepository;          // Interfaz
    @Mock
    private QuestionRepository questionRepository;  // Interfaz
    @InjectMocks
    private ExamServiceImpl examService;            // Implementación Concreta

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);         // Habilita las anotaciones de mockito: @Mock, @InjectMocks
    }
}
````

2. Con `@ExtendWith(MockitoExtension.class)`

Otra forma más moderna y recomendada es usar la extensión de `JUnit 5`:

Agregamos la dependencia:

````xml

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.20.0</version>
    <scope>test</scope>
</dependency>
````

Y anotamos la clase de test:

````java

@ExtendWith(MockitoExtension.class) // Habilita las anotaciones de mockito: @Mock, @InjectMocks
class ExamServiceImplExtensionTest {
    @Mock
    private ExamRepository examRepository;          // Interfaz
    @Mock
    private QuestionRepository questionRepository;  // Interfaz
    @InjectMocks
    private ExamServiceImpl examService;            // Implementación concreta
}
````

### ✅ Conclusión

Con cualquiera de las dos formas (`openMocks` o `@ExtendWith`), eliminamos la necesidad de inicializar manualmente
los mocks y la inyección de dependencias se maneja automáticamente. Esto hace los tests más limpios, legibles y
fáciles de mantener.

## 📝 Realizando más pruebas con los repositorios

Hasta este punto, nuestros repositorios solo permitían lecturas (`findAll`, `findQuestionByExamId`). Ahora, simularemos
también operaciones de escritura (guardar datos).

### Nuevos métodos en las interfaces

````java
public interface ExamRepository {
    List<Exam> findAll();

    Exam saveExam(Exam exam);
}
````

````java
public interface QuestionRepository {
    List<String> findQuestionByExamId(Long examId);

    void saveQuestions(List<String> questions);
}
````

````java
public interface ExamService {
    Exam findExamByName(String name);

    Exam findExamByNameWithQuestions(String name);

    Exam saveExam(Exam exam);
}
````

### Implementación en ExamServiceImpl

````java
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;

    @Override
    public Exam saveExam(Exam exam) {
        List<String> questions = exam.getQuestions();
        if (!questions.isEmpty()) {
            this.questionRepository.saveQuestions(questions);
        }
        return this.examRepository.saveExam(exam);
    }
}

````

📌 Lógica aplicada:

1. Si el examen tiene preguntas, primero se persisten las preguntas.
2. Luego, se guarda el examen en el repositorio.

### Caso 1: Guardar un examen sin preguntas

````java

@ExtendWith(MockitoExtension.class)
class ExamServiceImplExtensionTest {
    @Test
    void shouldSaveExamWithoutQuestionsAndSkipQuestionPersistence() {
        Mockito.when(this.examRepository.saveExam(Mockito.any(Exam.class))).thenReturn(ExamFixtures.getValidExam());

        Exam exam = this.examService.saveExam(ExamFixtures.getValidExam());

        assertThat(exam)
                .isNotNull()
                .extracting(Exam::getId, Exam::getName, Exam::getQuestions)
                .containsExactly(9L, "Docker", ExamFixtures.getEmptyExams());
        Mockito.verify(this.examRepository).saveExam(Mockito.any(Exam.class));
        Mockito.verify(this.questionRepository, Mockito.never()).saveQuestions(Mockito.anyList());
    }
}
````

🔍 Explicación:

- Se mockea la respuesta de `saveExam`.
- Se verifica que sí se haya llamado a `saveExam`.
- Se verifica que nunca se haya llamado a `saveQuestions`, porque no hay preguntas.

### Caso 2: Guardar un examen con preguntas

````java

@ExtendWith(MockitoExtension.class)
class ExamServiceImplExtensionTest {
    @Test
    void shouldSaveExamWithQuestionsAndPersistBothExamAndQuestions() {
        Exam exam = ExamFixtures.getValidExam();
        exam.setQuestions(ExamFixtures.getQuestions());
        Mockito.when(this.examRepository.saveExam(Mockito.any(Exam.class))).thenReturn(exam);
        Mockito.doNothing().when(this.questionRepository).saveQuestions(Mockito.anyList());

        Exam examSaved = this.examService.saveExam(exam);

        assertThat(examSaved)
                .isNotNull()
                .extracting(Exam::getId, Exam::getName, Exam::getQuestions)
                .containsExactly(9L, "Docker", ExamFixtures.getQuestions());
        Mockito.verify(this.examRepository).saveExam(Mockito.any(Exam.class));
        Mockito.verify(this.questionRepository).saveQuestions(Mockito.anyList());
    }
}
````

🔍 Explicación:

- Se mockea `saveExam` para devolver el mismo examen.
- Como `saveQuestions` retorna `void`, se usa `doNothing()` para simular que no haga nada.
- Se verifica que ambos repositorios fueron invocados correctamente.

## 🔢 Test del id incremental en el método guardar usando Invocation Argument

Cuando guardamos un examen, este inicialmente no tiene id (es `null`). En un escenario real, al persistir en la base
de datos, esta le asigna un id autogenerado. Podemos simular ese comportamiento en nuestros tests con `Answer<T>`
de `Mockito`.

### 🧪 Ejemplo de test usando `when(...).then(new Answer<Exam>() {...})`

````java

@ExtendWith(MockitoExtension.class)
class ExamServiceImplExtensionTest {
    @Test
    void shouldAssignIdAndPersistExamWithQuestionsCorrectly() {
        Exam exam = ExamFixtures.getNewExam();  // Examen nuevo sin id
        exam.setQuestions(ExamFixtures.getQuestions());

        Mockito.when(this.examRepository.saveExam(Mockito.any(Exam.class))).then(new Answer<Exam>() {
            Long sequence = 8L;  // Secuencia inicial

            @Override
            public Exam answer(InvocationOnMock invocation) throws Throwable {
                Exam examToSave = invocation.getArgument(0);
                examToSave.setId(sequence++); // Simula autoincrement
                return examToSave;
            }
        });
        Mockito.doNothing().when(this.questionRepository).saveQuestions(Mockito.anyList());

        Exam savedExam = this.examService.saveExam(exam);

        assertThat(savedExam)
                .isNotNull()
                .extracting(Exam::getId, Exam::getName, Exam::getQuestions)
                .containsExactly(8L, "Kubernetes", ExamFixtures.getQuestions());
        Mockito.verify(this.examRepository).saveExam(Mockito.any(Exam.class));
        Mockito.verify(this.questionRepository).saveQuestions(Mockito.anyList());
    }
}
````

### 📌 ¿Qué hace el new `Answer<Exam>() {...}`?

1. Intercepta la llamada a `examRepository.saveExam(any(Exam.class))`.
2. Obtiene el argumento recibido: `Exam examToSave = invocation.getArgument(0)`.
3. Le asigna un Id secuencial.
4. Retorna el examen ya modificado → lo que simula que la base de datos devuelve el objeto con un id autogenerado.

Además de usar `Answer<T>`, también podríamos explorar el uso de `thenAnswer(...)` con expresiones lambda para
simplificar el código, sin necesidad de clases anónimas.

### 🧪 Ejemplo de test usando `when(...).thenAnswer(...)`

En `Mockito`, el método `thenAnswer(...)` es una forma más expresiva y moderna de definir comportamientos dinámicos en
un mock. A diferencia de `thenReturn(...)`, que siempre devuelve un valor fijo, con `thenAnswer(...)` podemos acceder
al `invocation` context (argumentos, mock invocado, etc.) y generar una respuesta en tiempo de ejecución.

Esto resulta útil cuando queremos simular escenarios más realistas, como la asignación de un ID incremental al guardar
una entidad o retornar valores diferentes según el argumento recibido.

En el siguiente ejemplo, usamos `thenAnswer(...)` para interceptar la llamada al repositorio y asignar un ID simulado
al examen antes de devolverlo, tal como lo haría una base de datos real:

````java

@ExtendWith(MockitoExtension.class)
class ExamServiceImplExtensionTest {
    @Test
    void shouldAssignIdAndPersistExamWithQuestionsUsingThenAnswer() {
        Exam exam = ExamFixtures.getNewExam();
        exam.setQuestions(ExamFixtures.getQuestions());

        Mockito.when(this.examRepository.saveExam(Mockito.any(Exam.class))).thenAnswer(invocation -> {
            Exam examToSave = invocation.getArgument(0);
            examToSave.setId(8L); // Simula autoincrement (puedes manejar secuencia si quieres)
            return examToSave;
        });
        Mockito.doNothing().when(this.questionRepository).saveQuestions(Mockito.anyList());

        Exam savedExam = this.examService.saveExam(exam);

        assertThat(savedExam)
                .isNotNull()
                .extracting(Exam::getId, Exam::getName, Exam::getQuestions)
                .containsExactly(8L, "Kubernetes", ExamFixtures.getQuestions());
        Mockito.verify(this.examRepository).saveExam(Mockito.any(Exam.class));
        Mockito.verify(this.questionRepository).saveQuestions(Mockito.anyList());
    }
}
````

## 🚨 Comprobaciones de excepciones usando `when(...).thenThrow(...)`

En este tipo de pruebas queremos validar que, bajo ciertas condiciones inválidas, nuestro servicio lance la excepción
esperada.

Para eso, Mockito nos permite configurar un mock para que arroje una excepción en lugar de devolver un valor normal.

````java

@ExtendWith(MockitoExtension.class)
class ExamServiceImplExtensionTest {
    @Test
    void shouldThrowIllegalArgumentExceptionWhenExamIdIsNullAndQuestionsAreRequested() {
        // (1) El repositorio devuelve exámenes con id = null
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getExamsWithNullIds());

        // (2) Si se llama al método con id = null, lanza IllegalArgumentException
        Mockito.when(this.questionRepository.findQuestionByExamId(Mockito.isNull())).thenThrow(IllegalArgumentException.class);

        // (3) Verificamos que al invocar el servicio, se lanza la excepción
        assertThatThrownBy(() -> this.examService.findExamByNameWithQuestions("Aritmética"))
                .isInstanceOf(IllegalArgumentException.class);

        // (4) Confirmamos que los mocks fueron invocados
        Mockito.verify(this.examRepository).findAll();
        Mockito.verify(this.questionRepository).findQuestionByExamId(Mockito.isNull());
    }
}
````

📌 Explicación paso a paso

1. `Simulamos la lista de exámenes`: `findAll()` devuelve exámenes con `id = null` → para provocar el error más
   adelante.
2. `Simulamos la excepción`: Configuramos que si se llama a `findQuestionByExamId(null)` → lance
   `IllegalArgumentException`.
3. `Validamos con AssertJ`: Usamos `assertThatThrownBy(...)` para comprobar que la excepción lanzada es la esperada.
4. `Verificaciones finales`: Con `verify(...)` nos aseguramos de que los mocks fueron efectivamente utilizados.

## 🎯 Argument Matchers en Mockito: argThat() vs eq()

Los `Argument Matchers` permiten verificar no solo que un método se llamó, sino también con qué argumentos exactos fue
invocado. Esto hace que las pruebas sean más flexibles y expresivas.

````java

@ExtendWith(MockitoExtension.class)
class ExamServiceImplExtensionTest {
    @Test
    void shouldVerifyCorrectExamIdIsUsedWhenFetchingQuestions() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());
        Mockito.when(this.questionRepository.findQuestionByExamId(Mockito.anyLong())).thenReturn(ExamFixtures.getQuestions());

        this.examService.findExamByNameWithQuestions("Aritmética");

        Mockito.verify(this.examRepository).findAll();

        // (1) Usando argThat con lógica personalizada
        Mockito.verify(this.questionRepository).findQuestionByExamId(Mockito.argThat(arg -> arg != null && arg.equals(1L)));

        // (2) Usando eq() para igualdad exacta
        Mockito.verify(this.questionRepository).findQuestionByExamId(Mockito.eq(1L));
    }
}
````

📌 Diferencias clave

| Matcher              | Uso típico                                                        | Ventaja                                             |
|----------------------|-------------------------------------------------------------------|-----------------------------------------------------|
| `eq(value)`          | Verificar un valor **exacto** (`eq(1L)`)                          | Más simple, claro, ideal para casos directos        |
| `argThat(predicate)` | Verificar con **expresiones lógicas** (`argThat(arg -> arg > 0)`) | Permite lógica compleja, condiciones personalizadas |

✅ Buenas prácticas

- Usa `eq()` cuando solo quieras comparar valores exactos → más legible.
- Usa `argThat()` cuando necesites condiciones adicionales (ej. no nulo, mayor que cero, empieza con cierto texto,
  etc.).
- Evita mezclar matchers y valores reales en una misma invocación, porque `Mockito` podría quejarse
  Ej.: `verify(repo).saveExam(eq(exam), true)` → aquí ambos deben ser matchers, es decir en realidad debería ser así
  `verify(repo).saveExam(eq(exam), eq(true))`, ambos parámetros son `Argument Matchers` y `Mockito` ya no protesta.

## Argument Matchers personalizados con clases

Hasta ahora hemos usado `Argument Matchers` provistos por `Mockito` (`any()`, `eq()`, `isNull()`, `argThat(...)` con
expresiones lambda, etc.). Sin embargo, en ocasiones necesitamos encapsular validaciones más específicas o generar
mensajes de error más claros cuando las verificaciones fallen. Para eso podemos crear nuestros propios matchers.

### Creando un ArgumentMatcher personalizado

Podemos implementar la interfaz `ArgumentMatcher<T>` para definir nuestra lógica de validación. En este ejemplo,
verificamos que el ID de un examen sea un número positivo y no nulo:

````java
public class ValidExamIdMatcher implements ArgumentMatcher<Long> {
    private Long examId;

    @Override
    public boolean matches(Long examId) {
        this.examId = examId;
        return this.examId != null && this.examId > 0;
    }

    @Override
    public String toString() {
        return String.format("El id del examen enviado fue %d, se esperaba que fuera un entero positivo", this.examId);
    }
}
````

Detalles importantes:

- `matches(...)`: Define la lógica para aceptar o rechazar el argumento.
- `toString()`: Sobrescribir este método es muy útil, ya que si la verificación falla, `Mockito` mostrará este mensaje,
  haciendo más fácil identificar el error.
- Tipo genérico (`Long`): Debe coincidir con el tipo del argumento del método a verificar. En nuestro caso,
  `findQuestionByExamId(Long examId)`.

### Uso en un test

Ahora aplicamos nuestro `ValidExamIdMatcher` en un test unitario:

````java

@ExtendWith(MockitoExtension.class)
class ExamServiceImplExtensionTest {
    @Test
    void shouldVerifyCorrectExamIdIsUsedWhenFetchingQuestions_ArgumentMatcher() {
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getExamsWithNegativeIds());
        Mockito.when(this.questionRepository.findQuestionByExamId(Mockito.anyLong())).thenReturn(ExamFixtures.getQuestions());

        this.examService.findExamByNameWithQuestions("Aritmética");

        Mockito.verify(this.examRepository).findAll();
        Mockito.verify(this.questionRepository).findQuestionByExamId(Mockito.argThat(new ValidExamIdMatcher()));
    }
}
````

### Ventajas de un matcher personalizado

1. Reutilización: Podemos usar `ValidExamIdMatcher` en múltiples tests sin repetir lógica.
2. Mensajes de error claros: Gracias al `toString()` sobreescrito, los fallos serán más descriptivos.
3. Mayor expresividad: El test se vuelve más legible, ya que el matcher describe la intención del chequeo.

## Capturando argumentos con `ArgumentCaptor`

En `Mockito`, los `ArgumentMatchers` (`any()`, `eq()`, `argThat()`, etc.) sirven para comprobar si un método se
llamó con determinados valores. Sin embargo, en ocasiones necesitamos obtener el valor real del argumento pasado al
método para realizar más afirmaciones (`assertions`) sobre él.

Para eso usamos `ArgumentCaptor`.

### Ejemplo práctico

Queremos asegurarnos de que, al buscar el examen `Aritmética`, el repositorio de preguntas haya sido invocado con el
`id = 1`.

````java

@ExtendWith(MockitoExtension.class)
class ExamServiceImplExtensionTest {
    @Test
    void shouldCaptureExamIdUsedToFetchQuestionsWithArgumentCaptor() {
        // (1) Stub: devolvemos lista de exámenes
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());

        // (2) Creamos captor para Long
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        // (3) Ejecutamos el método bajo prueba
        this.examService.findExamByNameWithQuestions("Aritmética");

        // (4) Capturamos el argumento real usado en la llamada
        Mockito.verify(this.questionRepository).findQuestionByExamId(captor.capture());

        // (5) Assertions sobre el valor capturado
        assertThat(captor.getValue()).isEqualTo(1L);
    }
}
````

Explicación paso a paso

1. `Stub del repositorio de exámenes`: aseguramos que `findAll()` devuelva nuestra lista conocida
   (`ExamFixtures.getAllExams()`).
2. `Definimos un captor` para el tipo de dato que queremos capturar (`Long`).
3. `Ejecutamos el método bajo prueba`, que internamente usará el repositorio de preguntas.
4. `Verificación con captura`: al verificar la llamada a `findQuestionByExamId(..)`, capturamos el valor del argumento
   real.
4. `Afirmación sobre el valor capturado`: comprobamos que realmente se llamó con `1L`.

📌 Conclusión
> `ArgumentCaptor` es especialmente útil cuando queremos validar valores dinámicos o estructuras complejas que no
> podemos cubrir fácilmente con `eq()` o `argThat()`. Es la herramienta recomendada cuando necesitamos inspeccionar
> argumentos después de la verificación.

### 1. Lo que estamos stubbeando

Aquí le estamos diciendo a `examRepository.findAll()` que devuelva una lista fija de exámenes
(`ExamFixtures.getAllExams()`), para que nuestro servicio pueda encontrar `Aritmética` y obtener el `id = 1L`.

````bash
Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());
````

### 2. Lo que NO estamos stubbeando

````bash
this.questionRepository.findQuestionByExamId(exam.getId());
````

Nuestro método de test anterior no tiene un `when(...).thenReturn(...)` para el
`this.questionRepository.findQuestionByExamId(...)`, entonces `¿Qué hace Mockito en este caso?`.
Cuando un método de un mock no está stubeado, `Mockito` devuelve un valor por defecto para ese tipo de dato:

- Para `primitivos numéricos` → `0`
- Para `boolean` → `false`
- Para `objetos` → `null`
- Para `listas` o `colecciones` → `Collections.emptyList()` (o sea, una lista vacía, no null).

### 3. Por qué el test igual funciona

En nuestro test no nos importa el resultado del método `findQuestionByExamId(...)`. El test se centra en capturar el
argumento con `ArgumentCaptor` y hacer un `assertThat(captor.getValue()).isEqualTo(1L)`.

Eso quiere decir que aunque `questionRepository.findQuestionByExamId(...)` devuelva una lista vacía, no afecta nada
porque nunca estamos verificando las preguntas devueltas, solo verificamos que `el argumento usado en la invocación` sea
correcto.

### 4. Si lo hubieramos necesitado

Si en nuestro test quisieramos también verificar que las preguntas se agregaron al examen (por ejemplo,
`assertThat(savedExam.getQuestions()).containsExactly(...)`), entonces sí necesitaríamos stubear:

````bash
Mockito.when(this.questionRepository.findQuestionByExamId(1L)).thenReturn(ExamFixtures.getQuestions());
````

✅ Conclusión:
> Funciona porque Mockito devuelve una lista vacía por defecto para el mock
> `questionRepository.findQuestionByExamId(...)`, y nuestro test no necesita usar ese retorno, solo comprobar que el
> método se llamó con el id correcto. El `ArgumentCaptor` no depende del valor retornado, solo de la invocación misma.

## 🎯 Argument Capture con anotación `@Captor`

En la lección anterior vimos cómo crear manualmente un `ArgumentCaptor` dentro del propio método de test.
Ahora, para simplificar el código y hacerlo más legible, podemos apoyarnos en la anotación `@Captor`, que nos permite
inyectar directamente un `ArgumentCaptor` en la clase de prueba.

📌 Ventaja principal:

> Ya no necesitas declarar el captor dentro del test con `ArgumentCaptor.forClass(...)`, sino que lo defines una sola
> vez como atributo y `Mockito` lo inicializa por ti.

### 📝 Ejemplo de uso con @Captor

````java

@ExtendWith(MockitoExtension.class)
class ExamServiceImplExtensionTest {

    @Mock
    private ExamRepository examRepository;
    @Mock
    private QuestionRepository questionRepository;
    @InjectMocks
    private ExamServiceImpl examService;

    @Captor
    private ArgumentCaptor<Long> examIdCaptor; //Se inicializa automáticamente

    @Test
    void shouldCaptureCorrectExamIdWhenFetchingQuestionsByName() {
        // Given
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());

        // When
        this.examService.findExamByNameWithQuestions("Aritmética");

        // Then
        Mockito.verify(this.questionRepository).findQuestionByExamId(this.examIdCaptor.capture());
        assertThat(this.examIdCaptor.getValue()).isEqualTo(1L);
    }
}
````

🔍 Explicación paso a paso

1. `@Captor`. Declara el `ArgumentCaptor` como atributo de la clase de prueba. Mockito lo inyecta al iniciar el contexto
   de prueba.
2. `Stub del repositorio`. Con `when(this.examRepository.findAll()).thenReturn(...)` simulamos que existen exámenes en
   la base de datos.
3. `Ejecución real del servicio`. Se invoca `findExamByNameWithQuestions("Aritmética")`, que internamente obtiene el
   examen y luego llama al repositorio de preguntas.
4. `Captura del argumento`. Con `examIdCaptor.capture()` interceptamos el valor real usado en la invocación
   `findQuestionByExamId(...)`.
5. `Assertion`. Verificamos que el id capturado (`captor.getValue()`) coincide con el esperado: `1L`.

💡 Diferencia con la versión manual

- `Manual`: se declara dentro del test `ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);`
- `Con anotación`: se declara una sola vez como atributo con `@Captor` y `Mockito` lo gestiona.

Ambas formas son válidas, pero `@Captor` hace el test más limpio y elimina repetición de código.

## 💥 `doThrow` → Lanzando excepciones en métodos void

Cuando usamos `Mockito` para stubear métodos que devuelven un valor, lo normal es apoyarnos en la sintaxis:

````bash
Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());
````

Pero... 🤔 `¿qué ocurre si el método que queremos simular es un void?`. En ese caso no podemos usar
`when(...).thenReturn(...)`, porque no hay un valor que retornar.

👉 Para esos escenarios entran en juego los métodos de la familia `do..()` (`doThrow`, `doNothing`, `doAnswer`, etc.).

### 📝 Ejemplo práctico con doThrow

````java

@ExtendWith(MockitoExtension.class)
class ExamServiceImplExtensionTest {

    @Test
    void shouldThrowExceptionWhenSavingQuestionsFailsDuringExamPersistence() {
        Exam exam = ExamFixtures.getNewExam();
        exam.setQuestions(ExamFixtures.getQuestions());

        Mockito.doThrow(IllegalArgumentException.class)
                .when(this.questionRepository).saveQuestions(Mockito.anyList());

        assertThatThrownBy(() -> this.examService.saveExam(exam))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
````

🔍 Explicación paso a paso

1. `Stub del método void`. Usamos `doThrow(...)` para indicar que, al invocar
   `questionRepository.saveQuestions(anyList())` se lanzará una excepción `IllegalArgumentException`.
2. `Ejecución del servicio`. Al llamar a `this.examService.saveExam(exam)`, internamente se intenta guardar el examen
   y también sus preguntas → lo que dispara la excepción configurada.
3. `Afirmación`. Con `assertThatThrownBy(...)` verificamos que efectivamente la excepción lanzada sea del tipo esperado.

## 🎭 Uso de `doAnswer` en Mockito

Hasta ahora ya habíamos usado la interfaz `Answer` de manera explícita, por ejemplo para asignar un id incremental a un
examen cuando era guardado. Sin embargo, `Mockito` también nos ofrece una forma más concisa y declarativa para el mismo
propósito: `doAnswer(...)`.

Básicamente, `doAnswer()` nos permite interceptar la llamada a un método de un mock, acceder a sus argumentos mediante
el invocation y luego devolver un resultado calculado dinámicamente.

### 📌 Ejemplo 1: Simular el guardado de un examen con ID asignado

````java

@ExtendWith(MockitoExtension.class)
class ExamServiceImplExtensionTest {
    @Test
    void shouldAssignIdAndPersistExamWithQuestionsUsingDoAnswer() {
        // given
        Exam exam = ExamFixtures.getNewExam();
        exam.setQuestions(ExamFixtures.getQuestions());

        Mockito.doAnswer(invocation -> {
            Exam examToSave = invocation.getArgument(0); // argumento en posición 0
            examToSave.setId(10L); // simulamos que la BD le asigna un ID
            return examToSave;
        }).when(this.examRepository).saveExam(Mockito.any(Exam.class));

        Mockito.doNothing().when(this.questionRepository).saveQuestions(Mockito.anyList());

        // when
        Exam savedExam = this.examService.saveExam(exam);

        // then
        assertThat(savedExam)
                .isNotNull()
                .extracting(Exam::getId, Exam::getName, Exam::getQuestions)
                .containsExactly(10L, "Kubernetes", ExamFixtures.getQuestions());
        Mockito.verify(this.examRepository).saveExam(Mockito.any(Exam.class));
        Mockito.verify(this.questionRepository).saveQuestions(Mockito.anyList());
    }
}
````

✅ Aquí el `doAnswer` actúa como un “simulador de BD”, asignando un ID al examen antes de devolverlo.

### 📌 Ejemplo 2: Retornar valores diferentes según el argumento recibido

A veces queremos que el mock devuelva respuestas distintas dependiendo del input. Para eso también podemos usar
`doAnswer()`:

````java

@ExtendWith(MockitoExtension.class)
class ExamServiceImplExtensionTest {
    @Test
    void shouldReturnExamWithFewQuestionsWhenIdMatchesConditionInDoAnswer() {
        // given
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());

        Mockito.doAnswer(invocation -> {
            Long examId = invocation.getArgument(0); // capturamos el id del examen
            return examId == 5L
                    ? ExamFixtures.getFewQuestions() // si es id = 5
                    : ExamFixtures.getEmptyExams();  // en cualquier otro caso
        }).when(this.questionRepository).findQuestionByExamId(Mockito.anyLong());

        // when
        Exam exam = this.examService.findExamByNameWithQuestions("Programación");

        // then
        assertThat(exam)
                .hasFieldOrPropertyWithValue("id", 5L)
                .hasFieldOrPropertyWithValue("name", "Programación")
                .hasFieldOrPropertyWithValue("questions", ExamFixtures.getFewQuestions());
    }
}
````

✨ Conceptos clave

- `doAnswer()` → se usa en lugar de `when(...).then(new Answer<Exam>() {...})` cuando necesitamos lógica más flexible
  para decidir qué devolver.
- `Invocation.getArgument(index)` → nos permite capturar los argumentos pasados al método del mock.
- Útil para casos dinámicos, donde el resultado depende del argumento recibido.

## ⚡ `doCallRealMethod`: ejecutar el método real en un mock

En la mayoría de los casos, cuando usamos `Mockito` creamos mocks de interfaces o clases abstractas y definimos su
comportamiento con `when(...).thenReturn(...)`, `doThrow(...)`, etc. Pero, ¿qué pasa si queremos
`que un mock ejecute su implementación real de un método` en lugar de un comportamiento simulado? 🤔

Para eso tenemos `doCallRealMethod()`, el cual permite invocar directamente el método real de un `mock (no el stub)`.
Eso sí, necesitamos que el mock se cree a partir de una clase concreta, ya que las interfaces y clases abstractas no
tienen implementación real.

### 🛠️ Paso 1: crear una implementación concreta

Creamos una implementación concreta de `QuestionRepository` que tenga un método real para probar:

````java
public class QuestionRepositoryImpl implements QuestionRepository {
    @Override
    public List<String> findQuestionByExamId(Long examId) {
        return List.of(
                "Pregunta 1 (real)",
                "Pregunta 2 (real)",
                "Pregunta 3 (real)",
                "Pregunta 4 (real)",
                "Pregunta 5 (real)"
        );
    }

    @Override
    public void saveQuestions(List<String> questions) {
        // No implementado porque no lo usamos en este test
    }
}
````

### 🛠️ Paso 2: usar doCallRealMethod en el test

En este caso, dejamos `ExamRepository` como un mock normal (`interfaz`), pero `QuestionRepository` lo cambiamos a la
implementación concreta para poder usar el método real.

````java

@ExtendWith(MockitoExtension.class)
class ExamServiceImplDoCallRealMethodTest {

    @Mock
    private ExamRepository examRepository;              // Interfaz (simulada)
    @Mock
    private QuestionRepositoryImpl questionRepository;  // Clase concreta (para usar doCallRealMethod)
    @InjectMocks
    private ExamServiceImpl examService;                // Clase concreta bajo prueba

    @Test
    void shouldInvokeRealMethodToFetchQuestionsAndReturnExpectedExam() {
        // given
        Mockito.when(this.examRepository.findAll()).thenReturn(ExamFixtures.getAllExams());
        Mockito.doCallRealMethod().when(this.questionRepository).findQuestionByExamId(Mockito.anyLong());

        // when
        Exam exam = this.examService.findExamByNameWithQuestions("Aritmética");

        // then
        assertThat(exam)
                .extracting(Exam::getId, Exam::getName)
                .containsExactly(1L, "Aritmética");
        assertThat(exam.getQuestions())
                .hasSize(5)
                .contains("Pregunta 1 (real)", "Pregunta 4 (real)", "Pregunta 5 (real)");
    }
}
````

🔎 Puntos clave

- `ExamRepository` se mantiene como mock puro porque solo lo necesitamos stubear (`when(...).thenReturn(...)`).
- `QuestionRepositoryImpl` es una implementación concreta, lo cual permite que `doCallRealMethod()` invoque realmente
  su método `findQuestionByExamId(...)`.
- Con esto logramos un mix entre mocks y lógica real, lo cual es muy útil cuando quieres probar parcialmente
  comportamientos sin renunciar a las ventajas de Mockito.

## 🕵️ Implementando espías con Spy - Llamadas reales

Un `Spy` es similar a un `Mock`, pero con una diferencia clave:
> Puede comportarse como un objeto real, permitiendo invocar las implementaciones originales de sus métodos.
> Al igual que el `Mock`, también nos da la opción de simular el comportamiento de cualquier método, pero con mayor
> flexibilidad.

La diferencia principal es que con `Mock` es obligatorio simular los métodos, lo que lo hace ideal para enfoques de
desarrollo como `TDD (Test Driven Development)`, donde aún no existe una implementación funcional. En cambio,
el `Spy` permite invocar métodos reales ya implementados, lo que resulta útil cuando queremos mantener la coherencia
entre lo ya desarrollado y la nueva funcionalidad que estamos testeando.

Este enfoque es especialmente valioso en escenarios donde:

- Ya existen métodos funcionales que queremos reutilizar en nuestras pruebas.
- Buscamos validar la integración parcial sin romper la lógica existente.
- Queremos combinar simulación y comportamiento real en un mismo objeto.

En el siguiente ejemplo, usaremos `spy()` para obtener la implementación real de los métodos. Para ello,
es necesario que los métodos `findAll()` de la clase `ExamRepositoryImpl` y `findQuestionsByExamId()`
de la clase `QuestionRepositoryImpl` estén previamente implementados y retornen datos "reales", ya que serán
invocados directamente durante las pruebas.

### 🛠️ Paso 1: Implementaciones concretas

Necesitamos implementaciones reales para que `spy()` pueda invocar sus métodos.

````java
public class ExamRepositoryImpl implements ExamRepository {
    @Override
    public List<Exam> findAll() {
        return List.of(
                new Exam(1L, "R_Aritmética"),
                new Exam(2L, "R_Geometría"),
                new Exam(3L, "R_Álgebra"),
                new Exam(4L, "R_Trigonometría"),
                new Exam(5L, "R_Programación"),
                new Exam(6L, "R_Bases de Datos"),
                new Exam(7L, "R_Estructura de datos"),
                new Exam(8L, "R_Java 17")
        );
    }

    @Override
    public Exam saveExam(Exam exam) {
        return null;
    }
}
````

````java
public class QuestionRepositoryImpl implements QuestionRepository {
    @Override
    public List<String> findQuestionByExamId(Long examId) {
        return List.of(
                "Pregunta 1 (real)",
                "Pregunta 2 (real)",
                "Pregunta 3 (real)",
                "Pregunta 4 (real)",
                "Pregunta 5 (real)"
        );
    }

    @Override
    public void saveQuestions(List<String> questions) {
        // No implementado porque no lo usamos en los test del doCallRealMethod
    }
}
````

### 🛠️ Paso 2: Crear Spies en el test

Ahora creamos `spies` de los repositorios y verificamos que efectivamente se llaman sus métodos reales.

````java
class ExamServiceImplSpyTest {
    @Test
    void shouldReturnRealExamWithQuestionsUsingSpiedRepositories() {
        // given: spies sobre implementaciones concretas
        ExamRepository examRepository = Mockito.spy(ExamRepositoryImpl.class);
        QuestionRepository questionRepository = Mockito.spy(QuestionRepositoryImpl.class);
        ExamServiceImpl examService = new ExamServiceImpl(examRepository, questionRepository);

        // when
        Exam exam = examService.findExamByNameWithQuestions("R_Aritmética");

        // then
        assertThat(exam)
                .extracting(Exam::getId, Exam::getName)
                .containsExactly(1L, "R_Aritmética");
        assertThat(exam.getQuestions())
                .isNotEmpty()
                .hasSize(5)
                .contains("Pregunta 3 (real)", "Pregunta 5 (real)");
    }
}
````

Con este enfoque, `ExamServiceImpl` está trabajando con lógica real de repositorios, pero seguimos teniendo el poder de
verificar invocaciones o incluso sobreescribir métodos específicos si lo necesitamos.
