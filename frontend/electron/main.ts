import { app, BrowserWindow, shell } from 'electron'
import { join } from 'path'

const isDev = process.env.NODE_ENV === 'development'
let mainWindow: BrowserWindow | null = null

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
    },
    show: false,
  })

  if (isDev) {
    const loadApp = () => {
      mainWindow?.loadURL('http://localhost:5173').catch(() => {
        // Vite pas encore prêt, on réessaie dans 1 seconde
        setTimeout(loadApp, 1000)
      })
    }

    // Attendre 30 secondes que Vite démarre
    setTimeout(loadApp, 30000)
    mainWindow.webContents.openDevTools()
  } else {
    mainWindow.loadFile(join(__dirname, '../dist/index.html'))
  }

  mainWindow.once('ready-to-show', () => {
    mainWindow?.show()
  })

  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    shell.openExternal(url)
    return { action: 'deny' }
  })

  mainWindow.on('closed', () => {
    mainWindow = null
  })
}

app.whenReady().then(() => {
  createWindow()
  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow()
  })
})

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') app.quit()
})