
from sklearn.metrics import (accuracy_score,f1_score,confusion_matrix,recall_score,precision_score)
import os
import sys

def evaluate_clf(tipo,model,trainx,testx,trainy,testy):
    
    #predicciones del modelo entrenado(del x del entreno y del x del testeo)
    
    pred_train = model.predict(trainx)
    pred_test = model.predict(testx)
    
    os.makedirs("metricasSpam",exist_ok=True)
    
    stdout = sys.stdout
    sys.stdout = open("model/metricasSpam/metricas_"+tipo+".txt","w")
    
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
    
    sys.stdout= stdout