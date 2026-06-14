"""
Zone Clustering Model — DBSCAN.
Groupe les adresses de livraison en zones géographiques cohérentes.
"""

from utils.logger import get_logger

logger = get_logger(__name__)

MODEL_VERSION = "1.0.0-baseline"


class ClusteringModel:

    def predict(
        self,
        delivery_points: list[dict],
        eps_km: float = 0.5,
        min_samples: int = 10,
    ) -> dict:

        if not delivery_points:
            return {
                "clusters": [],
                "noise_points": 0,
                "total_points": 0,
                "model_version": MODEL_VERSION,
            }

        # Baseline: simple grid clustering
        clusters: dict[str, list[dict]] = {}

        for point in delivery_points:
            lat = point.get("latitude", 0)
            lng = point.get("longitude", 0)

            grid_lat = round(lat * 10) / 10
            grid_lng = round(lng * 10) / 10
            cell_key = f"{grid_lat}_{grid_lng}"

            if cell_key not in clusters:
                clusters[cell_key] = []
            clusters[cell_key].append(point)

        result_clusters = []
        noise_points = 0

        for idx, (cell_key, points) in enumerate(clusters.items()):
            if len(points) < min_samples:
                noise_points += len(points)
                continue

            center_lat = sum(p.get("latitude", 0) for p in points) / len(points)
            center_lng = sum(p.get("longitude", 0) for p in points) / len(points)

            result_clusters.append({
                "cluster_id": idx,
                "center_latitude": round(center_lat, 6),
                "center_longitude": round(center_lng, 6),
                "delivery_count": len(points),
                "zone_name": f"Zone-{idx + 1}",
            })

        logger.info(
            "Clustering complete",
            clusters=len(result_clusters),
            noise_points=noise_points,
        )

        return {
            "clusters": result_clusters,
            "noise_points": noise_points,
            "total_points": len(delivery_points),
            "model_version": MODEL_VERSION,
        }