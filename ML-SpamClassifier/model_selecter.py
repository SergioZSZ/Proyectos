import pandas as pd
import os
import sys
import joblib

from sklearn.feature_extraction.text import TfidfVectorizer #scaler para letras, convierte a numeros
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.linear_model import LogisticRegression
from sklearn.svm import SVC
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import (precision_score,accuracy_score,recall_score,f1_score, confusion_matrix,classification_report)

os.makedirs("metricasSpam",exist_ok=True)
os.makedirs("models",exist_ok=True)



####################################################################################
def evaluate_clf(model, trainx,trainy,testx,testy):
    pred_train = model.predict(trainx)  
    pred_test = model.predict(testx)
    
    print("---- ENTRENAMIENTO ----")
    print("Accuracy:", accuracy_score(trainy, pred_train))
    print("Precision:", precision_score(trainy, pred_train))
    print("Recall:", recall_score(trainy, pred_train))
    print("F1:", f1_score(trainy, pred_train))

    print("\n---- TEST ----")
    print("Accuracy:", accuracy_score(testy, pred_test))
    print("Precision:", precision_score(testy, pred_test))
    print("Recall:", recall_score(testy, pred_test))
    print("F1:", f1_score(testy, pred_test))

    print("\nMatriz de confusión (test):")
    print(confusion_matrix(testy, pred_test))

    print("\nReporte de clasificación (test):")
    print(classification_report(testy, pred_test))

####################################################################################
#importamos dataset 
BASE_DIR =  r"C:\Users\jzaba\Documents\GitHub\Proyectos\ML-SpamClassifier"
file_path = os.path.join(BASE_DIR, "data", "SMSSpamCollection")

df = pd.read_csv(
    file_path,                  #path
    sep="\t",                   # separado por tabulador
    header=None,                # no tiene cabecera
    names=["label", "message"]  # nombres de columnas
)
#convertimos etiquetas a clasificar a 0 y 1
df["label_num"] = df["label"].map({"ham": 0, "spam": 1})

#a partir de la columna mensages queremos que nos diga si es spam o no
X = df["message"]
y = df["label_num"]

trainx,testx,trainy,testy = train_test_split(X,y,test_size=0.2, random_state=42,stratify=y)

#CREAMOS PIPELINES A PROBAR
pipeLR = Pipeline([
    ('tfidf',TfidfVectorizer()),
    ('model',LogisticRegression(max_iter=1000))
])

pipeRF = Pipeline([
    ('tfidf',TfidfVectorizer()),
    ('model',RandomForestClassifier(n_estimators=200, random_state=42))
])

pipeSVM = Pipeline([
    ('tfidf',TfidfVectorizer()),
    ('model',SVC())
])

#los entrenamos
pipeLR.fit(trainx,trainy)
pipeRF.fit(trainx,trainy)
pipeSVM.fit(trainx,trainy)

#los testeamos enviando las metricas a sus respectivos txt para decidir cual usar
sys.stdout = open("metricasSpam/LogisticRegression.txt", "w")
evaluate_clf(pipeLR,trainx,trainy,testx,testy)
sys.stdout.close()
sys.stdout = open("metricasSpam/RandomForest.txt", "w")
evaluate_clf(pipeRF,trainx,trainy,testx,testy)
sys.stdout.close()
sys.stdout = open("metricasSpam/SVM.txt", "w")
evaluate_clf(pipeSVM,trainx,trainy,testx,testy)
sys.stdout.close()

#exportamos el que nos cuadre mas
joblib.dump(pipeLR, "models/SpamModelLR.joblib")

