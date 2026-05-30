"""Clustering Router — Geographic zone clustering endpoints."""
from fastapi import APIRouter, HTTPException
from schemas.clustering import ClusteringRequest, ClusteringResponse, ClusterInfo
from models.clustering_model import ClusteringModel
from utils.logger import get_logger

logger = get_logger(__name__)
router = APIRouter()
model = ClusteringModel()


@router.post("/zones", response_model=ClusteringResponse)
async def cluster_zones(request: ClusteringRequest) -> ClusteringResponse:
    """Regroupe les adresses de livraison en zones géographiques."""
    try:
        result = model.predict(
            delivery_points=request.delivery_points,
            eps_km=request.eps_km,
            min_samples=request.min_samples,
        )
        return ClusteringResponse(
            clusters=[ClusterInfo(**c) for c in result["clusters"]],
            noise_points=result["noise_points"],
            total_points=result["total_points"],
            model_version=result["model_version"],
        )
    except Exception as e:
        logger.error("Clustering failed", error=str(e))
        raise HTTPException(status_code=500, detail=f"Clustering failed: {str(e)}")


@router.get("/health")
async def health():
    return {"router": "clustering", "status": "ready"}