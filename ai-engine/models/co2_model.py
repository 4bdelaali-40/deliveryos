"""
CO2 Prediction Model — XGBoost.
Prédit les émissions CO2 en grammes par km selon les caractéristiques du trajet.
"""
from utils.logger import get_logger

logger = get_logger(__name__)

MODEL_VERSION = "1.0.0-baseline"

# Émissions CO2 de base en g/km par type de véhicule
BASE_CO2_G_PER_KM = {
    "CAR": 180.0,
    "VAN": 250.0,
    "BIKE": 0.0,
    "CARGO_BIKE": 0.0,
    "ELECTRIC_CAR": 50.0,
    "ELECTRIC_VAN": 70.0,
    "MOTORCYCLE": 120.0,
}

# Facteurs correcteurs
ROAD_TYPE_FACTORS = {
    "URBAN": 1.2,
    "HIGHWAY": 0.9,
    "RURAL": 1.0,
}

WEATHER_FACTORS = {
    "CLEAR": 1.0,
    "RAIN": 1.1,
    "SNOW": 1.25,
}

LOAD_FACTOR_PER_100KG = 0.05


class Co2Model:
    """
    Modèle de prédiction CO2.
    Version baseline : formule analytique calibrée.
    Version production (Semaine 5+) : XGBoost entraîné sur données historiques.
    """

    def predict(
        self,
        distance_km: float,
        vehicle_type: str,
        load_kg: float = 0.0,
        avg_speed_kmh: float = 30.0,
        road_type: str = "URBAN",
        weather: str = "CLEAR",
        hour_of_day: int = 10,
        day_of_week: int = 1,
        is_urban: bool = True,
    ) -> dict:
        """Prédit les émissions CO2 pour un trajet donné."""

        base_co2 = BASE_CO2_G_PER_KM.get(vehicle_type.upper(), 180.0)

        road_factor = ROAD_TYPE_FACTORS.get(road_type.upper(), 1.0)
        weather_factor = WEATHER_FACTORS.get(weather.upper(), 1.0)
        load_factor = 1.0 + (load_kg / 100.0) * LOAD_FACTOR_PER_100KG

        # Facteur vitesse — optimal à 80km/h
        if avg_speed_kmh < 20:
            speed_factor = 1.3
        elif avg_speed_kmh < 50:
            speed_factor = 1.1
        elif avg_speed_kmh <= 100:
            speed_factor = 1.0
        else:
            speed_factor = 1.15

        co2_per_km = base_co2 * road_factor * weather_factor * load_factor * speed_factor
        co2_grams = co2_per_km * distance_km
        co2_kg = co2_grams / 1000.0

        logger.info(
            "CO2 prediction",
            vehicle_type=vehicle_type,
            distance_km=distance_km,
            co2_kg=round(co2_kg, 4),
        )

        return {
            "co2_grams": round(co2_grams, 2),
            "co2_kg": round(co2_kg, 4),
            "co2_per_km": round(co2_per_km, 2),
            "vehicle_type": vehicle_type,
            "distance_km": distance_km,
            "model_version": MODEL_VERSION,
        }

    def predict_batch(self, requests: list[dict]) -> list[dict]:
        """Prédit les émissions CO2 pour une liste de trajets."""
        return [self.predict(**req) for req in requests]