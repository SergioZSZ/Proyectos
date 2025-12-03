Actualmente en desarrollo de mejora.

# Spam Email Classifier (TF-IDF + ModeloSVC + FastAPI)

Este proyecto implementa un sistema completo de clasificación de correos electrónicos SPAM vs HAM, utilizando procesamiento avanzado de texto (TF-IDF), modelos supervisados tradicionales buscando el mejor modelo y mejores parámetros posibles con RandomizedSearchCV y probando también con una red neuronal MLP basada en TensorFlow/Keras.  
Incluye una API FastAPI para predicciones en tiempo real.

# Tecnologías utilizadas

- Python 3.12  
- scikit-learn  
- TensorFlow / Keras  
- pandas  
- FastAPI  
- Uvicorn  
- Pydantic  
- joblib  

# Generación de entorno

```bash
python -m venv .venv
pip install -r requirements.txt
```

# Estructura del proyecto

```
ML-SpamClassifier/
│
├── app/
│   ├── main.py
│   ├── routers/
│   └── schemas/
│
├── model/
│   ├── data/
│   │   └── SMSSpamCollection
│   ├── metricasSpam/
│   ├── examples/
│   ├── funciones_auxiliares/
         │──cleanText.py
         │── evaluateclf.py
         └── createModel.py
│   ├── trainer_MLP.py
│   ├── trainer_ML.py
│   ├── predict_example_ML.py
│   └── predict_example_mlp.py
└── README.md
└── requirements.txt
```

# Preprocesado de texto

El proceso se realiza en la función `clean_text()`:

- Conversión a minúsculas  
- Eliminación de URLs, emails, HTML  
- Eliminación de números  
- Eliminación de signos de puntuación  
- Stopwords (NLTK)  
- Lematización mediante WordNet  
- Tokenización  
- Limpieza de espacios  

Este preprocesado se inyecta en el TfidfVectorizer mediante el parámetro `preprocessor=`.

# Entrenamiento del modelo con MLP

El archivo principal de entrenamiento es:

```model/trainer_ML.py``` para probar el modelo SVC 
```model/trainer_MLP.py``` para probar el modelo MLP Sequential

generarán respectivamente ```modelML.joblib``` ```modelMLP.keras```
El pipeline incluye:

- TfidfVectorizer  
  - max_features = 40000  
  - ngram_range = (1,1)  
  - min_df = 5  
  - max_df = 0.9  

posteriormente con RandomizedSearchCV se realiza una búsqueda de mejores parametros para el 
modelo LinearSVC y LogisticRegresion para ver cual da mejores resultados.

el modelo MLP incluye:
- tfidfVectorizer con preprocesado propio, n_grams(1,2), eliminación de palabras demasiado    recurrentes y palabras poco frecuentes
- 3 capas Dense siendo la última "sigmoid" para clasificación binaria

Ejecutar entrenamiento:

```bash
python -m model.trainer_ML
python -m model.trainer_mlp
```

# Datasets disponibles y su impacto

El proyecto soporta varios datasets. Cada uno influye en la calidad del modelo, tiempo de entrenamiento y limitaciones.

## 1. SMSSpamCollection  
Ubicación: `model/data/SMSSpamCollection`

- Tamaño aproximado: 5.5k registros  
- Formato: SMS cortos, lenguaje informal  
- Separador original: `\t`  
- Para usarlo en el proyecto:  
  - Cambiar `sep="\t"`  
  - Definir las columnas como:  
    ```python
    names=["label", "text"]
    ```
- Tiempo de entrenamiento: muy rápido (2-5 segundos)  
- Precisión limitada por el tamaño reducido y dominio SMS  
- No detecta spam moderno basado en email  
- Adecuado para pruebas rápidas  

## 2. spam_Emails_data.csv (dataset grande ~190k correos)  
- muy grande para subir a GitHub
- descargable en: https://www.kaggle.com/datasets/meruvulikith/190k-spam-ham-email-dataset-for-classification

- Tamaño: aproximadamente 190.000 correos  
- Muy variado, incluye spam moderno  
- Ideal para entrenar la red neuronal MLP
- Separador original: `,`   
- Tiempos aproximados:
 `trainer_MLP`:
  - Entrenamiento del modelo MLP: 2-3 minutos
 `trainer_ML`:
  - Entrenamiento RandomizedSearchCV(combinaciones para mejor modelo/params): 6-7 minutos
  - Entrenamiento mejor modelo encontrado con RandomizedSearchCV: 1 minuto
- Mucho mejor rendimiento y generalización  
- Puede consumir más RAM por tener max 50k features



# Cómo cambiar de dataset

En los `trainer.py` modificar a la ruta a la del dataset que se quiera usar:

```python
BASE_DIR = "model/data/spam_Emails_data.csv"
```

Si el dataset usa coma como separador:

```python
df = pd.read_csv(BASE_DIR, sep=",")
```

Si no tiene cabeceras:

```python
names=["label", "text"]
```

Para SMSSpamCollection:

```python
sep="\t"
names=["label", "text"]
```

# Predicción de mensajes de prueba

```bash
python -m model.predict_example
```

# API FastAPI

```bash
uvicorn app.main:app --reload
```

Documentación:
http://localhost:8000/docs

# Endpoints principales

POST `/mail/predictMLP` para predecir con `modelMLP.keras`
POST `/mail/predictML`  para predecir con `modelML.joblib`


Body:
```json
{
  "message": "Win money now"
}
```

Respuesta:
```json
{
  "message": "Win money now",
  "tipo": "SPAM"
}
```

Body:
```json
{
  "message": "here you got the notes of the last lecture. thanks for comming"
}
```

Respuesta:
```json
{
  "message": "here you got the notes of the last lecture. thanks for comming",
  "tipo": "HAM"
}
```
# Limitaciones
- El modelo solo está entrenado con correos en inglés 
- Los datasets pequeños reducen precisión  
- Datasets genéricos no reflejan el vocabulario real de una empresa  
- Para producción se recomienda entrenar con:
  - correos ham reales de la organización
  - correos spam reales detectados internamente

# Licencia

MIT License.

# Autor

Sergio Zaballos Herrera
