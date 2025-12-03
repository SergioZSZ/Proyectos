import os
import sys
import time
import joblib
import pandas as pd

from model.funciones_auxiliares import clean_text,evaluate_clf, STOPWORDS
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.preprocessing import LabelEncoder
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import LinearSVC
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.impute import SimpleImputer



DATA_DIR = "model/data/spam_Emails_data.csv"
df = pd.read_csv(DATA_DIR,
                    sep=",",
                    header=0,
                    )

df["text"].fillna("")

preprocesado=time.time()
df["clean_text"]= df["text"].apply(clean_text)
finpreprocesado=time.time()
x=df["clean_text"]
y= df["label"]


trainx,testx,trainy,testy = train_test_split(x,y,test_size=0.2, random_state=42, stratify=y)

encoder = LabelEncoder()
trainy = encoder.fit_transform(trainy)
testy = encoder.transform(testy)
# 
pipe = Pipeline([
    ("vectorizer",TfidfVectorizer(
        ngram_range=(1,1),
        #stop_words = STOPWORDS_ENGLISH,
        max_df=0.9,
        min_df=5,
        max_features=50000)),
    
    ("model", LinearSVC(max_iter=3000))
])

#grid
params = [
    #1 linearSVC
    {
        "vectorizer__ngram_range":[(1,1),(1,2)],
        "vectorizer__max_df":[0.9,0.95],
        "vectorizer__min_df":[5,10],
        "vectorizer__max_features":[50000],
        
        "model":[LinearSVC()],
        "model__C":[0.01,0.1,1,10],  #penalización puesta a los errores
        "model__max_iter": [3000, 7000]
    },
    #2 Logistic
   {
        "vectorizer__ngram_range": [(1,1), (1,2)],
        "vectorizer__max_df": [0.9, 0.95],
        "vectorizer__min_df": [5, 10],
        "vectorizer__max_features": [50000],
        # "vectoricer__stop_words": [None, STOPWORDS_ENGLISH],  # opcional

        "model": [LogisticRegression()],
        "model__C": [0.1, 1, 10],
        "model__max_iter": [3000, 7000]
    }
]

#entrenamos el grid con una version mas pequeña del train
txgrid, _, tygrid, _ = train_test_split(
    trainx, trainy,
    train_size=0.2,
    stratify=trainy,
    random_state=42
)
grid = GridSearchCV(pipe,params,cv=5,scoring="f1",n_jobs=-1)

entrenado=time.time()
grid.fit(txgrid,tygrid)  
finentrenado=time.time()

print("Mejores params: ", grid.best_params_)

#entrenamos el mejor modelo encontrado
best_pipe = grid.best_estimator_
entrenado2=time.time()
best_pipe.fit(trainx,trainy)  
finentrenado2=time.time()



best_pipe = grid.best_estimator_
name = str(grid.best_params_.get("model"))
#metricas con predict
evaluado=time.time()
evaluate_clf(name,best_pipe, trainx,testx,trainy,testy)
finevaluado=time.time()



os.makedirs("model/models",exist_ok=True)
joblib.dump(best_pipe,"model/models/modelML.joblib")


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








