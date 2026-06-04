import { contextBridge, ipcRenderer } from 'electron'

contextBridge.exposeInMainWorld('electronAPI', {
  getVersion: (): Promise<string> =>
    ipcRenderer.invoke('app:get-version'),
  platform: process.platform,
})