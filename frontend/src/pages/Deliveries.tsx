/**
 * Deliveries page — scaffold uniquement pour la Semaine 1.
 * Le module complet sera implémenté en Semaine 3.
 */
export default function Deliveries() {
    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
            <div>
                <h1
                    style={{
                        fontSize: '24px',
                        fontWeight: 700,
                        color: 'hsl(var(--foreground))',
                        marginBottom: '4px',
                    }}
                >
                    Deliveries
                </h1>
                <p style={{ fontSize: '14px', color: 'hsl(var(--muted-foreground))' }}>
                    Manage your deliveries — coming in Week 3.
                </p>
            </div>

            <div
                style={{
                    padding: '24px',
                    borderRadius: '10px',
                    border: '1px solid hsl(var(--border))',
                    backgroundColor: 'hsl(var(--card))',
                    textAlign: 'center',
                    color: 'hsl(var(--muted-foreground))',
                    fontSize: '14px',
                }}
            >
                Full delivery management — Week 3
            </div>
        </div>
    )
}