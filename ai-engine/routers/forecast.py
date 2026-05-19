"""
Forecast Router — Demand forecasting.
Implementation complète avec Prophet en Semaine 9.
"""
from fastapi import APIRouter

router = APIRouter()


@router.get("/health")
async def health():
    return {"router": "forecast", "status": "ready"}