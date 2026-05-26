"""ETA Router — Estimated Time of Arrival prediction endpoints."""
from fastapi import APIRouter, HTTPException
from schemas.eta import (
    EtaBatchRequest,
    EtaBatchResponse,
    EtaPredictionRequest,
    EtaPredictionResponse,
)
from models.eta_model import EtaModel
from utils.logger import get_logger

logger = get_logger(__name__)
router = APIRouter()
model = EtaModel()


@router.post("/predict", response_model=EtaPredictionResponse)
async def predict_eta(request: EtaPredictionRequest) -> EtaPredictionResponse:
    """Prédit l'ETA pour un driver donné."""
    try:
        result = model.predict(
            current_latitude=request.current_latitude,
            current_longitude=request.current_longitude,
            destination_latitude=request.destination_latitude,
            destination_longitude=request.destination_longitude,
            stops_remaining=request.stops_remaining,
            hour_of_day=request.hour_of_day,
            day_of_week=request.day_of_week,
            weather=request.weather,
            historical_avg_speed_kmh=request.historical_avg_speed_kmh,
        )
        return EtaPredictionResponse(
            driver_id=request.driver_id,
            tour_id=request.tour_id,
            **result,
        )
    except Exception as e:
        logger.error("ETA prediction failed", error=str(e))
        raise HTTPException(status_code=500, detail=f"ETA prediction failed: {str(e)}")


@router.post("/predict/batch", response_model=EtaBatchResponse)
async def predict_eta_batch(request: EtaBatchRequest) -> EtaBatchResponse:
    """Prédit l'ETA pour une liste de drivers."""
    try:
        results = []
        for pred_request in request.predictions:
            result = model.predict(
                current_latitude=pred_request.current_latitude,
                current_longitude=pred_request.current_longitude,
                destination_latitude=pred_request.destination_latitude,
                destination_longitude=pred_request.destination_longitude,
                stops_remaining=pred_request.stops_remaining,
                hour_of_day=pred_request.hour_of_day,
                day_of_week=pred_request.day_of_week,
                weather=pred_request.weather,
                historical_avg_speed_kmh=pred_request.historical_avg_speed_kmh,
            )
            results.append(EtaPredictionResponse(
                driver_id=pred_request.driver_id,
                tour_id=pred_request.tour_id,
                **result,
            ))
        return EtaBatchResponse(predictions=results)
    except Exception as e:
        logger.error("ETA batch prediction failed", error=str(e))
        raise HTTPException(status_code=500, detail=f"Batch prediction failed: {str(e)}")


@router.get("/health")
async def health():
    return {"router": "eta", "status": "ready"}