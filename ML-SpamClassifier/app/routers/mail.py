from fastapi import APIRouter, HTTPException, status
from app import schemas
import joblib
from app.schemas.mail import Tipo
from model.funciones_auxiliares import clean_text, create_model
router = APIRouter()
model = joblib.load("model/models/modelTrained.joblib")

@router.post("/predict",response_model=schemas.MailOutput, status_code=status.HTTP_200_OK)
async def predict(mensaje_input: schemas.MailInput):
    tipo_pred: str
    
    
    pred = model.predict([mensaje_input.message])
    if pred[0] == 0:
        tipo_pred = Tipo.ham
    else:
        tipo_pred = Tipo.spam

    mensaje_output = schemas.MailOutput(
        message=mensaje_input.message,
        tipo=tipo_pred
    )
        
    return mensaje_output