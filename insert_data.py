"""
DeliveryOS - Script de seeding complet
Insere des donnees realistes pour toutes les tables
"""

import psycopg2
import bcrypt
import uuid
import random
from datetime import datetime, timedelta, date
import sys

# ─── CONFIG ───────────────────────────────────────────────────────────────────
DB_CONFIG = {
    "host": "localhost",
    "port": 5432,
    "dbname": "deliveryos",
    "user": "deliveryos",
    "password": "deliveryos_secret",
}

# ─── HELPERS ──────────────────────────────────────────────────────────────────
def uid():
    return str(uuid.uuid4())

def hash_pw(pw):
    return bcrypt.hashpw(pw.encode(), bcrypt.gensalt(10)).decode()

def rand_date(days_back=90, days_forward=30):
    offset = random.randint(-days_back, days_forward)
    return (date.today() + timedelta(days=offset)).isoformat()

def rand_ts(days_back=90, days_forward=0):
    offset = random.randint(-days_back * 24 * 60, days_forward * 24 * 60)
    return datetime.utcnow() + timedelta(minutes=offset)

def past_ts(days_back=90):
    offset = random.randint(1, days_back * 24 * 60)
    return datetime.utcnow() - timedelta(minutes=offset)

# ─── DONNEES DE BASE ──────────────────────────────────────────────────────────

# Villes et coordonnees (region parisienne)
VILLES = [
    ("Paris 1er",   "75001", 48.8606,  2.3475),
    ("Paris 11e",   "75011", 48.8589,  2.3796),
    ("Paris 15e",   "75015", 48.8422,  2.2945),
    ("Paris 18e",   "75018", 48.8917,  2.3440),
    ("Boulogne",    "92100", 48.8352,  2.2409),
    ("Vincennes",   "94300", 48.8479,  2.4391),
    ("Montreuil",   "93100", 48.8638,  2.4481),
    ("Saint-Denis", "93200", 48.9362,  2.3567),
    ("Versailles",  "78000", 48.8044,  2.1204),
    ("Creteil",     "94000", 48.7848,  2.4559),
    ("Nanterre",    "92000", 48.8924,  2.2070),
    ("Levallois",   "92300", 48.8946,  2.2874),
]

PRENOMS = ["Lucas", "Emma", "Hugo", "Lea", "Thomas", "Camille", "Maxime", "Sophie",
           "Antoine", "Julie", "Nicolas", "Marie", "Pierre", "Chloe", "Julien", "Laura",
           "Alexandre", "Manon", "Romain", "Pauline", "Kevin", "Amelie", "Mehdi", "Sarah"]

NOMS = ["Martin", "Bernard", "Dubois", "Thomas", "Robert", "Richard", "Petit", "Durand",
        "Leroy", "Moreau", "Simon", "Laurent", "Lefebvre", "Michel", "Garcia", "David",
        "Bertrand", "Roux", "Vincent", "Fournier", "Morel", "Girard", "Andre", "Lefevre"]

VEHICULES_DATA = [
    ("EL-001-AA", "ELECTRIC_CAR",  "Renault",  "Zoe",       2023, 500.0,  2.5, 0.0,   "ELECTRIC"),
    ("VA-002-BB", "VAN",           "Renault",  "Master",    2021, 1500.0, 12.0, 250.0, "DIESEL"),
    ("CA-003-CC", "CAR",           "Peugeot",  "208",       2022, 300.0,  1.8, 180.0, "PETROL"),
    ("EL-004-DD", "ELECTRIC_CAR",  "Tesla",    "Model 3",   2023, 450.0,  2.2, 0.0,   "ELECTRIC"),
    ("VA-005-EE", "VAN",           "Citroen",  "Jumper",    2020, 1800.0, 14.0, 260.0, "DIESEL"),
    ("CB-006-FF", "CARGO_BIKE",    "Bullit",   "Cargo",     2023, 100.0,  0.3, 0.0,   "ELECTRIC"),
    ("CA-007-GG", "CAR",           "Toyota",   "Yaris",     2022, 280.0,  1.6, 160.0, "HYBRID"),
    ("EL-008-HH", "ELECTRIC_VAN",  "Renault",  "Kangoo E",  2023, 800.0,  4.0, 0.0,   "ELECTRIC"),
]

