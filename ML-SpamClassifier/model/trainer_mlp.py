import tensorflow as tf
from keras.models import Sequential
from keras.layers import Dense, Dropout, Embedding, GlobalAveragePooling1D, TextVectorization


import os
import sys
import time
import joblib
import pandas as pd
from sklearn.preprocessing import LabelEncoder

from model.funciones_auxiliares import evaluate_clf
from sklearn.model_selection import train_test_split

from keras.callbacks import EarlyStopping
#import matplotlib.pyplot as plt

############################################################
callbacks = [
    EarlyStopping(
        patience=3,                 # si durante 2 épocas no mejora para
        restore_best_weights=True,  # recupera los pesos óptimos(donde dejo de mejorar)
        monitor="val_loss"          # métrica a vigilar que no mejora
    )
]

max_tokens = 20000
sequence_length = 200
embedding_dim = 64

vectorizer = TextVectorization(
    max_tokens=max_tokens,
    output_sequence_length=sequence_length,
    standardize="lower_and_strip_punctuation"
)

#ESTA FUNCION DEBE ESTAR CON SUS IMPORTS NECESARIOS EN EL SCRIPT EN EL QUE EXPORTEMOS
model = Sequential([
        vectorizer,
        Embedding(max_tokens,embedding_dim),
        GlobalAveragePooling1D(),
        Dense(32, activation="relu"),
        Dropout(0.4),                         #apaga en cada batch el 40% de neuronas(distintas en cad batch) evita que se aprenda el dataset el modelo
        Dense(16, activation="relu"),
        Dense(1, activation="sigmoid")
    ])
model.compile(                                      #los modelos Seq hay que compilarlos
        optimizer="adam",                               #muy buen optimizador
        loss="binary_crossentropy",                     #es un binario de 0 o 1 
        metrics=["accuracy","precision","recall"])
############################################################
def clean_for_windows(text, encoding="cp1252"):
    # Elimina caracteres que no se pueden guardar en esa codificación
    if not isinstance(text, str):
        return text
    return text.encode(encoding, errors="ignore").decode(encoding)

'''
DATA_DIR = "model/data/SMSSpamCollection"
df = pd.read_csv(DATA_DIR,
                    sep="\t",
                    header=None,
                    names=["label","text"]
                    )
df = df.dropna() ##borrar filas vacias
#print(df.head())
df["text"] = df["text"].map(clean_for_windows)
df["label"] = df["label"].map(clean_for_windows)
x=df["text"]
y= df["label"]
'''

DATA_DIR = "model/data/spam_Emails_data.csv"
df = pd.read_csv(DATA_DIR,
                    sep=",",
                    header=0,
                    )
df = df.dropna() ##borrar filas vacias
#print(df.head())
df["text"] = df["text"].map(clean_for_windows)
df["label"] = df["label"].map(clean_for_windows)
x=df["text"]
y= df["label"]

encoder = LabelEncoder()
y_encoded = encoder.fit_transform(y)


trainx,testx,trainy,testy = train_test_split(x,y_encoded,test_size=0.2, random_state=42, stratify=y)

#adaptamos el trainx 
vectorizer.adapt(trainx)


# tf.data
batch_size = 32
trainx = trainx.to_numpy()
testx = testx.to_numpy()
train_ds = tf.data.Dataset.from_tensor_slices((trainx, trainy)).batch(batch_size).prefetch(tf.data.AUTOTUNE)
test_ds = tf.data.Dataset.from_tensor_slices((testx, testy)).batch(batch_size).prefetch(tf.data.AUTOTUNE)

#entrenamos
model.fit(train_ds, validation_data=test_ds, epochs=10, callbacks=callbacks)



#metricas con predict
evaluate_clf("Sequential",model, trainx,testx,trainy,testy)

os.makedirs("model",exist_ok=True)
model.save("model/models/modelMLP.keras")
#para cargar   model = tf.keras.models.load_model("model/models/modelMLP.keras")

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





