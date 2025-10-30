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

