"""Schémas Pydantic pour la prédiction ETA."""
from pydantic import BaseModel
from typing import Optional


class EtaPredictionRequest(BaseModel):
    driver_id: str
    tour_id: str
    current_latitude: float
    current_longitude: float
    destination_latitude: float
    destination_longitude: float
    stops_remaining: int
    hour_of_day: int = 10
    day_of_week: int = 1
    weather: str = "CLEAR"
    historical_avg_speed_kmh: float = 30.0


class EtaPredictionResponse(BaseModel):
    driver_id: str
    tour_id: str
    eta_minutes: int
    eta_timestamp: str
    confidence: float
    model_version: str


class EtaBatchRequest(BaseModel):
    predictions: list[EtaPredictionRequest]


class EtaBatchResponse(BaseModel):
    predictions: list[EtaPredictionResponse]