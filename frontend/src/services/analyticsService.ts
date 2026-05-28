import apiClient from './api'
import type { ApiResponse } from '@/types'

export interface KpiData {
  totalDeliveries: number
  deliveredCount: number
  failedCount: number
  createdCount: number
  inTransitCount: number
  deliveryRate: number
  firstAttemptDeliveryRate: number
  totalCo2Kg: number
  co2PerDelivery: number
  period: string
}

export interface ChartDataPoint {
  date: string
  deliveries: number
  delivered: number
  failed: number
}

const analyticsService = {
  async getKpis(period: string = 'MONTHLY'): Promise<KpiData> {
    const { data } = await apiClient.get<ApiResponse<KpiData>>(
      '/analytics/kpis', { params: { period } }
    )
    return data.data
  },
}

export default analyticsService