import { useEffect } from 'react'
import { MapContainer, TileLayer } from 'react-leaflet'
import 'leaflet/dist/leaflet.css'
import { Wifi, WifiOff, Users } from 'lucide-react'
import { useFleetTracking } from '@/hooks/useFleetTracking'
import { DriverMarker } from '@/components/map/DriverMarker'

export default function Tracking() {
  const { positions, isConnected, connect, disconnect } = useFleetTracking()

  useEffect(() => {
    connect()
    return () => disconnect()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', gap: '16px' }}>

      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <h1 style={{ fontSize: '24px', fontWeight: 700, color: 'hsl(var(--foreground))' }}>
            Live Tracking
          </h1>
          <p style={{ fontSize: '14px', color: 'hsl(var(--muted-foreground))' }}>
            Real-time fleet positions
          </p>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          {/* Driver count */}
          <div style={{
            display: 'flex', alignItems: 'center', gap: '6px',
            padding: '6px 12px', borderRadius: '99px',
            backgroundColor: 'hsl(var(--muted))',
            fontSize: '13px', fontWeight: 500,
          }}>
            <Users size={14} />
            <span>{positions.size} active</span>
          </div>

          {/* Connection status */}
          <div style={{
            display: 'flex', alignItems: 'center', gap: '6px',
            padding: '6px 12px', borderRadius: '99px',
            backgroundColor: isConnected ? '#dcfce7' : '#fee2e2',
            color: isConnected ? '#15803d' : '#dc2626',
            fontSize: '13px', fontWeight: 500,
          }}>
            {isConnected ? <Wifi size={14} /> : <WifiOff size={14} />}
            <span>{isConnected ? 'Connected' : 'Disconnected'}</span>
          </div>
        </div>
      </div>

      {/* Map */}
      <div style={{
        position: 'relative',
        flex: 1,
        borderRadius: '12px',
        overflow: 'hidden',
        border: '1px solid hsl(var(--border))',
        minHeight: '500px',
      }}>
        <MapContainer
          center={[48.8566, 2.3522]}
          zoom={12}
          style={{ height: '100%', width: '100%' }}
        >
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
          />

          {Array.from(positions.values()).map((position) => (
            <DriverMarker
              key={position.driverId}
              position={position}
            />
          ))}
        </MapContainer>

        {/* No drivers overlay */}
        {positions.size === 0 && (
          <div style={{
            position: 'absolute',
            top: '50%', left: '50%',
            transform: 'translate(-50%, -50%)',
            textAlign: 'center',
            color: 'hsl(var(--muted-foreground))',
            backgroundColor: 'hsl(var(--card))',
            padding: '12px 20px',
            borderRadius: '8px',
            border: '1px solid hsl(var(--border))',
            pointerEvents: 'none',
            zIndex: 400,
          }}>
            <p style={{ fontSize: '14px', margin: 0 }}>
              {isConnected
                ? 'No active drivers at the moment'
                : 'Connecting to live tracking...'}
            </p>
          </div>
        )}
      </div>
    </div>
  )
}
