# [🚀 Introducción a Testcontainers en un proyecto Java Spring Boot](https://testcontainers.com/guides/testing-spring-boot-rest-api-using-testcontainers/)

Basado en el tutorial oficial de `Testcontainers`, con adaptaciones y ampliaciones realizadas por mí para
contextualizarlo dentro de proyectos empresariales y entornos reales de desarrollo.

---

### 🌱 Objetivo general

En esta guía implementaremos un proyecto `Spring Boot` que utiliza `Spring Data JPA` y una base de datos `PostgreSQL`
para exponer endpoints `API REST`.

La meta será comprender cómo integrar `Testcontainers` para levantar un contenedor `Docker` de `PostgreSQL` y realizar
pruebas de integración reales en un entorno completamente aislado.

# 🏗️ Fase 1 — Proyecto base

Construiremos rápidamente la base del proyecto: entidades, repositorios y controladores. Esta fase corresponde al modo
de desarrollo (`dev`), por lo que las pruebas se ejecutarán usando el perfil de desarrollo.

Aquí también podríamos incluir perfiles adicionales (`qa`, `prod`), pero lo principal será tener un proyecto funcional
que servirá de soporte para la siguiente fase.

## ⚙️ Dependencias iniciales

Creamos el proyecto
desde [Spring Initializr](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.7&packaging=jar&jvmVersion=21&groupId=dev.magadiflo&artifactId=spring-testcontainers&name=spring-testcontainers&description=Demo%20project%20for%20Spring%20Boot&packageName=dev.magadiflo.testcontainers.app&dependencies=web,data-jpa,postgresql,lombok)
con las siguientes dependencias iniciales:

````xml
<!--Spring Boot 3.5.7-->
<!--Java 21-->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
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
</dependencies>
````

#### 💬 Nota

> En esta fase `aún no incorporamos Testcontainers`.
>
> El propósito es preparar una estructura sólida y un entorno funcional (`dev`) para luego introducir la
> infraestructura de contenedores en la siguiente fase (`test`).

## 🧩 Creando la entidad JPA Customer

Comenzaremos definiendo una entidad JPA llamada `Customer`, que representará a nuestros clientes en la base de datos.

````java

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;
}
````

## 🗂️ Creando el repositorio Spring Data JPA

Definimos la interfaz `CustomerRepository`, que extiende de `JpaRepository` para heredar los métodos CRUD básicos y
aprovechar el poder de `Spring Data JPA`.

````java
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
}
````

Definimos un método derivado de consulta (`Query Method`) para poder tener más casos con los que elaborar nuestros test.
`Spring Data JPA` genera automáticamente la query en tiempo de ejecución basándose en el nombre del método
(`findByEmail`).

## ⚙️ Creando el Servicio CustomerService

En esta sección definimos la capa de servicio, que encapsula la lógica de negocio y actúa como intermediaria entre el
`controlador REST` y el `repositorio JPA`.

⚠️ Como mencionamos, nuestro objetivo no es profundizar en la arquitectura de la aplicación, sino construir una base
funcional sobre la cual probaremos `Testcontainers` en la siguiente fase.

Por `simplicidad` en este ejemplo, se utilizarán directamente las entidades JPA en los endpoints en lugar de
Data Transfer Objects (DTOs). No obstante, es importante recalcar que, en un proyecto de producción real,
esto se considera una mala práctica; `siempre se recomienda usar DTOs` para evitar exponer las entidades de dominio.

````java
public interface CustomerService {
    List<Customer> findAllCustomers();

    Customer findCustomerById(Long id);

    Customer findCustomerByEmail(String email);

    Customer saveCustomer(Customer customer);

    Customer updateCustomer(Long id, Customer customer);

    void deleteCustomerById(Long id);
}
````

Esta interfaz define los métodos que luego serán implementados por la clase de servicio principal.
Permite separar el contrato de negocio de su implementación concreta, facilitando inyección de dependencias,
pruebas unitarias y mocking.

````java

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public List<Customer> findAllCustomers() {
        return this.customerRepository.findAll();
    }

    @Override
    public Customer findCustomerById(Long id) {
        return this.customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente con id " + id + " no encontrado"));
    }

    @Override
    public Customer findCustomerByEmail(String email) {
        return this.customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Cliente con email " + email + " no encontrado"));
    }

    @Override
    @Transactional
    public Customer saveCustomer(Customer customer) {
        return this.customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, Customer customer) {
        return this.customerRepository.findById(id)
                .map(customerDB -> {
                    customerDB.setName(customer.getName());
                    customerDB.setEmail(customer.getEmail());
                    return customerDB;
                })
                .map(this.customerRepository::save)
                .orElseThrow(() -> new NoSuchElementException("Cliente con id " + id + " no encontrado"));
    }

    @Override
    @Transactional
    public void deleteCustomerById(Long id) {
        Customer customer = this.customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente con id " + id + " no encontrado"));
        this.customerRepository.deleteById(customer.getId());
    }
}
````

## 🌐 Creando los Endpoints API REST

Para finalizar esta primera fase, creamos el controlador REST encargado de exponer los endpoints HTTP que permitirán
interactuar con el servicio `CustomerService.`

Este controlador representa el punto de entrada de nuestra API y utiliza los métodos definidos en la capa de servicio
para procesar las solicitudes CRUD.

