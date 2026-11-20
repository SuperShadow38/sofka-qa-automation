function fn() {
  var env = karate.env || 'demo';
  var environments = karate.read('classpath:environments.json');
  var baseUrl = karate.properties['api.base.url'] || (environments[env] || {}).baseUrl;
  if (!baseUrl) throw new Error('Ambiente desconocido: ' + env + '. Configura api.base.url');
  karate.configure('connectTimeout', 15000);
  karate.configure('readTimeout', 20000);
  karate.configure('logPrettyRequest', true);
  karate.configure('logPrettyResponse', true);
  return { baseUrl: baseUrl };
}
