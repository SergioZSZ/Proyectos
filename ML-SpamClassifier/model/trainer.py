import os
import sys
import time
import joblib
import pandas as pd

from model.funciones_auxiliares import clean_text,evaluate_clf
from model.funciones_auxiliares import create_model
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer

from sklearn.pipeline import Pipeline
from scikeras.wrappers import KerasClassifier

from keras.callbacks import EarlyStopping
#import matplotlib.pyplot as plt

callbacks = [
    EarlyStopping(
        patience=2,                 # si durante 2 épocas no mejora para
        restore_best_weights=True,  # recupera los pesos óptimos(donde dejo de mejorar)
        monitor="val_loss"          # métrica a vigilar que no mejora
    )
]

DATA_DIR = "model/data/SMSSpamCollection"
df = pd.read_csv(DATA_DIR,
                    sep="\t",
                    header=None,
                    names=["label","message"]
                    )
df = df.dropna() ##borrar filas vacias
#print(df.head())

df["label_num"]=df["label"].map({"ham":0,"spam":1})
x=df["message"]
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
    
    ("model", KerasClassifier(
        model=create_model,      
        epochs=15,                  
        validation_split=0.1,       
        batch_size=32,                                          
        callbacks=callbacks,       
        #class_weight={0: 1.0, 1: 1.2}
    ))
])

#entrenamos
pipeS.fit(trainx,trainy)  

#metricas con predict
evaluate_clf("Sequential",pipeS, trainx,testx,trainy,testy)

os.makedirs("model",exist_ok=True)
joblib.dump(pipeS,"model/models/modelTrained.joblib")

'''
#guardamos el historial asi
keras_step = pipeS.named_steps["model"]
history = keras_step.history_   # History de Keras 

#ver curva de aprendizaje,puede que util en algunos casos
plt.plot(history["accuracy"], label="accuracy")
plt.plot(history["precision"], label="precision")
plt.plot(history["recall"], label="recall")
plt.plot(history["val_loss"], label="val_loss")
plt.plot(history["val_accuracy"], label="val_accuracy")
plt.legend()
plt.show()
'''





