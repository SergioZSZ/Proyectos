from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, DeclarativeBase
from app.config import settings

#necesario para los models, se tiene que declarar de una clase heredando DeclarativeBase
class Base(DeclarativeBase):
    pass

#motor de conexion a la bbdd, necesario para iniciar sesiones para entrar a ella
#analogía: Engine → el ascensor del edificio: se encarga de mover cosas entre pisos (Python ↔ base de datos).
#Session → una persona que usa ese ascensor: abre la puerta, hace sus cosas y se baja (más abajo lo vemos).

engine = create_engine(settings.DATABASE_URL)

#sesiones, usan el motor para realizar consultas sql           #usamos el motor engine
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

#autocommit  y autoflush a false para que no haga commit tras cada operacion a no ser
#que lo hagas tu en codigo y para que no envie los datos a la bd antes de cada query