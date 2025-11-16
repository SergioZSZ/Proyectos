# Sistema de Colas de Mensajes con Java RMI

Este repositorio contiene parte de la implementación de un sistema de colas de mensajes
desarrollado en Java utilizando RMI (Remote Method Invocation).

El proyecto está basado en una práctica académica, pero aquí solo se incluyen los ficheros
que corresponden al trabajo realizado por el autor. No se incluyen interfaces, código de
apoyo, scripts ni ningún otro material proporcionado por la asignatura.

## Descripción general

El sistema implementa un servicio de colas de mensajes gestionado por un broker central.
Los clientes se conectan al broker mediante Java RMI para:

- Crear o acceder a colas identificadas por nombre.
- Enviar mensajes a una cola.
- Recibir mensajes de una cola según la política definida.
- Trabajar en modos de tipo productor/consumidor o publicador/suscriptor
  (según la lógica implementada).

La responsabilidad funcional de cada fichero es:

- `Broker.java`: implementación del broker, encargado de registrar las colas,
  aceptar peticiones remotas y coordinar el servicio.
- `QueueImpl.java`: implementación concreta de la cola de mensajes (estructura
  de datos, sincronización, almacenamiento de mensajes, etc.).
- `MQClient.java`: cliente que usa el servicio de colas a través de RMI y
  ejerce como ejemplo de uso del sistema.

## Estructura del proyecto

La estructura propuesta para este repositorio es la siguiente:

```text
mq-rmi/
 ├── src/
 │    ├── broker/
 │    │      ├── Broker.java
 │    │      └── QueueImpl.java
 │    └── mq/
 │           └── MQClient.java
 └── README.md
```

Los paquetes indicados en las primeras líneas de cada fichero (`package ...;`)
se han mantenido tal y como se usaron en la práctica original. Esto facilita
ubicar el código en un proyecto más grande si es necesario.

## Dependencias y código no incluido

Para centrarse únicamente en el código propio del autor, este repositorio
no incluye:

- Interfaces RMI y clases comunes utilizadas por el broker y los clientes.
- Código de infraestructura, scripts de compilación o ejecución.
- Material completo de la práctica, enunciados o ficheros auxiliares.

Por tanto, tal y como está, este repositorio está orientado a mostrar la
implementación y la organización del código, más que a ofrecer un sistema
ejecutable de forma inmediata. Para compilar y ejecutar el sistema es necesario:

1. Definir las interfaces remotas y clases compartidas (por ejemplo, para
   describir las operaciones disponibles en el broker y en las colas).
2. Configurar un proyecto Java con dichas interfaces e incluir estos ficheros.
3. Ajustar los comandos de compilación y ejecución (por ejemplo, usando
   `javac` y `rmiregistry` o un sistema de construcción como Maven/Gradle).

## Uso orientativo

Un posible flujo, en un proyecto completo, sería:

1. Iniciar el registro RMI:
   ```bash
   rmiregistry 1099
   ```

2. Lanzar el broker:
   ```bash
   java broker.Broker
   ```

3. Ejecutar uno o varios clientes que utilicen `MQClient` para conectarse
   al broker y trabajar con las colas.

Los detalles concretos dependen de las interfaces y clases auxiliares que
se decida utilizar en el proyecto completo.

## Aviso

Este repositorio contiene únicamente código implementado por el autor.
No se incluye ningún fichero original de la práctica de la asignatura,
ni enunciados ni código de apoyo, por lo que es adecuado para su uso como
proyecto público en GitHub dentro de un portfolio personal.

## Licencia

MIT. El código puede utilizarse y modificarse libremente.
