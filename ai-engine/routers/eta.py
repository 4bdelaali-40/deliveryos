"""
ETA Router — Estimated Time of Arrival prediction.
Implementation complète avec LSTM en Semaine 8.
"""
from fastapi import APIRouter

router = APIRouter()


@router.get("/health")
async def health():
    return {"router": "eta", "status": "ready"}