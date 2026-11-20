# Sofka QA Automation

Automatización de una compra como invitado en OpenCart y del ciclo de vida de un usuario en Swagger Petstore. El proyecto utiliza Java y Maven Wrapper, con dos módulos independientes: **Serenity BDD con Screenplay** para la interfaz web y **Karate** para la API.

Incluye configuración por ambiente, datos ficticios, validaciones de negocio, limpieza de usuarios, reportes HTML y evidencias de ejecuciones reales.

**Repositorio público:** [SuperShadow38/sofka-qa-automation](https://github.com/SuperShadow38/sofka-qa-automation).

## Alcance

### Ejercicio 1: compra E2E

1. Abrir OpenCart en una sesión nueva y registrar la URL efectiva.
2. Agregar dos productos distintos y disponibles.
3. Comprobar productos y cantidades en el carrito.
4. Completar **Guest Checkout** con datos ficticios.
5. Seleccionar el envío configurado y **Cash On Delivery**.
6. Confirmar la orden y verificar el texto exacto **`Your order has been placed!`**.

Los productos predeterminados son **HTC Touch HD** (`28`) e **iPod Shuffle** (`34`), una unidad de cada uno. Se comprueba su disponibilidad; la prueba no los sustituye automáticamente cuando cambia el inventario.

### Ejercicio 2: ciclo de usuario API

Un escenario secuencial ejecuta las siguientes operaciones sobre Petstore V2:

| Operación | Endpoint | Validación principal |
| --- | --- | --- |
| Crear | `POST /user` | Usuario ficticio con `username` único y respuesta de creación |
| Consultar | `GET /user/{username}` | Esquema y datos persistidos iguales a los enviados |
| Actualizar | `PUT /user/{username}` | Cambiar `firstName` y `email`, conservando la identidad |
| Consultar cambios | `GET /user/{username}` | Datos actualizados persistidos y `username` conservado |
| Eliminar | `DELETE /user/{username}` | Respuesta de eliminación |
| Comprobar ausencia | `GET /user/{username}` | Respuesta `404` con usuario no encontrado |

El hook `afterScenario` intenta eliminar al usuario propio si falla una aserción intermedia y conserva el error original cuando también falla la limpieza.

## Tecnologías y requisitos

| Componente | Versión o requisito |
| --- | --- |
| Java | JDK 21; `JAVA_HOME` configurado y `java` en `PATH` |
| Maven | 3.9.9, descargado por Maven Wrapper |
| Maven Wrapper | 3.3.2 |
| Serenity BDD | 4.2.34 |
| Selenium | BOM 4.38.0 |
| Karate | 1.5.1, grupo Maven `io.karatelabs` |
| Navegador web | Microsoft Edge estable instalado |
| Consola | PowerShell 7 o Bash; `curl` o `wget` para el Wrapper en Bash |
| Control de versiones | Git |

**No es necesario instalar Maven globalmente.** Selenium Manager descarga el driver compatible con Edge. El módulo API no necesita navegador.

La primera ejecución requiere Internet para descargar Maven, dependencias y el driver. También se necesita acceso a OpenCart y Petstore. En redes corporativas, configurar el proxy correspondiente sin desactivar la validación TLS.

## Inicio rápido

Desde la raíz del proyecto, verificar Java y Maven Wrapper:

**PowerShell**

```powershell
java -version
.\mvnw.cmd -version
.\scripts\run.ps1 -Suite all -Label regression
```

**Bash**

```bash
java -version
bash mvnw -version
bash scripts/run.sh all
```

Los scripts ejecutan ambos módulos y archivan la consola, los resultados y los reportes en `evidence/runs/`.

## Ejecución por módulo

### PowerShell

```powershell
# Ejercicio web
.\mvnw.cmd -B -ntp -pl web-tests clean verify

# Ejercicio API y pruebas locales de limpieza
.\mvnw.cmd -B -ntp -pl api-tests clean verify

# Ambos módulos; continuar con el otro si uno falla
.\mvnw.cmd -B -ntp -fae clean verify
```

Para archivar las evidencias de cada ejecución:

```powershell
.\scripts\run.ps1 -Suite web -Label manual
.\scripts\run.ps1 -Suite api -Label manual
.\scripts\run.ps1 -Suite all -Label regression
```

### Bash

```bash
# Ejercicio web
bash mvnw -B -ntp -pl web-tests clean verify

# Ejercicio API y pruebas locales de limpieza
bash mvnw -B -ntp -pl api-tests clean verify

# Ambos módulos
bash mvnw -B -ntp -fae clean verify
```

Para archivar las evidencias:

```bash
bash scripts/run.sh web
bash scripts/run.sh api
bash scripts/run.sh all
```

También es posible seleccionar directamente el POM de un módulo, por ejemplo: `bash mvnw -f api-tests/pom.xml clean verify`.

Usar **`verify`** para ejecutar los escenarios de integración y generar los reportes. `test` ejecuta las pruebas unitarias, pero no los escenarios `*IT` configurados con Failsafe.

## Arquitectura

```text
sofka-qa-automation/
├── .github/workflows/regression.yml
├── .mvn/wrapper/
├── api-tests/
│   ├── pom.xml
│   └── src/test/
│       ├── java/com/sofka/qa/api/
│       └── resources/
│           ├── environments.json
│           ├── karate-config.js
│           └── users/
├── web-tests/
│   ├── pom.xml
│   └── src/test/
│       ├── java/com/sofka/qa/web/
│       │   ├── data/
│       │   ├── questions/
│       │   ├── tasks/
│       │   ├── ui/
│       │   └── GuestPurchaseIT.java
│       └── resources/
│           ├── data/demo.properties
│           └── serenity.conf
├── docs/
├── evidence/
├── scripts/
├── mvnw
├── mvnw.cmd
├── pom.xml
├── readme.txt
└── conclusiones.txt
```

En el módulo web, **Tasks** encapsulan recorridos, **Questions** exponen verificaciones, **Targets** centralizan selectores en `ui/Store.java` y **data** contiene los datos configurables. El escenario JUnit coordina al actor y sus acciones mediante Screenplay.

En el módulo API, los escenarios, payloads, esquemas y limpieza se organizan por recurso. Ambos módulos pueden ejecutarse sin compilar el otro.

## Configuración

### Web

- Navegador y headless: [`serenity.conf`](web-tests/src/test/resources/serenity.conf).
- Productos y comprador: [`demo.properties`](web-tests/src/test/resources/data/demo.properties).
- URL inicial: `http://opencart.abstracta.us/`. En las ejecuciones verificadas, el navegador redirigió a HTTPS; la URL efectiva queda registrada en Serenity.
- Headless activado por defecto.

Ejecutar con navegador visible:

```powershell
.\mvnw.cmd -pl web-tests clean verify '-Dheadless.mode=false'
```

```bash
bash mvnw -pl web-tests clean verify -Dheadless.mode=false
```

Sobrescribir la URL:

```powershell
.\mvnw.cmd -pl web-tests clean verify '-Dweb.base.url=https://opencart.abstracta.us/'
```

```bash
bash mvnw -pl web-tests clean verify -Dweb.base.url=https://opencart.abstracta.us/
```

Cualquier clave de `demo.properties` puede sobrescribirse mediante `-Dclave=valor`. Para un ambiente nuevo, agregar `data/qa.properties` con todas las claves requeridas y ejecutar con `-Denvironment=qa`.

### API

- Ambientes: [`environments.json`](api-tests/src/test/resources/environments.json).
- Configuración y tiempos de espera: [`karate-config.js`](api-tests/src/test/resources/karate-config.js).
- URL predeterminada: `https://petstore.swagger.io/v2`.

```powershell
.\mvnw.cmd -pl api-tests clean verify '-Dapi.base.url=https://petstore.swagger.io/v2'
```

```bash
bash mvnw -pl api-tests clean verify -Dapi.base.url=https://petstore.swagger.io/v2
```

Para seleccionar otro ambiente, agregar su entrada en `environments.json` y usar `-Dkarate.env=qa`.

## Reportes y evidencias

| Resultado | Ruta generada |
| --- | --- |
| Reporte web | `web-tests/target/site/serenity/index.html` |
| Resumen API real | `api-tests/target/karate-reports/karate-summary.html` |
| Detalle API real | `api-tests/target/karate-reports/users.lifecycle.html` |
| Resultados de integración | `<módulo>/target/failsafe-reports/` |
| Tests locales de limpieza | `api-tests/target/surefire-reports/` |
| Detalle de fallos controlados | `api-tests/target/cleanup-verification/` |
| Evidencias archivadas | `evidence/runs/` y `evidence/history/` |

Los scripts copian reportes originales, recursos, capturas y resultados XML a una carpeta por ejecución. Conservan la consola y el comando con su código de salida. PowerShell genera `execution.json`; Bash genera `execution.txt` y usa UTC para el nombre de la carpeta.

**Pruebas locales de limpieza:** `CleanupTest` provoca deliberadamente una aserción fallida contra un servidor HTTP local para verificar dos comportamientos: borrar el usuario después del fallo y preservar el error original si falla la limpieza. Los reportes Karate internos muestran ese fallo intencional; los **dos tests JUnit exteriores deben aprobar**. El reporte del ciclo real de Petstore se encuentra por separado en `karate-reports/`.

Los reportes HTML deben abrirse localmente conservando sus carpetas de recursos. GitHub permite consultar los archivos, pero no renderiza estos reportes como un sitio web. Extraer los ZIP históricos antes de abrirlos.

Consultar el [índice de evidencias](evidence/INDEX.txt), la [matriz de trazabilidad](docs/traceability.csv) y las [conclusiones](conclusiones.txt).

## Resultados verificados

Ejecuciones realizadas el **20 de noviembre de 2026** en Windows, Java 21 y Microsoft Edge 157 headless:

| Comprobación | Resultado |
| --- | --- |
| Compra E2E web | 1 prueba aprobada |
| Ciclo de usuario contra Petstore | 1 escenario aprobado |
| Limpieza ante fallos con servidor local | 2 pruebas aprobadas |
| Ejecución conjunta desde la raíz | Ambos módulos `SUCCESS` |
| Reproducción desde clon local limpio mediante Git Bash | Ambos módulos `SUCCESS`, salida `0` |

La reproducción compiló desde fuentes sin copiar `target` ni herramientas locales. Se reutilizó la caché normal de dependencias y driver del usuario. El detalle está en [`clean-clone.json`](evidence/preflight/clean-clone.json).

Reportes archivados de esa reproducción:

- [Serenity: compra web](evidence/runs/20261120-210607-all-bash/web/serenity/index.html).
- [Karate: ciclo API real](evidence/runs/20261120-210607-all-bash/api/karate-reports/karate-summary.html).

Estos resultados corresponden a las ejecuciones conservadas; no garantizan la disponibilidad futura de los servicios compartidos.

## Integración continua

El [workflow de regresión](.github/workflows/regression.yml) está preparado para GitHub Actions con Windows y Java 21. Se activa por envío a `main`, pull request o ejecución manual.

Ejecuta ambos módulos y conserva las evidencias generadas por ese run durante 14 días, incluso cuando falla la suite. Consultar el estado y los artefactos en [GitHub Actions](https://github.com/SuperShadow38/sofka-qa-automation/actions).

## Consideraciones de ejecución

- Los datos de comprador y usuarios son ficticios; no utilizar información personal ni credenciales reales.
- El checkout utiliza exclusivamente **Cash On Delivery** de demostración. Si esa opción no existe, la prueba se detiene antes de confirmar. No se ingresan tarjetas ni se realizan transferencias.
- El inventario y los servicios son compartidos. Una falta de stock o una caída externa debe investigarse y registrarse como fallo o bloqueo.
- No se reintentan automáticamente compras ni mutaciones de usuarios.
- El contrato Petstore V2 no detalla todas las respuestas exitosas de las mutaciones. Las aserciones validan el `200` y el `ApiResponse` observados en el servidor demo, además de la persistencia mediante consultas.
- `clean` borra los reportes de `target`; usar los scripts para conservarlos en `evidence`.
- No ejecutar dos builds simultáneos sobre el mismo módulo.
- `.tools/`, `target/` y archivos de entorno sensibles están excluidos del repositorio.
- Se verificaron PowerShell y Git Bash en Windows. No se certificaron Linux, macOS ni otros navegadores.

## Evaluación desde GitHub

Para reproducir la entrega como evaluador:

```bash
git clone https://github.com/SuperShadow38/sofka-qa-automation.git sofka-evaluator
cd sofka-evaluator
bash scripts/run.sh all
```

En PowerShell, sustituir el último comando por:

```powershell
.\scripts\run.ps1 -Suite all -Label evaluator
```

## Documentación adicional

- [Instrucciones en texto requeridas por el enunciado](readme.txt).
- [Resultados, hallazgos y limitaciones](conclusiones.txt).
- [Trazabilidad de requisitos](docs/traceability.csv).
- [Fuentes oficiales y decisiones de versiones](docs/references.md).
- [Índice de evidencias](evidence/INDEX.txt).
