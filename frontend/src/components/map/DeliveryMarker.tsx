import { Marker, Popup } from 'react-leaflet'
import L from 'leaflet'
import type { Delivery } from '@/types'

interface DeliveryMarkerProps {
    delivery: Delivery
    stopOrder?: number
    isSelected?: boolean
    onClick?: (delivery: Delivery) => void
}

const createMarkerIcon = (order: number, priority: string, isSelected: boolean) => {
    const colors: Record<string, string> = {
        VIP: '#ef4444',
        URGENT: '#f97316',
        NORMAL: '#3b82f6',
    }
    const color = isSelected ? '#8b5cf6' : (colors[priority] ?? '#3b82f6')

    return L.divIcon({
        className: '',
        html: `
      <div style="
        width: 32px;
        height: 32px;
        background: ${color};
        border: 2px solid white;
        border-radius: 50% 50% 50% 0;
        transform: rotate(-45deg);
        box-shadow: 0 2px 4px rgba(0,0,0,0.3);
        display: flex;
        align-items: center;
        justify-content: center;
      ">
        <span style="
          transform: rotate(45deg);
          color: white;
          font-size: 11px;
          font-weight: 700;
        ">${order}</span>
      </div>
    `,
        iconSize: [32, 32],
        iconAnchor: [16, 32],
        popupAnchor: [0, -32],
    })
}

export function DeliveryMarker({
                                   delivery,
                                   stopOrder = 0,
                                   isSelected = false,
                                   onClick,
                               }: DeliveryMarkerProps) {
    if (!delivery.latitude || !delivery.longitude) return null

    return (
        <Marker
            position={[delivery.latitude, delivery.longitude]}
            icon={createMarkerIcon(stopOrder, delivery.priority, isSelected)}
            eventHandlers={{ click: () => onClick?.(delivery) }}
        >
            <Popup>
                <div style={{ minWidth: '200px' }}>
                    <p style={{ fontWeight: 700, marginBottom: '4px' }}>
                        Stop {stopOrder} — {delivery.priority}
                    </p>
                    <p style={{ fontSize: '13px', marginBottom: '2px' }}>
                        {delivery.recipientName}
                    </p>
                    <p style={{ fontSize: '12px', color: '#6b7280' }}>
                        {delivery.address}
                    </p>
                    {delivery.timeWindowStart && (
                        <p style={{ fontSize: '12px', color: '#6b7280', marginTop: '4px' }}>
                            Window: {delivery.timeWindowStart} — {delivery.timeWindowEnd}
                        </p>
                    )}
                    <p style={{ fontSize: '11px', color: '#9ca3af', marginTop: '4px' }}>
                        {delivery.trackingCode}
                    </p>
                </div>
            </Popup>
        </Marker>
    )
}