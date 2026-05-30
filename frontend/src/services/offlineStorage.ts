/**
 * Offline Storage — IndexedDB wrapper pour le mode hors-ligne.
 * Stocke les actions en attente de synchronisation.
 */

export interface PendingAction {
  id: string
  type: 'CREATE_DELIVERY' | 'UPDATE_STATUS' | 'UPDATE_GPS' | 'PROOF_OF_DELIVERY'
  payload: unknown
  createdAt: string
  retryCount: number
}

const DB_NAME = 'deliveryos-offline'
const DB_VERSION = 1
const STORE_NAME = 'pending-actions'

class OfflineStorage {
  private db: IDBDatabase | null = null

  async init(): Promise<void> {
    return new Promise((resolve, reject) => {
      const request = indexedDB.open(DB_NAME, DB_VERSION)

      request.onupgradeneeded = (event) => {
        const db = (event.target as IDBOpenDBRequest).result
        if (!db.objectStoreNames.contains(STORE_NAME)) {
          const store = db.createObjectStore(STORE_NAME, { keyPath: 'id' })
          store.createIndex('type', 'type', { unique: false })
          store.createIndex('createdAt', 'createdAt', { unique: false })
        }
      }

      request.onsuccess = (event) => {
        this.db = (event.target as IDBOpenDBRequest).result
        resolve()
      }

      request.onerror = () => reject(request.error)
    })
  }

  async addPendingAction(action: Omit<PendingAction, 'id' | 'createdAt' | 'retryCount'>): Promise<void> {
    if (!this.db) await this.init()

    const pendingAction: PendingAction = {
      ...action,
      id: crypto.randomUUID(),
      createdAt: new Date().toISOString(),
      retryCount: 0,
    }

    return new Promise((resolve, reject) => {
      const tx = this.db!.transaction(STORE_NAME, 'readwrite')
      const store = tx.objectStore(STORE_NAME)
      const request = store.add(pendingAction)
      request.onsuccess = () => resolve()
      request.onerror = () => reject(request.error)
    })
  }

  async getPendingActions(): Promise<PendingAction[]> {
    if (!this.db) await this.init()

    return new Promise((resolve, reject) => {
      const tx = this.db!.transaction(STORE_NAME, 'readonly')
      const store = tx.objectStore(STORE_NAME)
      const request = store.getAll()
      request.onsuccess = () => resolve(request.result)
      request.onerror = () => reject(request.error)
    })
  }

  async removePendingAction(id: string): Promise<void> {
    if (!this.db) await this.init()

    return new Promise((resolve, reject) => {
      const tx = this.db!.transaction(STORE_NAME, 'readwrite')
      const store = tx.objectStore(STORE_NAME)
      const request = store.delete(id)
      request.onsuccess = () => resolve()
      request.onerror = () => reject(request.error)
    })
  }

  async countPendingActions(): Promise<number> {
    if (!this.db) await this.init()

    return new Promise((resolve, reject) => {
      const tx = this.db!.transaction(STORE_NAME, 'readonly')
      const store = tx.objectStore(STORE_NAME)
      const request = store.count()
      request.onsuccess = () => resolve(request.result)
      request.onerror = () => reject(request.error)
    })
  }

  async clearAll(): Promise<void> {
    if (!this.db) await this.init()

    return new Promise((resolve, reject) => {
      const tx = this.db!.transaction(STORE_NAME, 'readwrite')
      const store = tx.objectStore(STORE_NAME)
      const request = store.clear()
      request.onsuccess = () => resolve()
      request.onerror = () => reject(request.error)
    })
  }
}

export const offlineStorage = new OfflineStorage()
export default offlineStorage