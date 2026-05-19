"""
CO2 Router — Carbon emission prediction.
Implementation complète avec XGBoost en Semaine 5.
"""
from fastapi import APIRouter

router = APIRouter()


@router.get("/health")
async def health():
    return {"router": "co2", "status": "ready"}