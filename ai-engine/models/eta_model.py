"""
ETA Prediction Model — LSTM baseline.
Prédit le temps restant en minutes pour compléter une tournée.
Version baseline : formule analytique.
Version production : LSTM entraîné sur données historiques GPS.
"""
import math
from datetime import datetime, timedelta
from utils.logger import get_logger

logger = get_logger(__name__)

MODEL_VERSION = "1.0.0-baseline"

WEATHER_SPEED_FACTORS = {
    "CLEAR": 1.0,
    "RAIN": 0.85,
    "SNOW": 0.65,
}

HOUR_TRAFFIC_FACTORS = {
    range(0, 6): 1.2,
    range(6, 9): 0.7,
    range(9, 11): 0.9,
    range(11, 14): 0.85,
    range(14, 17): 0.9,
    range(17, 20): 0.7,
    range(20, 24): 1.1,
}


class EtaModel:

    def predict(
        self,
        current_latitude: float,
        current_longitude: float,
        destination_latitude: float,
        destination_longitude: float,
        stops_remaining: int,
        hour_of_day: int = 10,
        day_of_week: int = 1,
        weather: str = "CLEAR",
        historical_avg_speed_kmh: float = 30.0,
    ) -> dict:

        distance_km = self._haversine(
            current_latitude, current_longitude,
            destination_latitude, destination_longitude,
        ) * 1.3

        weather_factor = WEATHER_SPEED_FACTORS.get(weather.upper(), 1.0)
        traffic_factor = self._get_traffic_factor(hour_of_day)
        effective_speed = historical_avg_speed_kmh * weather_factor * traffic_factor

        travel_minutes = int((distance_km / effective_speed) * 60)
        service_minutes = stops_remaining * 5
        total_minutes = travel_minutes + service_minutes

        confidence = min(0.95, max(0.60, 1.0 - (stops_remaining * 0.05)))

        eta_timestamp = (
            datetime.now() + timedelta(minutes=total_minutes)
        ).strftime("%Y-%m-%dT%H:%M:%S")

        logger.info(
            "ETA prediction",
            distance_km=round(distance_km, 2),
            total_minutes=total_minutes,
            confidence=round(confidence, 2),
        )

        return {
            "eta_minutes": total_minutes,
            "eta_timestamp": eta_timestamp,
            "confidence": round(confidence, 2),
            "model_version": MODEL_VERSION,
        }

    def _haversine(
        self, lat1: float, lon1: float, lat2: float, lon2: float
    ) -> float:
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

    def _get_traffic_factor(self, hour: int) -> float:
        for hour_range, factor in HOUR_TRAFFIC_FACTORS.items():
            if hour in hour_range:
                return factor
        return 1.0