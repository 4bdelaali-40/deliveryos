import { create } from 'zustand'
import { persist, createJSONStorage } from 'zustand/middleware'
import type { User, AuthTokens, Notification } from '@/types'

// ─────────────────────────────────────────────────────────────
// Auth Slice
// ─────────────────────────────────────────────────────────────
interface AuthSlice {
    user: User | null
    accessToken: string | null
    refreshToken: string | null
    isAuthenticated: boolean
    setAuth: (user: User, tokens: AuthTokens) => void
    updateUser: (user: User) => void
    setAccessToken: (token: string) => void
    logout: () => void
}

// ─────────────────────────────────────────────────────────────
// Notification Slice
// ─────────────────────────────────────────────────────────────
interface NotificationSlice {
    notifications: Notification[]
    unreadCount: number
    addNotification: (notification: Notification) => void
    markAsRead: (id: string) => void
    markAllAsRead: () => void
    setNotifications: (notifications: Notification[]) => void
}

// ─────────────────────────────────────────────────────────────
// UI Slice
// ─────────────────────────────────────────────────────────────
interface UiSlice {
    sidebarCollapsed: boolean
    toggleSidebar: () => void
    setSidebarCollapsed: (collapsed: boolean) => void
}

// ─────────────────────────────────────────────────────────────
// Offline Slice
// ─────────────────────────────────────────────────────────────
interface OfflineSlice {
    isOnline: boolean
    pendingSyncCount: number
    lastSyncAt: string | null
    setOnline: (online: boolean) => void
    setPendingSyncCount: (count: number) => void
    setLastSyncAt: (date: string) => void
}

// ─────────────────────────────────────────────────────────────
// Combined Store
// ─────────────────────────────────────────────────────────────
type AppStore = AuthSlice & NotificationSlice & UiSlice & OfflineSlice

export const useStore = create<AppStore>()(
    persist(
        (set) => ({
            // ── Auth ────────────────────────────────────────────────
            user: null,
            accessToken: null,
            refreshToken: null,
            isAuthenticated: false,

            setAuth: (user, tokens) =>
                set({
                    user,
                    accessToken: tokens.accessToken,
                    refreshToken: tokens.refreshToken,
                    isAuthenticated: true,
                }),

            updateUser: (user) => set({ user }),

            setAccessToken: (accessToken) => set({ accessToken }),

            logout: () =>
                set({
                    user: null,
                    accessToken: null,
                    refreshToken: null,
                    isAuthenticated: false,
                    notifications: [],
                    unreadCount: 0,
                }),

            // ── Notifications ────────────────────────────────────────
            notifications: [],
            unreadCount: 0,

            addNotification: (notification) =>
                set((state) => ({
                    notifications: [notification, ...state.notifications].slice(0, 50),
                    unreadCount: state.unreadCount + (notification.isRead ? 0 : 1),
                })),

            markAsRead: (id) =>
                set((state) => ({
                    notifications: state.notifications.map((n) =>
                        n.id === id
                            ? { ...n, isRead: true, readAt: new Date().toISOString() }
                            : n
                    ),
                    unreadCount: Math.max(0, state.unreadCount - 1),
                })),

            markAllAsRead: () =>
                set((state) => ({
                    notifications: state.notifications.map((n) => ({
                        ...n,
                        isRead: true,
                        readAt: n.readAt ?? new Date().toISOString(),
                    })),
                    unreadCount: 0,
                })),

            setNotifications: (notifications) =>
                set({
                    notifications,
                    unreadCount: notifications.filter((n) => !n.isRead).length,
                }),

            // ── UI ───────────────────────────────────────────────────
            sidebarCollapsed: false,
            toggleSidebar: () =>
                set((state) => ({ sidebarCollapsed: !state.sidebarCollapsed })),
            setSidebarCollapsed: (collapsed) =>
                set({ sidebarCollapsed: collapsed }),

            // ── Offline ──────────────────────────────────────────────
            isOnline: true,
            pendingSyncCount: 0,
            lastSyncAt: null,
            setOnline: (isOnline) => set({ isOnline }),
            setPendingSyncCount: (pendingSyncCount) => set({ pendingSyncCount }),
            setLastSyncAt: (lastSyncAt) => set({ lastSyncAt }),
        }),
        {
            name: 'deliveryos-store',
            storage: createJSONStorage(() => localStorage),
            // On persiste uniquement auth + UI — jamais les données métier
            partialize: (state) => ({
                user: state.user,
                accessToken: state.accessToken,
                refreshToken: state.refreshToken,
                isAuthenticated: state.isAuthenticated,
                sidebarCollapsed: state.sidebarCollapsed,
            }),
        }
    )
)