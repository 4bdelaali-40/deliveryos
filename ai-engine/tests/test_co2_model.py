"""Tests for CO2 Prediction Model."""
import pytest
from models.co2_model import Co2Model


def test_co2_model_returns_prediction():
    model = Co2Model()
    result = model.predict(
        distance_km=10.0,
        vehicle_type="CAR",
        load_kg=50.0,
    )
    assert result is not None
    assert result["co2_grams"] > 0
    assert result["co2_kg"] > 0
    assert result["co2_per_km"] > 0
    assert result["model_version"] is not None


def test_electric_vehicle_lower_emissions():
    model = Co2Model()
    car_result = model.predict(distance_km=10.0, vehicle_type="CAR")
    electric_result = model.predict(distance_km=10.0, vehicle_type="ELECTRIC_CAR")
    assert electric_result["co2_grams"] < car_result["co2_grams"]


def test_bike_has_zero_emissions():
    model = Co2Model()
    result = model.predict(distance_km=10.0, vehicle_type="BIKE")
    assert result["co2_grams"] == 0.0
    assert result["co2_kg"] == 0.0


def test_rain_increases_emissions():
    model = Co2Model()
    clear_result = model.predict(
        distance_km=10.0, vehicle_type="CAR", weather="CLEAR"
    )
    rain_result = model.predict(
        distance_km=10.0, vehicle_type="CAR", weather="RAIN"
    )
    assert rain_result["co2_grams"] > clear_result["co2_grams"]


def test_highway_lower_than_urban():
    model = Co2Model()
    urban_result = model.predict(
        distance_km=10.0, vehicle_type="CAR", road_type="URBAN"
    )
    highway_result = model.predict(
        distance_km=10.0, vehicle_type="CAR", road_type="HIGHWAY"
    )
    assert highway_result["co2_grams"] < urban_result["co2_grams"]


def test_batch_prediction():
    model = Co2Model()
    requests = [
        {"distance_km": 5.0, "vehicle_type": "CAR"},
        {"distance_km": 10.0, "vehicle_type": "VAN"},
        {"distance_km": 3.0, "vehicle_type": "BIKE"},
    ]
    results = model.predict_batch(requests)
    assert len(results) == 3
    assert results[2]["co2_grams"] == 0.0