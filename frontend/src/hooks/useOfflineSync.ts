import { useEffect, useCallback } from 'react'
import syncService from '@/services/syncService'
import offlineStorage from '@/services/offlineStorage'
import { useStore } from '@/store/useStore'

export function useOfflineSync() {
  const { setOnline, setPendingSyncCount, isOnline } = useStore()

  // Détecte les changements de connectivité
  useEffect(() => {
    const handleOnline = async () => {
      setOnline(true)
      console.log('Connection restored — starting sync')
      await syncService.sync()
    }

    const handleOffline = () => {
      setOnline(false)
      console.log('Connection lost — offline mode activated')
    }

    window.addEventListener('online', handleOnline)
    window.addEventListener('offline', handleOffline)

    // État initial
    setOnline(navigator.onLine)

    return () => {
      window.removeEventListener('online', handleOnline)
      window.removeEventListener('offline', handleOffline)
    }
  }, [setOnline])

  // Auto-sync toutes les 30 secondes
  useEffect(() => {
    const stopAutoSync = syncService.startAutoSync(30_000)
    return stopAutoSync
  }, [])

  // Met à jour le compteur de pending actions
  useEffect(() => {
    const updateCount = async () => {
      const count = await offlineStorage.countPendingActions()
      setPendingSyncCount(count)
    }

    updateCount()
    const interval = setInterval(updateCount, 10_000)
    return () => clearInterval(interval)
  }, [setPendingSyncCount])

  const manualSync = useCallback(async () => {
    if (!isOnline) return
    await syncService.sync()
  }, [isOnline])

  return { manualSync }
}