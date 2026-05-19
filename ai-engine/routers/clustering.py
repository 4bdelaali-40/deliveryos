"""
Clustering Router — Geographic zone clustering.
Implementation complète avec DBSCAN en Semaine 9.
"""
from fastapi import APIRouter

router = APIRouter()


@router.get("/health")
async def health():
    return {"router": "clustering", "status": "ready"}