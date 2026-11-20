PRUEBA TECNICA SOFKA - AUTOMATIZACION WEB Y API

1. REQUISITOS
JDK 21 (JAVA_HOME apuntando al JDK y java disponible en PATH), Git y acceso
a Internet. Maven 3.9.9 se descarga mediante Maven Wrapper 3.3.2: no instalar
Maven global. Serenity BDD 4.2.34, Selenium BOM 4.38.0, Karate 1.5.1.
Para web: Microsoft Edge estable instalado; Selenium Manager descarga su driver
compatible desde Microsoft. No se incluye un driver específico de una máquina.
API no necesita navegador. PowerShell 7 para los scripts .ps1, o Bash con curl
o wget para mvnw. Comandos ejecutados desde la raíz de este proyecto.

Acceso necesario: repo.maven.apache.org, petstore.swagger.io,
opencart.abstracta.us, msedgedriver.microsoft.com y servicios de Microsoft para
resolver la versión de Edge. El primer arranque requiere descargar dependencias.
Si una red corporativa las bloquea, configurar su proxy de Maven/Selenium.
No desactivar la validación TLS. No usar credenciales o datos personales reales.

2. COMANDOS POWERSHELL
java -version
.\mvnw.cmd -version

Ejercicio 1:
.\mvnw.cmd -B -ntp -pl web-tests clean verify

Ejercicio 2 (incluye dos comprobaciones locales de limpieza y el ciclo real):
.\mvnw.cmd -B -ntp -pl api-tests clean verify

Ambos, continuando con el otro módulo aunque uno falle:
.\mvnw.cmd -B -ntp -fae clean verify

También desde un módulo sin compilar el otro:
.\mvnw.cmd -f web-tests/pom.xml clean verify
.\mvnw.cmd -f api-tests/pom.xml clean verify

Ejecutar y archivar consola, código de salida y reportes:
.\scripts\run.ps1 -Suite web -Label manual
.\scripts\run.ps1 -Suite api -Label manual
.\scripts\run.ps1 -Suite all -Label regression

3. COMANDOS BASH
java -version
bash mvnw -version
bash mvnw -B -ntp -pl web-tests clean verify
bash mvnw -B -ntp -pl api-tests clean verify
bash mvnw -B -ntp -fae clean verify
bash mvnw -f web-tests/pom.xml clean verify
bash mvnw -f api-tests/pom.xml clean verify
bash scripts/run.sh web
bash scripts/run.sh api
bash scripts/run.sh all

4. CONFIGURACION
Web: web-tests/src/test/resources/serenity.conf configura Edge y headless.
Los datos están en web-tests/src/test/resources/data/demo.properties.
Cada clave de datos puede sobrescribirse mediante -Dclave=valor.
Por defecto: productos 28 HTC Touch HD y 34 iPod Shuffle, una unidad cada uno;
se validan nombre, disponibilidad y carrito, sin sustituir silenciosamente productos.
URL inicial HTTP del enunciado: se registra la URL efectiva HTTPS en el reporte.
Checkout usa Flat Rate y exclusivamente Cash On Delivery, con datos ficticios.
Si no está disponible el pago de demostración, falla antes de confirmar la orden.
No se ingresan tarjetas, no se ejecutan transferencias ni pagos reales.

PowerShell, navegador visible:
.\mvnw.cmd -pl web-tests clean verify '-Dheadless.mode=false'

Bash, navegador visible:
bash mvnw -pl web-tests clean verify -Dheadless.mode=false

Cambiar URL (PowerShell; en Bash sustituir .\mvnw.cmd por bash mvnw):
.\mvnw.cmd -pl web-tests clean verify '-Dweb.base.url=https://opencart.abstracta.us/'

Nuevo ambiente web: agregar data/qa.properties con todas las claves del ejemplo
y ejecutar con -Denvironment=qa. Los selectores se mantienen en ui/Store.java;
Tasks contienen recorridos, Questions verificaciones, data datos configurables.
No se usa POM: la última solicitud exige Screenplay. JUnit describe el escenario;
no se agrega una capa Cucumber innecesaria.

API: api-tests/src/test/resources/environments.json y karate-config.js.
Base utilizada: https://petstore.swagger.io/v2. Agregar un ambiente en JSON y
usar -Dkarate.env=qa, o sobrescribir directamente:
.\mvnw.cmd -pl api-tests clean verify '-Dapi.base.url=https://petstore.swagger.io/v2'
bash mvnw -pl api-tests clean verify -Dapi.base.url=https://petstore.swagger.io/v2

