import { useQuery } from 'react-query'
import { KpiCard } from '@/components/charts/KpiCard'
import apiClient from '@/services/api'
import type { ApiResponse } from '@/types'

interface CarbonKpis {
  totalCo2Kg: number
  co2PerDelivery: number
  co2PerKm: number
  electricVehiclesPct: number
  targetReductionPct: number
  currentReductionPct: number
  status: string
}

export default function CarbonDashboard() {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div>
        <h1 style={{ fontSize: '24px', fontWeight: 700, color: 'hsl(var(--foreground))' }}>
          Carbon Intelligence
        </h1>
        <p style={{ fontSize: '14px', color: 'hsl(var(--muted-foreground))' }}>
          CO2 emissions tracking and carbon objectives
        </p>
      </div>

      {/* KPI Cards */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
        gap: '16px',
      }}>
        <KpiCard
          label="Total CO2"
          value="0 kg"
          subtitle="This month"
          color="#10b981"
        />
        <KpiCard
          label="CO2 per Delivery"
          value="0 g"
          subtitle="Average"
          color="#06b6d4"
        />
        <KpiCard
          label="Electric Vehicles"
          value="0%"
          subtitle="Of fleet"
          color="#8b5cf6"
        />
        <KpiCard
          label="Carbon Objective"
          value="On Track"
          subtitle="Monthly target"
          color="#f59e0b"
        />
      </div>

      {/* Vehicle comparison */}
      <div style={{
        padding: '24px',
        borderRadius: '12px',
        border: '1px solid hsl(var(--border))',
        backgroundColor: 'hsl(var(--card))',
      }}>
        <p style={{ fontWeight: 600, color: 'hsl(var(--foreground))', marginBottom: '16px' }}>
          CO2 by Vehicle Type
        </p>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {[
            { type: 'Electric Car', co2: 50, color: '#10b981' },
            { type: 'Cargo Bike', co2: 0, color: '#06b6d4' },
            { type: 'Car', co2: 180, color: '#3b82f6' },
            { type: 'Van', co2: 250, color: '#f97316' },
          ].map((item) => (
            <div key={item.type} style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              <span style={{ width: '120px', fontSize: '13px', color: 'hsl(var(--foreground))' }}>
                {item.type}
              </span>
              <div style={{ flex: 1, height: '8px', borderRadius: '99px', backgroundColor: 'hsl(var(--muted))' }}>
                <div style={{
                  height: '100%',
                  borderRadius: '99px',
                  backgroundColor: item.color,
                  width: `${(item.co2 / 250) * 100}%`,
                  transition: 'width 0.5s ease',
                }} />
              </div>
              <span style={{ width: '60px', fontSize: '12px', color: 'hsl(var(--muted-foreground))', textAlign: 'right' }}>
                {item.co2}g/km
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}