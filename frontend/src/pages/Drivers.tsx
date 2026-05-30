import { useQuery } from 'react-query'
import apiClient from '@/services/api'
import type { ApiResponse, PageResponse, User } from '@/types'

const BADGE_COLORS: Record<string, string> = {
  ECO_CHAMPION: '#10b981',
  ZERO_DELAY: '#3b82f6',
  TOP_DELIVERY: '#f59e0b',
  PERFECT_WEEK: '#8b5cf6',
  CO2_SAVER: '#06b6d4',
  SPEED_STAR: '#ef4444',
  RELIABILITY_KING: '#6b7280',
}

const BADGE_LABELS: Record<string, string> = {
  ECO_CHAMPION: 'Eco Champion',
  ZERO_DELAY: 'Zero Delay',
  TOP_DELIVERY: 'Top Delivery',
  PERFECT_WEEK: 'Perfect Week',
  CO2_SAVER: 'CO2 Saver',
  SPEED_STAR: 'Speed Star',
  RELIABILITY_KING: 'Reliability King',
}

export default function Drivers() {
  const { data, isLoading } = useQuery('drivers', async () => {
    const { data } = await apiClient.get<ApiResponse<PageResponse<User>>>(
      '/users', { params: { role: 'DRIVER', size: 50 } }
    )
    return data.data.content
  })

  const drivers = data ?? []

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <div>
        <h1 style={{ fontSize: '24px', fontWeight: 700, color: 'hsl(var(--foreground))' }}>
          Drivers
        </h1>
        <p style={{ fontSize: '14px', color: 'hsl(var(--muted-foreground))' }}>
          {drivers.length} drivers registered
        </p>
      </div>

      {isLoading ? (
        <p style={{ color: 'hsl(var(--muted-foreground))' }}>Loading drivers...</p>
      ) : (
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
          gap: '16px',
        }}>
          {drivers.map((driver) => (
            <div
              key={driver.id}
              style={{
                padding: '20px',
                borderRadius: '12px',
                border: '1px solid hsl(var(--border))',
                backgroundColor: 'hsl(var(--card))',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '16px' }}>
                <div style={{
                  width: '44px', height: '44px',
                  borderRadius: '50%',
                  backgroundColor: 'hsl(var(--primary))',
                  color: 'white',
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  fontSize: '16px', fontWeight: 700,
                }}>
                  {driver.firstName[0]}{driver.lastName[0]}
                </div>
                <div>
                  <p style={{ fontWeight: 600, color: 'hsl(var(--foreground))' }}>
                    {driver.firstName} {driver.lastName}
                  </p>
                  <p style={{ fontSize: '12px', color: 'hsl(var(--muted-foreground))' }}>
                    {driver.email}
                  </p>
                </div>
                <span style={{
                  marginLeft: 'auto',
                  padding: '2px 8px',
                  borderRadius: '99px',
                  fontSize: '11px',
                  fontWeight: 500,
                  backgroundColor: driver.isActive ? '#dcfce7' : '#fee2e2',
                  color: driver.isActive ? '#15803d' : '#dc2626',
                }}>
                  {driver.isActive ? 'Active' : 'Inactive'}
                </span>
              </div>

              {/* Badges placeholder */}
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }}>
                {['ECO_CHAMPION', 'ZERO_DELAY'].map((badge) => (
                  <span
                    key={badge}
                    style={{
                      padding: '2px 8px',
                      borderRadius: '99px',
                      fontSize: '11px',
                      fontWeight: 500,
                      backgroundColor: BADGE_COLORS[badge] + '20',
                      color: BADGE_COLORS[badge],
                      border: `1px solid ${BADGE_COLORS[badge]}40`,
                    }}
                  >
                    {BADGE_LABELS[badge]}
                  </span>
                ))}
              </div>
            </div>
          ))}

          {drivers.length === 0 && (
            <div style={{
              gridColumn: '1 / -1',
              padding: '40px',
              textAlign: 'center',
              color: 'hsl(var(--muted-foreground))',
              border: '1px dashed hsl(var(--border))',
              borderRadius: '12px',
            }}>
              No drivers registered yet
            </div>
          )}
        </div>
      )}
    </div>
  )
}