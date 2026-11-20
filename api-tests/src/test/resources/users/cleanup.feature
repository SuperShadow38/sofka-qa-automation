@ignore
Feature: Limpieza del usuario propio después de un fallo
  Scenario:
    * configure afterScenario = null
    Given url cleanupBaseUrl
    And path 'user', cleanupUsername
    When method delete
    * def cleanupStatus = responseStatus
    * print 'DELETE de limpieza:', cleanupUsername, cleanupStatus, response
    Given path 'user', cleanupUsername
    When method get
    * def absenceStatus = responseStatus
    * print 'Verificación de limpieza:', absenceStatus, response
