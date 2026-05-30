"""Schémas Pydantic pour le clustering géographique."""
from pydantic import BaseModel


class ClusteringRequest(BaseModel):
    delivery_points: list[dict]
    eps_km: float = 0.5
    min_samples: int = 10


class ClusterInfo(BaseModel):
    cluster_id: int
    center_latitude: float
    center_longitude: float
    delivery_count: int
    zone_name: str


class ClusteringResponse(BaseModel):
    clusters: list[ClusterInfo]
    noise_points: int
    total_points: int
    model_version: str