````java

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(this.customerService.findAllCustomers());
    }

    @GetMapping(path = "/{customerId}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(this.customerService.findCustomerById(customerId));
    }

    @GetMapping(path = "/email/{email}")
    public ResponseEntity<Customer> getCustomer(@PathVariable String email) {
        return ResponseEntity.ok(this.customerService.findCustomerByEmail(email));
    }

    @PostMapping
    public ResponseEntity<Customer> saveCustomer(@RequestBody Customer customer) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.customerService.saveCustomer(customer));
    }

    @PutMapping(path = "/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long customerId, @RequestBody Customer customer) {
        return ResponseEntity.ok(this.customerService.updateCustomer(customerId, customer));
    }

    @DeleteMapping(path = "/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        this.customerService.deleteCustomerById(customerId);
        return ResponseEntity.noContent().build();
    }
}
````

## 🐘 Creación de la base de datos con Docker Compose (perfil `dev`)

En esta fase configuraremos una base de datos `PostgreSQL` que servirá únicamente para el entorno de desarrollo (`dev`).

Aunque podríamos usar una base de datos instalada localmente o una remota, en este caso preferimos utilizar `Docker`
para mantener el entorno aislado, reproducible y consistente con buenas prácticas de desarrollo modernas.

💡 `Importante`: Aún no estamos utilizando `Testcontainers`.
> En esta etapa, usamos `Docker` de forma manual para levantar un contenedor de `PostgreSQL` persistente para
> desarrollo.
>
> En la siguiente fase, cuando entremos a la parte de tests, emplearemos `Testcontainers`, que también usa `Docker`,
> pero de forma efímera y automatizada, ideal para entornos de prueba.

### 📄 Archivo `compose.yml`

Definimos un servicio `s-postgres` que crea un contenedor de `PostgreSQL` para el entorno `dev`.

````yml
services:
  s-postgres:
    image: postgres:17-alpine
    container_name: c-postgres
    restart: unless-stopped
    ports:
      - '5432:5432'
    environment:
      POSTGRES_DB: db_spring_testcontainers_dev
      POSTGRES_USER: dev_user
      POSTGRES_PASSWORD: dev_password
    networks:
      - docker-test-net
````

Ejecutamos el siguiente comando para crear y levantar el contenedor de `PostgreSQL`:

````bash
D:\programming\spring\01.udemy\02.andres_guzman\03.junit_y_mockito_2023\java-spring-test-suite (feature/spring-testcontainers)
$ docker compose -f ./docker/compose.yml up -d                                                                                
[+] Running 3/3                                                                                                               
 ✔ Container c-postgres    Started                                                                                            
 ✔ Container c-mysql-test  Running                                                                                            
 ✔ Container c-mysql       Running                                                                                            
````

En este ejemplo también se observan otros contenedores (`c-mysql`, `c-mysql-test`) que forman parte de otros entornos
del mismo proyecto. El contenedor relevante para esta fase es `c-postgres`.

Podemos comprobar que `PostgreSQL` se está ejecutando correctamente con:

````bash
$ docker container ls -a
CONTAINER ID   IMAGE                 COMMAND                  CREATED         STATUS          PORTS                                         NAMES
e33f5b8fe224   postgres:17-alpine    "docker-entrypoint.s…"   3 minutes ago   Up 3 minutes    0.0.0.0:5432->5432/tcp, [::]:5432->5432/tcp   c-postgres
d8d12fb0e1bf   mysql:8.0.41-debian   "docker-entrypoint.s…"   3 days ago      Up 48 minutes   0.0.0.0:3307->3306/tcp, [::]:3307->3306/tcp   c-mysql-test
d0d5997e6ff1   mysql:8.0.41-debian   "docker-entrypoint.s…"   13 days ago     Up 48 minutes   0.0.0.0:3306->3306/tcp, [::]:3306->3306/tcp   c-mysql
````

Como el contenedor `c-postgres` aparece con el estado `“Up”`, significa que la base de datos está lista para recibir
conexiones desde nuestra aplicación Spring Boot.

## 🧾 Definiendo scripts de inicialización

Para trabajar de manera organizada, crearemos un `script SQL de inicialización` que poblará la base de datos con datos
de ejemplo durante la `etapa de desarrollo`.

Esto nos permitirá tener registros iniciales para probar los endpoints REST sin depender todavía de Testcontainers.

📂 El script estará ubicado en `src/main/resources/sql/data-dev.sql`.

````sql
-- Reiniciar IDs
TRUNCATE TABLE customers RESTART IDENTITY;

-- Insertar datos de ejemplo
INSERT INTO customers(name, email)
VALUES('María Briones', 'maria.briones@gmail.com'),
('Karito Casanova', 'karito.casanova@gmail.com'),
('Luis Castillo', 'luis.castillo@gmail.com'),
('Diego Campomanes', 'diego.campomanes@gmail.com'),
('Alexander Villanueva', 'alexander.villanueva@gmail.com');
````

| Sección                                     | Descripción                                                                                                             |
|---------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| `TRUNCATE TABLE customers RESTART IDENTITY` | Limpia completamente las tablas y reinicia los identificadores autoincrementales.                                       |
| `INSERT INTO customers(...) VALUES (...)`   | Inserta registros iniciales en la tabla `customers`. Estos datos se usarán en la etapa de desarrollo o pruebas locales. |

## ⚙️ Agregando propiedades de configuración y perfil dev

En esta fase configuraremos el entorno de desarrollo del proyecto (`dev`). Definiremos las propiedades generales
en el archivo `application.yml` y las específicas del entorno en `application-dev.yml`.

💡 Nota: En este punto dejaremos activado el perfil `dev` (definido en `spring.profiles.active`).
Esto significa que la aplicación se ejecutará conectándose a la base de datos local de desarrollo y cargará los datos
iniciales definidos en `data-dev.sql`.

