"""CO2 Router — Carbon emission prediction endpoints."""
from fastapi import APIRouter, HTTPException
from schemas.co2 import (
    Co2BatchRequest,
    Co2BatchResponse,
    Co2PredictionRequest,
    Co2PredictionResponse,
)
from models.co2_model import Co2Model
from utils.logger import get_logger

logger = get_logger(__name__)
router = APIRouter()
model = Co2Model()


@router.post("/predict", response_model=Co2PredictionResponse)
async def predict_co2(request: Co2PredictionRequest) -> Co2PredictionResponse:
    """Prédit les émissions CO2 pour un trajet."""
    try:
        result = model.predict(
            distance_km=request.distance_km,
            vehicle_type=request.vehicle_type,
            load_kg=request.load_kg,
            avg_speed_kmh=request.avg_speed_kmh,
            road_type=request.road_type,
            weather=request.weather,
            hour_of_day=request.hour_of_day,
            day_of_week=request.day_of_week,
            is_urban=request.is_urban,
        )
        return Co2PredictionResponse(**result)
    except Exception as e:
        logger.error("CO2 prediction failed", error=str(e))
        raise HTTPException(status_code=500, detail=f"Prediction failed: {str(e)}")


@router.post("/predict/batch", response_model=Co2BatchResponse)
async def predict_co2_batch(request: Co2BatchRequest) -> Co2BatchResponse:
    """Prédit les émissions CO2 pour une liste de trajets."""
    try:
        results = []
        for pred_request in request.predictions:
            result = model.predict(
                distance_km=pred_request.distance_km,
                vehicle_type=pred_request.vehicle_type,
                load_kg=pred_request.load_kg,
                avg_speed_kmh=pred_request.avg_speed_kmh,
                road_type=pred_request.road_type,
                weather=pred_request.weather,
                hour_of_day=pred_request.hour_of_day,
                day_of_week=pred_request.day_of_week,
                is_urban=pred_request.is_urban,
            )
            results.append(Co2PredictionResponse(**result))

        total_co2_kg = sum(r.co2_kg for r in results)

        return Co2BatchResponse(
            predictions=results,
            total_co2_kg=round(total_co2_kg, 4),
        )
    except Exception as e:
        logger.error("CO2 batch prediction failed", error=str(e))
        raise HTTPException(status_code=500, detail=f"Batch prediction failed: {str(e)}")


@router.get("/health")
async def health():
    return {"router": "co2", "status": "ready"}