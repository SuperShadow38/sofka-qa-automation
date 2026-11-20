# Plan de implementación y decisiones

Especificación: PDF original de dos páginas, revisado completo el 2026-11-20.
Se adopta la última instrucción del usuario: Screenplay para web, no POM.

## Arquitectura
Java 21, Maven 3.9.9 mediante Wrapper, Serenity 4.2.34, Karate 1.5.1
(grupo io.karatelabs). Dos módulos sin dependencia entre sí. Failsafe ejecuta
las pruebas de integración y permite generar reportes antes de fallar verify.
La raíz permite -fae para comprobar ambos aunque uno falle.

## Tareas
- [ ] Explorar URL efectiva, DOM, productos y checkout en navegador limpio.
- [ ] Crear Wrapper oficial, POM padre, módulos y configuración.
- [ ] Definir escenarios primero; implementar Tasks, Targets y Questions web.
- [ ] Implementar ciclo API y limpieza que preserve el error original.
- [ ] Ejecutar cada módulo y después la raíz, conservar reportes originales.
- [ ] Revisar y corregir errores de código, comprobar reproducción desde copia limpia.
- [ ] Documentar comandos, trazabilidad, resultados reales y publicación manual.

## Criterios de revisión
No pagos reales; checkout solo con pago de demostración. Sesión web nueva.
Dos productos distintos configurables. Username único. Sin reintentos de compras.
Consultar datos persistidos, no solo HTTP status. Limpieza ante fallos API.
No convertir bloqueos externos en resultados aprobados. Sin credenciales reales.
Versiones y fuentes oficiales en docs/references.md. Comentarios solo puntuales.

## Registro
- Workspace inicial: solo PDF y renders temporales, sin código ni AGENTS.md.
- Java 21 instalado; Maven no instalado, Edge disponible.
- DNS restringido en sandbox; comprobación con acceso de red: ambos sitios y
  Maven Central devuelven 200. Se requiere acceso de red para ejecutar.
- Maven y dependencias se descargan desde Maven Central; herramientas locales
  se mantienen ignoradas en .tools y no forman parte de la entrega.
