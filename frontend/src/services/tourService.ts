import apiClient from './api'
import type { ApiResponse, PageResponse } from '@/types'

export interface TourStop {
    deliveryId: string
    stopOrder: number
    eta: string
    distanceFromPrevKm: number
    co2FromPrevKg: number
}

export interface Tour {
    id: string
    driverId?: string
    vehicleId?: string
    date: string
    status: string
    totalDistanceKm?: number
    totalCo2Kg?: number
    estimatedDurationMin?: number
    aiOptimized: boolean
    optimizationGainKm?: number
    optimizationGainPct?: number
    stops: TourStop[]
    createdAt: string
}

export interface OptimizeRoutesRequest {
    date: string
    vehicleIds: string[]
    driverIds: string[]
    depotLatitude: number
    depotLongitude: number
}

export interface VrpTourResult {
    vehicleId: string
    driverId: string
    stops: {
        deliveryId: string
        stopOrder: number
        eta: string
        distanceFromPrevKm: number
        co2FromPrevKg: number
    }[]
    totalDistanceKm: number
    totalCo2Kg: number
    totalDurationMin: number
}

export interface VrpResult {
    tours: VrpTourResult[]
    totalDistanceKm: number
    totalCo2Kg: number
    unassignedDeliveryIds: string[]
    executionMs: number
    solverStatus: string
}

const tourService = {
    async findAll(date?: string): Promise<PageResponse<Tour>> {
        const params = date ? { date } : {}
        const { data } = await apiClient.get<ApiResponse<PageResponse<Tour>>>(
            '/tours', { params }
        )
        return data.data
    },

    async findById(id: string): Promise<Tour> {
        const { data } = await apiClient.get<ApiResponse<Tour>>(`/tours/${id}`)
        return data.data
    },

    async optimizeRoutes(request: OptimizeRoutesRequest): Promise<VrpResult> {
        const { data } = await apiClient.post<ApiResponse<VrpResult>>(
            '/ai/optimize-routes', request
        )
        return data.data
    },

    async startTour(id: string): Promise<Tour> {
        const { data } = await apiClient.patch<ApiResponse<Tour>>(
            `/tours/${id}/start`
        )
        return data.data
    },

    async completeTour(id: string): Promise<Tour> {
        const { data } = await apiClient.patch<ApiResponse<Tour>>(
            `/tours/${id}/complete`
        )
        return data.data
    },
}

export default tourService