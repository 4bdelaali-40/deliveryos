/**
 * Sync Service — synchronise les actions offline avec le backend.
 */
import apiClient from './api'
import offlineStorage, { type PendingAction } from './offlineStorage'
import { useStore } from '@/store/useStore'

const MAX_RETRY_COUNT = 3

class SyncService {
  private isSyncing = false

  async sync(): Promise<void> {
    if (this.isSyncing) return
    if (!navigator.onLine) return

    this.isSyncing = true
    const { setPendingSyncCount, setLastSyncAt } = useStore.getState()

    try {
      const pendingActions = await offlineStorage.getPendingActions()

      if (pendingActions.length === 0) {
        setPendingSyncCount(0)
        setLastSyncAt(new Date().toISOString())
        return
      }

      let syncedCount = 0

      for (const action of pendingActions) {
        try {
          await this.processAction(action)
          await offlineStorage.removePendingAction(action.id)
          syncedCount++
        } catch (error) {
          console.error(`Failed to sync action ${action.id}:`, error)

          if (action.retryCount >= MAX_RETRY_COUNT) {
            await offlineStorage.removePendingAction(action.id)
            console.warn(`Action ${action.id} removed after ${MAX_RETRY_COUNT} retries`)
          }
        }
      }

      const remaining = await offlineStorage.countPendingActions()
      setPendingSyncCount(remaining)
      setLastSyncAt(new Date().toISOString())

      console.log(`Sync complete: ${syncedCount} actions synced, ${remaining} remaining`)
    } finally {
      this.isSyncing = false
    }
  }

  private async processAction(action: PendingAction): Promise<void> {
    switch (action.type) {
      case 'CREATE_DELIVERY':
        await apiClient.post('/deliveries', action.payload)
        break
      case 'UPDATE_STATUS':
        const { id, status } = action.payload as { id: string; status: string }
        await apiClient.patch(`/deliveries/${id}/status`, { status })
        break
      case 'UPDATE_GPS':
        // GPS positions are ephemeral — skip if offline too long
        break
      case 'PROOF_OF_DELIVERY':
        const proof = action.payload as { id: string; data: FormData }
        await apiClient.post(`/deliveries/${proof.id}/proof`, proof.data)
        break
      default:
        console.warn('Unknown action type:', action.type)
    }
  }

  startAutoSync(intervalMs: number = 30_000): () => void {
    const interval = setInterval(() => {
      if (navigator.onLine) {
        this.sync()
      }
    }, intervalMs)

    return () => clearInterval(interval)
  }
}

export const syncService = new SyncService()
export default syncService