Más adelante, en la `Fase 2`, cambiaremos el perfil a `test`, donde utilizaremos `Testcontainers` para crear una
base de datos efímera dentro de un contenedor.

Esta estructura permite mantener una configuración limpia y escalable, muy común en entornos empresariales donde
existen varios perfiles como `dev`, `qa`, `staging` y `prod`.

### 🗂️ Configuración base: `src/main/resources/application.yml`

````yml
server:
  port: 8080

spring:
  application:
    name: spring-testcontainers
  profiles:
    active: dev
  jpa:
    open-in-view: false
````

- `open-in-view: false`. Se desactiva (false) para evitar mantener abierta la sesión de Hibernate durante la vista, una
  buena práctica en aplicaciones REST.

### 🧩 Perfil de desarrollo: `src/main/resources/application-dev.yml`

````yml
server:
  error:
    include-message: always

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/db_spring_testcontainers_dev
    username: dev_user
    password: dev_password

  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
    defer-datasource-initialization: true # Espera a que Hibernate cree tablas antes de ejecutar scripts SQL

  sql:
    init:
      mode: always # Ejecuta siempre scripts al iniciar
      data-locations: classpath:sql/data-dev.sql # Ruta al script de datos iniciales

logging:
  level:
    root: INFO
    dev.magadiflo.testcontainers.app: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.orm.jdbc.bind: TRACE
    org.springframework.web: DEBUG
    org.springframework.transaction: DEBUG
    org.springframework.data.jpa: DEBUG
````

| Sección                                            | Descripción                                                                                                                                                                        |
|----------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `server.error.include-message: always`             | Configura que el mensaje de error se incluya siempre en las respuestas HTTP. Es útil para depuración durante el desarrollo, pero 🚫 *no se recomienda en producción*.              |
| `spring.datasource.*`                              | Define los parámetros de conexión a la base de datos **PostgreSQL** usada en el entorno de desarrollo.                                                                             |
| `spring.jpa.hibernate.ddl-auto: update`            | Permite que Hibernate cree o actualice las tablas en función de las entidades. Es ideal en desarrollo, aunque se recomienda deshabilitarlo en producción.                          |
| `spring.jpa.properties.hibernate.format_sql: true` | Formatea las sentencias SQL generadas por Hibernate, facilitando su lectura en los logs.                                                                                           |
| `spring.jpa.defer-datasource-initialization: true` | 💡 Indica a Spring Boot que espere a que Hibernate haya creado las tablas antes de ejecutar los scripts SQL (`data-dev.sql`).                                                      |
| `spring.sql.init.mode: always`                     | Fuerza la ejecución del script SQL en cada arranque de la aplicación, asegurando una base de datos limpia con datos iniciales.                                                     |
| `spring.sql.init.data-locations`                   | Especifica la ubicación del script de inicialización (`data-dev.sql`).                                                                                                             |
| `logging.level.*`                                  | Configura los niveles de logging detallados. En desarrollo, se recomienda mantener logs más verbosos para inspeccionar consultas, transacciones y comportamiento de la aplicación. |

## 🎯 Probando los Endpoints REST

Llegados a este punto, la aplicación Spring Boot se encuentra completamente configurada y la base de datos `PostgreSQL`
está activa dentro de su contenedor.

Procedemos ahora a `verificar el correcto funcionamiento de los endpoints REST` del controlador `CustomerController`.

Para ello, utilizaremos el comando `curl` (junto con `jq` para formatear la salida JSON) desde la terminal.

> ⚠️ `Nota`: Esta sección corresponde a `pruebas de desarrollo manuales`.
> Aquí no se están ejecutando tests automatizados. La idea aquí es `levantar la aplicación localmente` y verificar
> que los endpoints funcionan correctamente antes de pasar a la fase de pruebas con `Testcontainers`.

````bash
$ curl -v http://localhost:8080/api/v1/customers | jq
>
< HTTP/1.1 200
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Thu, 23 Oct 2025 21:07:25 GMT
<
[
  {
    "id": 1,
    "name": "María Briones",
    "email": "maria.briones@gmail.com"
  },
  {...},
  {
    "id": 5,
    "name": "Alexander Villanueva",
    "email": "alexander.villanueva@gmail.com"
  }
]
````

````bash
$ curl -v http://localhost:8080/api/v1/customers/1 | jq
>
< HTTP/1.1 200
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Thu, 23 Oct 2025 21:08:20 GMT
<
{
  "id": 1,
  "name": "María Briones",
  "email": "maria.briones@gmail.com"
}
````

````bash
$ curl -v http://localhost:8080/api/v1/customers/email/karito.casanova@gmail.com | jq
>
< HTTP/1.1 200
< Content-Disposition: inline;filename=f.txt
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Thu, 23 Oct 2025 21:09:03 GMT
<
{
  "id": 2,
  "name": "Karito Casanova",
  "email": "karito.casanova@gmail.com"
}
````

````bash
$ curl -v -X POST -H "content-type: application/json" -d "{\"name\": \"Melissa\", \"email\": \"meli@gmail.com\"}" http://localhost:8080/api/v1/customers | jq
>
< HTTP/1.1 201
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Thu, 23 Oct 2025 21:10:50 GMT
<
{
  "id": 6,
  "name": "Melissa",
  "email": "meli@gmail.com"
}
````

````bash
$ curl -v -X PUT -H "content-type: application/json" -d "{\"name\": \"Melissa Katherine\", \"email\": \"meli_kathe@gmail.com\"}" http://localhost:8080/api/v1/customers/6 | jq
>
< HTTP/1.1 200
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Thu, 23 Oct 2025 21:11:41 GMT
<
{
  "id": 6,
  "name": "Melissa Katherine",
  "email": "meli_kathe@gmail.com"
}
````

