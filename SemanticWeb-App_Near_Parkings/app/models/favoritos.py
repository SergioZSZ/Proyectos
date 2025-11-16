#tabla favoritos de la bbdd

from sqlalchemy.orm import DeclarativeBase,Mapped,mapped_column
from sqlalchemy import Boolean, String, Text, Float, Integer
from app.db import Base

class Favoritos(Base):
    __tablename__ = "favoritos"

    #id tabla
    identifier: Mapped[int] = mapped_column(primary_key=True)
    
    family: Mapped[int] = mapped_column(Integer)
    name: Mapped[str] = mapped_column(String)
    email: Mapped[str] = mapped_column(String)
    streetAddress: Mapped[str] = mapped_column(String)
    postalCode: Mapped[str] = mapped_column(String)
    autonomousCommunity: Mapped[str] = mapped_column(String)
    wikidataAutonomousCommunity: Mapped[str] = mapped_column(Text)  # URI, Text porque no tiene máximo de caracteres
    category: Mapped[str] = mapped_column(String)
    categoryCode: Mapped[str] = mapped_column(String)
    country: Mapped[str] = mapped_column(String)
    wikidataCountry: Mapped[str] = mapped_column(Text)  # URI
    URLIcon: Mapped[str] = mapped_column(Text)  # URL
    long: Mapped[float] = mapped_column(Float)
    lat: Mapped[float] = mapped_column(Float)
    openStreetMapCoordinates: Mapped[str] = mapped_column(String)
    city: Mapped[str] = mapped_column(String)
    wikidataCity: Mapped[str] = mapped_column(Text)  # URI
    EMTParking: Mapped[bool] = mapped_column(Boolean)
    type: Mapped[str] = mapped_column(String)



    