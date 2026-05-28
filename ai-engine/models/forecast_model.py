"""
Demand Forecasting Model — Prophet baseline.
Prédit le nombre de livraisons par zone pour les prochains jours.
"""
from datetime import datetime, timedelta
from utils.logger import get_logger

logger = get_logger(__name__)

MODEL_VERSION = "1.0.0-baseline"

DAY_OF_WEEK_FACTORS = {
    0: 1.2,   # Lundi
    1: 1.1,   # Mardi
    2: 1.0,   # Mercredi
    3: 1.1,   # Jeudi
    4: 1.3,   # Vendredi
    5: 0.6,   # Samedi
    6: 0.3,   # Dimanche
}


class ForecastModel:

    def predict(
        self,
        zone: str,
        days_ahead: int,
        historical_data: list[dict],
    ) -> list[dict]:

        if not historical_data:
            avg_deliveries = 50
        else:
            avg_deliveries = sum(
                d.get("deliveries", 50) for d in historical_data
            ) / len(historical_data)

        forecasts = []
        base_date = datetime.now()

        for i in range(1, days_ahead + 1):
            forecast_date = base_date + timedelta(days=i)
            day_of_week = forecast_date.weekday()
            day_factor = DAY_OF_WEEK_FACTORS.get(day_of_week, 1.0)

            predicted = max(0, int(avg_deliveries * day_factor))
            recommended_drivers = max(1, predicted // 15)
            confidence = min(0.95, max(0.60, 0.90 - (i * 0.03)))

            forecasts.append({
                "date": forecast_date.strftime("%Y-%m-%d"),
                "zone": zone,
                "predicted_deliveries": predicted,
                "recommended_drivers": recommended_drivers,
                "confidence": round(confidence, 2),
            })

            logger.info(
                "Forecast",
                zone=zone,
                date=forecast_date.strftime("%Y-%m-%d"),
                predicted=predicted,
            )

        return forecasts