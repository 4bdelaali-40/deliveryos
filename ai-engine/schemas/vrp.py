"""Schémas Pydantic pour le VRP."""
from pydantic import BaseModel, Field
from typing import Optional


class DeliveryPoint(BaseModel):
    id: str
    address: str
    latitude: float
    longitude: float
    weight_kg: float = 0.0
    volume_m3: float = 0.0
    priority: str = "NORMAL"
    time_window_start: Optional[str] = None
    time_window_end: Optional[str] = None
    service_time_min: int = 5


class Vehicle(BaseModel):
    id: str
    driver_id: str
    capacity_kg: float
    capacity_m3: float
    start_latitude: float
    start_longitude: float
    end_latitude: Optional[float] = None
    end_longitude: Optional[float] = None
    max_duration_min: int = 480


class VrpRequest(BaseModel):
    deliveries: list[DeliveryPoint] = Field(min_length=1)
    vehicles: list[Vehicle] = Field(min_length=1)
    date: str
    optimize_for: str = "DISTANCE"


class StopResult(BaseModel):
    delivery_id: str
    stop_order: int
    eta: str
    distance_from_prev_km: float
    co2_from_prev_kg: float


class TourResult(BaseModel):
    vehicle_id: str
    driver_id: str
    stops: list[StopResult]
    total_distance_km: float
    total_co2_kg: float
    total_duration_min: int


class VrpResponse(BaseModel):
    tours: list[TourResult]
    total_distance_km: float
    total_co2_kg: float
    unassigned_delivery_ids: list[str]
    execution_ms: int
    solver_status: str