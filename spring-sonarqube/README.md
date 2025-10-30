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

