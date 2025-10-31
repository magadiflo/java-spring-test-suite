# 🚀 Integración de SonarQube con Spring Boot

- 📚 **Fuente base**: Tutorial del canal [Un Programador Nace](https://www.youtube.com/watch?v=H7XvwUMaCno)
- 💻 **Proyecto base**: `spring-rest-api`, parte del conjunto de proyectos `java-spring-test-suite`.

---

Antes de integrar `SonarQube` con nuestro proyecto `Spring Boot`, exploraremos una herramienta muy útil a nivel de
IDE que complementa el análisis estático de código: `SonarQube for IDE` (antes `SonarLint`).

## 🧩 Plugin SonarQube for IDE (Antes SonarLint)

`SonarQube for IDE`, anteriormente conocido como `SonarLint`, es una extensión oficial gratuita de `Sonar` que realiza
`análisis estático de código en tiempo real` directamente desde el IDE.

Su propósito principal es `mejorar la calidad y seguridad del código desde el momento en que se escribe`,
antes incluso de que llegue al repositorio o sea analizado por el servidor de `SonarQube`.

### ⚙️ Características clave

- Analiza el código fuente a medida que lo escribes.
- Detecta code smells, bugs, vulnerabilidades y malas prácticas.
- Ofrece quick fixes y sugerencias con contexto técnico detallado.
- Explica el "por qué" de cada problema, actuando como un tutor técnico.
- Puede conectarse a un servidor `SonarQube local` o en la nube (`SonarCloud`) para sincronizar reglas y políticas de
  calidad.
- Compatible con múltiples lenguajes: Java, Kotlin, C#, JavaScript, TypeScript, Python, Go, PHP, HTML, CSS, entre otros.

> 💡 `En entornos corporativos`, esta herramienta permite mantener consistencia en las reglas de calidad, facilitar
> revisiones de código y detectar vulnerabilidades antes de subir cambios al repositorio.

### 🛠️ Instalación del plugin SonarQube for IDE

Instalemos el plugin desde `IntelliJ IDEA`:

1. Abre `File → Settings → Plugins`.
2. Ve a la pestaña `Marketplace`.
3. Busca `SonarQube for IDE`.
4. Haz clic en `Install`.
5. Reinicia el IDE para activar el plugin.

En la siguiente imagen, se muestra el plugin correctamente instalado:

![01.png](assets/01.png)

## 🎯 Funcionamiento de SonarQube for IDE

Una vez instalado, el plugin se `activa automáticamente`. El análisis puede ejecutarse de dos formas:

- 🕒 `Automático`: cada vez que escribes o modificas código.
- ⚡ `Manual`: usando combinaciones de teclas, el menú contextual o la consola interna de `SonarQube for IDE`.

Veamos un ejemplo práctico.

Supongamos que abrimos el controlador `BankController` de nuestro proyecto y modificamos el método de un endpoint
para usar un tipo comodín `(?)` en la respuesta. Al guardar, el plugin analiza el código y muestra en la esquina
superior derecha un ícono rojo con un número, indicando la cantidad de incidencias detectadas.

![02.png](assets/02.png)

Si hacemos clic en el ícono rojo, se abrirá una consola en la parte inferior del IDE mostrando los detalles de los
hallazgos. En este caso, `Sonar` detecta una violación de regla en la `línea 21`:

> ⚠️ “No se deben usar tipos comodín genéricos en los tipos de retorno.”

Esto significa que usar `ResponseEntity<?>` puede reducir la legibilidad y la claridad del contrato del endpoint.
La práctica recomendada sería definir un tipo concreto o una clase DTO específica.

### 💡 Nota práctica

En entornos profesionales, las empresas suelen combinar el uso de `SonarQube for IDE` con una instancia central de
`SonarQube Server`. Esto permite:

- Aplicar reglas de calidad homogéneas en todo el equipo.
- Evitar “sorpresas” en el análisis del pipeline CI/CD.
- Mejorar la consistencia del código antes de los code reviews.

Por tanto, `SonarQube for IDE actúa como la primera línea de defensa` en la calidad del código, detectando errores
antes de que lleguen al servidor o repositorio.

### 🎯 Próximo paso

Ya entendemos el propósito del plugin y su rol en el desarrollo diario. A continuación, procederemos con la integración
de `SonarQube` + `Spring Boot`, que es el objetivo central de este tutorial.

---

# 🧠 ¿Qué es SonarQube?

`SonarQube` es una plataforma `open-source` para el `análisis estático de código` desarrollada por `SonarSource` que
ayuda a los equipos de desarrollo a `mejorar la calidad, seguridad y mantenibilidad del software`.

Su función principal es `analizar el código fuente` de un proyecto y generar un `informe de calidad` con métricas,
advertencias y recomendaciones sobre aspectos como:

- 🪲 `Bugs (errores potenciales)`: fragmentos de código que podrían causar fallos en tiempo de ejecución.
- ⚔️ `Vulnerabilidades`: posibles brechas de seguridad en el código.
- 🌫️ `Code Smells`: malas prácticas que afectan la legibilidad o mantenibilidad.
- 🧱 `Duplicaciones`: bloques de código repetidos que dificultan el mantenimiento.
- 🧪 `Coverage`: porcentaje de cobertura de pruebas unitarias e integración.
- 🧩 `Debt Ratio (deuda técnica)`: tiempo estimado para corregir las malas prácticas detectadas.

### 💡 ¿Por qué las empresas utilizan SonarQube?

En entornos empresariales, `SonarQube` se convierte en un `componente clave del ciclo de integración continua (CI/CD)`.
Permite mantener estándares de calidad de código homogéneos entre los equipos y garantizar que las nuevas versiones
del software no introduzcan errores ni vulnerabilidades.

Usos típicos en empresas:

1. 🔍 Se ejecuta automáticamente durante el `build` del proyecto (por ejemplo, en `Jenkins`, `GitLab CI` o
   `GitHub Actions`).
2. 📦 El servidor `SonarQube` recibe los resultados del análisis.
3. 📈 Se generan `dashboards` con métricas de calidad y umbrales `(Quality Gates)`.
4. 🚫 Si el código no cumple los estándares definidos, el pipeline puede fallar antes del despliegue.
5. 💬 En otras palabras, `SonarQube` actúa como un guardia de calidad continua, previniendo que código inseguro o mal
   estructurado llegue a producción.

### ⚙️ Arquitectura básica de SonarQube

Un entorno típico de SonarQube consta de tres componentes principales:

| Componente                              | Descripción                                                                                                                              |
|-----------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| 🧩 **SonarQube Server**                 | Servidor principal que recibe los resultados del análisis, los almacena en una base de datos y muestra los reportes en una interfaz web. |
| 🧠 **SonarScanner**                     | Cliente o agente que analiza el código y envía los resultados al servidor. Se ejecuta localmente o dentro de un pipeline CI/CD.          |
| 🧑‍💻 **SonarQube for IDE (SonarLint)** | Plugin del IDE que permite analizar el código en tiempo real mientras se escribe.                                                        |

### 🎯 Beneficios clave

- ✅ Mejora la calidad del código desde etapas tempranas.
- ✅ Reduce la deuda técnica acumulada en proyectos grandes.
- ✅ Promueve estándares de desarrollo uniformes en toda la organización.
- ✅ Facilita auditorías de seguridad y cumplimiento (por ejemplo, OWASP, CWE, etc.).
- ✅ Se integra fácilmente con herramientas empresariales como Jenkins, GitLab CI/CD, GitHub Actions o Azure DevOps.

### 💡 Conclusión

`SonarQube` no solo es una herramienta de análisis, sino un `ecosistema completo de calidad de software`.
Ayuda a los equipos a mantener un código `limpio, seguro y sostenible`, alineando la práctica técnica diaria con las
políticas de calidad de la empresa.

## [🐳 Configurar Docker Compose para SonarQube](https://docs.sonarsource.com/sonarqube/latest/setup-and-upgrade/install-the-server/installing-sonarqube-from-docker/)

> 📌 Parte de esta configuración se basa en la documentación oficial de `SonarQube`, en el apartado
> [Installing SonarQube Server Developer or Enterprise Edition from the Docker image](https://docs.sonarsource.com/sonarqube/latest/setup-and-upgrade/install-the-server/installing-sonarqube-from-docker/),
> con ajustes adicionales para adaptarla a un entorno de desarrollo profesional.

Antes de integrar `SonarQube` con `Spring Boot`, debemos levantar su entorno de ejecución. Para ello, utilizaremos
`Docker Compose`, que nos permitirá orquestar fácilmente tanto el servicio de `SonarQube` como su base de datos
`PostgreSQL`.

### 📁 Estructura del archivo

Crearemos un archivo llamado `compose.yml` dentro del directorio `spring-sonarqube/docker`.
Este archivo definirá los servicios y volúmenes necesarios para que `SonarQube` funcione correctamente.

Este archivo define dos servicios:

- `s-sonarqube`: servidor `SonarQube`.
- `s-postgres-sonar`: base de datos `PostgreSQL`.

````yml
services:
  s-sonarqube:
    image: sonarqube:25.10.0.114319-community
    container_name: c-sonarqube
    restart: unless-stopped
    ports:
      - '9000:9000'
    environment:
      SONAR_JDBC_URL: jdbc:postgresql://s-postgres-sonar:5432/db_sonar_dev
      SONAR_JDBC_USERNAME: sonar_dev_user
      SONAR_JDBC_PASSWORD: sonar_dev_password
    volumes:
      - sonarqube_data:/opt/sonarqube/data
      - sonarqube_extensions:/opt/sonarqube/extensions
      - sonarqube_logs:/opt/sonarqube/logs
    networks:
      - sonarqube-net
    depends_on:
      - s-postgres-sonar

  s-postgres-sonar:
    image: postgres:17-alpine
    container_name: c-postgres-sonar
    restart: unless-stopped
    ports:
      - '5433:5432'
    environment:
      POSTGRES_DB: db_sonar_dev
      POSTGRES_USER: sonar_dev_user
      POSTGRES_PASSWORD: sonar_dev_password
    volumes:
      - postgres-sonar-data:/var/lib/postgresql/data
    networks:
      - sonarqube-net

volumes:
  sonarqube_data:
    name: sonarqube_data
  sonarqube_extensions:
    name: sonarqube_extensions
  sonarqube_logs:
    name: sonarqube_logs
  postgres-sonar-data:
    name: postgres-sonar-data

networks:
  sonarqube-net:
    name: sonarqube-net
````

### ⚙️ Explicación técnica de la configuración

| Componente                                  | Descripción                                                                                                                       |
|---------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| `image: sonarqube:25.10.0.114319-community` | Usa la versión estable más reciente de SonarQube Community Edition.                                                               |
| `ports: 9000:9000`                          | Expone `SonarQube` en el puerto 9000. Accederás en `http://localhost:9000` (usuario y contraseña por defecto: `admin` / `admin`). |
| `SONAR_JDBC_URL`                            | URL JDBC para conectar SonarQube con PostgreSQL.                                                                                  |
| `SONAR_JDBC_USERNAME`                       | Usuario de la base de datos.                                                                                                      |
| `SONAR_JDBC_PASSWORD`                       | Contraseña de la base de datos.                                                                                                   |
| `depends_on`                                | Garantiza que la base de datos se levante antes que SonarQube.                                                                    |
| `restart: unless-stopped`                   | Reinicia el contenedor si falla, excepto si fue detenido manualmente.                                                             |

#### 🗄️ Configuración de la base de datos

> `SonarQube` no debe usarse con `H2` en producción. En entornos empresariales siempre se recomienda `PostgreSQL`,
> ya que es el único motor oficialmente soportado para producción.

### 📦 Volúmenes persistentes

Estos volúmenes permiten mantener datos entre reinicios o actualizaciones:

| Volumen                | Propósito                                                       |
|------------------------|-----------------------------------------------------------------|
| `sonarqube_data`       | Índices de Elasticsearch, configuraciones y datos de análisis.  |
| `sonarqube_logs`       | Logs de acceso, procesos web, CE y Elasticsearch.               |
| `sonarqube_extensions` | Plugins instalados y drivers JDBC (como Oracle si se requiere). |
| `postgres-sonar-data`  | atos persistentes de la base de datos PostgreSQL.               |

> 🧠 `Nota técnica`: En entornos corporativos, estos volúmenes son clave para mantener la integridad del sistema
> ante actualizaciones o migraciones.

### ✅ Buenas prácticas corporativas

- 🔒 `PostgreSQL como base de datos`: Es la opción recomendada por `SonarQube` y ampliamente usada en empresas por su
  estabilidad y soporte oficial.
- 📁 `Separación de volúmenes`: Facilita backups, restauraciones y actualizaciones sin pérdida de datos.
- 🧩 `Uso de Alpine en PostgreSQL`: Reduce el tamaño de la imagen y mejora tiempos de despliegue.

## 🚀 Levantando el entorno SonarQube con Docker Compose

Una vez definido el archivo `compose.yml`, podemos levantar los contenedores de `SonarQube` y `PostgreSQL` ejecutando
el siguiente comando desde la raíz del proyecto o asegurándonos de apuntar correctamente a la ruta donde se encuentra
el `compose.yml`:

````bash
D:\programming\spring\01.udemy\02.andres_guzman\03.junit_y_mockito_2023\java-spring-test-suite (feature/spring-sonarqube)
$ docker compose -f .\spring-sonarqube\docker\compose.yml up -d                                                          
...
 ✔ Network sonarqube-net        Created                                                                                  
 ✔ Volume sonarqube_extensions  Created                                                                                  
 ✔ Volume sonarqube_logs        Created                                                                                  
 ✔ Volume postgres-sonar-data   Created                                                                                  
 ✔ Volume sonarqube_data        Created                                                                                  
 ✔ Container c-postgres-sonar   Started                                                                                  
 ✔ Container c-sonarqube        Started                                                                                   
````

Este comando:

- Descarga las imágenes necesarias (sonarqube y postgres).
- Crea la red y los volúmenes definidos.
- Inicia ambos contenedores en segundo plano (-d = detached mode)

### 🧠 Verificando contenedores activos

Podemos confirmar que ambos servicios se están ejecutando con el siguiente comando:

````bash
$ docker container ls -a
CONTAINER ID   IMAGE                                COMMAND                  CREATED         STATUS             PORTS                                         NAMES
5ad90230cf61   sonarqube:25.10.0.114319-community   "/opt/sonarqube/dock…"   3 minutes ago   Up 3 minutes       0.0.0.0:9000->9000/tcp, [::]:9000->9000/tcp   c-sonarqube
c02d7529f0cb   postgres:17-alpine                   "docker-entrypoint.s…"   3 minutes ago   Up 3 minutes       0.0.0.0:5433->5432/tcp, [::]:5433->5432/tcp   c-postgres-sonar
````

### 🧩 Verificando la base de datos y sus tablas

Para confirmar que la base de datos fue creada correctamente dentro del contenedor de `PostgreSQL`, podemos acceder
a él directamente:

````bash
D:\programming\spring\01.udemy\02.andres_guzman\03.junit_y_mockito_2023\java-spring-test-suite (feature/spring-sonarqube)
$ docker container exec -it c-postgres-sonar /bin/sh
/ # psql -U sonar_dev_user -d db_sonar_dev
psql (17.5)
Type "help" for help.

db_sonar_dev=# \l
                                                               List of databases
     Name     |     Owner      | Encoding | Locale Provider |  Collate   |   Ctype    | Locale | ICU Rules |         Access privileges
--------------+----------------+----------+-----------------+------------+------------+--------+-----------+-----------------------------------
 db_sonar_dev | sonar_dev_user | UTF8     | libc            | en_US.utf8 | en_US.utf8 |        |           |
 postgres     | sonar_dev_user | UTF8     | libc            | en_US.utf8 | en_US.utf8 |        |           |
 template0    | sonar_dev_user | UTF8     | libc            | en_US.utf8 | en_US.utf8 |        |           | =c/sonar_dev_user                +
              |                |          |                 |            |            |        |           | sonar_dev_user=CTc/sonar_dev_user
 template1    | sonar_dev_user | UTF8     | libc            | en_US.utf8 | en_US.utf8 |        |           | =c/sonar_dev_user                +
              |                |          |                 |            |            |        |           | sonar_dev_user=CTc/sonar_dev_user
(4 rows)

db_sonar_dev=# \dt
                      List of relations
 Schema |           Name            | Type  |     Owner
--------+---------------------------+-------+----------------
 public | active_rule_parameters    | table | sonar_dev_user
 public | active_rules              | table | sonar_dev_user
 public | alm_pats                  | table | sonar_dev_user
 ...
 public | user_dismissed_messages   | table | sonar_dev_user
 public | user_roles                | table | sonar_dev_user
 public | user_tokens               | table | sonar_dev_user
 public | users                     | table | sonar_dev_user
 public | webhook_deliveries        | table | sonar_dev_user
 public | webhooks                  | table | sonar_dev_user
(109 rows)

db_sonar_dev=# 
````

En este punto, `SonarQube` ya ha inicializado su esquema interno en `PostgreSQL`, creando más de 100 tablas que
almacenan reglas, análisis, configuraciones de proyectos, usuarios, tokens, entre otros. Esto confirma que la conexión
entre `SonarQube` y `PostgreSQL` está funcionando correctamente.

### 🖥️ Visualización desde DBeaver (opcional)

También podemos verificar la estructura de la base de datos conectándonos desde `DBeaver`
(o cualquier cliente SQL) usando las credenciales definidas en el `compose.yml`:

![03.png](assets/03.png)

💡 Consejo profesional:
> En entornos empresariales se acostumbra conectar herramientas de administración como `pgAdmin` o `DBeaver` a
> contenedores `PostgreSQL` para validaciones rápidas, debugging y auditoría de integraciones.

## 🌐 Accediendo a SonarQube

Una vez que los contenedores de `SonarQube` y `PostgreSQL` estén en ejecución, podemos acceder a la interfaz web de
`SonarQube` desde nuestro navegador.

🔗 URL de acceso: http://localhost:9000

> 💡 `Recordatorio`: Este puerto (`9000`) fue configurado previamente en el archivo `compose.yml`, dentro del mapeo
> `9000:9000`.

### 🔑 Inicio de sesión inicial

Al ingresar por primera vez, `SonarQube` mostrará una pantalla de autenticación solicitando usuario y contraseña.
Las credenciales por defecto son:

| Campo      | Valor por defecto |
|------------|-------------------|
| Usuario    | `admin`           |
| Contraseña | `admin`           |

![04.png](assets/04.png)

### 🔐 Cambio obligatorio de contraseña

Por razones de seguridad, `SonarQube` nos solicitará actualizar la contraseña del usuario `admin` en el primer acceso.
En este ejemplo, definimos una nueva contraseña segura:

| Campo      | Valor por defecto    |
|------------|----------------------|
| Usuario    | `admin`              |
| Contraseña | `$onarQubeAdmin2025` |

![05.png](assets/05.png)

⚙️ Explicación técnica:

> `SonarQube` almacena las credenciales de usuarios en la base de datos `PostgreSQL` y encripta las contraseñas usando
> `BCrypt`. Por política interna, no permite continuar usando la contraseña por defecto en instancias nuevas para
> evitar accesos no autorizados.

### 🏠 Pantalla principal de SonarQube

Una vez actualizada la contraseña, seremos redirigidos al `Dashboard principal`, donde podremos:

- Crear o importar proyectos.
- Ver resultados de análisis de calidad.
- Configurar reglas de análisis, plugins y políticas de seguridad.

![06.png](assets/06.png)

## 🔐 Generando un Token de Seguridad para el acceso desde Spring Boot a SonarQube

Para que nuestro proyecto `Spring Boot` pueda autenticarse ante el servidor de `SonarQube`, debemos generar un
`token de seguridad`. Este token sustituye el uso de `usuario/contraseña` y es el mecanismo estándar de autenticación
cuando integramos `SonarQube` con herramientas externas (por ejemplo, `Maven`, `Jenkins` o `GitHub Actions`).

### 👤 Accediendo a la configuración de usuario

Desde la interfaz principal de `SonarQube`, hacemos clic en nuestro perfil (parte superior derecha) y seleccionamos:
`My Account → Security`.

![07.png](assets/07.png)

### ⚙️ Creación del token

En la pestaña `Security`, completamos los siguientes campos:

| Campo          | Valor sugerido          | Descripción                                                                      |
|----------------|-------------------------|----------------------------------------------------------------------------------|
| **Name**       | `local-analysis`        | Nombre identificador del token (puede ser cualquier alias descriptivo).          |
| **Type**       | `Global Analysis Token` | Permite ejecutar análisis sobre cualquier proyecto asociado al usuario.          |
| **Expires in** | `No expiration`         | En este ejemplo no tendrá fecha de expiración, ya que se usará en entorno local. |

Luego hacemos clic en el botón `Generate`.

![08.png](assets/08.png)

### 📋 Copiando el token generado

`SonarQube` mostrará un mensaje como el siguiente:

> ✅ “Se ha creado un nuevo token llamado `local-analysis`. ¡Asegúrate de copiarlo ahora, no podrás volver a verlo!”

Esto significa que el token solo se muestra una vez al momento de su creación. Cópialo y guárdalo de forma segura,
ya que no podrá recuperarse después.

````
Token: sqa_a0bc5503b5942ddf4739558c59bbb35b66869a66 
````

![09.png](assets/09.png)

⚠️ Importante:
> Nunca compartas este token en repositorios públicos, documentación compartida o pipelines sin cifrado.
> Cualquier persona que posea el token puede ejecutar análisis o modificar configuraciones en tu servidor `SonarQube`.

## 🧩 Configurando el Plugin JaCoCo para medir la cobertura de código

Antes de integrar `SonarQube` con nuestra aplicación `Spring Boot`, es fundamental configurar correctamente el
`plugin de JaCoCo`, ya que este será el encargado de `generar los reportes de cobertura de pruebas unitarias` que
`SonarQube` utilizará para sus `análisis de calidad`.

> 💡 `JaCoCo (Java Code Coverage)` es una herramienta open source que mide qué porcentaje del código ha sido ejecutado
> durante las pruebas. Nos ayuda a identificar clases o métodos no cubiertos por tests y es el estándar más usado junto
> con `SonarQube`.

### ⚙️ Configuración del plugin en el `pom.xml`

Agregamos la siguiente configuración dentro del archivo `pom.xml` de nuestro proyecto:

````xml

<project>
    <properties>
        <!-- Versión del plugin JaCoCo -->
        <jacoco.version>0.8.12</jacoco.version>
    </properties>
    <build>
        <plugins>
            <!--JaCoCo Maven Plugin-->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>${jacoco.version}</version>
                <configuration>
                    <excludes>
                        <!-- Clases de arranque -->
                        <exclude>**/SpringSonarQubeApplication.class</exclude>

                        <!-- DTOs y entidades JPA -->
                        <exclude>**/dto/**</exclude>
                        <exclude>**/entity/**</exclude>

                        <!-- Mappers y configuraciones -->
                        <exclude>**/mapper/**</exclude>
                        <exclude>**/config/**</exclude>

                        <!-- Excepciones personalizadas -->
                        <exclude>**/exception/*Exception.class</exclude>

                        <!-- Clases generadas automáticamente -->
                        <exclude>**/*MapperImpl.class</exclude>
                    </excludes>
                </configuration>
                <executions>
                    <!--Preparar agente de JaCoCo antes de ejecutar tests-->
                    <execution>
                        <id>prepare-agent</id>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>

                    <!--Generar reporte después de ejecutar tests-->
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>

                    <!-- Define un umbral mínimo de cobertura aceptable -->
                    <execution>
                        <id>check</id>
                        <goals>
                            <goal>check</goal>
                        </goals>
                        <configuration>
                            <rules>
                                <rule>
                                    <element>PACKAGE</element>
                                    <limits>
                                        <limit>
                                            <counter>LINE</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.80</minimum> <!-- 80% cobertura mínima -->
                                        </limit>
                                    </limits>
                                </rule>
                            </rules>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <!--/JaCoCo Maven Plugin-->
        </plugins>
    </build>

</project>
````

### 📂 Ubicación del reporte

Por defecto, `JaCoCo` genera los resultados en:

````
target/site/jacoco/index.html     ← Reporte HTML
target/site/jacoco/jacoco.xml     ← Reporte XML (versiones recientes)
target/jacoco.exec                ← Reporte binario (para SonarQube)
````

> 🔎 `Tip`: Podemos abrir el archivo `index.html` en el navegador para visualizar de forma gráfica qué clases y métodos
> fueron cubiertos por los tests.

### 💡 Conexión con SonarQube

`SonarQube` leerá estos reportes de `JaCoCo` automáticamente al ejecutar:

````bash
$ mvn clean verify sonar:sonar
````

Siempre que en nuestra configuración de `Sonar` (por ejemplo, en `sonar-project.properties`) hayamos definido la
propiedad:

````properties
sonar.jacoco.reportPaths=target/jacoco.exec
````

o en versiones más recientes:

````properties
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
````

## ✅ Ejecutando pruebas unitarias y validando cobertura con JaCoCo

Una vez configurado el plugin de `JaCoCo`, procedemos a ejecutar nuestras pruebas unitarias para generar los
reportes de cobertura.

### ▶️ Ejecución de pruebas unitarias

Ejecutamos el siguiente comando:

````bash
D:\programming\spring\01.udemy\02.andres_guzman\03.junit_y_mockito_2023\java-spring-test-suite\spring-sonarqube (feature/spring-sonarqube)
$ mvn clean test
[INFO] Scanning for projects...
...
[INFO]
[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO]
[INFO] --- jacoco:0.8.12:report (report) @ spring-sonarqube ---
[INFO] Loading execution data file D:\programming\spring\01.udemy\02.andres_guzman\03.junit_y_mockito_2023\java-spring-test-suite\spring-sonarqube\target\jacoco.exec
[INFO] Analyzed bundle 'spring-sonarqube' with 4 classes
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  18.799 s
[INFO] Finished at: 2025-10-31T17:07:00-05:00
[INFO] ------------------------------------------------------------------------
````

El resultado muestra que todas las pruebas se ejecutaron correctamente y que `JaCoCo` generó los archivos de reporte
correspondientes (`.exec`, `.xml` y `.html`).

### 📊 Verificando cobertura mínima con Maven

Para validar el porcentaje de cobertura definido en el plugin (`<minimum>0.80</minimum>`), ejecutamos:

````bash
D:\programming\spring\01.udemy\02.andres_guzman\03.junit_y_mockito_2023\java-spring-test-suite\spring-sonarqube (feature/spring-sonarqube)
$ mvn clean verify
[INFO] Scanning for projects...
[INFO]
...
[INFO]
[INFO] --- jacoco:0.8.12:check (check) @ spring-sonarqube ---
[INFO] Loading execution data file D:\programming\spring\01.udemy\02.andres_guzman\03.junit_y_mockito_2023\java-spring-test-suite\spring-sonarqube\target\jacoco.exec
[INFO] Analyzed bundle 'spring-sonarqube' with 4 classes
[WARNING] Rule violated for package dev.magadiflo.app.exception: lines covered ratio is 0.28, but expected minimum is 0.80
[WARNING] Rule violated for package dev.magadiflo.app.controller: lines covered ratio is 0.55, but expected minimum is 0.80
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  18.779 s
[INFO] Finished at: 2025-10-31T17:08:05-05:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.jacoco:jacoco-maven-plugin:0.8.12:check (check) on project spring-sonarqube: Coverage checks have not been met. See log for details. -> [Help 1]
[ERROR]
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoExecutionException
````

Durante esta fase, `JaCoCo` aplicará las reglas de cobertura configuradas en el bloque `<check>` del `pom.xml`.
En este caso, vemos que dos paquetes no alcanzaron el umbral mínimo del `80%`:

- `dev.magadiflo.app.exception`: `28%`
- `dev.magadiflo.app.controller`: `55%`

