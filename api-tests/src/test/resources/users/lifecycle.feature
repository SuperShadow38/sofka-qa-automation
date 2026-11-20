@api @regression @smoke
Feature: Ciclo de vida de usuario ficticio en Petstore V2

  Background:
    * url baseUrl
    * header Accept = 'application/json'
    * def username = 'sofka_' + java.util.UUID.randomUUID().toString().replaceAll('-', '')
    * def userId = java.lang.System.currentTimeMillis()
    * def deleted = false
    * def user = read('user.json')
    * def userSchema = read('user-schema.json')
    * configure afterScenario = read('cleanup.js')

  Scenario: API-01 Crear, consultar, actualizar, consultar y eliminar el mismo usuario
    * print 'Variables de ejecución:', { baseUrl: '#(baseUrl)', username: '#(username)', userId: '#(userId)' }
    Given path 'user'
    And request user
    When method post
    Then status 200
    And match response contains { code: 200, type: '#string', message: '#(userId + "")' }

    Given path 'user', username
    When method get
    Then status 200
    And match response == userSchema
    And match response contains user

    * copy updatedUser = user
    * set updatedUser.firstName = 'Ana Actualizada'
    * set updatedUser.email = 'qa.updated@example.com'
    Given path 'user', username
    And request updatedUser
    When method put
    Then status 200
    And match response contains { code: 200, type: '#string', message: '#(userId + "")' }

    Given path 'user', username
    When method get
    Then status 200
    And match response == userSchema
    And match response contains updatedUser
    And match response.username == username
    And match response.firstName != user.firstName
    And match response.email != user.email

    Given path 'user', username
    When method delete
    Then status 200
    And match response contains { code: 200, type: '#string', message: '#(username)' }

    Given path 'user', username
    When method get
    Then status 404
    And match response contains { code: 1, type: 'error', message: 'User not found' }
    * def deleted = true
