"""
VRP Router — Vehicle Routing Problem optimization.
Implementation complète avec OR-Tools en Semaine 4.
"""
from fastapi import APIRouter

router = APIRouter()


@router.get("/health")
async def health():
    return {"router": "vrp", "status": "ready"}