STATUTS_LIVRAISON = ["CREATED", "ASSIGNED", "PICKED_UP", "IN_TRANSIT", "DELIVERED", "FAILED", "RETURNED"]
STATUTS_POIDS = [70, 10, 5, 5, 75, 10, 5]  # distribution realiste

BADGE_TYPES = ["FIRST_DELIVERY", "SPEED_STAR", "ECO_DRIVER", "PERFECT_WEEK",
               "100_DELIVERIES", "ZERO_FAILED", "TOP_RATED"]


def connect():
    return psycopg2.connect(**DB_CONFIG)


def seed_drivers(cur, admin_id, dispatcher_id):
    """Insere 8 chauffeurs"""
    print("  Insertion des chauffeurs...")
    driver_ids = []

    pw_hash = hash_pw("password123")
    for i in range(8):
        did = uid()
        prenom = PRENOMS[i]
        nom = NOMS[i]
        email = f"{prenom.lower()}.{nom.lower()}@deliveryos.fr"

        cur.execute("""
            INSERT INTO users (id, role, first_name, last_name, email, password_hash, phone, is_active,
                               mfa_enabled, failed_attempts, last_login_at, created_at, updated_at)
            VALUES (%s, 'DRIVER', %s, %s, %s, %s, %s, true, false, 0, %s, %s, %s)
            ON CONFLICT (email) DO UPDATE SET role = 'DRIVER'
            RETURNING id
        """, (
            did, prenom, nom, email, pw_hash,
            f"+336{random.randint(10000000, 99999999)}",
            past_ts(30), past_ts(60), past_ts(60)
        ))
        row = cur.fetchone()
        if row:
            driver_ids.append(row[0])

    print(f"    {len(driver_ids)} chauffeurs inseres")
    return driver_ids


def seed_vehicles(cur):
    """Insere 8 vehicules"""
    print("  Insertion des vehicules...")
    vehicle_ids = []

    for plate, vtype, brand, model, year, cap_kg, cap_m3, co2, fuel in VEHICULES_DATA:
        vid = uid()
        mileage = random.randint(5000, 80000)
        last_rev = (date.today() - timedelta(days=random.randint(30, 365))).isoformat()
        next_rev = mileage + random.randint(10000, 30000)

        cur.execute("""
            INSERT INTO vehicles (id, plate_number, type, brand, model, year,
                                  capacity_kg, capacity_m3, co2_per_km, fuel_type,
                                  mileage_km, last_revision_date, next_revision_km,
                                  is_available, created_at, updated_at)
            VALUES (%s, %s, %s::vehicle_type, %s, %s, %s, %s, %s, %s, %s::fuel_type,
                    %s, %s, %s, true, NOW(), NOW())
            ON CONFLICT (plate_number) DO UPDATE SET brand = EXCLUDED.brand
            RETURNING id
        """, (vid, plate, vtype, brand, model, year,
              cap_kg, cap_m3, co2, fuel,
              mileage, last_rev, next_rev))
        row = cur.fetchone()
        if row:
            vehicle_ids.append(row[0])

    print(f"    {len(vehicle_ids)} vehicules inseres")
    return vehicle_ids


