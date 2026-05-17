-- ============================================================
-- V1__init_schema.sql
-- DeliveryOS — Initial Database Schema
-- ============================================================

-- ─────────────────────────────────────────
-- ENUM Types
-- ─────────────────────────────────────────
CREATE TYPE user_role AS ENUM (
    'SUPER_ADMIN', 'ADMIN', 'DISPATCHER', 'DRIVER', 'VIEWER'
);

CREATE TYPE delivery_status AS ENUM (
    'CREATED', 'ASSIGNED', 'PICKED_UP', 'IN_TRANSIT',
    'DELIVERED', 'FAILED', 'RETURNED'
);

CREATE TYPE delivery_priority AS ENUM (
    'NORMAL', 'URGENT', 'VIP'
);

CREATE TYPE tour_status AS ENUM (
    'PLANNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'
);

CREATE TYPE vehicle_type AS ENUM (
    'CAR', 'VAN', 'BIKE', 'CARGO_BIKE',
    'ELECTRIC_CAR', 'ELECTRIC_VAN', 'MOTORCYCLE'
);

CREATE TYPE fuel_type AS ENUM (
    'DIESEL', 'PETROL', 'ELECTRIC', 'HYBRID'
);

CREATE TYPE notification_type AS ENUM (
    'ALERT', 'INFO', 'WARNING', 'SUCCESS'
);

CREATE TYPE notification_channel AS ENUM (
    'IN_APP', 'EMAIL', 'SMS'
);

CREATE TYPE carbon_objective_status AS ENUM (
    'ON_TRACK', 'AT_RISK', 'EXCEEDED'
);

CREATE TYPE carbon_period AS ENUM (
    'MONTHLY', 'QUARTERLY', 'YEARLY'
);

