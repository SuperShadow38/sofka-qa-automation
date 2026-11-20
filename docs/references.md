# Fuentes y versiones

- Screenplay Tasks, Targets, WaitUntil y preguntas:
  https://serenity-bdd.github.io/docs/screenplay/screenplay_webdriver
- Serenity 4.2.34: https://repo.maven.apache.org/maven2/net/serenity-bdd/serenity-core/4.2.34/
- Karate 1.5.1, documentación versionada (JUnit5, match, afterScenario, call):
  https://github.com/karatelabs/karate/tree/v1.5.1
- Karate 1.5.1 se publica bajo io.karatelabs, no com.intuit.karate:
  https://repo.maven.apache.org/maven2/io/karatelabs/karate-junit5/1.5.1/
- Maven Wrapper: https://maven.apache.org/wrapper/
- Failsafe y fases: https://maven.apache.org/surefire/maven-failsafe-plugin/usage.html
- Contrato Petstore V2: https://petstore.swagger.io/v2/swagger.json

Java 21 LTS. Se fijan versiones disponibles y compatibles; no se persigue la
última versión mayor. No se mezclan Karate V2 y su documentación con Karate 1.5.1.
POST user define éxito como default; PUT y DELETE omiten un código de éxito.
Se validará 200 y ApiResponse del servidor de demostración como comportamiento
observado, separado de las garantías incompletas del contrato Swagger.

Selenium BOM 4.38.0 en el módulo web: la versión transitiva intentaba usar el
endpoint retirado msedgedriver.azureedge.net. La versión fijada permite la
descarga desde el servidor actual de Microsoft sin rutas locales en el código.
https://www.selenium.dev/documentation/selenium_manager/
https://github.com/SeleniumHQ/selenium/issues/16058