````bash
$ curl -v -X DELETE http://localhost:8080/api/v1/customers/6 | jq
>
< HTTP/1.1 204
< Date: Thu, 23 Oct 2025 21:12:11 GMT
<
````

# 🎯 Fase 2 — Pruebas con Testcontainers

## 📦 ¿Qué es Testcontainers y por qué usarlo?

`Testcontainers` es una biblioteca Java que permite ejecutar `contenedores Docker durante las pruebas automatizadas`.
A diferencia de otros enfoques como:

- Usar `H2` (base de datos en memoria que no refleja el comportamiento real de `PostgreSQL` en producción).
- Usar una instancia `PostgreSQL local`, que puede contener datos sucios o versiones distintas.
- `Mockear el repositorio`, lo cual no prueba SQL real ni integridad de la base de datos.

Con Testcontainers, obtenemos beneficios claros:

- ✅ Levantas un `contenedor PostgreSQL real y limpio` para cada test.
- ✅ El contenedor se destruye automáticamente al finalizar, evitando contaminación de datos.
- ✅ Garantizas `paridad entre test y producción` (mismo tipo y versión de base de datos).
- ✅ Cada desarrollador o pipeline CI tiene su `entorno aislado y reproducible`.

Esto se traduce en `tests más confiables y cercanos al entorno real`, lo que mejora la calidad y reduce errores por
diferencias de entorno.

## ⚙️ Dependencias necesarias

Desde [Spring Initializr](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.7&packaging=jar&jvmVersion=21&groupId=dev.magadiflo&artifactId=spring-testcontainers&name=spring-testcontainers&description=Demo%20project%20for%20Spring%20Boot&packageName=dev.magadiflo.testcontainers.app&dependencies=web,data-jpa,postgresql,lombok,testcontainers)
podemos agregar las dependencias necesarias para trabajar con `Testcontainers` en nuestra fase de pruebas.

Notar que en automático cuando seleccionamos el `Testcontainers` desde la web Spring Initializr se agregan dos
dependencias adicionales `junit-jupiter `y `postgresql`, ambos con `groupId` `org.testcontainers`.

````xml

<dependencies>
    <!-- Integración Testcontainers con Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-testcontainers</artifactId>
        <scope>test</scope>
    </dependency>
    <!-- Integración con JUnit 5 -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    <!-- Módulo de soporte Testcontainers para PostgreSQL -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
````

| Dependencia                        | Descripción                                                                                                                                                                                                                                                                                                                                                                                                       |
|------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `spring-boot-testcontainers`       | Proporciona la **integración oficial de Spring Boot con Testcontainers**, habilitando la detección y autoconfiguración automática de contenedores para las pruebas. Internamente incluye módulos de `spring-boot-autoconfigure` y `testcontainers`. 💡 De ellos, el más importante es **`testcontainers`**, ya que actúa como el **core** que gestiona la creación, ciclo de vida y conexión de los contenedores. |
| `org.testcontainers:postgresql`    | Módulo específico de Testcontainers que sabe cómo inicializar y configurar un contenedor **PostgreSQL** listo para tests.                                                                                                                                                                                                                                                                                         |
| `org.testcontainers:junit-jupiter` | Integra Testcontainers con **JUnit 5**, manejando automáticamente el ciclo de vida de los contenedores (inicio, stop, limpieza) mediante extensiones de JUnit.                                                                                                                                                                                                                                                    |

> 💡 `Nota`: Todas las dependencias están con `scope=test`, porque se necesitan únicamente durante la ejecución de
> tests y no forman parte del artefacto final.

## 🧩 Definiendo datos para las pruebas

Para nuestras pruebas con `Testcontainers`, necesitamos definir datos iniciales que se carguen automáticamente cada
vez que se levante el contenedor de base de datos.

De esta forma, garantizamos que `todas las pruebas comiencen en un estado limpio y predecible`.

### 🧹 Limpieza de datos `src/test/resources/sql-test/cleanup-postgres.sql`

Este script elimina todos los registros de la tabla `customers` y `reinicia la secuencia de IDs`, garantizando que los
identificadores empiecen nuevamente desde 1 en cada prueba.

💡 `PostgreSQL` permite truncar tablas relacionadas si se añade la cláusula `CASCADE`, útil cuando existen claves
foráneas. En este caso no es necesario porque solo usamos la tabla `customers`.

````sql
TRUNCATE TABLE customers RESTART IDENTITY;
````

### 🧪 Datos de prueba `src/test/resources/sql-test/data-test.sql`

Estos registros se usarán en los tests para validar los endpoints y operaciones sobre la base de datos de forma
reproducible.

````sql
INSERT INTO customers(name, email)
VALUES('Milagros Díaz', 'milagros@gmail.com'),
('Kiara Lozano', 'kiara@gmail.com'),
('Yrma Guerrero', 'yrmagerreron@outlook.com'),
('Lesly Águila', 'lesly@gmail.com'),
('Briela Cirilo', 'briela@gmail.com'),
('Cielo Fernández', 'cielo@gmail.com'),
('Susana Alvarado', 'susana@gmail.com'),
('Analucía Urbina', 'analucia@gmail.com');
````

### 🧭 Clase de constantes para los scripts

Para evitar repetir rutas en los tests, centralizamos las ubicaciones en una clase de utilidades:

````java

@UtilityClass
public class TestScripts {
    // Limpieza de base de datos
    public static final String CLEANUP_POSTGRES = "/sql-test/cleanup-postgres.sql";

    // Datos de prueba comunes
    public static final String DATA_TEST = "/sql-test/data-test.sql";
}
````

