function() {
  if (karate.get('deleted') || !karate.get('username')) return;
  var originalError = karate.info.errorMessage;
  try {
    var result = karate.call('classpath:users/cleanup.feature', {
      cleanupBaseUrl: karate.get('baseUrl'), cleanupUsername: karate.get('username')
    });
    karate.log('Limpieza posterior:', result.cleanupStatus, result.absenceStatus);
    if (result.absenceStatus !== 404) throw new Error('No se confirmó la limpieza del usuario');
  } catch (error) {
    karate.log('Limpieza fallida para ' + karate.get('username') + ': ' + error);
    if (!originalError) throw error;
  }
}
