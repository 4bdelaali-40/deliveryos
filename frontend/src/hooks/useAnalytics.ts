import { useQuery } from 'react-query'
import analyticsService, { type KpiData } from '@/services/analyticsService'

export function useKpis(period: string = 'MONTHLY') {
  return useQuery<KpiData>(
    ['kpis', period],
    () => analyticsService.getKpis(period),
    {
      staleTime: 1000 * 60 * 5,
      retry: 2,
    }
  )
}