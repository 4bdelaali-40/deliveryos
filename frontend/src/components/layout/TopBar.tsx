import { Bell, Wifi, WifiOff } from 'lucide-react'
import { useStore } from '@/store/useStore'

/**
 * Barre supérieure — indicateurs de statut + notifications.
 */
export function TopBar() {
    const { unreadCount, isOnline, pendingSyncCount } = useStore()

    return (
        <header
            style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                height: '64px',
                padding: '0 24px',
                backgroundColor: 'hsl(var(--card))',
                borderBottom: '1px solid hsl(var(--border))',
                flexShrink: 0,
            }}
        >
            {/* Left — placeholder breadcrumb */}
            <div />

            {/* Right — indicateurs */}
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>

                {/* Offline badge */}
                {!isOnline && (
                    <div
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '6px',
                            padding: '4px 12px',
                            borderRadius: '99px',
                            backgroundColor: 'hsl(var(--warning) / 0.1)',
                            color: 'hsl(var(--warning))',
                            fontSize: '12px',
                            fontWeight: 500,
                        }}
                    >
                        <WifiOff size={14} />
                        <span>Offline</span>
                        {pendingSyncCount > 0 && (
                            <span
                                style={{
                                    marginLeft: '4px',
                                    padding: '1px 6px',
                                    borderRadius: '99px',
                                    backgroundColor: 'hsl(var(--warning) / 0.2)',
                                }}
                            >
                {pendingSyncCount} pending
              </span>
                        )}
                    </div>
                )}

                {/* Online indicator */}
                {isOnline && (
                    <Wifi size={16} style={{ color: 'hsl(var(--success))' }} />
                )}

                {/* Notifications */}
                <button
                    style={{
                        position: 'relative',
                        padding: '8px',
                        borderRadius: '8px',
                        border: 'none',
                        background: 'transparent',
                        cursor: 'pointer',
                        color: 'hsl(var(--muted-foreground))',
                        display: 'flex',
                        alignItems: 'center',
                    }}
                    aria-label="Notifications"
                >
                    <Bell size={20} />
                    {unreadCount > 0 && (
                        <span
                            style={{
                                position: 'absolute',
                                top: '4px',
                                right: '4px',
                                minWidth: '16px',
                                height: '16px',
                                padding: '0 4px',
                                borderRadius: '99px',
                                backgroundColor: 'hsl(var(--destructive))',
                                color: 'hsl(var(--destructive-foreground))',
                                fontSize: '10px',
                                fontWeight: 700,
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                            }}
                        >
              {unreadCount > 99 ? '99+' : unreadCount}
            </span>
                    )}
                </button>
            </div>
        </header>
    )
}