def seed_deliveries(cur, admin_id, count=120):
    """Insere des livraisons realistes"""
    print(f"  Insertion de {count} livraisons...")
    delivery_ids = []

    statuts = random.choices(STATUTS_LIVRAISON, weights=STATUTS_POIDS, k=count)

    for i, statut in enumerate(statuts):
        did = uid()
        tracking = f"DOS-{2024000 + i:07d}"
        ville = random.choice(VILLES)
        prenom_client = random.choice(PRENOMS)
        nom_client = random.choice(NOMS)

        # Coordonnees avec leger bruit
        lat = ville[2] + random.uniform(-0.02, 0.02)
        lng = ville[3] + random.uniform(-0.02, 0.02)

        # Date planifiee
        delta = random.randint(-45, 15)
        scheduled = (date.today() + timedelta(days=delta)).isoformat()

        # Date de livraison si DELIVERED
        delivered_at = None
        if statut == "DELIVERED":
            delivered_at = datetime.utcnow() - timedelta(days=random.randint(0, 45))

        priority = random.choices(["NORMAL", "URGENT", "VIP"], weights=[75, 20, 5])[0]

        cur.execute("""
            INSERT INTO deliveries (
                id, tracking_code, status, recipient_name, recipient_phone, recipient_email,
                address, city, postal_code, latitude, longitude,
                weight_kg, volume_m3, priority,
                time_window_start, time_window_end,
                scheduled_date, delivered_at,
                attempt_count, created_by, created_at, updated_at
            ) VALUES (
                %s, %s, %s::delivery_status, %s, %s, %s,
                %s, %s, %s, %s, %s,
                %s, %s, %s::delivery_priority,
                %s, %s,
                %s, %s,
                %s, %s, %s, %s
            )
            ON CONFLICT (tracking_code) DO NOTHING
            RETURNING id
        """, (
            did, tracking, statut,
            f"{prenom_client} {nom_client}",
            f"+336{random.randint(10000000, 99999999)}",
            f"{prenom_client.lower()}.{nom_client.lower()}@email.fr",
            f"{random.randint(1, 150)} rue {random.choice(['de la Paix', 'du Commerce', 'Victor Hugo', 'Jean Jaures', 'de la Republique'])}",
            ville[0], ville[1],
            round(lat, 8), round(lng, 8),
            round(random.uniform(0.5, 30.0), 2),
            round(random.uniform(0.01, 1.5), 3),
            priority,
            f"{random.randint(8, 11):02d}:00:00",
            f"{random.randint(12, 18):02d}:00:00",
            scheduled,
            delivered_at,
            random.randint(1, 3) if statut in ("FAILED", "RETURNED") else 1,
            admin_id,
            past_ts(60), past_ts(30)
        ))
        row = cur.fetchone()
        if row:
            delivery_ids.append((row[0], statut, lat, lng))

    print(f"    {len(delivery_ids)} livraisons inserees")
    return delivery_ids


