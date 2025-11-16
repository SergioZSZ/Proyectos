#schemas interactuan con la API
from typing import Optional
from pydantic import BaseModel, EmailStr, Field, AnyUrl

class ParkingsBase(BaseModel):
    identifier: int
    family: int
    name: str
    email: str | None
    streetAddress: str
    postalCode: str
    autonomousCommunity: str
    wikidataAutonomousCommunity: str
    category: str
    categoryCode: str
    country: str
    wikidataCountry: str
    URLIcon: str
    long: float
    lat: float
    openStreetMapCoordinates: str
    city: str
    wikidataCity: str
    EMTParking: bool
    type: str
    
class ParkingsNearby(ParkingsBase):
    distancia_km: float

#necesario para que la bbdd lea los atributos con sus tipos directamente
    class Config:
        from_attributes = True