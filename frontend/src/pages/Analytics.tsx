import { useState } from 'react'
import { KpiCard } from '@/components/charts/KpiCard'
import { DeliveryStatusChart } from '@/components/charts/DeliveryChart'
import { useKpis } from '@/hooks/useAnalytics'

export default function Analytics() {
  const [period, setPeriod] = useState('MONTHLY')
  const { data: kpis, isLoading } = useKpis(period)

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>

      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <h1 style={{ fontSize: '24px', fontWeight: 700, color: 'hsl(var(--foreground))' }}>
            Analytics
          </h1>
          <p style={{ fontSize: '14px', color: 'hsl(var(--muted-foreground))' }}>
            Performance metrics and insights
          </p>
        </div>

        <select
          value={period}
          onChange={(e) => setPeriod(e.target.value)}
          style={{
            padding: '8px 12px',
            borderRadius: '8px',
            border: '1px solid hsl(var(--border))',
            backgroundColor: 'hsl(var(--card))',
            color: 'hsl(var(--foreground))',
            fontSize: '14px',
          }}
        >
          <option value="DAILY">Daily</option>
          <option value="WEEKLY">Weekly</option>
          <option value="MONTHLY">Monthly</option>
        </select>
      </div>

      {/* KPI Cards */}
      {isLoading ? (
        <div style={{ textAlign: 'center', color: 'hsl(var(--muted-foreground))' }}>
          Loading metrics...
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
              label="Failed"
              value={kpis.failedCount.toLocaleString()}
              color="#ef4444"
            />
            <KpiCard
              label="First Attempt Rate"
              value={`${kpis.firstAttemptDeliveryRate}%`}
              color="#8b5cf6"
            />
            <KpiCard
              label="CO2 Total"
              value={`${kpis.totalCo2Kg.toFixed(1)} kg`}
              subtitle={`${kpis.co2PerDelivery.toFixed(0)}g per delivery`}
              color="#06b6d4"
            />
          </div>

          {/* Charts */}
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