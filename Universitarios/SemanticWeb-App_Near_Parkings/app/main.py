from fastapi import FastAPI, APIRouter
from fastapi.staticfiles import StaticFiles
from app.routers import parkings, favoritos

app = FastAPI()

app.include_router(parkings.router, prefix="/parkings", tags=["parkings"])
app.include_router(favoritos.router, prefix="/favoritos", tags=["favoritos"])
