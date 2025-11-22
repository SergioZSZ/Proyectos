from __future__ import annotations
from typing import Iterable, Optional, Sequence, Tuple, Dict, Any
from sqlalchemy.orm import Session
from sqlalchemy import select, update as sa_update, delete as sa_delete, or_
from app.models.favoritos import Favoritos
from app.schemas.parkings import ParkingsBase

# CREATE
def create_favorito(db: Session, p: ParkingsBase) -> Favoritos:
    """
    Crea un Favoritos. `data` debe contener las claves del modelo.
    Si `identifier` es autogenerado, omítelo en `data`.
    """
    data = p.model_dump()
    obj = Favoritos(**data)
    db.add(obj)
    db.commit()
    db.refresh(obj)
    return obj

# READ (uno)
def get_favorito(db: Session, identifier: int) -> Optional[Favoritos]:
    return db.get(Favoritos, identifier)

# LIST (con búsqueda y paginación)
def list_favoritos(
    db: Session,
    q: Optional[str] = None,
    skip: int = 0,
    limit: int = 50,
) -> Sequence[Favoritos]:
    stmt = select(Favoritos)
    if q:
        like = f"%{q}%"
        stmt = stmt.where(
            or_(
                Favoritos.streetAddress.like(like),
                Favoritos.name.ilike(like),
                Favoritos.city.ilike(like),
                Favoritos.category.ilike(like),
                Favoritos.email.ilike(like),
            )
        )
    stmt = stmt.offset(skip).limit(limit)
    return db.execute(stmt).scalars().all()

# COUNT (útil para paginación)
def count_favoritos(db: Session, q: Optional[str] = None) -> int:
    # Uso select(Favoritos).where(...).count() no es portable.
    # Hacemos un COUNT(*) con subquery para consistencia.
    base = select(Favoritos)
    if q:
        like = f"%{q}%"
        base = base.where(
            or_(
                Favoritos.name.ilike(like),
                Favoritos.city.ilike(like),
                Favoritos.category.ilike(like),
                Favoritos.email.ilike(like),
            )
        )
    subq = base.subquery()
    from sqlalchemy import func
    return db.scalar(select(func.count()).select_from(subq)) or 0


# DELETE
def delete_favorito(db: Session, identifier: int):
    """
    Borra el favorito. Devuelve True si existía y se borró, False si no existía.
    """
    stmt = sa_delete(Favoritos).where(Favoritos.identifier == identifier)
    result = db.execute(stmt)
    db.commit()
    return result.rowcount > 0


