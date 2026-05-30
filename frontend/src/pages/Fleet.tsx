import { useQuery } from 'react-query'
import apiClient from '@/services/api'
import type { ApiResponse, PageResponse, Vehicle } from '@/types'

export default function Fleet() {
  const { data, isLoading } = useQuery('vehicles', async () => {
    const { data } = await apiClient.get<ApiResponse<PageResponse<Vehicle>>>(
      '/vehicles'
    )
    return data.data.content
  })

  const vehicles = data ?? []

  const getTypeColor = (type: string) => {
    const colors: Record<string, string> = {
      ELECTRIC_CAR: '#10b981',
      ELECTRIC_VAN: '#10b981',
      BIKE: '#10b981',
      CARGO_BIKE: '#10b981',
      CAR: '#3b82f6',
      VAN: '#6b7280',
      MOTORCYCLE: '#f97316',
    }
    return colors[type] ?? '#6b7280'
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div>
        <h1 style={{ fontSize: '24px', fontWeight: 700, color: 'hsl(var(--foreground))' }}>
          Fleet Management
        </h1>
        <p style={{ fontSize: '14px', color: 'hsl(var(--muted-foreground))' }}>
          {vehicles.length} vehicles registered
        </p>
      </div>

      {isLoading ? (
        <p style={{ color: 'hsl(var(--muted-foreground))' }}>Loading vehicles...</p>
      ) : (
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
          gap: '16px',
        }}>
          {vehicles.map((vehicle) => (
            <div
              key={vehicle.id}
              style={{
                padding: '20px',
                borderRadius: '12px',
                border: '1px solid hsl(var(--border))',
                backgroundColor: 'hsl(var(--card))',
                borderLeft: `4px solid ${getTypeColor(vehicle.type)}`,
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '12px' }}>
                <p style={{ fontWeight: 700, color: 'hsl(var(--foreground))' }}>
                  {vehicle.plateNumber}
                </p>
                <span style={{
                  padding: '2px 8px',
                  borderRadius: '99px',
                  fontSize: '11px',
                  fontWeight: 500,
                  backgroundColor: vehicle.isAvailable ? '#dcfce7' : '#fee2e2',
                  color: vehicle.isAvailable ? '#15803d' : '#dc2626',
                }}>
                  {vehicle.isAvailable ? 'Available' : 'In Use'}
                </span>
              </div>
              <p style={{ fontSize: '13px', color: 'hsl(var(--muted-foreground))', marginBottom: '4px' }}>
                {vehicle.brand} {vehicle.model} — {vehicle.type}
              </p>
              <p style={{ fontSize: '12px', color: 'hsl(var(--muted-foreground))', marginBottom: '4px' }}>
                Capacity: {vehicle.capacityKg}kg / {vehicle.capacityM3}m³
              </p>
              <p style={{ fontSize: '12px', color: 'hsl(var(--muted-foreground))' }}>
                CO2: {vehicle.co2PerKm}g/km — {vehicle.mileageKm.toLocaleString()} km
              </p>
            </div>
          ))}

          {vehicles.length === 0 && (
            <div style={{
              gridColumn: '1 / -1',
              padding: '40px',
              textAlign: 'center',
              color: 'hsl(var(--muted-foreground))',
              border: '1px dashed hsl(var(--border))',
              borderRadius: '12px',
            }}>
              No vehicles registered yet
            </div>
          )}
        </div>
      )}
    </div>
  )
}