def seed_tours(cur, driver_ids, vehicle_ids, delivery_ids, count=25):
    """Insere des tournees avec leurs arrets"""
    print(f"  Insertion de {count} tournees...")
    tour_ids = []

    # Livraisons disponibles pour assignation (ASSIGNED, PICKED_UP, IN_TRANSIT, DELIVERED)
    assignable = [d for d in delivery_ids if d[1] in ("ASSIGNED", "PICKED_UP", "IN_TRANSIT", "DELIVERED")]
    random.shuffle(assignable)
    used_deliveries = set()
    delivery_pool = list(assignable)

    statuts_tour = ["PLANNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"]
    poids_tour = [20, 15, 55, 10]

    for i in range(count):
        tid = uid()
        driver_id = random.choice(driver_ids)
        vehicle_id = random.choice(vehicle_ids)
        statut = random.choices(statuts_tour, weights=poids_tour)[0]

        delta = random.randint(-30, 10)
        tour_date = (date.today() + timedelta(days=delta)).isoformat()

        distance = round(random.uniform(20, 150), 2)
        co2 = round(distance * random.uniform(0.0, 0.25), 3)
        duration = random.randint(120, 480)

        started_at = None
        completed_at = None
        if statut in ("IN_PROGRESS", "COMPLETED"):
            started_at = datetime.utcnow() - timedelta(hours=random.randint(1, 8))
        if statut == "COMPLETED":
            completed_at = started_at + timedelta(minutes=duration)

        ai_optimized = random.random() < 0.6
        gain_km = round(random.uniform(5, 30), 2) if ai_optimized else None
        gain_pct = round(random.uniform(5, 25), 2) if ai_optimized else None

        # Depot = Paris centre
        depot_lat = 48.8534 + random.uniform(-0.01, 0.01)
        depot_lng = 2.3488 + random.uniform(-0.01, 0.01)

        cur.execute("""
            INSERT INTO tours (
                id, driver_id, vehicle_id, date, status,
                total_distance_km, total_co2_kg, estimated_duration_min,
                started_at, completed_at,
                ai_optimized, optimization_gain_km, optimization_gain_pct,
                depot_latitude, depot_longitude,
                created_at, updated_at
            ) VALUES (
                %s, %s, %s, %s, %s::tour_status,
                %s, %s, %s,
                %s, %s,
                %s, %s, %s,
                %s, %s,
                NOW(), NOW()
            )
            RETURNING id
        """, (
            tid, driver_id, vehicle_id, tour_date, statut,
            distance, co2, duration,
            started_at, completed_at,
            ai_optimized, gain_km, gain_pct,
            round(depot_lat, 8), round(depot_lng, 8)
        ))
        row = cur.fetchone()
        if not row:
            continue
        tour_id = row[0]
        tour_ids.append(tour_id)

        # Arrets de la tournee (4 a 10 livraisons par tour)
        n_stops = random.randint(4, 10)
        candidates = [d for d in delivery_pool if d[0] not in used_deliveries]
        stops = candidates[:n_stops]

        for order, (del_id, del_statut, del_lat, del_lng) in enumerate(stops, 1):
            used_deliveries.add(del_id)
            eta = (started_at or datetime.utcnow()) + timedelta(minutes=order * 25)
            actual = eta + timedelta(minutes=random.randint(-5, 15)) if statut == "COMPLETED" else None
            dist_prev = round(random.uniform(2, 15), 2)
            co2_prev = round(dist_prev * random.uniform(0.0, 0.25), 3)

            try:
                cur.execute("""
                    INSERT INTO tour_stops (
                        id, tour_id, delivery_id, stop_order,
                        eta, actual_arrival,
                        distance_from_prev_km, co2_from_prev_kg
                    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                    ON CONFLICT DO NOTHING
                """, (uid(), tour_id, del_id, order, eta, actual, dist_prev, co2_prev))
            except Exception:
                pass

    print(f"    {len(tour_ids)} tournees inserees")
    return tour_ids


def seed_co2_records(cur, tour_ids, vehicle_ids, driver_ids):
    """Insere des enregistrements CO2 historiques (90 jours)"""
    print("  Insertion des enregistrements CO2...")
    count = 0

    # Records historiques journaliers
    for days_back in range(1, 91):
        record_date = (date.today() - timedelta(days=days_back)).isoformat()
        n_records = random.randint(3, 8)

        for _ in range(n_records):
            distance = round(random.uniform(15, 120), 2)
            vehicle_types = ["CAR", "VAN", "ELECTRIC_CAR", "CARGO_BIKE", "ELECTRIC_VAN"]
            vtype = random.choice(vehicle_types)
            co2_per_km = 0.0 if "ELECTRIC" in vtype or vtype == "CARGO_BIKE" else random.uniform(0.12, 0.28)
            co2_kg = round(distance * co2_per_km, 3)
            predicted = round(co2_kg * random.uniform(0.9, 1.1), 3)

            tour_id = random.choice(tour_ids) if tour_ids else None
            vehicle_id = random.choice(vehicle_ids) if vehicle_ids else None
            driver_id = random.choice(driver_ids) if driver_ids else None

            cur.execute("""
                INSERT INTO co2_records (
                    id, tour_id, vehicle_id, driver_id,
                    date, distance_km, co2_kg, co2_per_km,
                    vehicle_type, predicted_co2, model_version, created_at
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s::vehicle_type, %s, %s, NOW())
            """, (
                uid(), tour_id, vehicle_id, driver_id,
                record_date, distance, co2_kg, round(co2_per_km, 3),
                vtype, predicted, "v1.2"
            ))
            count += 1

    print(f"    {count} enregistrements CO2 inseres")


