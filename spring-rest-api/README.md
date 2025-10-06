# 🌱 Spring Boot: REST API - Tests

Este proyecto corresponde a la etapa de aprendizaje de `pruebas en Spring Boot`. La idea es construir una aplicación
base con `Spring Boot` + `JPA` + `MySQL` y sobre ella aplicar diferentes tipos de pruebas:

- 🧪 Pruebas unitarias
- 🔗 Pruebas de integración

Además, hemos agregado `Swagger (OpenAPI)` para documentar y probar fácilmente nuestros endpoints REST.

---

## ⚙️ Dependencias Iniciales

El proyecto fue generado desde
[Spring Initializr](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.6&packaging=jar&jvmVersion=21&groupId=dev.magadiflo&artifactId=spring-rest-api&name=spring-rest-api&description=Demo%20project%20for%20Spring%20Boot&packageName=dev.magadiflo.app&dependencies=web,data-jpa,lombok,mysql,validation).

📌 `Nota importante`: Al agregar `spring-boot-starter-web`, automáticamente se incluye `spring-boot-starter-test`,
el cual ya trae herramientas para pruebas como:

- ✅ JUnit 5
- ✅ Mockito
- ✅ AssertJ

Estas librerías serán claves para escribir nuestras pruebas.

### 📦 Dependencias en `pom.xml`

````xml
<!--Spring Boot 3.5.6-->
<!--Java 21-->
<!--lombok-mapstruct-binding.version 0.2.0-->
<!--openapi.version 2.8.13-->
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
</dependencies>
````

## 📚 Configurando Swagger (SpringDoc OpenAPI)

- [springdoc-openapi v2.8.13](https://springdoc.org/)
- [Spring Boot 3 + Spring Doc + Swagger : Un ejemplo](https://sacavix.com/2023/03/spring-boot-3-spring-doc-swagger-un-ejemplo/)

`Swagger` se integra en nuestro proyecto de `Spring Boot` con la siguiente dependencia, misma que agregamos en el
`pom.xml` anterior.

````xml

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>${openapi.version}</version>
</dependency>
````

📌 No requiere configuración adicional, ya que se integra automáticamente con `Spring Boot`.

### 🌐 Endpoints disponibles

Según la documentación de esta dependencia ([springdoc-openapi v2.8.13](https://springdoc.org/)) nos dice que para la
integración entre `spring-boot` y `swagger-ui`, debemos agregar la dependencia a la lista de dependencias de
nuestro proyecto (no necesitamos configuración adicional).

Una vez levantada la aplicación (`mvn spring-boot:run`), se habilitan automáticamente los siguientes recursos:

- 📖 `Swagger UI` (interfaz gráfica): http://localhost:8080/swagger-ui/index.html
- 📄 `OpenAPI` en JSON: http://localhost:8080/v3/api-docs
- 📄 `OpenAPI` en YAML: http://localhost:8080/v3/api-docs.yaml

⚠️ Importante:

- `localhost` → host local
- `8080` → puerto por defecto de Spring Boot
- `context-path` → depende de tu configuración de la aplicación

### 🎯 ¿Por qué usar Swagger en este proyecto?

- 📌 Documentación automática de la API REST.
- 📌 Pruebas rápidas de endpoints sin depender de Postman o Curl.
- 📌 Facilita la comunicación con otros desarrolladores y equipos de QA.
- 📌 Exportación en `JSON/YAML`, útil para integraciones con otras herramientas.

> En este caso, aunque tenemos `Swagger UI` disponible, también utilizaremos `cURL` para ejecutar pruebas rápidas desde
> la terminal y registrar los resultados directamente en esta documentación.

## 🛡️ [Configurando Plugin con MapStruct](https://github.com/magadiflo/webflux-r2dbc-crud/blob/main/README.md)

Como vamos a trabajar con `MapStruct` necesitamos ampliar el `maven-compiler-plugin` para activar la generación de
código de `MapStruct`.

````xml

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
</plugins>
````

---

# 🏗️ Fase 1 — Construcción del Proyecto Base (sin tests aún)

---

## 🏦 Modelo de Datos

En esta primera parte definimos las entidades base del dominio:

- ➡️ Bank (banco)
- ➡️ Account (cuenta bancaria)

Ambas entidades están modeladas con `JPA (Jakarta Persistence API)` y usan `Lombok` para eliminar código repetitivo
(getters, setters, constructores, builder).

### 🏛️ Entidad: Bank

Representa un banco dentro del sistema. Cada banco administra múltiples cuentas y realiza transferencias entre ellas.

````java

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "banks")
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer totalTransfers;
}
````

### 💰 Entidad: Account

Representa una cuenta bancaria con su titular y saldo disponible.

````java

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String holder;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
}
````

**Campos:**

- `id`: Identificador único autogenerado.
- `holder`: Nombre del titular de la cuenta (máx. 100 caracteres).
- `balance`: Saldo monetario con alta precisión decimal.

#### 💡 Detalle sobre `precision` y `scale`

- `precision = 19` → número total de dígitos significativos permitidos (enteros + decimales).
- `scale = 2` → cantidad de dígitos decimales (por ejemplo: centavos).
- Esto permite representar valores monetarios muy grandes, manteniendo la exactitud decimal.

> En términos prácticos, `según la configuración JPA definida en esta entidad`, el campo `balance` podrá almacenar
> hasta `17 dígitos enteros y 2 decimales`, por ejemplo: `99999999999999999.99`.
>
> Cabe resaltar que esta restricción proviene de la configuración `precision` y `scale` en la anotación `@Column`,
> no del tipo `BigDecimal` en sí.
