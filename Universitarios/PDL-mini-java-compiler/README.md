# PDL-Mini-Java-Compiler

Proyecto académico desarrollado en la asignatura **Procesadores de Lenguajes (PDL)** cuyo objetivo es implementar un **mini compilador de Java** compuesto por:

- Analizador léxico  
- Analizador sintáctico descendente  
- Gestión de tabla de símbolos (TS) mediante pila de tablas  
- Detección y reporte de errores léxicos, sintácticos y semánticos  
- Generación automática de ficheros de salida (tokens, parse, TS, errores)  
- Módulo “lector” que integra y ejecuta todo el proceso de compilación

Este proyecto procesa un programa Java reducido, analiza su estructura y genera los artefactos propios de la primera fase de un compilador.

---

## 📂 Estructura del proyecto

PDL-mini-java-compiler
│
├─ lector/ # Archivos de entrada/salida del compilador
│ ├─ entrada_programa.txt # Programa de entrada a analizar
│ ├─ tokens.txt # Tokens generados por el análisis léxico
│ ├─ parse.txt # Resultado del análisis sintáctico
│ ├─ TS.txt # Tablas de símbolos generadas
│ └─ errores.txt # Errores detectados durante la compilación
│
└─ pdl/
└─ src/
├─ analizador_lexico/ # Autómatas, tabla de tokens, clases auxiliares
├─ analizador_sintactico/ # Analizador sintáctico descendente
├─ TS/ # Pila de tablas y gestión de símbolos
└─ lector/ # Módulo principal (lector.java)


---

## 🧠 Funcionamiento del compilador

El mini-compilador sigue el flujo clásico de un procesador de lenguajes:

### 1. Lectura de entrada  
El archivo de entrada es:


---

### 2. Análisis Léxico  
Ubicado en `analizador_lexico/`:

- Reconoce identificadores, constantes, operadores, símbolos…
- Implementado mediante autómatas
- Genera objetos `Tokens`
- Reporta errores léxicos si los hay  
- Escribe la salida en `tokens.txt`

---

### 3. Análisis Sintáctico  
Ubicado en `analizador_sintactico/`:

- Implementación recursiva descendente
- Comprueba que la secuencia de tokens cumple la gramática
- Detecta y reporta errores sintácticos
- Escrita en `parse.txt`

---

### 4. Tabla de Símbolos  
Ubicado en `TS/`:

- Implementa una **pila de tablas** (modela ámbitos)
- Cada bloque o estructura crea un nuevo nivel
- Se registran variables, tipos, posiciones y atributos semánticos

Salida: `TS.txt`

---

### 5. Gestión de errores  
Si se produce un error durante cualquier fase:

- Se detiene la compilación  
- Se genera exclusivamente `errores.txt`  

---

### 6. Integración con `lector.java`  
El archivo `lector.java`:

- Ejecuta todas las fases del compilador  
- Genera los ficheros `tokens.txt`, `parse.txt`, `TS.txt` o `errores.txt`  
- Controla la entrada y salida del análisis  

---

## ▶️ Compilar y ejecutar (VS Code / terminal)

Ejecutar siempre desde: 
pdl/src

### 1. Compilar todo el proyecto:

```bat
javac TS\*.java analizador_lexico\*.java analizador_sintactico\*.java lector\*.java

java lector.lector



### 2. Ejecutar el compilador:

- lector/tokens.txt
- lector/parse.txt
- lector/TS.txt
- lector/errores.txt (solo si hay errores)

### 3. Archivos generados:

`lector/tokens.txt`
`lector/parse.txt`
`lector/TS.txt`
`lector/errores.txt` (solo si hay errores)

## 4. Autor:
Sergio Zaballos Herrera
Grado en Ingeniería Informática — ETSIINF UPM
Asignatura: Procesadores de Lenguajes (PDL)