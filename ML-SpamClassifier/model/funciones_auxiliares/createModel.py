import tensorflow as tf
from keras.models import Sequential
from keras.layers import Dense, Dropout



#ESTA FUNCION DEBE ESTAR CON SUS IMPORTS NECESARIOS EN EL SCRIPT EN EL QUE EXPORTEMOS
def create_model(meta): 
    model = Sequential([
        Dense(16, activation="relu", input_shape=(meta["n_features_in_"],)),
        Dropout(0.4),                         #apaga en cada batch el 40% de neuronas(distintas en cad batch) evita que se aprenda el dataset el modelo
        Dense(8, activation="relu"),
        Dense(1, activation="sigmoid")
    ])
    model.compile(                                      #los modelos Seq hay que compilarlos
        optimizer="adam",                               #muy buen optimizador
        loss="binary_crossentropy",                     #es un binario de 0 o 1 
        metrics=["accuracy",                            #las metricas que quermos guardar
            tf.keras.metrics.Precision(name="precision"),
            tf.keras.metrics.Recall(name="recall")
    ])
    return model
