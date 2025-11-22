# Near Parkings – Aplicación Semántica de Parkings en Madrid

Proyecto desarrollado en la asignatura **Web Semántica** (Grupo 20, ETSIINF-UPM).

Near Parkings es una aplicación completa que permite **consultar, filtrar y visualizar** aparcamientos de la red EMT en la ciudad de Madrid, combinando:

- Tecnologías de **Web Semántica** (ontologías, RDF, SPARQL),
- Un **backend** en FastAPI,
- Una **GUI de escritorio** en Tkinter con mapa y gestión de favoritos.

---

## 1. Descripción general

La aplicación consume un grafo RDF generado a partir del dataset oficial de aparcamientos de la EMT.  
Sobre ese grafo se ejecutan consultas SPARQL (con RDFlib) para:

- Buscar parkings por nombre o por calle,
- Localizar parkings cercanos a unas coordenadas (latitud/longitud),
- Calcular distancias a partir de sus coordenadas,
- Gestionar una lista de **parkings favoritos**, que se almacena en PostgreSQL.

La interfaz de usuario (frontend) permite:

- Buscar parkings y verlos en un **mapa interactivo**,
- Ver los detalles de cada parking (dirección, tipo, etc.),
- Añadir/eliminar favoritos, que se persisten en la base de datos.

---

## 2. Arquitectura del proyecto

El proyecto está dividido en tres capas principales:

### 2.1. Capa semántica (RDF / Ontología)

- Ontología OWL modelada con **CHOWLk** y editada en **Protégé**.
- Transformación de los datos originales (CSV) a RDF mediante:
  - **YARRRML** (reglas de mapeo),
  - **RMLMapper** (ejecución de las reglas).
- Grafo RDF en formato **Turtle (.ttl)** cargado con **RDFlib** en el backend.

### 2.2. Backend (FastAPI)

Ubicado en la carpeta `backend/app/`, incluye:

- `main.py`  
  Punto de entrada de la API FastAPI. Registra los routers de:
  - `/parkings` – operaciones de búsqueda sobre el grafo RDF,
  - `/favoritos` – operaciones CRUD sobre la tabla de favoritos en PostgreSQL.

- `config.py`  
  Gestión de variables de entorno con `pydantic-settings` (`SettingsConfigDict`, `.env`).

- `db.py`  
  Creación del **engine** de SQLAlchemy y la base del ORM.

- `db_deps.py`  
  Dependencia `get_db()` para inyectar sesiones de base de datos en los endpoints.

- `routers/`  
  - `parkings.py`: endpoints para búsquedas y filtrados de parkings sobre el grafo RDF.  
  - `favoritos.py`: endpoints CRUD para la entidad Favorito.

- `schemas/`  
  Esquemas Pydantic para validar las entradas/salidas de la API.

- `rdf/`  
  - `parkingsGraph.py`: carga del grafo RDF y definición de consultas SPARQL (búsqueda, cercanía, etc.).  
  - `parkings.ttl`: grafo RDF con los aparcamientos de la EMT.

### 2.3. Frontend (Tkinter)

Ubicado en `frontend/`:

- `client.py`  
  Aplicación de escritorio con Tkinter que consume la API del backend:
  - Buscador de parkings por nombre / calle,
  - Visualización de los resultados en mapa,
  - Gestión de favoritos (alta/baja),
  - Interacción visual con el parking seleccionado.

---

## 3. Estructura de carpetas

```bash
near-parkings/
├─ docker-compose.yml
├─ README.md
├─ backend/
│  ├─ Dockerfile
│  ├─ requirements.txt
│  ├─ .env_example
│  ├─ .env          # (no subir a GitHub; solo local)
│  ├─ alembic.ini
│  ├─ alembic/
│  │  └─ versions/
│  │     └─ 0a88c14a6b6a_crear_tabla_favoritos.py
│  └─ app/
│     ├─ main.py
│     ├─ config.py
│     ├─ db.py
│     ├─ db_deps.py
│     ├─ models.py
│     ├─ routers/
│     │  ├─ parkings.py
│     │  └─ favoritos.py
│     ├─ schemas/
│     │  └─ parkings.py
│     └─ rdf/
│        ├─ parkingsGraph.py
│        └─ parkings.ttl
└─ frontend/
   ├─ client.py
   └─ requirements_frontend.txt
```
## Cómo iniciar el proyecto

### Requisitos
- Docker  
- Docker Compose
- python

### Pasos

1. BACKEND: Desde la raíz del proyecto, ejecutar:

```bash
docker compose up --build
```
Esto levanta:

- db → PostgreSQL 16 (expuesto en localhost:5434)
- migrate → primera migración a la db
- backend → FastAPI (expuesto en localhost:8000)

Acceder a la documentación de la API: ``` http://localhost:8000/docs ```

Para detener los contenedores se debe hacer ```docker compose down```


2. FRONTEND: Desde la carpeta ```/frontend```, ejecutar:
```bash
python -m venv .venv
```
generando un entorno en el cual instalaremos las dependencias del ```requirements_frontend.txt```

lo activamos con:
  Windows:```.venv\Scripts\activate ```
  Linux:```.venv/bin/Activate.ps1```

realizamos: ```pip install -r requirements_frontend.txt``` para instalar dependencias en el

en la misma carpeta ```/frontend``` ejecutamos ```python client.py```




