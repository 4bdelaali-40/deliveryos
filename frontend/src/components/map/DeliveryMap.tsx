import { useEffect, useState } from 'react'
import { MapContainer, TileLayer } from 'react-leaflet'
import 'leaflet/dist/leaflet.css'
import { DeliveryMarker } from './DeliveryMarker'
import { RouteLayer } from './RouteLayer'
import { MapControls } from './MapControls'
import { useRouteOptimization } from '@/hooks/useRouteOptimization'
import type { Delivery } from '@/types'

// Fix Leaflet default icon issue with Vite
import L from 'leaflet'
import iconUrl from 'leaflet/dist/images/marker-icon.png'
import iconShadowUrl from 'leaflet/dist/images/marker-shadow.png'

L.Marker.prototype.options.icon = L.icon({
    iconUrl,
    shadowUrl: iconShadowUrl,
    iconSize: [25, 41],
    iconAnchor: [12, 41],
})

interface DeliveryMapProps {
    deliveries: Delivery[]
    date: string
    vehicleIds?: string[]
    driverIds?: string[]
    depotLatitude?: number
    depotLongitude?: number
}

const ROUTE_COLORS = [
    '#3b82f6',
    '#10b981',
    '#f97316',
    '#8b5cf6',
    '#ef4444',
]

export function DeliveryMap({
                                deliveries,
                                date,
                                vehicleIds = [],
                                driverIds = [],
                                depotLatitude = 48.8566,
                                depotLongitude = 2.3522,
                            }: DeliveryMapProps) {
    const [selectedDelivery, setSelectedDelivery] = useState<Delivery | null>(null)
    const { result, isOptimizing, optimize, reset } = useRouteOptimization()

    const handleOptimize = () => {
        if (vehicleIds.length === 0 || driverIds.length === 0) return
        optimize({
            date,
            vehicleIds,
            driverIds,
            depotLatitude,
            depotLongitude,
        })
    }

    // Groupe les livraisons par tournée optimisée
    const getDeliveriesForTour = (tourIndex: number): Delivery[] => {
        if (!result) return []
        const tour = result.tours[tourIndex]
        if (!tour) return []
        return tour.stops
            .map((stop) => deliveries.find((d) => d.id === stop.deliveryId))
            .filter((d): d is Delivery => d !== undefined)
    }

    return (
        <div style={{ position: 'relative', height: '100%', width: '100%' }}>
            <MapContainer
                center={[depotLatitude, depotLongitude]}
                zoom={12}
                style={{ height: '100%', width: '100%' }}
            >
                <TileLayer
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                />

                {/* Routes optimisées */}
                {result &&
                    result.tours.map((tour, index) => (
                        <RouteLayer
                            key={tour.vehicleId}
                            deliveries={getDeliveriesForTour(index)}
                            color={ROUTE_COLORS[index % ROUTE_COLORS.length]}
                        />
                    ))}

                {/* Marqueurs livraisons */}
                {result
                    ? result.tours.map((tour, tourIndex) =>
                        tour.stops.map((stop) => {
                            const delivery = deliveries.find((d) => d.id === stop.deliveryId)
                            if (!delivery) return null
                            return (
                                <DeliveryMarker
                                    key={delivery.id}
                                    delivery={delivery}
                                    stopOrder={stop.stopOrder}
                                    isSelected={selectedDelivery?.id === delivery.id}
                                    onClick={setSelectedDelivery}
                                />
                            )
                        })
                    )
                    : deliveries.map((delivery, index) => (
                        <DeliveryMarker
                            key={delivery.id}
                            delivery={delivery}
                            stopOrder={index + 1}
                            isSelected={selectedDelivery?.id === delivery.id}
                            onClick={setSelectedDelivery}
                        />
                    ))}
            </MapContainer>

            <MapControls
                onOptimize={handleOptimize}
                onReset={reset}
                isOptimizing={isOptimizing}
                isOptimized={result !== null}
                totalDistanceKm={result?.totalDistanceKm}
                totalCo2Kg={result?.totalCo2Kg}
            />
        </div>
    )
}