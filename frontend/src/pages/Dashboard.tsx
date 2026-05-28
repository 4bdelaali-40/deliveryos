import { useKpis } from '@/hooks/useAnalytics'
import { KpiCard } from '@/components/charts/KpiCard'
import { DeliveryStatusChart } from '@/components/charts/DeliveryChart'

export default function Dashboard() {
  const { data: kpis, isLoading } = useKpis('MONTHLY')

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>

      {/* Header */}
      <div>
        <h1 style={{ fontSize: '24px', fontWeight: 700, color: 'hsl(var(--foreground))' }}>
          Dashboard
        </h1>
        <p style={{ fontSize: '14px', color: 'hsl(var(--muted-foreground))' }}>
          Welcome to DeliveryOS
        </p>
      </div>

      {/* KPI Cards */}
      {isLoading ? (
        <div style={{ textAlign: 'center', color: 'hsl(var(--muted-foreground))' }}>
          Loading...
        </div>
      ) : kpis ? (
        <>
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '16px',
          }}>
            <KpiCard
              label="Total Deliveries"
              value={kpis.totalDeliveries.toLocaleString()}
              color="#3b82f6"
            />
            <KpiCard
              label="Delivery Rate"
              value={`${kpis.deliveryRate}%`}
              subtitle={`${kpis.deliveredCount} delivered`}
              color="#10b981"
            />
            <KpiCard
              label="In Transit"
              value={kpis.inTransitCount.toLocaleString()}
              color="#f97316"
            />
            <KpiCard
              label="CO2 Today"
              value={`${kpis.totalCo2Kg.toFixed(1)} kg`}
              color="#06b6d4"
            />
          </div>

          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))',
            gap: '16px',
          }}>
            <DeliveryStatusChart
              delivered={kpis.deliveredCount}
              failed={kpis.failedCount}
              inTransit={kpis.inTransitCount}
              created={kpis.createdCount}
            />
          </div>
        </>
      ) : null}
    </div>
  )
}