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

## 🏦 Modelo de Datos — Entidades JPA

En esta primera parte definimos las `entidades del dominio principal` del sistema bancario:

- ➡️ Bank (banco)
- ➡️ Account (cuenta bancaria)

Ambas entidades están modeladas con `JPA (Jakarta Persistence API)` y usan `Lombok` para eliminar código repetitivo
(getters, setters, constructores, builder, etc.).

### 🔗 Relación Bidireccional `Bank ↔ Account`

El modelo de datos incluye una relación bidireccional entre `Bank` y `Account`:

- Un `Bank` puede tener múltiples `Account` asociadas.
- Cada `Account` pertenece a un único `Bank`.

````
Bank
 └─── Account
        ↳ bank_id (FK) 
````

- `Bank → Account`. Relación `@OneToMany` con `cascade = ALL` y `orphanRemoval = true`, lo que implica:
    - Si se elimina una cuenta de la lista, se elimina de la base.
    - Si se elimina el banco, se eliminan sus cuentas.
    - Ideal para mantener integridad y evitar cuentas huérfanas.


- `Account → Bank`. Relación `@ManyToOne` con `@JoinColumn(name = "bank_id")`, que:
    - Define la clave foránea en la tabla `accounts`.
    - Permite acceder al banco desde una cuenta.

### 🏛️ Entidad: Bank

Representa un banco dentro del sistema. Cada banco administra múltiples cuentas y registra el número total de
transferencias realizadas.

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

    @ToString.Exclude           // Evita ciclo infinito en toString()
    @EqualsAndHashCode.Exclude  // No usar la lista en equals/hashCode
    @Builder.Default            // Mantiene la inicialización (new ArrayList<>()) con @Builder
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "bank")
    private List<Account> accounts = new ArrayList<>();

    // Métodos helper para mantener sincronizada la relación bidireccional
    public void addAccount(Account account) {
        this.accounts.add(account);
        account.setBank(this);
    }

    public void removeAccount(Account account) {
        this.accounts.remove(account);
        account.setBank(null);
    }
}
````

| Campo            | Tipo            | Descripción                                      |
|------------------|-----------------|--------------------------------------------------|
| `id`             | `Long`          | Identificador único del banco.                   |
| `name`           | `String`        | Nombre del banco (único, obligatorio).           |
| `totalTransfers` | `Integer`       | Total de transferencias realizadas por el banco. |
| `accounts`       | `List<Account>` | Lista de cuentas asociadas al banco.             |

#### 🔗 Relación con Account

- Anotación: `@OneToMany(mappedBy = "bank")`
- Define el lado inverso de la relación (la entidad `Account` contiene la `FK` `bank_id`).
- `cascade = CascadeType.ALL` → Esta propiedad indica que todas las operaciones de persistencia realizadas sobre la
  entidad padre (`Bank`) se propagan automáticamente a sus hijos (`Account`). Es decir, se propaga las operaciones
  (persist, merge, remove, refresh, detach).
- `orphanRemoval = true` → Esta propiedad indica que si una entidad hija (`Account`) se elimina de la colección del
  padre (`Bank`), también se elimina de la base de datos, aunque no se haya llamado explícitamente a
  `accountRepository.delete()`.
- `addAccount() / removeAccount()` → Métodos de conveniencia para mantener la consistencia bidireccional. Garantizan
  que ambas entidades se mantengan sincronizadas.

| Práctica                                          | Justificación                                                               |
|---------------------------------------------------|-----------------------------------------------------------------------------|
| `addAccount(...)`, `removeAccount(...)`           | Mantienen sincronía entre objetos en memoria, evitando relaciones rotas.    |
| `@ToString.Exclude`, `@EqualsAndHashCode.Exclude` | Previene ciclos infinitos y errores en colecciones bidireccionales.         |
| `@Builder.Default`                                | Evita que `Lombok` sobrescriba la inicialización de la lista en el builder. |

### 💰 Entidad: Account

Representa una cuenta bancaria asociada a un banco específico. Cada cuenta tiene un `titular (holder)` y
un `saldo (balance)`.

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

    @ToString.Exclude                   // Evita ciclo infinito en toString()
    @EqualsAndHashCode.Exclude          // No usar la relación en equals/hashCode
    @ManyToOne(fetch = FetchType.LAZY)  // Mantener LAZY por buenas prácticas (default EAGER)
    @JoinColumn(name = "bank_id")
    private Bank bank;
}
````

