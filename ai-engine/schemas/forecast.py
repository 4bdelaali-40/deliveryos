"""Schémas Pydantic pour le demand forecasting."""
from pydantic import BaseModel


class ForecastRequest(BaseModel):
    zone: str
    days_ahead: int = 7
    historical_data: list[dict]


class ForecastDay(BaseModel):
    date: str
    zone: str
    predicted_deliveries: int
    recommended_drivers: int
    confidence: float


class ForecastResponse(BaseModel):
    zone: str
    forecasts: list[ForecastDay]
    model_version: str