Username único con UUID y firstName interpretado como nombre a actualizar.
Se conserva username e id y se comprueban todos los datos enviados en cada GET.
El contrato Swagger no especifica todos los códigos/cuerpos exitosos: las
mutaciones comprueban el 200 y ApiResponse observados en el servidor demo.
No se reintentan automáticamente compras ni mutaciones API.

5. REPORTES Y TRAZABILIDAD
Web: web-tests/target/site/serenity/index.html
API real: api-tests/target/karate-reports/karate-summary.html
Detalle API: api-tests/target/karate-reports/users.lifecycle.html
Resultados integración: cada módulo/target/failsafe-reports/
Pruebas locales de limpieza: api-tests/target/surefire-reports/
Detalle de fallos controlados: api-tests/target/cleanup-verification/
Matriz: docs/traceability.csv. Fuentes: docs/references.md.

Los dos tests CleanupTest provocan deliberadamente un nombre incorrecto en un
servidor HTTP local: comprueban que se elimina el usuario y que, si la limpieza
también falla, se conserva la aserción original. Esos reportes internos Karate
muestran FAILURE intencional; los dos tests JUnit deben estar PASS. No sustituyen
el ciclo contra Petstore real. El reporte de regresión real está en karate-reports.

Los scripts copian los reportes originales (incluidos recursos, JSON y capturas)
a evidence/runs/<fecha>-<suite>-<etiqueta>/. Cada ejecución tiene consola y
metadatos con el comando y código de salida. Revisar evidence/INDEX.txt para
identificar las ejecuciones finales y los intentos de diagnóstico.
Maven devuelve código distinto de cero ante fallos; Failsafe permite generar
Serenity antes de fallar verify. -fae permite ejecutar ambos módulos.
No ejecutar dos builds simultáneos sobre el mismo módulo: clean borra target.
Los reportes generados en target se sobrescriben con clean; evidence se conserva.
El workflow de GitHub Actions usa Windows/Java 21 y archiva evidence aunque falle.

6. REPOSITORIO PUBLICO
Publicado con autorización explícita del propietario en:
https://github.com/SuperShadow38/sofka-qa-automation
Los commits usan SuperShadow38 y su correo noreply de GitHub.
El remoto origin está configurado en la entrega local.
Para publicar una copia en otro repositorio, crear uno público vacío, sin README.
Revisar antes de publicar que solo existan datos ficticios y las evidencias deseadas.
La entrega ya contiene commits locales para verificar un clon limpio. git init
es solo necesario si se recibe una copia sin .git; omitirlo en el repo existente.
Si git status no muestra cambios pendientes, omitir también git add/commit:
la entrega ya está versionada localmente.
Desde esta carpeta (los comandos Git son iguales en PowerShell y Bash):

git init -b main
git add .
git diff --cached --stat
git status --short
git commit -m "Implement web and API automation with reproducible evidence"
git remote add origin https://github.com/USUARIO/REPOSITORIO.git
git push -u origin main

Sustituir USUARIO y REPOSITORIO y omitir remote add si origin ya está definido.
No subir .tools, target, .env ni credenciales.
No incluir el PDF del enunciado si no se dispone de permiso para redistribuirlo.

7. COMPROBAR COMO EVALUADOR DESDE UN CLON LIMPIO
En otra carpeta:
git clone https://github.com/SuperShadow38/sofka-qa-automation.git sofka-evaluator
cd sofka-evaluator

PowerShell:
.\mvnw.cmd -version
.\scripts\run.ps1 -Suite all -Label evaluator

Bash:
bash mvnw -version
bash scripts/run.sh all

Esperado: ambos módulos SUCCESS; un E2E web, un ciclo API y dos tests locales
de limpieza aprobados. Abrir los reportes HTML, verificar confirmación exacta y
secuencia API. Un fallo del servicio externo debe investigarse y conservarse,
no transformarse en PASS. Las órdenes demo consumen stock del entorno compartido.

La reproducción local desde un clon limpio pasó con Git Bash en Windows y se
documenta en conclusiones.txt y evidence/preflight/clean-clone.json;
El workflow remoto y sus artefactos pueden consultarse en:
https://github.com/SuperShadow38/sofka-qa-automation/actions
No se promete estabilidad del servicio compartido.
