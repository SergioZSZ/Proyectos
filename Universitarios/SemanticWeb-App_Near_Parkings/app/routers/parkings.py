import math
from typing import Optional
from fastapi import APIRouter, HTTPException, Query
from app import schemas
from app.rdf.parkingsGraph import g, q_buscar_parking, q_buscar_parking_nearby

from rdflib import Literal, XSD

from app.schemas.parkings import ParkingsBase

router = APIRouter()
###########################################################################
#calculo distancia
def haversine(lat1, lon1, lat2, lon2):
    R = 6371  # Radio de la Tierra en km
    phi1, phi2 = math.radians(lat1), math.radians(lat2)
    dphi = math.radians(lat2 - lat1)
    dlambda = math.radians(lon2 - lon1)
    a = math.sin(dphi / 2) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(dlambda / 2) ** 2
    return R * 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
###########################################################################
@router.get("/", response_model=list[ParkingsBase], tags=["parkings"])
async def list_parkings(
    q: Optional[str] = Query(None, description="Búsqueda por nombre, calle o ciudad (contiene)"),
    emt: Optional[bool] = Query(None, description="Filtra por parkings EMT (true/false)"),
):
    limit: int = 100
    offset: int = 0
    results = g.query(q_buscar_parking)
    rows = []

    for r in results:


        _id = r.id
        _fam = r.family
        _name = r.name
        _mail = r.email
        _icon = r.URLIcon
        _addr = r.streetAddress
        _pc = r.postalCode
        _ac_name = r.nautonomousCommunity
        _ac_wd = r.wikidataAutonomousCommunity
        _cat = r.category
        _catc = r.categoryCode
        _cty_name = r.ncity
        _cty_wd = r.wikidataCity
        _country_name = r.ncountry
        _country_wd = r.wikidataCountry
        _lat = float(r.lat)
        _lon = float(r.long)
        _emt = bool(r.EMTParking)
        _type = r.type
        _osm = r.openStreetMapCoordinates

        # filtros básicos
        if q:
            qlow = q.strip().lower()
            if not any([
                (_name or "").lower().find(qlow) >= 0,
                (_addr or "").lower().find(qlow) >= 0,
                (_cty_name or "").lower().find(qlow) >= 0,
            ]):
                continue
        if emt is not None and _emt != emt:
            continue

        rows.append(ParkingsBase(
            identifier=_id,
            family=int(_fam),
            name=_name,
            email=_mail,
            streetAddress=_addr,
            postalCode=_pc ,
            autonomousCommunity=_ac_name,
            wikidataAutonomousCommunity=str(_ac_wd),
            category=_cat ,
            categoryCode=str(_catc),
            country=_country_name,
            wikidataCountry=str(_country_wd ),
            URLIcon=str(_icon ),
            long=float(_lon),
            lat=float(_lat),
            openStreetMapCoordinates=str(_osm),
            city=_cty_name,
            wikidataCity=str(_cty_wd),
            EMTParking=_emt,
            type=_type,
        ))

    # no se lanza 404: si no hay coincidencias, devuelve lista vacía
    end = offset + limit
    return rows[offset:end]








@router.get("/nearby", response_model=list[schemas.ParkingsNearby])
async def get_nearby_parkings(
    lat: float,
    lon: float,
    limit: int = Query(5, ge=1, le=50),
):

    results = g.query(q_buscar_parking_nearby)   

    parkings = []
    for r in results:
            plat = float(r.lat)
            plon = float(r.long)
            dist = haversine(lat, lon, plat, plon)
            res2 = g.query(q_buscar_parking, initBindings={"id": Literal(r.id, datatype=XSD.int)})
            r2 = next(iter(res2),None)
            
            parking = schemas.ParkingsNearby(
                identifier = r.id,
                family = str(r2.family),
                name = str(r2.name),
                email= str(r2.email or "Sin mail"),    
                streetAddress=str(r2.streetAddress),
                postalCode=str(r2.postalCode),
                autonomousCommunity=str(r2.nautonomousCommunity),
                wikidataAutonomousCommunity=str(r2.wikidataAutonomousCommunity),
                category=str(r2.category),
                categoryCode=str(r2.categoryCode),
                country=str(r2.ncountry),
                wikidataCountry=str(r2.wikidataCountry),
                URLIcon = str(r2.URLIcon),
                long=float(r2.long),
                lat=float(r2.lat),
                openStreetMapCoordinates=str(r2.openStreetMapCoordinates),
                city=str(r2.ncity),
                wikidataCity=str(r2.wikidataCity),
                EMTParking=bool(r2.EMTParking),
                type= str(r2.type),
                distancia_km = round(dist,2)
            )
            parkings.append(parking)


    if not parkings:
        raise HTTPException(status_code=404, detail="No se encontraron parkings con coordenadas válidas.")

    # Ordenar por distancia ascendente y devolver los n primeros
    parkings.sort(key=lambda p: (p.distancia_km is None, p.distancia_km))
    return parkings[:limit]







@router.get("/{id}",response_model=schemas.ParkingsBase)
async def getParking(id: int):

    results = g.query(q_buscar_parking, initBindings={"id": Literal(id, datatype=XSD.int)})
    
    primer_elem = next(iter(results), None)

    if not primer_elem:
        raise HTTPException(status_code=404, detail="Parking no encontrado")


    parking = schemas.ParkingsBase(
    identifier = id,
    family = str(primer_elem.family),
    name = str(primer_elem.name),
    email= str(primer_elem.email or "Sin mail"),    
    streetAddress=str(primer_elem.streetAddress),
    postalCode=str(primer_elem.postalCode),
    autonomousCommunity=str(primer_elem.nautonomousCommunity),
    wikidataAutonomousCommunity=str(primer_elem.wikidataAutonomousCommunity),
    category=str(primer_elem.category),
    categoryCode=str(primer_elem.categoryCode),
    country=str(primer_elem.ncountry),
    wikidataCountry=str(primer_elem.wikidataCountry),
    URLIcon = str(primer_elem.URLIcon),
    long=float(primer_elem.long),
    lat=float(primer_elem.lat),
    openStreetMapCoordinates=str(primer_elem.openStreetMapCoordinates),
    city=str(primer_elem.ncity),
    wikidataCity=str(primer_elem.wikidataCity),
    EMTParking=bool(primer_elem.EMTParking),
    type= str(primer_elem.type)
    )
    return parking