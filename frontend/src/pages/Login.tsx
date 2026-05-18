/**
 * Login page — scaffold uniquement pour la Semaine 1.
 * L'authentification complète (JWT + MFA) sera implémentée en Semaine 2.
 */
export default function Login() {
    return (
        <div
            style={{
                display: 'flex',
                height: '100vh',
                width: '100vw',
                alignItems: 'center',
                justifyContent: 'center',
                backgroundColor: 'hsl(var(--background))',
            }}
        >
            <div
                style={{
                    width: '100%',
                    maxWidth: '400px',
                    padding: '32px',
                    borderRadius: '12px',
                    border: '1px solid hsl(var(--border))',
                    backgroundColor: 'hsl(var(--card))',
                    boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
                }}
            >
                {/* Logo */}
                <h1
                    style={{
                        fontSize: '24px',
                        fontWeight: 700,
                        marginBottom: '4px',
                        color: 'hsl(var(--foreground))',
                    }}
                >
                    Delivery<span style={{ color: 'hsl(var(--primary))' }}>OS</span>
                </h1>

                <p
                    style={{
                        fontSize: '14px',
                        color: 'hsl(var(--muted-foreground))',
                        marginBottom: '32px',
                    }}
                >
                    Enterprise delivery management platform
                </p>

                {/* Placeholder */}
                <div
                    style={{
                        padding: '16px',
                        borderRadius: '8px',
                        backgroundColor: 'hsl(var(--muted))',
                        textAlign: 'center',
                        fontSize: '13px',
                        color: 'hsl(var(--muted-foreground))',
                    }}
                >
                     Auth JWT + MFA implémentée en Semaine 2
                </div>
            </div>
        </div>
    )
}