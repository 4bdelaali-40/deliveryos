import { app, BrowserWindow, shell, ipcMain } from 'electron'
import { join } from 'path'
import { spawn, type ChildProcess } from 'child_process'

// ─────────────────────────────────────────
// Constants
// ─────────────────────────────────────────
const VITE_DEV_SERVER_URL = process.env.VITE_DEV_SERVER_URL ?? 'http://localhost:5173'
const isDev = process.env.NODE_ENV === 'development'

let mainWindow: BrowserWindow | null = null
let backendProcess: ChildProcess | null = null

// ─────────────────────────────────────────
// Create Window
// ─────────────────────────────────────────
function createWindow(): void {
  mainWindow = new BrowserWindow({
    width: 1440,
    height: 900,
    minWidth: 1024,
    minHeight: 768,
    title: 'DeliveryOS',
    webPreferences: {
      preload: join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false,
      sandbox: false,
    },
    titleBarStyle: process.platform === 'darwin' ? 'hiddenInset' : 'default',
    show: false,
  })

  // Load the app
  if (isDev) {
    mainWindow.loadURL(VITE_DEV_SERVER_URL)
    mainWindow.webContents.openDevTools()
  } else {
    mainWindow.loadFile(join(__dirname, '../dist/index.html'))
  }

  // Show when ready to prevent white flash
  mainWindow.once('ready-to-show', () => {
    mainWindow?.show()
  })

  // Open external links in browser
  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    shell.openExternal(url)
    return { action: 'deny' }
  })

  mainWindow.on('closed', () => {
    mainWindow = null
  })
}

// ─────────────────────────────────────────
// App Lifecycle
// ─────────────────────────────────────────
app.whenReady().then(() => {
  createWindow()

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow()
    }
  })
})

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

app.on('quit', () => {
  if (backendProcess) {
    backendProcess.kill()
  }
})

// ─────────────────────────────────────────
// IPC Handlers
// ─────────────────────────────────────────
ipcMain.handle('app:get-version', () => app.getVersion())

ipcMain.handle('app:open-external', (_, url: string) => {
  shell.openExternal(url)
})