-- ─────────────────────────────────────────
-- USERS
-- ─────────────────────────────────────────
CREATE TABLE users (
                       id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       role            user_role NOT NULL,
                       first_name      VARCHAR(100) NOT NULL,
                       last_name       VARCHAR(100) NOT NULL,
                       email           VARCHAR(255) UNIQUE NOT NULL,
                       password_hash   VARCHAR(255) NOT NULL,
                       phone           VARCHAR(20),
                       avatar_url      VARCHAR(500),
                       is_active       BOOLEAN NOT NULL DEFAULT true,
                       mfa_secret      VARCHAR(100),
                       mfa_enabled     BOOLEAN NOT NULL DEFAULT false,
                       failed_attempts INTEGER NOT NULL DEFAULT 0,
                       locked_until    TIMESTAMP WITH TIME ZONE,
                       last_login_at   TIMESTAMP WITH TIME ZONE,
                       created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                       updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- ─────────────────────────────────────────
-- REFRESH TOKENS
-- ─────────────────────────────────────────
CREATE TABLE refresh_tokens (
                                id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                token_hash  VARCHAR(255) UNIQUE NOT NULL,
                                expires_at  TIMESTAMP WITH TIME ZONE NOT NULL,
                                revoked     BOOLEAN NOT NULL DEFAULT false,
                                revoked_at  TIMESTAMP WITH TIME ZONE,
                                ip_address  VARCHAR(45),
                                user_agent  TEXT,
                                created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);

-- ─────────────────────────────────────────
-- VEHICLES
-- ─────────────────────────────────────────
CREATE TABLE vehicles (
                          id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          plate_number        VARCHAR(20) UNIQUE NOT NULL,
                          type                vehicle_type NOT NULL,
                          brand               VARCHAR(100),
                          model               VARCHAR(100),
                          year                INTEGER,
                          capacity_kg         DECIMAL(8,2) NOT NULL CHECK (capacity_kg > 0),
                          capacity_m3         DECIMAL(8,2) NOT NULL CHECK (capacity_m3 > 0),
                          co2_per_km          DECIMAL(8,2) NOT NULL CHECK (co2_per_km >= 0),
                          fuel_type           fuel_type,
                          mileage_km          INTEGER NOT NULL DEFAULT 0,
                          last_revision_date  DATE,
                          next_revision_km    INTEGER,
                          is_available        BOOLEAN NOT NULL DEFAULT true,
                          created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                          updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_vehicles_type ON vehicles(type);
CREATE INDEX idx_vehicles_is_available ON vehicles(is_available);

-- ─────────────────────────────────────────
-- DELIVERIES
-- ─────────────────────────────────────────
CREATE TABLE deliveries (
                            id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            tracking_code       VARCHAR(50) UNIQUE NOT NULL,
                            status              delivery_status NOT NULL DEFAULT 'CREATED',
                            recipient_name      VARCHAR(200) NOT NULL,
                            recipient_phone     VARCHAR(20),
                            recipient_email     VARCHAR(255),
                            address             TEXT NOT NULL,
                            city                VARCHAR(100),
                            postal_code         VARCHAR(20),
                            latitude            DECIMAL(10,8),
                            longitude           DECIMAL(11,8),
                            location            GEOGRAPHY(POINT, 4326),
                            weight_kg           DECIMAL(8,2) CHECK (weight_kg > 0),
                            volume_m3           DECIMAL(8,3) CHECK (volume_m3 > 0),
                            priority            delivery_priority NOT NULL DEFAULT 'NORMAL',
                            time_window_start   TIME,
                            time_window_end     TIME,
                            scheduled_date      DATE,
                            notes               TEXT,
                            qr_code_url         VARCHAR(500),
                            proof_photo_url     VARCHAR(500),
                            signature_url       VARCHAR(500),
                            delivered_at        TIMESTAMP WITH TIME ZONE,
                            delivered_latitude  DECIMAL(10,8),
                            delivered_longitude DECIMAL(11,8),
                            failed_reason       TEXT,
                            attempt_count       INTEGER NOT NULL DEFAULT 0,
                            created_by          UUID REFERENCES users(id),
                            created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                            updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_deliveries_status ON deliveries(status);
CREATE INDEX idx_deliveries_scheduled_date ON deliveries(scheduled_date);
CREATE INDEX idx_deliveries_priority ON deliveries(priority);
CREATE INDEX idx_deliveries_tracking_code ON deliveries(tracking_code);
CREATE INDEX idx_deliveries_location ON deliveries USING GIST(location);

-- ─────────────────────────────────────────
-- TOURS
-- ─────────────────────────────────────────
CREATE TABLE tours (
                       id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       driver_id               UUID REFERENCES users(id),
                       vehicle_id              UUID REFERENCES vehicles(id),
                       date                    DATE NOT NULL,
                       status                  tour_status NOT NULL DEFAULT 'PLANNED',
                       total_distance_km       DECIMAL(10,2),
                       total_co2_kg            DECIMAL(10,3),
                       estimated_duration_min  INTEGER,
                       started_at              TIMESTAMP WITH TIME ZONE,
                       completed_at            TIMESTAMP WITH TIME ZONE,
                       ai_optimized            BOOLEAN NOT NULL DEFAULT false,
                       optimization_gain_km    DECIMAL(10,2),
                       optimization_gain_pct   DECIMAL(5,2),
                       depot_latitude          DECIMAL(10,8),
                       depot_longitude         DECIMAL(11,8),
                       notes                   TEXT,
                       created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                       updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tours_driver_id ON tours(driver_id);
CREATE INDEX idx_tours_date ON tours(date);
CREATE INDEX idx_tours_status ON tours(status);

-- ─────────────────────────────────────────
-- TOUR STOPS
-- ─────────────────────────────────────────
CREATE TABLE tour_stops (
                            id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            tour_id                 UUID NOT NULL REFERENCES tours(id) ON DELETE CASCADE,
                            delivery_id             UUID NOT NULL REFERENCES deliveries(id),
                            stop_order              INTEGER NOT NULL,
                            eta                     TIMESTAMP WITH TIME ZONE,
                            actual_arrival          TIMESTAMP WITH TIME ZONE,
                            distance_from_prev_km   DECIMAL(8,2),
                            co2_from_prev_kg        DECIMAL(8,3),
                            UNIQUE(tour_id, stop_order),
                            UNIQUE(tour_id, delivery_id)
);

CREATE INDEX idx_tour_stops_tour_id ON tour_stops(tour_id);

-- ─────────────────────────────────────────
-- GPS TRACKS
-- ─────────────────────────────────────────
CREATE TABLE gps_tracks (
                            id          BIGSERIAL PRIMARY KEY,
                            driver_id   UUID NOT NULL REFERENCES users(id),
                            tour_id     UUID REFERENCES tours(id),
                            latitude    DECIMAL(10,8) NOT NULL,
                            longitude   DECIMAL(11,8) NOT NULL,
                            location    GEOGRAPHY(POINT, 4326),
                            speed_kmh   DECIMAL(5,2),
                            heading     DECIMAL(5,2),
                            recorded_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_gps_tracks_driver_tour ON gps_tracks(driver_id, tour_id);
CREATE INDEX idx_gps_tracks_recorded_at ON gps_tracks(recorded_at);
CREATE INDEX idx_gps_tracks_location ON gps_tracks USING GIST(location);

-- ─────────────────────────────────────────
-- CO2 RECORDS
-- ─────────────────────────────────────────
CREATE TABLE co2_records (
                             id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             tour_id         UUID REFERENCES tours(id),
                             vehicle_id      UUID REFERENCES vehicles(id),
                             driver_id       UUID REFERENCES users(id),
                             date            DATE NOT NULL,
                             distance_km     DECIMAL(10,2),
                             co2_kg          DECIMAL(10,3),
                             co2_per_km      DECIMAL(8,3),
                             vehicle_type    vehicle_type,
                             predicted_co2   DECIMAL(10,3),
                             model_version   VARCHAR(20),
                             created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_co2_records_driver_id ON co2_records(driver_id);
CREATE INDEX idx_co2_records_date ON co2_records(date);

-- ─────────────────────────────────────────
-- AUDIT LOGS
-- ─────────────────────────────────────────
CREATE TABLE audit_logs (
                            id          BIGSERIAL PRIMARY KEY,
                            user_id     UUID REFERENCES users(id),
                            action      VARCHAR(100) NOT NULL,
                            entity_type VARCHAR(50),
                            entity_id   UUID,
                            old_value   JSONB,
                            new_value   JSONB,
                            ip_address  VARCHAR(45),
                            user_agent  TEXT,
                            created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);

-- ─────────────────────────────────────────
-- NOTIFICATIONS
-- ─────────────────────────────────────────
CREATE TABLE notifications (
                               id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               type        notification_type NOT NULL,
                               channel     notification_channel NOT NULL DEFAULT 'IN_APP',
                               title       VARCHAR(200) NOT NULL,
                               message     TEXT NOT NULL,
                               is_read     BOOLEAN NOT NULL DEFAULT false,
                               read_at     TIMESTAMP WITH TIME ZONE,
                               created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id, is_read);

-- ─────────────────────────────────────────
-- CARBON OBJECTIVES
-- ─────────────────────────────────────────
CREATE TABLE carbon_objectives (
                                   id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   period                  carbon_period NOT NULL,
                                   target_date             DATE NOT NULL,
                                   baseline_co2_kg         DECIMAL(10,3) NOT NULL,
                                   target_reduction_pct    DECIMAL(5,2) NOT NULL,
                                   current_co2_kg          DECIMAL(10,3),
                                   status                  carbon_objective_status NOT NULL DEFAULT 'ON_TRACK',
                                   created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                                   updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- ─────────────────────────────────────────
-- DRIVER BADGES (gamification)
-- ─────────────────────────────────────────
CREATE TABLE driver_badges (
                               id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               driver_id   UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               badge_type  VARCHAR(50) NOT NULL,
                               earned_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                               period      VARCHAR(20)
);

CREATE INDEX idx_driver_badges_driver_id ON driver_badges(driver_id);

-- ─────────────────────────────────────────
-- Auto-update updated_at trigger
-- ─────────────────────────────────────────
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_vehicles_updated_at
    BEFORE UPDATE ON vehicles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_deliveries_updated_at
    BEFORE UPDATE ON deliveries
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tours_updated_at
    BEFORE UPDATE ON tours
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_carbon_objectives_updated_at
    BEFORE UPDATE ON carbon_objectives
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();