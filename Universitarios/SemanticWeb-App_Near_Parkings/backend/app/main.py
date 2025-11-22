from fastapi import FastAPI, APIRouter
from fastapi.staticfiles import StaticFiles
import uvicorn
from app.routers import parkings, favoritos
import os
app = FastAPI()

app.include_router(parkings.router, prefix="/parkings", tags=["parkings"])
app.include_router(favoritos.router, prefix="/favoritos", tags=["favoritos"])


@app.get("/")
def root():
    return {"status": "Near_Parkings API OK"}


#uvicorn app.main:app --host 0.0.0.0 --port $PORT


