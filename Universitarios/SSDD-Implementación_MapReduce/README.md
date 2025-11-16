# map --- Herramienta distribuida estilo MapReduce (implementación en C)

Este repositorio contiene mi implementación personal de la práctica
"map" de la asignatura Sistemas Distribuidos (UPM).\
El proyecto consiste en desarrollar una herramienta distribuida, similar
a la fase Map de MapReduce, utilizando C, sockets TCP y programación
concurrente.

Nota: Este repositorio incluye únicamente código de mi autoría.\
No contiene enunciados, material docente ni código proporcionado por la
universidad.

## Descripción del proyecto

El objetivo es implementar un sistema capaz de ejecutar en paralelo un
programa de usuario (un mapper) sobre diferentes partes de un fichero de
entrada.\
El sistema se divide en tres componentes principales:

### 1. Manager (Gestor)

Encargado de administrar los trabajadores del sistema.

Responsabilidades: - Aceptar conexiones concurrentes mediante threads. -
Registrar nuevos trabajadores que se den de alta. - Atender peticiones
de reserva de nodos por parte del cliente. - Mantener el estado
(libre/ocupado) de cada trabajador. - Liberar los nodos reservados
cuando el cliente finaliza su ejecución.

### 2. Worker (Trabajador)

Proceso que ejecuta las tareas enviadas por el cliente.

Responsabilidades: - Registrarse en el gestor al arrancar. - Convertirse
en servidor secuencial. - Procesar tareas enviadas por el cliente. -
Leer únicamente el bloque asignado del fichero de entrada. - Redirigir
entrada y salida estándar hacia el programa mapper. - Ejecutar el
programa mediante execvp. - Confirmar al cliente cuando termine la
tarea.

### 3. map (Cliente / Maestro)

El cliente coordina un trabajo completo.

Responsabilidades: - Solicitar al gestor una reserva de N
trabajadores. - Calcular cuántos bloques tiene el fichero de entrada. -
Enviar a los trabajadores toda la información del trabajo. - Manejar
ejecución paralela y reasignación de tareas mediante select().

## Arquitectura implementada

               +----------------+
               |    Manager     |
               | (concurrente)  |
               +-------+--------+
                       ^
                       |
              reserva  |
                       v
       +---------------+---------------+
       |                               |
    +--+--------+               +------+------+
    | Worker 1  |               | Worker 2    |
    | (sec.)    |               | (sec.)      |
    +--+--------+               +------+------+
          ^                           ^
          | tareas                    | tareas
          |                           |
          +------------+--------------+
                       |
                       v
                   +---+---+
                   |  map  |
                   |cliente|
                   +-------+

## Tecnologías utilizadas

-   C (POSIX)
-   Sockets TCP
-   select() para multiplexación
-   Threads en el gestor
-   fork + execvp
-   sendfile
-   Pipes y redirección de ficheros

## Estructura del repositorio

    manager_node/
        manager.c
    worker_node/
        worker.c
    client_node/
        map.c
    README.md

## Características destacadas

-   Implementación completa de las ocho fases de la práctica.
-   Protocolo de comunicación simple y extensible.
-   Gestión robusta de desconexiones.
-   Zero-copy mediante sendfile.
-   Distribución dinámica de tareas con select().

## Ejemplo conceptual de uso

    ./manager 12345
    ./worker localhost 12345
    ./worker localhost 12345
    ./map localhost 12345 2 524288 entrada.txt salida/ programas/swap_case.py

## Aprendizajes principales

-   Diseño de sistemas distribuidos.
-   Comunicación TCP de bajo nivel.
-   Concurrencia y coordinación maestro--trabajador.
-   Multiplexación de sockets.
-   Ejecución eficiente de procesos externos.

## Licencia

Este repositorio contiene únicamente mi propio código.\
El diseño del ejercicio pertenece a la asignatura Sistemas Distribuidos
(UPM).
