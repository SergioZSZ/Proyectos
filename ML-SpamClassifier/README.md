# Spam Message Classifier (SMSSpamCollection)

## Descripción del proyecto
Este proyecto implementa un clasificador de mensajes SPAM vs HAM utilizando el dataset SMSSpamCollection.  
Incluye procesamiento de texto con TF-IDF, entrenamiento de varios modelos supervisados, selección del mejor modelo y una API FastAPI para predicciones en tiempo real.

## Tecnologías utilizadas
- Python 3.12
- scikit-learn
- pandas
- FastAPI
- Uvicorn
- Pydantic
- joblib

## Estructura del proyecto
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
│   ├── models/
│   │   └── SpamModelSVM.joblib
│   └── model_selecter.py
│   └──predict_example.py
│
├── requirements.txt
└── README.md
```

## Entrenamiento del modelo
El script model_selecter.py:
1. Carga el dataset
2. Divide los datos con estratificación
3. Construye pipelines TF-IDF + modelo
4. Entrena Logistic Regression, Random Forest y SVM
5. Evalúa cada modelo con métricas detalladas
6. Selecciona y exporta el mejor modelo
7. Guarda métricas en model/metricasSpam/

Ejecutar entrenamiento:

```python model/model_selecter.py```

## Métricas generadas
Incluyen:
- Accuracy
- Precision
- Recall
- F1-score
- Matriz de confusión
- Classification Report

## Predicción de nuevos mensajes
El archivo ```predict_example.py``` permite cargar el modelo exportado y clasificar nuevos textos.

Ejecutar:
```python predict_example.py```

## API FastAPI
El proyecto incluye una API REST para clasificar mensajes en tiempo real.

Ejecutar la API:
```uvicorn app.main:app --reload```

Documentación interactiva:
http://localhost:8000/docs

### Endpoint principal
POST /mail/isSpam
```
Body:
{
  "message": "Win a free tickets, No prize! click on the link below."
}
```
Respuesta:
```
{
  "message": "Win a free tickets, No prize! click on the link below.",
  "tipo": "SPAM"
}
```
## Limitaciones del dataset
El dataset:
- Solo contiene mensajes SMS no muy largos
- No incluye spam moderno
- No detecta phishing avanzado
- Es un dataset educativo, no orientado a producción

Para que un sistema de detección de spam sea realmente preciso en un entorno real, el modelo debe entrenarse con datos que representen fielmente los correos que recibe una organización.

Un modelo entrenado únicamente con datasets genéricos, pequeños o que no corresponden al dominio real (como datasets públicos no relacionados con la empresa) suele fallar más, especialmente en los correos “ham” propios del negocio.

Lo ideal es utilizar:

Correos reales de spam detectados por la empresa, y

Correos ham reales del entorno corporativo (comunicaciones internas, tickets, notificaciones, etc.).

Entrenar con datos reales del dominio permite al modelo aprender el estilo, estructura, vocabulario y patrones propios de la organización, lo que mejora significativamente la precisión y reduce falsos positivos y falsos negativos.

## Licencia
MIT License.

## Autor
Sergio Zaballos Herrera
