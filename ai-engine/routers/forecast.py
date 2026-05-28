"""Forecast Router — Demand forecasting endpoints."""
from fastapi import APIRouter, HTTPException
from schemas.forecast import ForecastRequest, ForecastResponse, ForecastDay
from models.forecast_model import ForecastModel
from utils.logger import get_logger

logger = get_logger(__name__)
router = APIRouter()
model = ForecastModel()


@router.post("/predict", response_model=ForecastResponse)
async def predict_demand(request: ForecastRequest) -> ForecastResponse:
    """Prédit la demande de livraisons pour une zone donnée."""
    try:
        forecasts = model.predict(
            zone=request.zone,
            days_ahead=request.days_ahead,
            historical_data=request.historical_data,
        )
        return ForecastResponse(
            zone=request.zone,
            forecasts=[ForecastDay(**f) for f in forecasts],
            model_version="1.0.0-baseline",
        )
    except Exception as e:
        logger.error("Forecast failed", error=str(e))
        raise HTTPException(status_code=500, detail=f"Forecast failed: {str(e)}")


@router.get("/health")
async def health():
    return {"router": "forecast", "status": "ready"}