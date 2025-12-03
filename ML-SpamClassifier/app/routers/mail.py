from fastapi import APIRouter, HTTPException, status
from app import schemas
import joblib
from app.schemas.mail import Tipo
import tensorflow as tf
router = APIRouter()

pipelsvc = joblib.load("model/models/modelML.joblib")

vectorizer = joblib.load("model/models/modelMLP/vectorizer.joblib")
model = tf.keras.models.load_model("model/models/modelMLP/model.keras")


@router.post("/predictMLP",response_model=schemas.MailOutput, status_code=status.HTTP_200_OK)
async def predict_mlp(mensaje_input: schemas.MailInput):
    tipo_pred: str
    x_vec = vectorizer.transform([mensaje_input.message])
    x_dense = x_vec.toarray()
    
    pred = model.predict(x_dense)
    prob = float(pred[0][0])
    
    if prob < 0.5:
        tipo_pred = Tipo.ham
    else:
        tipo_pred = Tipo.spam

    mensaje_output = schemas.MailOutput(
        message=mensaje_input.message,
        tipo=tipo_pred
    )
        
    return mensaje_output


@router.post("/predictLSVC",response_model=schemas.MailOutput, status_code=status.HTTP_200_OK)
async def predict_lsvc(mensaje_input: schemas.MailInput):
    tipo_pred: str
    pred = pipelsvc.predict([mensaje_input.message])
    
    if pred[0] == 0:
        tipo_pred = Tipo.ham
    else:
        tipo_pred = Tipo.spam

    mensaje_output = schemas.MailOutput(
        message=mensaje_input.message,
        tipo=tipo_pred
    )
        
    return mensaje_output