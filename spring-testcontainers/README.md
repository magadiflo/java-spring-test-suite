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

### 💡 Relación indirecta con Testcontainers

Aunque en esta fase todavía no usamos `Testcontainers`, esta entidad es una `pieza esencial` para las pruebas que
realizaremos posteriormente. Cuando usemos `Testcontainers` con `PostgreSQL`:

- El `contenedor levantará una base de datos PostgreSQL limpia y aislada`.
- Hibernate, a través de las configuraciones de JPA, `creará automáticamente la tabla` `customers` al inicializar el
  contexto de Spring Boot dentro del contenedor.
- En las pruebas, podremos persistir, consultar y eliminar instancias de `Customer` en un entorno idéntico a producción,
  sin depender de una base instalada localmente.
