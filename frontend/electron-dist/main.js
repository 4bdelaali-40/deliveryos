"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const electron_1 = require("electron");
const path_1 = require("path");
const isDev = process.env.NODE_ENV === 'development';
let mainWindow = null;
function createWindow() {
    mainWindow = new electron_1.BrowserWindow({
        width: 1440,
        height: 900,
        minWidth: 1024,
        minHeight: 768,
        title: 'DeliveryOS',
        webPreferences: {
            preload: (0, path_1.join)(__dirname, 'preload.js'),
            contextIsolation: true,
            nodeIntegration: false,
        },
        show: false,
    });
    if (isDev) {
        const loadApp = () => {
            mainWindow?.loadURL('http://localhost:5173').catch(() => {
                // Vite pas encore prêt, on réessaie dans 1 seconde
                setTimeout(loadApp, 1000);
            });
        };
        // Attendre 30 secondes que Vite démarre
        setTimeout(loadApp, 30000);
        mainWindow.webContents.openDevTools();
    }
    else {
        mainWindow.loadFile((0, path_1.join)(__dirname, '../dist/index.html'));
    }
    mainWindow.once('ready-to-show', () => {
        mainWindow?.show();
    });
    mainWindow.webContents.setWindowOpenHandler(({ url }) => {
        electron_1.shell.openExternal(url);
        return { action: 'deny' };
    });
    mainWindow.on('closed', () => {
        mainWindow = null;
    });
}
electron_1.app.whenReady().then(() => {
    createWindow();
    electron_1.app.on('activate', () => {
        if (electron_1.BrowserWindow.getAllWindows().length === 0)
            createWindow();
    });
});
electron_1.app.on('window-all-closed', () => {
    if (process.platform !== 'darwin')
        electron_1.app.quit();
});
