# mini-java-compiler
Analizador Léxico, Sintáctico y Semántico en Java  
Proyecto académico – Procesadores de Lenguajes (UPM)

## Descripción del proyecto
Este proyecto implementa el front-end completo de un compilador educativo para un lenguaje de programación similar a Java. Incluye:

- Analizador léxico basado en un autómata finito determinista.
- Analizador sintáctico descendente LL(1) mediante tabla predictiva.
- Analizador semántico usando traducción dirigida por la sintaxis.
- Gestión de tabla de símbolos con ámbitos globales y locales.
- Sistema de gestión de errores léxicos, sintácticos y semánticos.
- Generación automática de archivos de salida: tokens, parse, tabla de símbolos y errores.

## Estructura del repositorio
```
mini-java-compiler/
    pdl/
        src/
            analizador_lexico/
            analizador_sintactico/
            analizador_semantico/
            TS/
            gestor_errores/
            lector/
                lector.java
    lector/
        entrada_programa.txt
        tokens.txt
        parse.txt
        TS.txt
        errores.txt
    casos_de_prueba/
        aciertos/
        errores/
README.md
```

## Funcionamiento del directorio `lector`
El proyecto trabaja utilizando la carpeta `lector/` como punto de entrada y salida:

- **Entrada del programa fuente:**  
  `lector/entrada_programa.txt`

- **Archivos generados automáticamente:**  
  - `lector/tokens.txt`  
  - `lector/parse.txt`  
  - `lector/TS.txt`  
  - `lector/errores.txt`

El analizador sobrescribe estos archivos en cada ejecución.

## Compilación
Desde la raíz del proyecto:

```
javac -d bin pdl/src/**/**/*.java
```

En caso de problemas con la expansión `**`, puede compilarse módulo a módulo o utilizar un IDE como IntelliJ o Eclipse.

## Ejecución
1. Editar `lector/entrada_programa.txt` para añadir el programa que se desea analizar.
2. Ejecutar desde la raíz del proyecto:

```
java -cp bin lector.lector
```

3. Los resultados aparecerán en la carpeta `lector/`.

## Casos de prueba
El directorio `casos_de_prueba/` contiene ejemplos correctos y con errores, junto con su salida correspondiente: tokens, parse, árboles sintácticos, tabla de símbolos y mensajes de error.

## Autor
Sergio Zaballos Herrera  
ETSIINF – Universidad Politécnica de Madrid
