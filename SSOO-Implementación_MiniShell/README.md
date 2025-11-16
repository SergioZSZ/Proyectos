# Minishell en C

Este repositorio contiene la implementación de un minishell desarrollado
en lenguaje C sobre un sistema tipo Unix.\
El proyecto reproduce las funcionalidades esenciales de un intérprete de
mandatos, centrándose en el uso de llamadas al sistema y en la gestión
correcta de procesos, tuberías y redirecciones.

Este repositorio incluye exclusivamente el código implementado por el
autor.\
Los ficheros de apoyo suministrados por la universidad (scanner, parser,
Makefile original, enunciado o cualquier material docente) no se
incluyen aquí.

## Objetivo del proyecto

El propósito de este minishell es entender y aplicar:

-   Creación y control de procesos mediante `fork`, `execvp` y `wait`.
-   Manejo de tuberías con `pipe` y redirecciones con `dup2` y `open`.
-   Interpretación de mandatos simples y secuencias conectadas por `|`.
-   Gestión de ejecución en primer plano y en segundo plano.
-   Implementación de mandatos internos y sustituciones básicas del
    shell.

## Funcionalidades implementadas

Dependiendo de la versión desarrollada, este minishell puede incluir:

### Ejecución de mandatos externos

Permite ejecutar programas del sistema con sus argumentos:

    ls -l /etc

### Redirecciones

-   Entrada estándar: `< fichero`
-   Salida estándar: `> fichero`
-   Redirección de error estándar: `>& fichero`

Ejemplo:

    cat < entrada.txt > salida.txt

### Pipelines

Permite conectar la salida de un proceso con la entrada de otro.

    ls -l | grep txt | wc -l

### Ejecución en background

Incorpora soporte para ejecutar procesos sin bloquear el shell:

    sleep 10 &

### Mandatos internos típicos

Según la implementación, pueden incluir:

-   `cd`\
-   `umask`\
-   `time`\
-   `read`\
-   Actualización de variables especiales (`prompt`, `mypid`, `bgpid`,
    `status`)

### Sustitución de variables y metacaracteres

-   Expansión de `~`\
-   Expansión de `$VAR`\
-   Expansión de comodines simples (`?`)

## Compilación

Este proyecto solo incluye el archivo `main.c`, por lo que la
instrucción de compilación debe adaptarse al entorno donde se utilice.

Un ejemplo genérico:

    gcc -Wall -Wextra -O2 main.c -o minishell

Si tu versión requiere enlazar con los ficheros generados por `lex` o
`yacc`, es necesario integrarlos manualmente en tu entorno de pruebas.
Dichos ficheros no se incluyen en este repositorio.

## Ejecución

    ./minishell

El shell mostrará el prompt configurado (por defecto `msh>` o el que se
haya implementado) y ejecutará los mandatos introducidos.

## Estructura del repositorio

    minishell/
     ├── README.md
     └── main.c

## Aviso importante

Este repositorio contiene únicamente código original implementado por el
autor.\
No se incluyen materiales proporcionados por la asignatura, ya que no se
permite su redistribución pública.

## Licencia

MIT. Este proyecto puede ser utilizado y modificado libremente.
