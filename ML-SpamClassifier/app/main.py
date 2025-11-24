from fastapi import FastAPI, APIRouter
from app.routers import mail
app = FastAPI()

app.include_router(mail.router, prefix="/mail",tags=["mail"])

@app.get("/")
def root():
    return {"status":"API status OK"}