La anotación `@UtilityClass` de `Lombok` convierte la clase en una utilidad estática:

- Impide la creación de instancias.
- Marca automáticamente todos los campos como static final.
- Marca automáticamente todos los métodos como static.

## ⚙️ Definiendo propiedades de configuración para pruebas

Para evitar posibles conflictos con los archivos de configuración del entorno principal (`src/main/resources`),
es recomendable definir un `archivo de configuración por defecto para los tests`.

📌 Ruta recomendada: `src/test/resources/application.yml`

````yml
server:
  port: 0

spring:
  application:
    name: spring-testcontainers
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true
        show_sql: false
  sql:
    init:
      mode: never

logging:
  level:
    root: INFO
    dev.magadiflo.testcontainers.app: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.orm.jdbc.bind: TRACE
    org.springframework.test: DEBUG
````

### 🔍 ¿Y qué pasa con el perfil `test`?

En un escenario normal (`sin Testcontainers`), definiríamos un archivo:

📁 `src/test/resources/application-test.yml`

````yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/db_spring_testcontainers_test
    username: test_user
    password: test_password
````

💡 Pero en este proyecto `NO usaremos` este archivo.

### ✅ ¿Por qué NO crear `application-test.yml`?

| Motivo                                              | Explicación                                                                           |
|-----------------------------------------------------|---------------------------------------------------------------------------------------|
| `Testcontainers` genera parámetros reales           | URL, usuario y contraseña son proporcionados dinámicamente por el contenedor Postgres |
| No hay más propiedades exclusivas del perfil `test` | El archivo solo colocaría valores que serían reemplazados                             |
| Evitamos configuración duplicada o inútil           | Menos archivos, menos ruido 🔇                                                        |

### 🧠 ¿Seguir usando `@ActiveProfiles("test")`?

✅ `¡Sí!`. Incluso sin archivo `application-test.yml`, Spring Boot:

1. Activa el perfil test
2. Busca la configuración correspondiente
3. Como no hay archivo `application-test.yml`
4. ➝ hereda la configuración del `application.yml` (configuración por defecto) ubicado en `src/test/resources`

📌 En resumen:

> Activamos el perfil `test` por semántica y buenas prácticas, pero dejamos que `Testcontainers` se encargue del
> datasource 🐳

📌 Nota final

Esta decisión se puede reconsiderar más adelante si el perfil `test` requiere:

- Configuración de logging diferente
- Propiedades específicas para pruebas de integración
- Feature flags activados solo en test ✅

Por ahora: `menos es más` 🎯

## 🧪 Testcontainers (Enfoque Manual) con `@DynamicPropertySource`

Este enfoque es el más flexible y ampliamente utilizado en entornos reales, ya que permite configurar propiedades
dinámicas del contenedor (URL, usuario, contraseña, puerto aleatorio, etc.) en tiempo de ejecución.

### 🧱 ¿Cuándo usar este enfoque?

- Cuando necesitas personalizar propiedades específicas del contenedor.
- Cuando trabajas con múltiples contenedores o propiedades complejas.
- Cuando quieres mantener la configuración desacoplada del `application-test.yml`.
- Cuando tu versión de `Spring Boot` es anterior a `3.1` (sin soporte para `@ServiceConnection`).

### 🧩 Clase Base para Tests de Integración

📂 Ubicación: `src/test/java/dev/magadiflo/testcontainers/app/commons/AbstractPostgresManualTest.java`

Creamos una clase abstracta que actuará como plantilla para todas las pruebas que usen `PostgreSQL`.
Este patrón es común en empresas porque:

- ✅ Evita duplicar configuración en cada prueba
- ✅ Centraliza la gestión del contenedor
- ✅ Permite cambiar fácilmente la version del contenedor o parámetros globales

````java

@Slf4j
@Testcontainers
public abstract class AbstractPostgresManualTest {

    @Container
    protected static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        log.info("Sobrescribe las propiedades de Spring Data JPA con valores del contenedor");
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }

    @Test
    void connectionEstablished() {
        assertThat(POSTGRESQL_CONTAINER.isCreated()).isTrue();
        assertThat(POSTGRESQL_CONTAINER.isRunning()).isTrue();
        log.info("Contenedor PostgreSQL iniciado en: {}", POSTGRESQL_CONTAINER.getJdbcUrl());
    }
}
````

📌 Explicación de las anotaciones clave

| Anotación / Concepto                          | Descripción                                                                                                                                                                                                 |
|-----------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `@Testcontainers`                             | Activa la integración con JUnit para iniciar/detener contenedores automáticamente. La extensión Testcontainers encuentra todos los campos anotados con `@Container` y llama a sus métodos de ciclo de vida. |
| `@Container`                                  | Indica qué contenedor será gestionado por Testcontainers.                                                                                                                                                   |
| `static` en el contenedor                     | Hace que el contenedor sea **singleton**: se crea **una sola vez** por clase (más rápido).                                                                                                                  |
| `PostgreSQLContainer<>("postgres:17-alpine")` | Crea un contenedor con la imagen oficial de `PostgreSQL 17` en versión ligera (`alpine`).                                                                                                                   |
| `@DynamicPropertySource`                      | Registra dinámicamente las propiedades **antes** de levantar el contexto de Spring.                                                                                                                         |
| `DynamicPropertyRegistry`                     | Inserta en Spring la configuración real obtenida del contenedor iniciado.                                                                                                                                   |

### 🎯 ¿Por qué usar `@DynamicPropertySource`?

Porque `PostgreSQL` siempre asigna un puerto aleatorio (e.g. `54321`, `58734`,..), por lo que NO podemos definir
la URL en `application-test.yml`.