| Campo     | Tipo         | Descripción                                            |
|-----------|--------------|--------------------------------------------------------|
| `id`      | `Long`       | Identificador único de la cuenta.                      |
| `holder`  | `String`     | Nombre del titular (máx. 100 caracteres, obligatorio). |
| `balance` | `BigDecimal` | Saldo monetario con alta precisión decimal.            |
| `bank`    | `Bank`       | Banco al que pertenece esta cuenta `(FK)`.             |

#### 💡 Detalle sobre `precision` y `scale`

- `precision = 19` → número total de dígitos significativos permitidos (enteros + decimales).
- `scale = 2` → cantidad de dígitos decimales (por ejemplo: centavos).
- Esto permite representar valores monetarios muy grandes, manteniendo la exactitud decimal.

> En términos prácticos, `según la configuración JPA definida en esta entidad`, el campo `balance` podrá almacenar
> hasta `17 dígitos enteros y 2 decimales`, por ejemplo: `99999999999999999.99`.
>
> Cabe resaltar que esta restricción proviene de la configuración `precision` y `scale` en la anotación `@Column`,
> no del tipo `BigDecimal` en sí.

## 📦 Capa de Transferencia de Datos (DTOs)

En esta sección definimos los `Data Transfer Objects (DTOs)`, los cuales representan los datos que se envían y reciben
a través de la `API REST`.

Los `DTOs` permiten desacoplar la capa de persistencia (`Entity`) de la capa de exposición (`Controller`),
evitando exponer directamente nuestras entidades JPA y facilitando la validación, serialización y versionado.

### 🧾 AccountRequest

Este DTO se utiliza para crear o actualizar cuentas bancarias. Incluye validaciones de entrada que garantizan la
integridad de los datos enviados por el cliente.

````java
public record AccountRequest(@NotBlank
                             @Size(max = 100)
                             String holder,

                             @NotNull
                             @Min(0)
                             @Digits(integer = 17, fraction = 2)
                             BigDecimal balance) {
}
````

✅ Validaciones aplicadas

| Anotación                             | Significado                                                                                                         |
|---------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| `@NotBlank`                           | El nombre del titular no puede ser nulo ni vacío.                                                                   |
| `@Size(max = 100)`                    | Longitud máxima de 100 caracteres para el nombre del titular.                                                       |
| `@NotNull`                            | El saldo no puede ser nulo.                                                                                         |
| `@Min(0)`                             | El saldo inicial no puede ser negativo.                                                                             |
| `@Digits(integer = 17, fraction = 2)` | El número puede tener hasta **17 enteros y 2 decimales**, consistente con la configuración de la entidad `Account`. |

### 🔁 TransactionRequest

DTO que representa una solicitud de transferencia bancaria. Es decir, cuando un cliente solicita mover dinero de una
cuenta origen a una cuenta destino dentro de un banco específico.

````java
public record TransactionRequest(@NotNull
                                 @Positive
                                 Long bankId,

                                 @NotNull
                                 @Positive
                                 Long sourceAccountId,

                                 @NotNull
                                 @Positive
                                 Long targetAccountId,

                                 @NotNull
                                 @Positive
                                 @Digits(integer = 17, fraction = 2)
                                 BigDecimal amount) {
}
````

✅ Validaciones aplicadas

| Campo             | Validaciones                             | Descripción                                             |
|-------------------|------------------------------------------|---------------------------------------------------------|
| `bankId`          | `@NotNull`, `@Positive`                  | Identificador del banco que realiza la transacción.     |
| `sourceAccountId` | `@NotNull`, `@Positive`                  | ID de la cuenta de origen (de donde sale el dinero).    |
| `targetAccountId` | `@NotNull`, `@Positive`                  | ID de la cuenta de destino (a donde llega el dinero).   |
| `amount`          | `@NotNull`, `@Positive`, `@Digits(17,2)` | Monto a transferir, con precisión monetaria controlada. |

⚙️ Estas validaciones garantizan que los IDs sean válidos y que el monto sea siempre positivo.

### 💳 AccountResponse

Este DTO representa la respuesta devuelta por la API cuando se consulta o crea una cuenta bancaria. Contiene
información pública y segura del recurso.

````java
public record AccountResponse(Long id,
                              String holder,
                              BigDecimal balance) {
}
````
