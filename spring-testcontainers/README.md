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
