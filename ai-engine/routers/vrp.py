"""VRP Router — Route optimization endpoints."""
from fastapi import APIRouter, HTTPException
from schemas.vrp import VrpRequest, VrpResponse
from models.vrp_solver import VrpSolver
from utils.logger import get_logger

logger = get_logger(__name__)
router = APIRouter()
solver = VrpSolver()


@router.post("/optimize", response_model=VrpResponse)
async def optimize_routes(request: VrpRequest) -> VrpResponse:
    """
    Optimise les tournées de livraison avec OR-Tools CVRP.
    Contraintes : capacité véhicule, fenêtres de temps, durée max.
    """
    try:
        logger.info(
            "VRP optimization requested",
            deliveries=len(request.deliveries),
            vehicles=len(request.vehicles),
        )
        result = solver.solve(request)
        return result
    except Exception as e:
        logger.error("VRP optimization failed", error=str(e))
        raise HTTPException(status_code=500, detail=f"Optimization failed: {str(e)}")


@router.get("/health")
async def health():
    return {"router": "vrp", "status": "ready"}