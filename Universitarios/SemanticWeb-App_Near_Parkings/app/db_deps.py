from typing import Generator
from app.db import SessionLocal


'''
Defines una dependencia de FastAPI. El tipo Generator le dice a FastAPI que esta función:

hará algo antes de ceder el control (crear la sesión),

cederá un valor (la sesión) con yield,

y hará algo después cuando termine la petición (cerrar la sesión).
'''
def get_db() -> Generator:
    db = SessionLocal()
    try:
        yield db #CEDE la sesion para hacer peticion a otro código y despues vuelve a este código
    finally:
        db.close() #termina la peticiones
