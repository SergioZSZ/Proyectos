import os
import sys
import time
import joblib
import pandas as pd
from scipy.stats import loguniform

from model.funciones_auxiliares import clean_text_MLP,evaluate_clf
from sklearn.model_selection import RandomizedSearchCV, train_test_split, GridSearchCV
from sklearn.preprocessing import LabelEncoder
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import LinearSVC
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline


#cargamos dataset
DATA_DIR = "model/data/spam_Emails_data.csv"
df = pd.read_csv(DATA_DIR,
                    sep=",",
                    header=0,
                    )
#rellenamos vacios
df["text"]=df["text"].fillna("")

#preprocesado para medir tiempo
preprocesado=time.time()
df["clean_text"]= df["text"].apply(clean_text_MLP)
finpreprocesado=time.time()
x=df["clean_text"]
y= df["label"]

#separacion traintest
trainx,testx,trainy,testy = train_test_split(x,y,test_size=0.2, random_state=42, stratify=y)

#codificacion labels
encoder = LabelEncoder()
trainy = encoder.fit_transform(trainy)
testy = encoder.transform(testy)

#pipe con tfidf vectorizer y modelo(valores basics antes del gridSearch)
pipe = Pipeline([
    ("vectorizer",TfidfVectorizer(
        ngram_range=(1,1),
        max_df=0.9,
        min_df=5,
        max_features=50000)),
    
    ("model", LinearSVC(max_iter=3000))
])

# parámetros RandomSearch
param_dist = [
    # 1) LinearSVC
    {
        "vectorizer__ngram_range": [(1,1), (1,2)],
        "vectorizer__max_df": [0.9, 0.95],
        "vectorizer__min_df": [5, 10],
        "vectorizer__max_features": [50000],

        "model": [LinearSVC()],
        "model__C": loguniform(1e-2, 10),   # sustituye [0.01, 0.1, 1, 10]
        "model__max_iter": [3000, 7000]
    },

    # 2) Logistic Regression
    {
        "vectorizer__ngram_range": [(1,1), (1,2)],
        "vectorizer__max_df": [0.9, 0.95],
        "vectorizer__min_df": [5, 10],
        "vectorizer__max_features": [50000],

        "model": [LogisticRegression()],
        "model__C": loguniform(1e-1, 10),   # sustituye [0.1, 1, 10]
        "model__max_iter": [3000, 7000]
    }
]

# subset para la búsqueda
txgrid, _, tygrid, _ = train_test_split(
    trainx, trainy,
    train_size=0.2,
    stratify=trainy,
    random_state=42
)

# RandomizedSearchCV
random_search = RandomizedSearchCV(
    estimator=pipe,
    param_distributions=param_dist,
    n_iter=25,              # pruebas totales (profesional: 20–50)
    cv=5,
    scoring="f1",
    n_jobs=-1,
    random_state=42
)

entrenado = time.time()
random_search.fit(txgrid, tygrid)
finentrenado = time.time()

print("Mejores params:", random_search.best_params_)

#entrenamos el mejor modelo encontrado
best_pipe = random_search.best_estimator_
entrenado2=time.time()
best_pipe.fit(trainx,trainy)  
finentrenado2=time.time()



name = str(random_search.best_params_.get("model"))
#metricas con predict
evaluado=time.time()
evaluate_clf(name,best_pipe, trainx,testx,trainy,testy)
finevaluado=time.time()



os.makedirs("model/models",exist_ok=True)
os.makedirs("model/models/modelML",exist_ok=True)
joblib.dump(best_pipe,"model/models/modelML/modelML.joblib")


#dar los minutos y segundos de cada proceso
prep= (finpreprocesado-preprocesado)
entr= (finentrenado-entrenado)
entr2= (finentrenado2-entrenado2)
ev= (finevaluado-evaluado)

prep_min, prep_sec= divmod(prep, 60)
entr_min, entr_sec = divmod(entr, 60)
entr2_min, entr2_sec = divmod(entr2, 60)
ev_min, ev_sec= divmod(ev, 60)

print(f"prep: {int(prep_min)} min {prep_sec:.2f} s")
print(f"entrGrid: {int(entr_min)} min {entr_sec:.2f} s")
print(f"entrPipe: {int(entr2_min)} min {entr2_sec:.2f} s")
print(f"ev: {int(ev_min)} min {ev_sec:.2f} s")








