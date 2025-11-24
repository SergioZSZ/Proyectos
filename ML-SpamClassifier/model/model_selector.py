import pandas as pd
import os
import joblib
from cleanText import clean_text
from evaluateclf import evaluate_clf


from sklearn.feature_extraction.text import TfidfVectorizer #scaler para letras, convierte a numeros
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.linear_model import LogisticRegression
from sklearn.svm import SVC
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import (precision_score,accuracy_score,recall_score,f1_score, confusion_matrix,classification_report)

import nltk
#descargarse la primera vez que se ejecute(para la funcion clean_text)
nltk.download('stopwords')  #paquete de palabras insignificantes para el preprocesado
nltk.download('wordnet')    #paquete de lexemas(sirve para going|went->go)
nltk.download('omw-1.4')    #mejorador del paquete de lexemas

os.makedirs("model/metricasSpam",exist_ok=True)
os.makedirs("model/models",exist_ok=True)


'''
si queremos usar el otro dataset mas grande
dir = "model/data/spamassassin-utf8-csv.csv"
df = pd.read_csv(dir,
                sep=",",
                header=0,
'''


#importamos dataset 
file_path = "model/data/SMSSpamCollection"
df = pd.read_csv(
    file_path,                  #path
    sep="\t",                   # separado por tabulador
    header=None,                # no tiene cabecera este dataset
    names=["label", "message"]  # nombres de columnas que añadimos
)

#convertimos etiquetas a clasificar a 0 y 1
df["label_num"] = df["label"].map({"ham": 0, "spam": 1})

#a partir de la columna mensages queremos que nos diga si es spam o no
X = df["message"]
y = df["label_num"]

trainx,testx,trainy,testy = train_test_split(X,y,test_size=0.2, random_state=42,stratify=y)

#CREAMOS PIPELINES A PROBAR
pipeLR = Pipeline([
    ('tfidf',TfidfVectorizer(
        preprocessor=clean_text, #preprocesador de texto para dejarlo mas clean(funcion hecha)
        tokenizer=str.split,   #obligatorio al preprocesar sin default
        ngram_range=(1,2),       #unigramas y bigramas unicamente
        min_df= 5,               #si pal no esta en minimo 5 mensajes, se descarta feature
        max_df=0.9,             #si pal esta en el 90% de mensajes no se usa
        max_features=20000      #para no sobresaturar la RAM ni overfittear
        )) ,
    ('model',LogisticRegression(max_iter=1000))
])



pipeSVM = Pipeline([
    ('tfidf',TfidfVectorizer(
        preprocessor=clean_text, #preprocesador de texto para dejarlo mas clean(funcion hecha)
        tokenizer=str.split,   #obligatorio al preprocesar sin default
        ngram_range=(1,2),       #unigramas y bigramas unicamente
        min_df= 5,               #si pal no esta en minimo 5 mensajes, se descarta feature
        max_df=0.9,             #si pal esta en el 90% de mensajes no se usa
        max_features=20000      #para no sobresaturar la RAM ni overfittear
        )),
    ('model',SVC(kernel="linear",class_weight="balanced"))
])

#los entrenamos
pipeLR.fit(trainx,trainy)
pipeSVM.fit(trainx,trainy)

#los testeamos enviando las metricas a sus respectivos txt para decidir cual usar
evaluate_clf("LR",pipeLR,trainx,testx,trainy,testy)
evaluate_clf("SVC",pipeSVM,trainx,testx,trainy,testy)

#exportamos el que nos cuadre mas con las metricas, elegimos joblib porque tiene
#mejor recall(menos correos HAM clasificados de SPAM)
joblib.dump(pipeSVM, "model/models/modelSVM.joblib")

