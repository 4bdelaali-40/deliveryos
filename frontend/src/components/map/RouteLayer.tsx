import { Polyline } from 'react-leaflet'
import type { Delivery } from '@/types'

interface RouteLayerProps {
    deliveries: Delivery[]
    color?: string
}

const ROUTE_COLORS = [
    '#3b82f6',
    '#10b981',
    '#f97316',
    '#8b5cf6',
    '#ef4444',
    '#06b6d4',
]

export function RouteLayer({ deliveries, color }: RouteLayerProps) {
    const positions = deliveries
        .filter((d) => d.latitude && d.longitude)
        .map((d) => [d.latitude!, d.longitude!] as [number, number])

    if (positions.length < 2) return null

    return (
        <Polyline
            positions={positions}
            pathOptions={{
                color: color ?? ROUTE_COLORS[0],
                weight: 3,
                opacity: 0.8,
                dashArray: '8 4',
            }}
        />
    )
}