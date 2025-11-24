# Spam Message Classifier (SMSSpamCollection)

## Descripción del proyecto
Este proyecto implementa un clasificador de mensajes SPAM vs HAM utilizando el dataset SMSSpamCollection, compuesto por mensajes SMS.  
El objetivo es demostrar el uso de técnicas básicas de procesamiento de texto y Machine Learning:

- Preprocesamiento con TF-IDF
- Entrenamiento de modelos supervisados
- Evaluación con métricas estándar
- Uso de scikit-learn Pipelines
- Exportación del modelo con joblib
- Predicción de nuevos mensajes mediante un script dedicado

Es un proyecto adecuado como introducción a NLP y clasificación de texto.

---

## Tecnologías utilizadas
- Python 3.12  
- scikit-learn  
- pandas  
- joblib  
- TF-IDF Vectorizer  
- Modelos utilizados: Logistic Regression, SVM, Random Forest  

---

## Estructura del proyecto
```
SpamML/
│
├── data/
│ └── SMSSpamCollection
│
├── metricasSpam/
│ ├── LogisticRegression.txt
│ ├── RandomForest.txt
│ └── SVM.txt
│
├── models/
│ └── SpamModelSVM.joblib
│
├── model_selecter.py
└── predict_example.py
└── requirements.txt
```

---

## Entrenamiento del modelo

El script `model_selecter.py` realiza:

1. Carga del dataset  
2. División en train/test con estratificación  
3. Creación de pipelines (TF-IDF + modelo)  
4. Entrenamiento de Logistic Regression, Random Forest y SVM  
5. Evaluación con métricas detalladas  
6. Exportación del mejor modelo  
7. Guardado automático de métricas en `metricasSpam/`  

### Ejecutar el entrenamiento

```bash
python model_selecter.py
```
Los resultados se almacenan en los archivos de texto generados y el modelo final en la carpeta ```models```.

## Evaluación

Para cada modelo se generan:
-```Accuracy```
-```Precision```
-```Recall```
-```F1-Score```
-```Matriz de confusión```
-```Classification report```

### Ejemplo de salida:

'''
---- ENTRENAMIENTO ----
Accuracy: 0.9750953556203724
Precision: 0.9939148073022313
Recall: 0.8193979933110368
F1: 0.8982584784601283

---- TEST ----
Accuracy: 0.9730941704035875
Precision: 1.0
Recall: 0.7986577181208053
F1: 0.8880597014925373

Matriz de confusi�n (test):
[[966   0]
 [ 30 119]]

Reporte de clasificaci�n (test):
              precision    recall  f1-score   support

           0       0.97      1.00      0.98       966
           1       1.00      0.80      0.89       149

    accuracy                           0.97      1115
   macro avg       0.98      0.90      0.94      1115
weighted avg       0.97      0.97      0.97      1115

'''

## Predicción de nuevos mensajes
El script ```predict_example.py``` permite cargar el modelo exportado y clasificar nuevo texto guardando los resultados en ```Predicciones de Ejemplo.txt```

Limitaciones del dataset

- El conjunto SMSSpamCollection contiene únicamente mensajes SMS, por lo que presenta limitaciones:
- No está diseñado para detectar spam moderno en correos electrónicos
- Algunos mensajes ambiguos o sutiles pueden clasificarse incorrectamente
- No incluye ejemplos actuales de phishing o fraudes más elaborados
- El lenguaje de marketing moderno no está representado
- El proyecto está orientado al aprendizaje de técnicas básicas de NLP y clasificación supervisada, - No a un sistema antispam de producción.

## Licencia
Proyecto distribuido bajo licencia MIT.
## Autor
Sergio Zaballos Herrera


