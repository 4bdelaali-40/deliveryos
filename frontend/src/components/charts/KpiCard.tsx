interface KpiCardProps {
  label: string
  value: string | number
  subtitle?: string
  color?: string
}

export function KpiCard({ label, value, subtitle, color = '#3b82f6' }: KpiCardProps) {
  return (
    <div
      style={{
        padding: '20px',
        borderRadius: '12px',
        border: '1px solid hsl(var(--border))',
        backgroundColor: 'hsl(var(--card))',
        borderLeft: `4px solid ${color}`,
      }}
    >
      <p
        style={{
          fontSize: '12px',
          fontWeight: 500,
          color: 'hsl(var(--muted-foreground))',
          textTransform: 'uppercase',
          letterSpacing: '0.05em',
          marginBottom: '8px',
        }}
      >
        {label}
      </p>
      <p
        style={{
          fontSize: '32px',
          fontWeight: 700,
          color: 'hsl(var(--foreground))',
          lineHeight: 1,
          marginBottom: '4px',
        }}
      >
        {value}
      </p>
      {subtitle && (
        <p style={{ fontSize: '12px', color: 'hsl(var(--muted-foreground))' }}>
          {subtitle}
        </p>
      )}
    </div>
  )
}