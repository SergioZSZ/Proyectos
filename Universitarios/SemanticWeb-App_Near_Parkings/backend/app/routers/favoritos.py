from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query, status
from rdflib import XSD, Literal
from sqlalchemy.orm import Session
from app import schemas, cruds
from app.rdf.parkingsGraph import g, q_buscar_parking_id
from app.db_deps import get_db


router = APIRouter()

##################################################################################3

# Función helper para validar existencia en RDF

def rdf_parking_exists(id: int) -> bool:
    res = False
    results = g.query(q_buscar_parking_id, initBindings={"id": Literal(id, datatype=XSD.int)})
    row = next(iter(results), None)

    if row:
        res = True
    print(res)
    return res
##################################################################################3


# GET /favoritos  -> listado con búsqueda y paginación
@router.get("/", response_model=List[schemas.ParkingsBase])
async def get_favoritos(
    q: Optional[str] = Query(None, description="Texto a buscar en name, city, category, email o calle"),
    skip: int = Query(0, ge=0),
    limit: int = Query(50, ge=1, le=200),
    db: Session = Depends(get_db),
):  
    items = cruds.list_favoritos(db, q=q, skip=skip, limit=limit)
    total = cruds.count_favoritos(db, q=q)
    return cruds.list_favoritos(db, q=q, skip=skip, limit=limit)




#post favs
@router.post("/", response_model=schemas.ParkingsBase, status_code=status.HTTP_201_CREATED)
async def create_favorito(
    data: schemas.ParkingsBase,
    db: Session = Depends(get_db),
):
    # Verifica campos requeridos
    if getattr(data, "identifier", None) is None or getattr(data, "name", None) is None:
        raise HTTPException(
            status_code=422,
            detail="El parking debe incluir 'id' y 'name'."
        )

    # Validar existencia en RDF por id y name
    var = rdf_parking_exists(data.identifier)
    if(var == False):
        raise HTTPException(
            status_code=400,
            detail=f"No existe ningún parking con id={data.identifier} en el RDF."
        )

    #objeto sin mail = Sin email
    if(data.email is None):
        data.email = "Sin email"
        
    obj = cruds.create_favorito(db, data)

    return obj






# DELETE
@router.delete("/{identifier}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_favorito(
    identifier: int,
    db: Session = Depends(get_db),
):
    ok = cruds.delete_favorito(db, identifier)
    if not ok:
        raise HTTPException(status_code=404, detail="Favorito no encontrado")
    # 204 No Content => sin body
    return True