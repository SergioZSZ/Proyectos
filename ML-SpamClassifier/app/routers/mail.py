from fastapi import APIRouter, HTTPException, status
from app import schemas
import joblib
from app.schemas.mail import Tipo

router = APIRouter()
model = joblib.load("model/models/SpamModelSVM.joblib")

@router.post("/isSpam",response_model=schemas.MailOutput, status_code=status.HTTP_200_OK)
async def isSpam(mensaje_input: schemas.MailInput):
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