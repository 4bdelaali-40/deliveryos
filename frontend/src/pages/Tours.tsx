import { useState } from 'react'
import { useQuery } from 'react-query'
import { DeliveryMap } from '@/components/map/DeliveryMap'
import apiClient from '@/services/api'
import tourService from '@/services/tourService'
import type { ApiResponse, PageResponse, Delivery, Vehicle, User } from '@/types'

const STATUS_COLORS: Record<string, string> = {
  PLANNED: '#6b7280',
  IN_PROGRESS: '#f97316',
  COMPLETED: '#10b981',
  CANCELLED: '#ef4444',
}

export default function Tours() {
  const [selectedDate, setSelectedDate] = useState(
    new Date().toISOString().split('T')[0]
  )
  const [selectedVehicleIds, setSelectedVehicleIds] = useState<string[]>([])
  const [selectedDriverIds, setSelectedDriverIds] = useState<string[]>([])

  // Livraisons non assignées pour la date choisie
  const { data: deliveriesData, isLoading: loadingDeliveries } = useQuery(
    ['deliveries-tours', selectedDate],
    async () => {
      const { data } = await apiClient.get<ApiResponse<PageResponse<Delivery>>>(
        '/deliveries',
        { params: { scheduledDate: selectedDate, status: 'CREATED', size: 100 } }
      )
      return data.data.content
    }
  )

  // Véhicules disponibles
  const { data: vehiclesData } = useQuery('vehicles-tours', async () => {
    const { data } = await apiClient.get<ApiResponse<PageResponse<Vehicle>>>(
      '/vehicles', { params: { size: 50 } }
    )
    return data.data.content
  })

  // Chauffeurs disponibles
  const { data: driversData } = useQuery('drivers-tours', async () => {
    const { data } = await apiClient.get<ApiResponse<PageResponse<User>>>(
      '/users', { params: { role: 'DRIVER', size: 50 } }
    )
    return data.data.content
  })

  // Tournees existantes pour la date choisie
  const { data: toursData, isLoading: loadingTours } = useQuery(
    ['tours', selectedDate],
    () => tourService.findAll(selectedDate)
  )

  const deliveries = deliveriesData ?? []
  const vehicles = vehiclesData ?? []
  const drivers = driversData ?? []
  const tours = toursData?.content ?? []

  const toggleVehicle = (id: string) => {
    setSelectedVehicleIds((prev) =>
      prev.includes(id) ? prev.filter((v) => v !== id) : [...prev, id]
    )
  }

  const toggleDriver = (id: string) => {
    setSelectedDriverIds((prev) =>
      prev.includes(id) ? prev.filter((d) => d !== id) : [...prev, id]
    )
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', gap: '16px' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h1 style={{ fontSize: '24px', fontWeight: 700, color: 'hsl(var(--foreground))' }}>
            Tours
          </h1>
          <p style={{ fontSize: '14px', color: 'hsl(var(--muted-foreground))' }}>
            {loadingDeliveries ? 'Loading...' : `${deliveries.length} deliveries to optimize`}
          </p>
        </div>

        <input
          type="date"
          value={selectedDate}
          onChange={(e) => setSelectedDate(e.target.value)}
          style={inputStyle}
        />
      </div>

      {/* Vehicle / Driver selection */}
      <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
        <SelectionGroup
          title="Vehicles"
          items={vehicles.map((v) => ({ id: v.id, label: `${v.plateNumber} (${v.type})` }))}
          selected={selectedVehicleIds}
          onToggle={toggleVehicle}
        />
        <SelectionGroup
          title="Drivers"
          items={drivers.map((d) => ({ id: d.id, label: `${d.firstName} ${d.lastName}` }))}
          selected={selectedDriverIds}
          onToggle={toggleDriver}
        />
      </div>

      {/* Map */}
      <div
        style={{
          flex: 1,
          borderRadius: '12px',
          overflow: 'hidden',
          border: '1px solid hsl(var(--border))',
          minHeight: '450px',
        }}
      >
        {deliveries.length === 0 && !loadingDeliveries ? (
          <div style={{
            height: '100%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'hsl(var(--muted-foreground))',
            fontSize: '14px',
            backgroundColor: 'hsl(var(--muted))',
          }}>
            No unassigned deliveries scheduled for {new Date(selectedDate).toLocaleDateString('fr-FR')}
          </div>
        ) : (
          <DeliveryMap
            deliveries={deliveries}
            date={selectedDate}
            vehicleIds={selectedVehicleIds}
            driverIds={selectedDriverIds}
            depotLatitude={48.8566}
            depotLongitude={2.3522}
          />
        )}
      </div>

      {selectedVehicleIds.length === 0 || selectedDriverIds.length === 0 ? (
        <p style={{ fontSize: '13px', color: 'hsl(var(--muted-foreground))' }}>
          Select at least one vehicle and one driver to enable route optimization.
        </p>
      ) : null}

      {/* Existing tours for this date */}
      <div>
        <h2 style={{ fontSize: '16px', fontWeight: 600, color: 'hsl(var(--foreground))', marginBottom: '12px' }}>
          Tours for {new Date(selectedDate).toLocaleDateString('fr-FR')}
        </h2>

        {loadingTours ? (
          <p style={{ color: 'hsl(var(--muted-foreground))', fontSize: '13px' }}>Loading tours...</p>
        ) : tours.length === 0 ? (
          <div style={{
            padding: '24px',
            textAlign: 'center',
            color: 'hsl(var(--muted-foreground))',
            border: '1px dashed hsl(var(--border))',
            borderRadius: '12px',
            fontSize: '13px',
          }}>
            No tours scheduled for this date
          </div>
        ) : (
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
            gap: '12px',
          }}>
            {tours.map((tour) => {
              const driver = drivers.find((d) => d.id === tour.driverId)
              const vehicle = vehicles.find((v) => v.id === tour.vehicleId)
              return (
                <div
                  key={tour.id}
                  style={{
                    padding: '16px',
                    borderRadius: '12px',
                    border: '1px solid hsl(var(--border))',
                    backgroundColor: 'hsl(var(--card))',
                    borderLeft: `4px solid ${STATUS_COLORS[tour.status] ?? '#6b7280'}`,
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                    <span style={{
                      padding: '2px 8px',
                      borderRadius: '99px',
                      fontSize: '11px',
                      fontWeight: 500,
                      backgroundColor: (STATUS_COLORS[tour.status] ?? '#6b7280') + '20',
                      color: STATUS_COLORS[tour.status] ?? '#6b7280',
                    }}>
                      {tour.status.replace('_', ' ')}
                    </span>
                    {tour.aiOptimized && (
                      <span style={{ fontSize: '11px', color: 'hsl(var(--muted-foreground))' }}>
                        AI optimized
                      </span>
                    )}
                  </div>

                  <p style={{ fontWeight: 600, fontSize: '14px', color: 'hsl(var(--foreground))', marginBottom: '4px' }}>
                    {driver ? `${driver.firstName} ${driver.lastName}` : 'Unassigned driver'}
                  </p>
                  <p style={{ fontSize: '12px', color: 'hsl(var(--muted-foreground))', marginBottom: '8px' }}>
                    {vehicle ? `${vehicle.plateNumber} — ${vehicle.type}` : 'No vehicle'}
                  </p>

                  <div style={{ display: 'flex', gap: '12px', fontSize: '12px', color: 'hsl(var(--muted-foreground))' }}>
                    <span>{tour.stops?.length ?? 0} stops</span>
                    {tour.totalDistanceKm != null && <span>{tour.totalDistanceKm.toFixed(1)} km</span>}
                    {tour.totalCo2Kg != null && <span>{tour.totalCo2Kg.toFixed(2)} kg CO2</span>}
                  </div>

                  {tour.optimizationGainPct != null && (
                    <p style={{ fontSize: '12px', color: '#10b981', marginTop: '8px' }}>
                      −{tour.optimizationGainPct.toFixed(0)}% vs unoptimized route
                    </p>
                  )}
                </div>
              )
            })}
          </div>
        )}
      </div>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────
// Selection group (checkboxes pills)
// ─────────────────────────────────────────────────────────────
function SelectionGroup({
  title,
  items,
  selected,
  onToggle,
}: {
  title: string
  items: { id: string; label: string }[]
  selected: string[]
  onToggle: (id: string) => void
}) {
  return (
    <div style={{ flex: 1, minWidth: '240px' }}>
      <p style={{
        fontSize: '12px',
        fontWeight: 600,
        textTransform: 'uppercase',
        letterSpacing: '0.05em',
        color: 'hsl(var(--muted-foreground))',
        marginBottom: '8px',
      }}>
        {title} ({selected.length} selected)
      </p>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }}>
        {items.length === 0 ? (
          <span style={{ fontSize: '12px', color: 'hsl(var(--muted-foreground))' }}>None available</span>
        ) : (
          items.map((item) => {
            const isSelected = selected.includes(item.id)
            return (
              <button
                key={item.id}
                onClick={() => onToggle(item.id)}
                style={{
                  padding: '6px 12px',
                  borderRadius: '99px',
                  fontSize: '12px',
                  fontWeight: 500,
                  border: `1px solid ${isSelected ? 'hsl(var(--primary))' : 'hsl(var(--border))'}`,
                  backgroundColor: isSelected ? 'hsl(var(--primary))' : 'transparent',
                  color: isSelected ? 'hsl(var(--primary-foreground))' : 'hsl(var(--foreground))',
                  cursor: 'pointer',
                  transition: 'all 0.15s',
                }}
              >
                {item.label}
              </button>
            )
          })
        )}
      </div>
    </div>
  )
}

const inputStyle: React.CSSProperties = {
  padding: '8px 12px',
  borderRadius: '8px',
  border: '1px solid hsl(var(--border))',
  backgroundColor: 'hsl(var(--card))',
  color: 'hsl(var(--foreground))',
  fontSize: '14px',
}
