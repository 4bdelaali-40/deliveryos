import { useState } from 'react'
import { useMutation } from 'react-query'
import toast from 'react-hot-toast'
import tourService, { type OptimizeRoutesRequest, type VrpResult } from '@/services/tourService'

interface UseRouteOptimizationReturn {
    result: VrpResult | null
    isOptimizing: boolean
    optimize: (request: OptimizeRoutesRequest) => void
    reset: () => void
}

export function useRouteOptimization(): UseRouteOptimizationReturn {
    const [result, setResult] = useState<VrpResult | null>(null)

    const mutation = useMutation(
        (request: OptimizeRoutesRequest) => tourService.optimizeRoutes(request),
        {
            onSuccess: (data) => {
                setResult(data)
                toast.success(
                    `Routes optimized — ${data.tours.length} tours, ${data.totalDistanceKm.toFixed(1)} km total`
                )
            },
            onError: () => {
                toast.error('Route optimization failed. Please try again.')
            },
        }
    )

    return {
        result,
        isOptimizing: mutation.isLoading,
        optimize: mutation.mutate,
        reset: () => setResult(null),
    }
}