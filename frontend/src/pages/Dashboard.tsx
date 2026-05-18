/**
 * Dashboard — scaffold uniquement pour la Semaine 1.
 * Le vrai dashboard BI sera implémenté en Semaine 9.
 */
export default function Dashboard() {
    return (
        <div
            style={{
                display: 'flex',
                flexDirection: 'column',
                gap: '24px',
            }}
        >
            {/* Header */}
            <div>
                <h1
                    style={{
                        fontSize: '24px',
                        fontWeight: 700,
                        color: 'hsl(var(--foreground))',
                        marginBottom: '4px',
                    }}
                >
                    Dashboard
                </h1>
                <p style={{ fontSize: '14px', color: 'hsl(var(--muted-foreground))' }}>
                    Welcome to DeliveryOS — your enterprise delivery management platform.
                </p>
            </div>

            {/* KPI Cards placeholder */}
            <div
                style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
                    gap: '16px',
                }}
            >
                {[
                    { label: 'Total Deliveries', value: '—' },
                    { label: 'Delivery Rate',    value: '—' },
                    { label: 'CO₂ Today',        value: '—' },
                    { label: 'Active Drivers',   value: '—' },
                ].map((kpi) => (
                    <div
                        key={kpi.label}
                        style={{
                            padding: '20px',
                            borderRadius: '10px',
                            border: '1px solid hsl(var(--border))',
                            backgroundColor: 'hsl(var(--card))',
                        }}
                    >
                        <p
                            style={{
                                fontSize: '12px',
                                fontWeight: 500,
                                color: 'hsl(var(--muted-foreground))',
                                marginBottom: '8px',
                                textTransform: 'uppercase',
                                letterSpacing: '0.05em',
                            }}
                        >
                            {kpi.label}
                        </p>
                        <p
                            style={{
                                fontSize: '28px',
                                fontWeight: 700,
                                color: 'hsl(var(--foreground))',
                            }}
                        >
                            {kpi.value}
                        </p>
                    </div>
                ))}
            </div>

            {/* Modules placeholder */}
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
                 Charts and analytics — implémentés en Semaine 9
            </div>
        </div>
    )
}