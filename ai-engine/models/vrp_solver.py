"""
VRP Solver — OR-Tools CVRP implementation.
"""
import math
import time
from datetime import datetime, timedelta

from ortools.constraint_solver import pywrapcp, routing_enums_pb2

from schemas.vrp import (
    StopResult,
    TourResult,
    VrpRequest,
    VrpResponse,
)

from utils.logger import get_logger

logger = get_logger(__name__)

DEFAULT_CO2_KG_PER_KM = 0.21


class VrpSolver:

    def solve(self, request: VrpRequest) -> VrpResponse:
        start_time = time.time()

        logger.info(
            "Starting VRP optimization",
            deliveries=len(request.deliveries),
            vehicles=len(request.vehicles),
        )

        all_locations = self._build_locations(request)
        distance_matrix = self._compute_distance_matrix(all_locations)

        num_vehicles = len(request.vehicles)
        depot_indices = list(range(num_vehicles))

        manager = pywrapcp.RoutingIndexManager(
            len(all_locations),
            num_vehicles,
            depot_indices,
            depot_indices,
        )
        routing = pywrapcp.RoutingModel(manager)

        def distance_callback(from_index, to_index):
            from_node = manager.IndexToNode(from_index)
            to_node = manager.IndexToNode(to_index)
            return int(distance_matrix[from_node][to_node] * 1000)

        transit_callback_index = routing.RegisterTransitCallback(distance_callback)
        routing.SetArcCostEvaluatorOfAllVehicles(transit_callback_index)

        self._add_capacity_constraints(routing, manager, request, num_vehicles)
        self._add_time_constraints(routing, manager, request, distance_matrix, num_vehicles)

        search_params = pywrapcp.DefaultRoutingSearchParameters()
        search_params.first_solution_strategy = (
            routing_enums_pb2.FirstSolutionStrategy.PATH_CHEAPEST_ARC
        )
        search_params.local_search_metaheuristic = (
            routing_enums_pb2.LocalSearchMetaheuristic.GUIDED_LOCAL_SEARCH
        )
        search_params.time_limit.seconds = 30

        solution = routing.SolveWithParameters(search_params)
        execution_ms = int((time.time() - start_time) * 1000)

        if solution:
            result = self._build_response(
                routing, manager, solution, request, distance_matrix, execution_ms
            )
            logger.info(
                "VRP solved",
                tours=len(result.tours),
                total_distance_km=result.total_distance_km,
                execution_ms=execution_ms,
            )
            return result

        logger.warning("VRP solver found no solution")
        return VrpResponse(
            tours=[],
            total_distance_km=0.0,
            total_co2_kg=0.0,
            unassigned_delivery_ids=[d.id for d in request.deliveries],
            execution_ms=execution_ms,
            solver_status="NO_SOLUTION",
        )

    def _build_locations(self, request: VrpRequest) -> list[tuple[float, float]]:
        locations = []
        for vehicle in request.vehicles:
            locations.append((vehicle.start_latitude, vehicle.start_longitude))
        for delivery in request.deliveries:
            locations.append((delivery.latitude, delivery.longitude))
        return locations

    def _compute_distance_matrix(
        self, locations: list[tuple[float, float]]
    ) -> list[list[float]]:
        n = len(locations)
        matrix = [[0.0] * n for _ in range(n)]
        for i in range(n):
            for j in range(n):
                if i != j:
                    matrix[i][j] = self._haversine(
                        locations[i][0], locations[i][1],
                        locations[j][0], locations[j][1],
                    ) * 1.3
        return matrix

    def _haversine(self, lat1: float, lon1: float, lat2: float, lon2: float) -> float:
        R = 6371.0
        dlat = math.radians(lat2 - lat1)
        dlon = math.radians(lon2 - lon1)
        a = (
            math.sin(dlat / 2) ** 2
            + math.cos(math.radians(lat1))
            * math.cos(math.radians(lat2))
            * math.sin(dlon / 2) ** 2
        )
        return R * 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

    def _add_capacity_constraints(self, routing, manager, request: VrpRequest, depot_count: int):
        def weight_callback(from_index):
            node = manager.IndexToNode(from_index)
            if node < depot_count:
                return 0
            return int(request.deliveries[node - depot_count].weight_kg * 100)

        weight_callback_index = routing.RegisterUnaryTransitCallback(weight_callback)
        routing.AddDimensionWithVehicleCapacity(
            weight_callback_index,
            0,
            [int(v.capacity_kg * 100) for v in request.vehicles],
            True,
            "Weight",
        )

        def volume_callback(from_index):
            node = manager.IndexToNode(from_index)
            if node < depot_count:
                return 0
            return int(request.deliveries[node - depot_count].volume_m3 * 1000)

        volume_callback_index = routing.RegisterUnaryTransitCallback(volume_callback)
        routing.AddDimensionWithVehicleCapacity(
            volume_callback_index,
            0,
            [int(v.capacity_m3 * 1000) for v in request.vehicles],
            True,
            "Volume",
        )

    def _add_time_constraints(
        self, routing, manager, request: VrpRequest,
        distance_matrix: list[list[float]], depot_count: int
    ):
        avg_speed_kmh = 30.0

        def time_callback(from_index, to_index):
            from_node = manager.IndexToNode(from_index)
            to_node = manager.IndexToNode(to_index)
            travel_min = int((distance_matrix[from_node][to_node] / avg_speed_kmh) * 60)
            service_min = 0
            if to_node >= depot_count:
                service_min = request.deliveries[to_node - depot_count].service_time_min
            return travel_min + service_min

        time_callback_index = routing.RegisterTransitCallback(time_callback)
        routing.AddDimension(time_callback_index, 60, 480, False, "Time")

        time_dimension = routing.GetDimensionOrDie("Time")
        for idx, delivery in enumerate(request.deliveries):
            node_index = manager.NodeToIndex(depot_count + idx)
            if delivery.time_window_start and delivery.time_window_end:
                start_min = self._time_to_minutes(delivery.time_window_start)
                end_min = self._time_to_minutes(delivery.time_window_end)
                time_dimension.CumulVar(node_index).SetRange(start_min, end_min)

    def _time_to_minutes(self, time_str: str) -> int:
        h, m = time_str.split(":")
        return int(h) * 60 + int(m)

    def _build_response(
        self, routing, manager, solution, request: VrpRequest,
        distance_matrix: list[list[float]], execution_ms: int
    ) -> VrpResponse:
        depot_count = len(request.vehicles)
        tours = []
        total_distance = 0.0
        total_co2 = 0.0
        assigned_ids = set()
        start_time = datetime(2024, 1, 1, 8, 0)

        for vehicle_idx, vehicle in enumerate(request.vehicles):
            index = routing.Start(vehicle_idx)
            stops = []
            prev_node = manager.IndexToNode(index)
            current_time = start_time
            tour_distance = 0.0

            while not routing.IsEnd(index):
                index = solution.Value(routing.NextVar(index))
                node = manager.IndexToNode(index)

                if not routing.IsEnd(index):
                    delivery = request.deliveries[node - depot_count]
                    assigned_ids.add(delivery.id)

                    dist_km = distance_matrix[prev_node][node]
                    co2_kg = dist_km * DEFAULT_CO2_KG_PER_KM
                    current_time += timedelta(minutes=int((dist_km / 30.0) * 60))
                    tour_distance += dist_km
                    total_co2 += co2_kg

                    stops.append(StopResult(
                        delivery_id=delivery.id,
                        stop_order=len(stops) + 1,
                        eta=current_time.strftime("%H:%M"),
                        distance_from_prev_km=round(dist_km, 2),
                        co2_from_prev_kg=round(co2_kg, 4),
                    ))

                    current_time += timedelta(minutes=delivery.service_time_min)
                    prev_node = node

            if stops:
                tour_duration = int((current_time - start_time).total_seconds() / 60)
                tours.append(TourResult(
                    vehicle_id=vehicle.id,
                    driver_id=vehicle.driver_id,
                    stops=stops,
                    total_distance_km=round(tour_distance, 2),
                    total_co2_kg=round(sum(s.co2_from_prev_kg for s in stops), 4),
                    total_duration_min=tour_duration,
                ))
                total_distance += tour_distance

        unassigned = [d.id for d in request.deliveries if d.id not in assigned_ids]

        return VrpResponse(
            tours=tours,
            total_distance_km=round(total_distance, 2),
            total_co2_kg=round(total_co2, 4),
            unassigned_delivery_ids=unassigned,
            execution_ms=execution_ms,
            solver_status="OPTIMAL" if not unassigned else "FEASIBLE",
        )