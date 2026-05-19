import { Outlet } from 'react-router-dom'
import { Sidebar } from './Sidebar'
import { TopBar } from './TopBar'
import { useStore } from '@/store/useStore'

/**
 * Layout principal — Sidebar + TopBar + contenu de la page.
 */
export function AppLayout() {
    const { sidebarCollapsed } = useStore()

    return (
        <div style={{ display: 'flex', height: '100vh', overflow: 'hidden' }}>
            <Sidebar />
            <div
                style={{
                    display: 'flex',
                    flexDirection: 'column',
                    flex: 1,
                    overflow: 'hidden',
                    marginLeft: sidebarCollapsed ? '64px' : '256px',
                    transition: 'margin-left 0.3s ease',
                }}
            >
                <TopBar />
                <main
                    style={{
                        flex: 1,
                        overflowY: 'auto',
                        padding: '24px',
                        backgroundColor: 'hsl(var(--background))',
                    }}
                >
                    <Outlet />
                </main>
            </div>
        </div>
    )
}