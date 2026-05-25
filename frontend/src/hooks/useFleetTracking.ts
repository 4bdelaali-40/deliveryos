import { useEffect, useState, useCallback } from 'react'
import websocketService from '@/services/websocket'
import { useStore } from '@/store/useStore'
import type { GpsPosition } from '@/types'

interface UseFleetTrackingReturn {
    positions: Map<string, GpsPosition>
    isConnected: boolean
    connect: () => void
    disconnect: () => void
}

export function useFleetTracking(): UseFleetTrackingReturn {
    const [positions, setPositions] = useState<Map<string, GpsPosition>>(new Map())
    const [isConnected, setIsConnected] = useState(false)
    const { isOnline } = useStore()

    const handlePositionUpdate = useCallback((position: GpsPosition) => {
        setPositions((prev) => {
            const next = new Map(prev)
            next.set(position.driverId, position)
            return next
        })
    }, [])

    const connect = useCallback(() => {
        websocketService.connect(() => {
            setIsConnected(true)
            websocketService.subscribeToFleet(handlePositionUpdate)
        })
    }, [handlePositionUpdate])

    const disconnect = useCallback(() => {
        websocketService.disconnect()
        setIsConnected(false)
        setPositions(new Map())
    }, [])

    useEffect(() => {
        if (isOnline) {
            connect()
        } else {
            disconnect()
        }

        return () => {
            disconnect()
        }
    }, [isOnline])

    return { positions, isConnected, connect, disconnect }
}