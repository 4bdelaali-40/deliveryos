-- ============================================================
-- DeliveryOS — Database Initialization
-- Ce script s'exécute une seule fois au premier démarrage
-- du container PostgreSQL.
-- ============================================================

-- Extensions PostGIS (géospatial)
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;

-- UUID natif PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Recherche texte rapide (LIKE optimisé)
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Vérification
SELECT name, default_version
FROM pg_available_extensions
WHERE name IN ('postgis', 'uuid-ossp', 'pg_trgm')
ORDER BY name;