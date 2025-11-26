import os
import sys
import time
import joblib
import pandas as pd

from model.funciones_auxiliares import clean_text,evaluate_clf
from model.funciones_auxiliares import create_model
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
x=df["text"]
y= df["label_num"]

trainx,testx,trainy,testy = train_test_split(x,y,test_size=0.2, random_state=42, stratify=y)


# 
pipeS = Pipeline([
    ("vectoricer",TfidfVectorizer(
        max_features = 50000,    
        preprocessor=clean_text,
        tokenizer=str.split,
        ngram_range=(1,3),
        max_df=0.9,
        min_df=5)),
    
    ("model", LinearSVC())
])

#entrenamos
pipeS.fit(trainx,trainy)  

#metricas con predict
evaluate_clf("SVC",pipeS, trainx,testx,trainy,testy)

os.makedirs("model",exist_ok=True)
joblib.dump(pipeS,"model/models/modelSVC.joblib")







