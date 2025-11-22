# Near Parkings – Aplicación Semántica de Parkings en Madrid
Proyecto desarrollado en la asignatura Web Semántica (Grupo 20, ETSIINF-UPM)

## Descripción del proyecto
Near Parkings es una aplicación completa para consultar, filtrar y visualizar parkings de Madrid utilizando tecnologías de Datos Enlazados, ontologías OWL y consultas SPARQL sobre un grafo RDF generado a partir del dataset oficial de Aparcamientos EMT.

Incluye:
- Ontología OWL generada con CHOWLk y editada en Protégé.
- Transformación CSV → RDF mediante YARRRML y RMLMapper.
- Backend en FastAPI que ejecuta consultas SPARQL con RDFlib.
- GUI en Tkinter con mapa, buscador, detalles y favoritos.

---

# 1. Funcionalidades principales

## Backend (FastAPI + RDF + SPARQL)
- Carga del grafo RDF de parkings.
- Endpoints REST:
  - Listar parkings filtrando por texto, familia, categoría y EMT.
  - Obtener detalles completos de un parking.
  - Parkings cercanos (lat, lon) con cálculo de distancia Haversine.
  - Gestión de favoritos (GET, POST, DELETE).
- SPARQL optimizado mediante `prepareQuery`.
- Enlaces semánticos a Wikidata y OSM.

## Interfaz gráfica (Tkinter)
- Buscador por nombre, calle o filtros.
- Visualización de resultados en mapa interactivo.
- Marcadores de parking con detalles emergentes.
- Gestión de favoritos desde la propia interfaz.
- Resaltado visual del parking seleccionado.

---

# 2. Tecnologías utilizadas

- **Python 3**
- **FastAPI**
- **RDFlib**
- **SPARQL**
- **RMLMapper / YARRRML Matey**
- **Ontologías OWL (CHOWLk + Protégé)**
- **Tkinter + ttkbootstrap**
- **TkinterMapView**
- **PostgreSQL + SQLAlchemy + Alembic** (para componentes internos del backend)

---

# 3. Estructura del repositorio

```
near_parkings/
│
├── app/                     # Backend FastAPI
│   ├── main.py
│   ├── routers/
│   ├── models/
│   ├── rdf/
│   └── utils/
│
├── client/                  # Interfaz gráfica Tkinter
│   └── frontend.py
│
├── data/                    # CSV, TTL, RDF resultante
│
├── ontology/                # Ontología en OWL y diagramas CHOWLk
│
├── mappings/                # Reglas YARRRML
│
├── docs/                    # Documentación opcional
│
├── requirements.txt
└── README.md
```

---

# 4. Dependencias del proyecto

Las dependencias incluidas en `requirements.txt` son:

| Dependencia           | Para qué sirve 
|-----------------------|----------------
| **fastapi**           | Framework principal para crear APIs rápidas, modernas y tipadas. 
| **uvicorn[standard]** | Servidor ASGI optimizado para ejecutar FastAPI. 
| **sqlalchemy**        | ORM para gestionar modelos y consultas SQL mediante Python. 
| **alembic**           | Migraciones automáticas de base de datos. 
| **pydantic-settings** | Manejo de settings y variables de entorno. 
| **python-dotenv**     | Carga de variables desde `.env`. 
| **psycopg[binary]**   | Driver PostgreSQL precompilado para conexión a BD. 



# 5. Creación del entorno y dependencias

Desde la raíz del proyecto:

```
python -m venv .venv
```

Activar entorno virtual:

- **Windows**
```
.venv\Scripts\activate
```

- **Linux/Mac**
```
source .venv/bin/activate
```

Instalar dependencias:

```
pip install -r requirements.txt
```

---

# 6. Base de datos (solo si se usa PostgreSQL)

Levantar la BD:

```
docker compose up -d
```

Detenerla:

```
docker compose down
```

Primeras migraciones obligatorias para funcionamiento de la BD:

```
alembic upgrade head
```

Generar nuevas migraciones si cambian modelos:

```
alembic revision --autogenerate -m "descripcion"
alembic upgrade head
```

---

# 7. Cómo iniciar el proyecto

1. Crear y activar entorno virtual.  
2. Instalar dependencias backend(`pip install -r requirements.txt`).  
2. Instalar dependencias frontend(client) (`pip install -r requirements_frontend.txt`).  
4. Levantar la base de datos:  
```
docker compose up -d
```  
4. Ejecutar la primera migración:  
```
alembic upgrade head
```  
5. Lanzar backend desde la carpeta raíz `near_parkings`:  
```
uvicorn app.main:app --reload
```  
6. Lanzar la GUI desde la carpeta `client`:  
```
python frontend.py
```

---

# 8. Ejecución general

### Backend
```
uvicorn app.main:app --reload
```

### Frontend
```
python client/frontend.py
```

---

# 9. Autores

Grupo 20 – Web Semántica  
ETSIINF – Universidad Politécnica de Madrid
