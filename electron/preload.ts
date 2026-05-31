import { contextBridge, ipcRenderer } from 'electron'

/**
 * Electron Preload — expose des APIs sécurisées au renderer process.
 * Utilise contextBridge pour isoler le contexte Node.js.
 */
contextBridge.exposeInMainWorld('electronAPI', {
  // App info
  getVersion: (): Promise<string> =>
    ipcRenderer.invoke('app:get-version'),

  // Open external URLs
  openExternal: (url: string): Promise<void> =>
    ipcRenderer.invoke('app:open-external', url),

  // Platform info
  platform: process.platform,
})

// TypeScript type declaration
declare global {
  interface Window {
    electronAPI: {
      getVersion: () => Promise<string>
      openExternal: (url: string) => Promise<void>
      platform: string
    }
  }
}