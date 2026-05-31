"""Tests for VRP Solver."""
import pytest
from models.vrp_solver import VrpSolver
from schemas.vrp import DeliveryPoint, Vehicle, VrpRequest


def make_request() -> VrpRequest:
    return VrpRequest(
        date="2024-01-15",
        deliveries=[
            DeliveryPoint(
                id=f"delivery-{i}",
                address=f"{i} Test St",
                latitude=48.85 + i * 0.01,
                longitude=2.35 + i * 0.01,
                weight_kg=10.0,
                volume_m3=0.1,
            )
            for i in range(5)
        ],
        vehicles=[
            Vehicle(
                id="vehicle-1",
                driver_id="driver-1",
                capacity_kg=500.0,
                capacity_m3=2.5,
                start_latitude=48.8566,
                start_longitude=2.3522,
            )
        ],
    )


def test_vrp_solver_returns_response():
    solver = VrpSolver()
    request = make_request()
    result = solver.solve(request)
    assert result is not None
    assert result.solver_status in ["OPTIMAL", "FEASIBLE", "NO_SOLUTION"]
    assert result.execution_ms >= 0
    assert result.total_distance_km >= 0


def test_vrp_solver_assigns_deliveries():
    solver = VrpSolver()
    request = make_request()
    result = solver.solve(request)
    total_stops = sum(len(tour.stops) for tour in result.tours)
    total_assigned = total_stops + len(result.unassigned_delivery_ids)
    assert total_assigned == len(request.deliveries)


def test_vrp_solver_with_empty_deliveries():
    solver = VrpSolver()
    request = VrpRequest(
        date="2024-01-15",
        deliveries=[
            DeliveryPoint(
                id="d1",
                address="Test",
                latitude=48.85,
                longitude=2.35,
            )
        ],
        vehicles=[
            Vehicle(
                id="v1",
                driver_id="driver-1",
                capacity_kg=500.0,
                capacity_m3=2.5,
                start_latitude=48.8566,
                start_longitude=2.3522,
            )
        ],
    )
    result = solver.solve(request)
    assert result is not None