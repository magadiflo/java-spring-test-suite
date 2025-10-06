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

En esta sección se definen los `Data Transfer Objects (DTOs)` — clases que representan la información que viaja entre
el cliente y la API REST.

Su propósito es aislar la capa de presentación del modelo de dominio, aplicando validaciones que garanticen la
consistencia de los datos antes de llegar a la lógica de negocio.

### 🧾 AccountCreateRequest

DTO utilizado para crear una nueva cuenta bancaria. Incluye validaciones que aseguran la integridad y coherencia de los
datos enviados por el cliente.

````java
public record AccountCreateRequest(@NotBlank(message = "El nombre del titular no puede estar vacío")
                                   @Size(max = 100, message = "El nombre del titular no puede superar los 100 caracteres")
                                   String holder,

                                   @NotNull(message = "El saldo inicial es obligatorio")
                                   @DecimalMin(value = "0.00", message = "El saldo no puede ser negativo")
                                   @Digits(integer = 17, fraction = 2, message = "El saldo debe tener hasta 17 dígitos enteros y 2 decimales")
                                   BigDecimal balance,

                                   @NotNull(message = "Debe especificarse el banco asociado")
                                   @Positive(message = "El identificador del banco debe ser un número positivo")
                                   Long bankId) {
}
````

✅ Validaciones aplicadas

| Anotación             | Descripción                                                                            |
|-----------------------|----------------------------------------------------------------------------------------|
| `@NotBlank`           | El nombre del titular no puede ser nulo ni contener solo espacios.                     |
| `@Size(max = 100)`    | Restringe la longitud del nombre a un máximo de 100 caracteres.                        |
| `@NotNull`            | El saldo y el banco asociado no pueden ser nulos.                                      |
| `@DecimalMin("0.00")` | Evita valores negativos en el saldo inicial.                                           |
| `@Digits(17, 2)`      | Mantiene la precisión monetaria alineada con la configuración de la entidad `Account`. |
| `@Positive`           | Asegura que el identificador del banco sea mayor que cero.                             |

🧩 `Nota`: Este DTO no expone información sensible ni lógica de negocio; se usa exclusivamente para entrada de datos.

### 🔁 TransactionRequest

DTO que representa una transferencia bancaria entre dos cuentas. Garantiza que ambas cuentas y el monto de la operación
sean válidos antes de procesar la transacción.

````java
public record TransactionRequest(@NotNull(message = "Debe especificarse el ID de la cuenta de origen")
                                 @Positive(message = "El ID de la cuenta origen debe ser un número positivo")
                                 Long sourceAccountId,

                                 @NotNull(message = "Debe especificarse el ID de la cuenta de destino")
                                 @Positive(message = "El ID de la cuenta destino debe ser un número positivo")
                                 Long targetAccountId,

                                 @NotNull(message = "Debe especificar el monto a transferir")
                                 @DecimalMin(value = "0.01", message = "El monto mínimo de transferencia es 0.01")
                                 @Digits(integer = 17, fraction = 2, message = "El monto debe tener hasta 17 dígitos enteros y 2 decimales")
                                 BigDecimal amount) {
}
````

🧠 Consideraciones de negocio

- Las validaciones se realizan a nivel de DTO, antes de ejecutar la transacción.
- En la capa de servicio se deberá comprobar que ambas cuentas pertenezcan al mismo banco.
- El monto no puede ser negativo ni cero.

### 💳 AccountResponse

Representa la información que se devuelve al cliente tras consultar o crear una cuenta.
Contiene solo los datos públicos de la cuenta, sin exponer relaciones completas ni información sensible.

````java
public record AccountResponse(Long id,
                              String holder,
                              BigDecimal balance,
                              String bankName) {
}
````

🔒 Este DTO se utiliza para responder peticiones REST, manteniendo un nivel seguro y controlado de exposición de datos.

### ✏️ AccountUpdateRequest

DTO para actualizar el titular de una cuenta bancaria existente. Mantiene las mismas validaciones del campo holder que
el DTO de creación.

````java
public record AccountUpdateRequest(@NotBlank(message = "El nombre del titular no puede estar vacío")
                                   @Size(max = 100, message = "El nombre del titular no puede superar los 100 caracteres")
                                   String holder) {
}
````

📘 Nota: Este patrón de DTO reducido se usa cuando solo se actualizan campos puntuales.

### 💰 DepositRequest

DTO utilizado para realizar depósitos en una cuenta existente.

````java
public record DepositRequest(@NotNull(message = "Debe especificar el monto a depositar")
                             @DecimalMin(value = "0.01", message = "El monto mínimo es 0.01")
                             @Digits(integer = 17, fraction = 2, message = "El monto debe tener hasta 17 dígitos enteros y 2 decimales")
                             BigDecimal amount) {
}
````

🪙 Se aplica el mismo criterio de precisión que en `Account.balance`, garantizando coherencia entre dominio y capa de
entrada.

### 💸 WithdrawalRequest

DTO utilizado para realizar retiros desde una cuenta bancaria.

````java
public record WithdrawalRequest(@NotNull(message = "Debe especificar el monto a retirar")
                                @DecimalMin(value = "0.01", message = "El monto mínimo es 0.01")
                                @Digits(integer = 17, fraction = 2, message = "El monto debe tener hasta 17 dígitos enteros y 2 decimales")
                                BigDecimal amount) {
}
````

🚨 Las reglas de negocio sobre límites de retiro o fondos insuficientes se manejarán en la capa de servicio.

