import { Marker, Popup } from 'react-leaflet'
import L from 'leaflet'
import type { GpsPosition, User } from '@/types'

interface DriverMarkerProps {
    position: GpsPosition
    driver?: User
}

const driverIcon = L.divIcon({
    className: '',
    html: `
    <div style="
      width: 36px;
      height: 36px;
      background: #10b981;
      border: 3px solid white;
      border-radius: 50%;
      box-shadow: 0 2px 6px rgba(0,0,0,0.3);
      display: flex;
      align-items: center;
      justify-content: center;
    ">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="white">
        <path d="M18.92 6.01C18.72 5.42 18.16 5 17.5 5h-11c-.66 0-1.21.42-1.42 1.01L3 12v8c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-1h12v1c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-8l-2.08-5.99zM6.5 16c-.83 0-1.5-.67-1.5-1.5S5.67 13 6.5 13s1.5.67 1.5 1.5S7.33 16 6.5 16zm11 0c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zM5 11l1.5-4.5h11L19 11H5z"/>
      </svg>
    </div>
  `,
    iconSize: [36, 36],
    iconAnchor: [18, 18],
    popupAnchor: [0, -18],
})

export function DriverMarker({ position, driver }: DriverMarkerProps) {
    return (
        <Marker
            position={[position.latitude, position.longitude]}
            icon={driverIcon}
        >
            <Popup>
                <div style={{ minWidth: '160px' }}>
                    <p style={{ fontWeight: 700, marginBottom: '4px' }}>
                        {driver ? `${driver.firstName} ${driver.lastName}` : 'Driver'}
                    </p>
                    {position.speedKmh !== undefined && (
                        <p style={{ fontSize: '12px', color: '#6b7280' }}>
                            Speed: {position.speedKmh.toFixed(1)} km/h
                        </p>
                    )}
                    <p style={{ fontSize: '11px', color: '#9ca3af', marginTop: '2px' }}>
                        {new Date(position.recordedAt).toLocaleTimeString()}
                    </p>
                </div>
            </Popup>
        </Marker>
    )
}