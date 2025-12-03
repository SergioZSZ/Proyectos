import os
import sys
import tensorflow as ts
import pandas as pd

from .funciones_auxiliares.cleanText import clean_text
from .funciones_auxiliares.evaluateclf import evaluemlp

from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from sklearn.feature_extraction.text import TfidfVectorizer

from keras.models import Sequential
from keras import layers
from keras.layers import Dropout,Dense
from keras.callbacks import EarlyStopping


###cargamos csv
DATA_DIR = "model/data/spam_Emails_data.csv"
df = pd.read_csv(DATA_DIR,
                    sep=",",
                    header=0,
                    )
df["text"].fillna("")


X=df["text"].fillna("")
y = df["label"]

#train test split
trainx,testx,trainy,testy = train_test_split(X,y,test_size=0.2,random_state=42,stratify=y)
#train val split
trainx,valx,trainy,valy = train_test_split(trainx,trainy,test_size=0.2,random_state=42,stratify=trainy)

##preprocesamiento
    #encoder labels
encoder = LabelEncoder()
trainy = encoder.fit_transform(trainy)
valy = encoder.transform(valy)
testy = encoder.transform(testy)

classes_names = encoder.classes_

    #tfidfVectorizer
vectorizer = TfidfVectorizer(
    preprocessor=clean_text,
    tokenizer=str.split,
    max_features=20000,
    ngram_range=(1,2),
    max_df=0.9,
    min_df=5
)

trainx = vectorizer.fit_transform(trainx)
valx = vectorizer.transform(valx)
testx = vectorizer.transform(testx)

#modelo y entrenamiento
model = Sequential([
    layers.Input(shape=(trainx.shape[1],)),  # tamaño del vector TF-IDF
    Dense(64,activation="relu"),
    Dropout(0.5),
    Dense(32,activation="relu"),
    Dropout(0.5),
    Dense(1,activation="sigmoid")
])
model.compile(
    optimizer="adam",
    loss="binary_crossentropy"
)
#earlyStopping
early_stopping = EarlyStopping(
    patience=3,
    restore_best_weights=True,
    monitor="val_loss"
)
history = model.fit(
    trainx,trainy,               #entrenadores
    validation_data=(valx,valy), #datos para validar el entrenamiento
    epochs=50,                  #vueltas al dataset, altas debido a earlystopping
    batch_size=32,          #rango de 32-64 es optimo para batchsize
    callbacks=[early_stopping]
)


evalueB(model,trainx,testx,trainy,testy,classes_names)
