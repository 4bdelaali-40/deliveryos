"""Schémas Pydantic pour la prédiction CO2."""
from pydantic import BaseModel
from typing import Optional


class Co2PredictionRequest(BaseModel):
    distance_km: float
    vehicle_type: str        # CAR, VAN, BIKE, CARGO_BIKE, ELECTRIC_CAR, ELECTRIC_VAN
    load_kg: float = 0.0
    avg_speed_kmh: float = 30.0
    road_type: str = "URBAN" # URBAN, HIGHWAY, RURAL
    weather: str = "CLEAR"   # CLEAR, RAIN, SNOW
    hour_of_day: int = 10
    day_of_week: int = 1
    is_urban: bool = True


class Co2PredictionResponse(BaseModel):
    co2_grams: float
    co2_kg: float
    co2_per_km: float
    vehicle_type: str
    distance_km: float
    model_version: str


class Co2BatchRequest(BaseModel):
    predictions: list[Co2PredictionRequest]


class Co2BatchResponse(BaseModel):
    predictions: list[Co2PredictionResponse]
    total_co2_kg: float