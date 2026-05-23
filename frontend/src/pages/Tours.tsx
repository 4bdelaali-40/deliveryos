import { useState } from 'react'
import { useQuery } from 'react-query'
import { DeliveryMap } from '@/components/map/DeliveryMap'
import type { Delivery } from '@/types'
import apiClient from '@/services/api'

export default function Tours() {
    const [selectedDate, setSelectedDate] = useState(
        new Date().toISOString().split('T')[0]
    )

    const { data: deliveriesData } = useQuery(
        ['deliveries', selectedDate],
        async () => {
            const { data } = await apiClient.get('/deliveries', {
                params: { scheduledDate: selectedDate, status: 'CREATED', size: 100 },
            })
            return data.data.content as Delivery[]
        }
    )

    const deliveries = deliveriesData ?? []

    return (
        <div style={{ display: 'flex', flexDirection: 'column', height: '100%', gap: '16px' }}>
            {/* Header */}
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <div>
                    <h1 style={{ fontSize: '24px', fontWeight: 700, color: 'hsl(var(--foreground))' }}>
                        Tours
                    </h1>
                    <p style={{ fontSize: '14px', color: 'hsl(var(--muted-foreground))' }}>
                        {deliveries.length} deliveries to optimize
                    </p>
                </div>

                {/* Date picker */}
                <input
                    type="date"
                    value={selectedDate}
                    onChange={(e) => setSelectedDate(e.target.value)}
                    style={{
                        padding: '8px 12px',
                        borderRadius: '8px',
                        border: '1px solid hsl(var(--border))',
                        backgroundColor: 'hsl(var(--card))',
                        color: 'hsl(var(--foreground))',
                        fontSize: '14px',
                    }}
                />
            </div>

            {/* Map */}
            <div
                style={{
                    flex: 1,
                    borderRadius: '12px',
                    overflow: 'hidden',
                    border: '1px solid hsl(var(--border))',
                    minHeight: '500px',
                }}
            >
                <DeliveryMap
                    deliveries={deliveries}
                    date={selectedDate}
                    vehicleIds={['vehicle-1', 'vehicle-2']}
                    driverIds={['driver-1', 'driver-2']}
                    depotLatitude={48.8566}
                    depotLongitude={2.3522}
                />
            </div>
        </div>
    )
}