- ✅ Obtiene en tiempo real las propiedades del contenedor.
- ✅ Spring arranca usando la DB real del Testcontainer.

### ¿Por qué el contenedor es `static`?

| Estrategia  | Comportamiento                                                   | Rendimiento        |
|-------------|------------------------------------------------------------------|--------------------|
| `No static` | Se crea un contenedor por test                                   | ❌ Muy lento        |
| `static `   | Se comparte un único contenedor para todos los tests de la clase | ✅ Mucho más rápido |

💡 En proyectos grandes, este cambio puede ahorrar minutos por build.

## 🧩 Clase de prueba para repositorio usando Testcontainers (Configuración Manual)

📁 `CustomerRepositoryManualTestcontainersTest.java`

````java

@Tag("testcontainers")
@ActiveProfiles("test")
@Sql(scripts = TestScripts.DATA_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryManualTestcontainersTest extends AbstractPostgresManualTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldReturnAllCustomersWhenDatabaseIsInitialized() {
        // when
        List<Customer> customers = this.customerRepository.findAll();

        // then
        assertThat(customers)
                .isNotEmpty()
                .hasSize(8)
                .extracting(Customer::getName)
                .contains("Lesly Águila", "Briela Cirilo", "Milagros Díaz");
    }

    @Test
    void shouldFindCustomerWhenValidEmail() {
        assertThat(this.customerRepository.findByEmail("yrmagerreron@outlook.com"))
                .isPresent()
                .hasValueSatisfying(customer -> {
                    assertThat(customer.getId()).isEqualTo(3);
                    assertThat(customer.getName()).isEqualTo("Yrma Guerrero");
                });
    }

    @Test
    void shouldDeleteAllCustomers() {
        assertThat(this.customerRepository.count()).isEqualTo(8);
        this.customerRepository.deleteAll();
        assertThat(this.customerRepository.count()).isZero();
    }
}
````

🧩 Explicación de las anotaciones clave

| Anotación                                    | Función                                                                                                                  |
|----------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| `@DataJpaTest`                               | Crea un **contexto mínimo** para pruebas de repositorio.                                                                 |
| `@AutoConfigureTestDatabase(replace = NONE)` | Indica a Spring que `no reemplace la base de datos configurada` (PostgreSQL en Testcontainers) por una embebida como H2. |
| `@ActiveProfiles("test")`                    | Indica que se debe usar la configuración del perfil `test`.                                                              |
| `@Sql(...)`                                  | Inserta **datos iniciales** antes de que se ejecute la clase de test.                                                    |
| `@Tag("testcontainers")`                     | Permite ejecutar o filtrar pruebas con Maven/IDE si se desea.                                                            |

🧠 Ciclo de vida de las pruebas

- 1️⃣ Spring levanta el contexto de pruebas.
- 2️⃣ `Testcontainers` inicia el contenedor `PostgreSQL`.
- 3️⃣ `@DynamicPropertySource` inyecta las propiedades reales del contenedor.
- 4️⃣ `@Sql` carga los datos iniciales.
- 5️⃣ Se ejecutan los métodos de prueba.
- 6️⃣ Al finalizar la clase → el contenedor se detiene.

### 📌 Importante sobre transacciones en `@DataJpaTest`

Spring `hace rollback después de cada test automáticamente`. Esto asegura aislamiento entre pruebas, evitando
contaminación de datos.

Es por eso que, aunque llamemos a deleteAll(), los datos vuelven para los siguientes tests (gracias al `rollback`).

### 📋 Ejecución de pruebas

Al ejecutar:

