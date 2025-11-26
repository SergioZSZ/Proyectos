import os
import sys
import time
import joblib
import pandas as pd

from model.funciones_auxiliares import clean_text,evaluate_clf, STOPWORDS_ENGLISH
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import LinearSVC
from sklearn.pipeline import Pipeline



DATA_DIR = "model/data/spam_Emails_data.csv"
df = pd.read_csv(DATA_DIR,
                    sep=",",
                    header=0,
                    )
df = df.dropna() ##borrar filas vacias
#print(df.head())

df["label_num"]=df["label"].map({"Ham":0,"Spam":1})

preprocesado=time.time()
df["clean_text"]= df["text"].apply(clean_text)
finpreprocesado=time.time()
x=df["clean_text"]
y= df["label_num"]

trainx,testx,trainy,testy = train_test_split(x,y,test_size=0.2, random_state=42, stratify=y)


# 
pipeS = Pipeline([
    ("vectoricer",TfidfVectorizer(
        tokenizer=str.split,
        ngram_range=(1,2),
        #stop_words = STOPWORDS_ENGLISH,
        max_df=0.9,
        min_df=5,
        max_features=40000)),
    
    ("model", LinearSVC())
])

#entrenamos
entrenado=time.time()
pipeS.fit(trainx,trainy)  
finentrenado=time.time()


#metricas con predict
evaluado=time.time()
evaluate_clf("SVC",pipeS, trainx,testx,trainy,testy)
finevaluado=time.time()



os.makedirs("model/models",exist_ok=True)
joblib.dump(pipeS,"model/models/modelSVC.joblib")


#dar los minutos y segundos de cada proceso
prep= (finpreprocesado-preprocesado)
entr= (finentrenado-entrenado)
ev= (finevaluado-evaluado)

prep_min, prep_sec= divmod(prep, 60)
entr_min, entr_sec = divmod(entr, 60)
ev_min, ev_sec= divmod(ev, 60)

print(f"prep: {int(prep_min)} min {prep_sec:.2f} s")
print(f"entr: {int(entr_min)} min {entr_sec:.2f} s")
print(f"ev: {int(ev_min)} min {ev_sec:.2f} s")