def seed_carbon_objectives(cur):
    """Insere des objectifs carbone"""
    print("  Insertion des objectifs carbone...")

    objectives = [
        ("MONTHLY",     date.today().replace(day=1) + timedelta(days=32),  2500.0, 15.0, 2100.0, "ON_TRACK"),
        ("QUARTERLY",   date(date.today().year, ((date.today().month - 1) // 3 + 1) * 3, 1), 7500.0, 20.0, 6200.0, "ON_TRACK"),
        ("YEARLY",      date(date.today().year, 12, 31), 30000.0, 25.0, 18500.0, "AT_RISK"),
    ]

    for period, target_date, baseline, reduction_pct, current, statut in objectives:
        cur.execute("""
            INSERT INTO carbon_objectives (
                id, period, target_date, baseline_co2_kg,
                target_reduction_pct, current_co2_kg, status,
                created_at, updated_at
            ) VALUES (%s, %s::carbon_period, %s, %s, %s, %s, %s::carbon_objective_status, NOW(), NOW())
        """, (uid(), period, target_date.isoformat(), baseline, reduction_pct, current, statut))

    print("    3 objectifs carbone inseres")


def seed_notifications(cur, driver_ids, admin_id):
    """Insere des notifications"""
    print("  Insertion des notifications...")
    count = 0

    notif_templates = [
        ("INFO",    "Tournee optimisee",       "Votre tournee de demain a ete optimisee par l'IA. Gain: 18%"),
        ("SUCCESS", "Livraison confirmee",     "La livraison DOS-2024001 a ete confirmee avec succes."),
        ("WARNING", "Retard possible",         "Trafic dense detecte sur votre itineraire. ETA revise +15min."),
        ("ALERT",   "Vehicule en maintenance", "Le vehicule VA-002-BB necessite une revision dans 500km."),
        ("INFO",    "Badge obtenu",            "Felicitations ! Vous avez obtenu le badge ECO_DRIVER."),
        ("SUCCESS", "Objectif carbone",        "Objectif carbone mensuel atteint : -17% CO2 ce mois."),
        ("INFO",    "Nouvelle livraison",      "3 nouvelles livraisons ont ete assignees a votre tournee."),
    ]

    all_users = driver_ids + [admin_id]
    for user_id in all_users:
        n = random.randint(3, 8)
        templates = random.choices(notif_templates, k=n)
        for ntype, title, message in templates:
            is_read = random.random() < 0.6
            read_at = past_ts(5) if is_read else None
            cur.execute("""
                INSERT INTO notifications (
                    id, user_id, type, channel, title, message,
                    is_read, read_at, created_at
                ) VALUES (%s, %s, %s::notification_type, 'IN_APP'::notification_channel, %s, %s, %s, %s, %s)
            """, (uid(), user_id, ntype, title, message, is_read, read_at, past_ts(10)))
            count += 1

    print(f"    {count} notifications inserees")


def seed_driver_badges(cur, driver_ids):
    """Insere des badges pour les chauffeurs"""
    print("  Insertion des badges chauffeurs...")
    count = 0

    for driver_id in driver_ids:
        n_badges = random.randint(1, 5)
        badges = random.sample(BADGE_TYPES, min(n_badges, len(BADGE_TYPES)))
        for badge in badges:
            cur.execute("""
                INSERT INTO driver_badges (id, driver_id, badge_type, earned_at, period)
                VALUES (%s, %s, %s, %s, %s)
            """, (uid(), driver_id, badge, past_ts(30), f"{date.today().year}-{date.today().month:02d}"))
            count += 1

    print(f"    {count} badges inseres")


def seed_gps_tracks(cur, driver_ids, tour_ids):
    """Insere des traces GPS pour les tournees recentes"""
    print("  Insertion des traces GPS...")
    count = 0

    recent_tours = tour_ids[:10] if len(tour_ids) >= 10 else tour_ids
    for tour_id in recent_tours:
        driver_id = random.choice(driver_ids)
        # Trajet autour de Paris
        lat, lng = 48.8534, 2.3488
        n_points = random.randint(20, 60)
        recorded = datetime.utcnow() - timedelta(hours=random.randint(1, 72))

        for j in range(n_points):
            lat += random.uniform(-0.005, 0.005)
            lng += random.uniform(-0.005, 0.005)
            speed = round(random.uniform(0, 80), 2)
            heading = round(random.uniform(0, 360), 2)
            recorded += timedelta(seconds=random.randint(30, 90))

            cur.execute("""
                INSERT INTO gps_tracks (
                    driver_id, tour_id, latitude, longitude,
                    speed_kmh, heading, recorded_at
                ) VALUES (%s, %s, %s, %s, %s, %s, %s)
            """, (driver_id, tour_id, round(lat, 8), round(lng, 8), speed, heading, recorded))
            count += 1

    print(f"    {count} points GPS inseres")


# ─── MAIN ─────────────────────────────────────────────────────────────────────
def main():
    print("\nDeliveryOS - Seeding des donnees realistes")
    print("=" * 50)

    try:
        conn = connect()
        conn.autocommit = False
        cur = conn.cursor()
        print("Connexion PostgreSQL etablie\n")

        # Recuperer les IDs des utilisateurs existants
        cur.execute("SELECT id, role FROM users WHERE email IN ('admin@deliveryos.fr', 'dispatcher@deliveryos.fr') ORDER BY role")
        existing = cur.fetchall()
        admin_id = next((str(r[0]) for r in existing if r[1] == 'SUPER_ADMIN'), None)
        dispatcher_id = next((str(r[0]) for r in existing if r[1] == 'DISPATCHER'), None)

        if not admin_id:
            print("ERREUR: admin@deliveryos.fr introuvable. Lance d'abord insert_test_users.py")
            sys.exit(1)

        print(f"Admin ID: {admin_id}")
        print(f"Dispatcher ID: {dispatcher_id}\n")

        # Seeding dans l'ordre des dependances
        driver_ids = seed_drivers(cur, admin_id, dispatcher_id)
        vehicle_ids = seed_vehicles(cur)
        delivery_ids = seed_deliveries(cur, admin_id, count=120)
        tour_ids = seed_tours(cur, driver_ids, vehicle_ids, delivery_ids, count=25)
        seed_co2_records(cur, tour_ids, vehicle_ids, driver_ids)
        seed_carbon_objectives(cur)
        seed_notifications(cur, driver_ids, admin_id)
        seed_driver_badges(cur, driver_ids)
        seed_gps_tracks(cur, driver_ids, tour_ids)

        conn.commit()
        print("\n" + "=" * 50)
        print("Seeding termine avec succes !")
        print("\nResume:")
        for table in ["users", "vehicles", "deliveries", "tours", "tour_stops",
                      "co2_records", "carbon_objectives", "notifications", "driver_badges", "gps_tracks"]:
            cur.execute(f"SELECT COUNT(*) FROM {table}")
            n = cur.fetchone()[0]
            print(f"  {table:<25} {n:>5} lignes")

        print("\nConnecte-toi avec:")
        print("  Email   : admin@deliveryos.fr")
        print("  Password: password123")

    except Exception as e:
        print(f"\nERREUR: {e}")
        import traceback
        traceback.print_exc()
        if 'conn' in locals():
            conn.rollback()
        sys.exit(1)
    finally:
        if 'cur' in locals():
            cur.close()
        if 'conn' in locals():
            conn.close()


if __name__ == "__main__":
    main()