````bash
D:\programming\spring\01.udemy\02.andres_guzman\03.junit_y_mockito_2023\java-spring-test-suite\spring-testcontainers (feature/spring-testcontainers)
$ mvn test
[INFO] Scanning for projects...
...
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running dev.magadiflo.testcontainers.app.integration.repository.CustomerRepositoryManualTestcontainersTest
...
2025-10-24T16:37:42.818-05:00  INFO 3416 --- [spring-testcontainers] [           main] d.m.t.a.c.AbstractPostgresManualTest     : Sobrescribe las propiedades de Spring Data JPA con valores del contenedor
2025-10-24T16:37:42.826-05:00  INFO 3416 --- [spring-testcontainers] [           main] stomerRepositoryManualTestcontainersTest : Starting CustomerRepositoryManualTestcontainersTest using Java 21.0.6 with PID 3416 (started by magadiflo in D:\programming\spring\01.udemy\02.andres_guzman\03.junit_y_mockito_2023\java-spring-test-suite\spring-testcontainers)
2025-10-24T16:37:42.826-05:00 DEBUG 3416 --- [spring-testcontainers] [           main] stomerRepositoryManualTestcontainersTest : Running with Spring Boot v3.5.7, Spring v6.2.12
2025-10-24T16:37:42.830-05:00  INFO 3416 --- [spring-testcontainers] [           main] stomerRepositoryManualTestcontainersTest : The following 1 profile is active: "test"
2025-10-24T16:37:43.183-05:00  INFO 3416 --- [spring-testcontainers] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2025-10-24T16:37:43.255-05:00  INFO 3416 --- [spring-testcontainers] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 62 ms. Found 1 JPA repository interface.
2025-10-24T16:37:43.517-05:00  INFO 3416 --- [spring-testcontainers] [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2025-10-24T16:37:43.572-05:00  INFO 3416 --- [spring-testcontainers] [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 6.6.33.Final
2025-10-24T16:37:43.610-05:00  INFO 3416 --- [spring-testcontainers] [           main] o.h.c.internal.RegionFactoryInitiator    : HHH000026: Second-level cache disabled
2025-10-24T16:37:43.880-05:00  INFO 3416 --- [spring-testcontainers] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2025-10-24T16:37:43.920-05:00  INFO 3416 --- [spring-testcontainers] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2025-10-24T16:37:44.089-05:00  INFO 3416 --- [spring-testcontainers] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@33eab2e8
...
2025-10-24T16:37:45.950-05:00 DEBUG 3416 --- [spring-testcontainers] [           main] o.s.t.c.transaction.TransactionContext   : Began transaction (1) for test class [dev.magadiflo.testcontainers.app.integration.repository.CustomerRepositoryManualTestcontainersTest]; test method [shouldDeleteAllCustomers]; transaction manager [org.springframework.orm.jpa.JpaTransactionManager@74901d7]; rollback [true]
...
2025-10-24T16:37:46.274-05:00 DEBUG 3416 --- [spring-testcontainers] [           main] org.hibernate.SQL                        :
    select
        count(*)
    from
        customers c1_0
2025-10-24T16:37:46.278-05:00 DEBUG 3416 --- [spring-testcontainers] [           main] o.s.t.c.transaction.TransactionContext   : Rolled back transaction (1) for test class [dev.magadiflo.testcontainers.app.integration.repository.CustomerRepositoryManualTestcontainersTest]; test method [shouldDeleteAllCustomers]
2025-10-24T16:37:47.000-05:00  INFO 3416 --- [spring-testcontainers] [ionShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2025-10-24T16:37:47.000-05:00 DEBUG 3416 --- [spring-testcontainers] [ionShutdownHook] org.hibernate.SQL                        :
    set client_min_messages = WARNING
[ERROR] Surefire is going to kill self fork JVM. The exit has elapsed 30 seconds after System.exit(0).
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  41.308 s
[INFO] Finished at: 2025-10-24T16:38:17-05:00
[INFO] ------------------------------------------------------------------------
````

Se observa en logs:

- ✅ Contenedor PostgreSQL creado.
- ✅ Conexión establecida.
- ✅ Hibernate generando schema create-drop.
- ✅ Datos cargados mediante @Sql.
- ✅ Pruebas ejecutadas correctamente.

⚠️ Aparece una advertencia informativa de Maven Surefire:

````bash
[ERROR] Surefire is going to kill self fork JVM. The exit has elapsed 30 seconds after System.exit(0). 
````

📌 Causa: Testcontainers tarda en cerrarse al finalizar el build

- ✅ Resultado Final: BUILD SUCCESS → sin problemas reales

### 🎯 Resultado del Test

- ✅ Todos los endpoints del repositorio se prueban contra PostgreSQL real.
- ✅ Sin dependencia de Docker local externo.
- ✅ Sin bases contaminadas.
- ✅ Entorno 100% reproducible.

## 🧩 Clase de prueba para controlador usando Testcontainers (Configuración Manual)

📁 `CustomerControllerManualTestcontainersTest.java`

Vamos a validar el comportamiento del controlador `CustomerController` en un entorno realista, utilizando una base de
datos PostgreSQL contenerizada mediante `Testcontainers`. Esta clase extiende la configuración base
`AbstractPostgresManualTest`, lo que permite reutilizar el contenedor sin duplicar configuración.

- `executionPhase = BEFORE_TEST_METHOD` garantiza que cada test empieza con la BD en un estado limpio, evitando
  contaminación entre pruebas.
- Debido al resultado no determinista de los registros devueltos por la BD, usamos `containsExactlyInAnyOrder()` para
  validar contenido sin depender del orden.

````java

@Slf4j
@Tag("testcontainers")
@ActiveProfiles("test")
@Sql(scripts = {TestScripts.CLEANUP_POSTGRES, TestScripts.DATA_TEST}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerManualTestcontainersTest extends AbstractPostgresManualTest {

    @Autowired
    private TestRestTemplate client;

    @Test
    void shouldReturnAllCustomersWhenTheyExist() {
        // given

        // when
        ResponseEntity<Customer[]> response = this.client.getForEntity("/api/v1/customers", Customer[].class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(8);
        assertThatList(List.of(response.getBody()))
                .hasSize(8)
                .extracting(Customer::getName)
                .containsExactlyInAnyOrder(
                        "Lesly Águila",
                        "Cielo Fernández",
                        "Susana Alvarado",
                        "Briela Cirilo",
                        "Milagros Díaz",
                        "Kiara Lozano",
                        "Analucía Urbina",
                        "Yrma Guerrero");
        assertThat(response.getBody())
                .filteredOn(customer -> customer.getId().equals(1L))
                .singleElement()
                .satisfies(customer -> {
                    assertThat(customer.getName()).isEqualTo("Milagros Díaz");
                    assertThat(customer.getEmail()).isEqualTo("milagros@gmail.com");
                });
    }

    @Test
    void shouldCreateNewCustomerSuccessfully() {
        // given
        Customer request = Customer.builder()
                .name("Nicol Sinchi")
                .email("nicol@gmail.com")
                .build();

        // when
        ResponseEntity<Customer> response = this.client.postForEntity("/api/v1/customers", request, Customer.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody())
                .isNotNull()
                .extracting(Customer::getId, Customer::getName, Customer::getEmail)
                .containsExactly(9L, request.getName(), request.getEmail());
    }

    @Test
    void shouldReturnCustomerDetailsWhenCustomerExists() {
        // given
        long customerId = 5L;

        // when
        ResponseEntity<Customer> response = this.client.getForEntity("/api/v1/customers/{customerId}", Customer.class, customerId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody())
                .isNotNull()
                .satisfies(customer -> {
                    assertThat(customer.getId()).isEqualTo(5);
                    assertThat(customer.getName()).isEqualTo("Briela Cirilo");
                    assertThat(customer.getEmail()).isEqualTo("briela@gmail.com");
                });
    }
}
````

Estos tests verifican el comportamiento end-to-end del controlador real, validando:

- ✅ Integración con Spring Boot.
- ✅ Comunicación HTTP real con TestRestTemplate.
- ✅ Persistencia en PostgreSQL mediante Testcontainers.
- ✅ Estado consistente de la BD antes de cada prueba.

## 🧪 Testcontainers (Enfoque Automático) con `@Testcontainers`, `@Container` y `@ServiceConnection`

Este es el enfoque `MODERNO` y `RECOMENDADO` desde `Spring Boot 3.1+`. Es el que las empresas están adoptando porque:

- Menos código boilerplate (no necesitas `@DynamicPropertySource`).
- Autoconfigura propiedades automáticamente.
- Más declarativo y fácil de leer.
- Menos propenso a errores (no hay que mapear propiedades manualmente).

En este enfoque, `Spring Boot detecta el contenedor y autoconfigura automáticamente` la conexión a la base de datos.

### 🧩 Clase Base para Tests de Integración

📂 Ubicación: `src/test/java/dev/magadiflo/testcontainers/app/commons/AbstractPostgresAutomaticTest.java`

Creamos una clase que centraliza la configuración del contenedor `PostgreSQL` usando `autoconfiguración automática`:

````java

@Slf4j
@Testcontainers
public abstract class AbstractPostgresAutomaticTest {

    @Container
    @ServiceConnection
    protected static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:17-alpine");

    @Test
    void connectionEstablished() {
        assertThat(POSTGRESQL_CONTAINER.isCreated()).isTrue();
        assertThat(POSTGRESQL_CONTAINER.isRunning()).isTrue();
        log.info("Contenedor PostgreSQL iniciado en: {}", POSTGRESQL_CONTAINER.getJdbcUrl());
    }
}
````

### 🌟 `@ServiceConnection`: la clave del enfoque automático

Esta anotación es la que hace toda la magia en `Spring Boot 3.1+`.

¿Qué hace `@ServiceConnection`?

- Detecta que es un `PostgreSQLContainer`.
- Autoconfigura automáticamente:

| Propiedad                             | Se obtiene del contenedor |
|---------------------------------------|---------------------------|
| `spring.datasource.url`               | ✅                         |
| `spring.datasource.username`          | ✅                         |
| `spring.datasource.password`          | ✅                         |
| `spring.datasource.driver-class-name` | ✅                         |

Esto significa:

- 🚫 No necesitamos `@DynamicPropertySource`.
- 🚫 No necesitamos `application-test.yml` con propiedades de DB.
- 🚫 No necesitamos configurar manualmente el `DataSource`.

### 🔍 ¿Cómo sabe Spring Boot qué configurar?

`Spring Boot` usa un mecanismo llamado `ConnectionDetails`, el cual ya está implementado para servicios conocidos:

- PostgreSQL
- MySQL
- MongoDB
- Redis
- Kafka
- Elasticsearch
- y muchos más.

Es literalmente “Convención sobre Configuración” aplicada a pruebas.

### ❓¿Cuándo usarías además `@DynamicPropertySource`?

`@ServiceConnection` solo autoconfigura la conexión principal. Pero si necesitamos otras propiedades extra como:

| Ejemplo de necesidad      | Se requiere configuración manual |
|---------------------------|----------------------------------|
| Ajustar pool (Hikari)     | ✅                                |
| Configurar DDL/JPA extra  | ✅                                |
| Feature flags / Logging   | ✅                                |
| Tu servicio personalizado | ✅                                |

## 🧩 Clase de prueba para repositorio usando Testcontainers (Configuración Automática)

Esta prueba valida la integración entre `Spring Data JPA` y `PostgreSQL` real usando `Testcontainers`.
Aprovecha la autoconfiguración habilitada desde nuestra clase base `AbstractPostgresAutomaticTest`.

📁 `CustomerRepositoryAutomaticTestcontainersTest.java`

````java

@Tag("testcontainers")
@ActiveProfiles("test")
@Sql(scripts = TestScripts.DATA_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryAutomaticTestcontainersTest extends AbstractPostgresAutomaticTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldReturnAllCustomersWhenDatabaseIsInitialized() {
        // when
        List<Customer> customers = this.customerRepository.findAll();

        // then
        assertThat(customers)
                .isNotEmpty()
                .hasSize(8)
                .extracting(Customer::getName)
                .contains("Lesly Águila", "Briela Cirilo", "Milagros Díaz");
    }

    @Test
    void shouldFindCustomerWhenValidEmail() {
        assertThat(this.customerRepository.findByEmail("yrmagerreron@outlook.com"))
                .isPresent()
                .hasValueSatisfying(customer -> {
                    assertThat(customer.getId()).isEqualTo(3);
                    assertThat(customer.getName()).isEqualTo("Yrma Guerrero");
                });
    }

    @Test
    void shouldSaveCustomer() {
        // given
        Customer customer = Customer.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        // when
        Customer savedCustomer = this.customerRepository.save(customer);

        // then
        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getName()).isEqualTo("John Doe");
        assertThat(savedCustomer.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldDeleteAllCustomers() {
        // given
        assertThat(this.customerRepository.count()).isEqualTo(8);

        // when
        this.customerRepository.deleteAll();

        // then
        assertThat(this.customerRepository.count()).isZero();
    }
}
````
