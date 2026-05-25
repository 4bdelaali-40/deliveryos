import { Client, type StompSubscription } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import type { GpsPosition } from '@/types'

type FleetUpdateCallback = (position: GpsPosition) => void

class WebSocketService {
    private client: Client | null = null
    private subscriptions: StompSubscription[] = []

    connect(onConnected?: () => void): void {
        this.client = new Client({
            webSocketFactory: () => new SockJS('/ws'),
            reconnectDelay: 5000,
            onConnect: () => {
                console.log('WebSocket connected')
                onConnected?.()
            },
            onDisconnect: () => {
                console.log('WebSocket disconnected')
            },
            onStompError: (frame) => {
                console.error('STOMP error:', frame)
            },
        })

        this.client.activate()
    }

    disconnect(): void {
        this.subscriptions.forEach((sub) => sub.unsubscribe())
        this.subscriptions = []
        this.client?.deactivate()
        this.client = null
    }

    subscribeToFleet(callback: FleetUpdateCallback): void {
        if (!this.client?.connected) return

        const sub = this.client.subscribe('/topic/fleet', (message) => {
            try {
                const position = JSON.parse(message.body) as GpsPosition
                callback(position)
            } catch (err) {
                console.error('Failed to parse GPS position:', err)
            }
        })

        this.subscriptions.push(sub)
    }

    subscribeToTour(tourId: string, callback: FleetUpdateCallback): void {
        if (!this.client?.connected) return

        const sub = this.client.subscribe(`/topic/tour/${tourId}`, (message) => {
            try {
                const position = JSON.parse(message.body) as GpsPosition
                callback(position)
            } catch (err) {
                console.error('Failed to parse tour position:', err)
            }
        })

        this.subscriptions.push(sub)
    }

    sendGpsUpdate(
        driverId: string,
        latitude: number,
        longitude: number,
        tourId?: string,
        speedKmh?: number,
        heading?: number
    ): void {
        if (!this.client?.connected) return

        this.client.publish({
            destination: '/app/gps/update',
            body: JSON.stringify({
                driverId,
                tourId,
                latitude,
                longitude,
                speedKmh,
                heading,
            }),
        })
    }

    isConnected(): boolean {
        return this.client?.connected ?? false
    }
}

export const websocketService = new WebSocketService()
export default websocketService