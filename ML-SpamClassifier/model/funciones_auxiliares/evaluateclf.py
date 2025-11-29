
from sklearn.metrics import (classification_report,accuracy_score,f1_score,confusion_matrix,recall_score,precision_score)
import os
import sys

def evaluate_clf(tipo,model,trainx,testx,trainy,testy):
    
    #predicciones del modelo entrenado(del x del entreno y del x del testeo)
    
    if(tipo=="SVC"):
        pred_train = model.predict(trainx)
        pred_test = model.predict(testx)
    else:
        #mlp da probabilidades, hay que pasarlo a enteros
        pred_train = (model.predict(trainx) > 0.5).astype("int32")
        pred_test  = (model.predict(testx) > 0.5).astype("int32")
        pred_train = pred_train.flatten() #aplanado necesario para confusion matrix
        pred_test = pred_test.flatten()
    
    os.makedirs("model/metricasSpam",exist_ok=True)
    
    stdout = sys.stdout
    f = open(f"model/metricasSpam/metricas_{tipo}.txt", "w", encoding="utf-8")
    sys.stdout = f
    
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

    print("\n---- CLASSIFICATION REPORT (TEST) ----")
    print(
        classification_report(
            testy,
            pred_test,
        target_names=["ham", "spam"]
        )
    )
    print("\nMatriz de confusión (test):")
    print(confusion_matrix(testy, pred_test))
    
    f.close()
    sys.stdout = stdout
    
    
