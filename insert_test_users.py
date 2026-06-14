#!/usr/bin/env python3
"""
Insert test users into DeliveryOS PostgreSQL database
Password: password123
Hash generated using bcrypt
"""

import psycopg2
from psycopg2 import sql
import bcrypt

# Database connection
conn = psycopg2.connect(
    host="localhost",
    port=5432,
    database="deliveryos",
    user="deliveryos",
    password="deliveryos_secret"
)

cursor = conn.cursor()

# Generate bcrypt hash for "password123"
password = "password123"
password_hash = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt(rounds=10)).decode('utf-8')

print(f"Generated bcrypt hash: {password_hash}")

# Test users to insert
users = [
    {
        "role": "SUPER_ADMIN",
        "first_name": "Admin",
        "last_name": "User",
        "email": "admin@deliveryos.fr",
        "phone": "+33612345678"
    },
    {
        "role": "DISPATCHER",
        "first_name": "Dispatcher",
        "last_name": "User",
        "email": "dispatcher@deliveryos.fr",
        "phone": "+33612345679"
    },
    {
        "role": "DRIVER",
        "first_name": "John",
        "last_name": "Driver",
        "email": "driver@deliveryos.fr",
        "phone": "+33612345680"
    }
]

# Insert users
for user in users:
    try:
        cursor.execute(
            sql.SQL("""
                INSERT INTO users (
                    role, first_name, last_name, email, password_hash, 
                    phone, is_active, mfa_enabled, created_at, updated_at
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW()
                ) ON CONFLICT (email) DO NOTHING
            """),
            (
                user["role"],
                user["first_name"],
                user["last_name"],
                user["email"],
                password_hash,
                user["phone"],
                True,  # is_active
                False  # mfa_enabled
            )
        )
        print(f"✅ Inserted: {user['email']} ({user['role']})")
    except Exception as e:
        print(f"❌ Error inserting {user['email']}: {e}")

# Verify users were inserted
cursor.execute("SELECT COUNT(*) FROM users WHERE email LIKE '%deliveryos%'")
count = cursor.fetchone()[0]
print(f"\n✅ Total users with @deliveryos.fr email: {count}")

# List all test users
cursor.execute("SELECT email, role FROM users WHERE email LIKE '%deliveryos%' ORDER BY email")
for email, role in cursor.fetchall():
    print(f"  - {email} ({role})")

conn.commit()
cursor.close()
conn.close()

print("\n✅ Done! You can now login with:")
print("  Email: admin@deliveryos.fr")
print("  Password: password123")
