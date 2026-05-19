import { NavLink, useNavigate } from 'react-router-dom'
import {
    LayoutDashboard,
    Package,
    Map,
    Radio,
    BarChart3,
    Leaf,
    Truck,
    Users,
    Settings,
    ChevronLeft,
    LogOut,
} from 'lucide-react'
import { useStore } from '@/store/useStore'
import type { UserRole } from '@/types'

interface NavItem {
    label: string
    path: string
    icon: React.ElementType
    roles?: UserRole[]
}

const NAV_ITEMS: NavItem[] = [
    { label: 'Dashboard',     path: '/dashboard', icon: LayoutDashboard },
    { label: 'Deliveries',    path: '/deliveries', icon: Package },
    { label: 'Tours',         path: '/tours',      icon: Map },
    { label: 'Live Tracking', path: '/tracking',   icon: Radio },
    { label: 'Analytics',     path: '/analytics',  icon: BarChart3 },
    { label: 'Carbon',        path: '/carbon',     icon: Leaf },
    {
        label: 'Fleet',
        path: '/fleet',
        icon: Truck,
        roles: ['SUPER_ADMIN', 'ADMIN', 'DISPATCHER'],
    },
    {
        label: 'Drivers',
        path: '/drivers',
        icon: Users,
        roles: ['SUPER_ADMIN', 'ADMIN', 'DISPATCHER'],
    },
    {
        label: 'Admin',
        path: '/admin',
        icon: Settings,
        roles: ['SUPER_ADMIN', 'ADMIN'],
    },
]

export function Sidebar() {
    const { sidebarCollapsed, toggleSidebar, user, logout } = useStore()
    const navigate = useNavigate()

    const handleLogout = () => {
        logout()
        navigate('/login', { replace: true })
    }

    const visibleItems = NAV_ITEMS.filter(
        (item) => !item.roles || (user && item.roles.includes(user.role))
    )

    return (
        <aside
            style={{
                position: 'fixed',
                left: 0,
                top: 0,
                zIndex: 40,
                display: 'flex',
                flexDirection: 'column',
                height: '100%',
                width: sidebarCollapsed ? '64px' : '256px',
                backgroundColor: 'hsl(var(--card))',
                borderRight: '1px solid hsl(var(--border))',
                transition: 'width 0.3s ease',
            }}
        >
            {/* ── Logo ─────────────────────────────────────────── */}
            <div
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    height: '64px',
                    padding: '0 16px',
                    borderBottom: '1px solid hsl(var(--border))',
                }}
            >
                {!sidebarCollapsed && (
                    <span style={{ fontSize: '18px', fontWeight: 700, color: 'hsl(var(--foreground))' }}>
            Delivery<span style={{ color: 'hsl(var(--primary))' }}>OS</span>
          </span>
                )}
                <button
                    onClick={toggleSidebar}
                    style={{
                        marginLeft: 'auto',
                        padding: '6px',
                        borderRadius: '6px',
                        border: 'none',
                        background: 'transparent',
                        cursor: 'pointer',
                        color: 'hsl(var(--muted-foreground))',
                        display: 'flex',
                        alignItems: 'center',
                    }}
                >
                    <ChevronLeft
                        size={16}
                        style={{
                            transform: sidebarCollapsed ? 'rotate(180deg)' : 'rotate(0deg)',
                            transition: 'transform 0.3s ease',
                        }}
                    />
                </button>
            </div>

            {/* ── Navigation ───────────────────────────────────── */}
            <nav style={{ flex: 1, overflowY: 'auto', padding: '16px 8px' }}>
                <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    {visibleItems.map((item) => (
                        <li key={item.path}>
                            <NavLink
                                to={item.path}
                                style={({ isActive }) => ({
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: '12px',
                                    padding: '10px 12px',
                                    borderRadius: '8px',
                                    textDecoration: 'none',
                                    fontSize: '14px',
                                    fontWeight: 500,
                                    backgroundColor: isActive ? 'hsl(var(--primary))' : 'transparent',
                                    color: isActive
                                        ? 'hsl(var(--primary-foreground))'
                                        : 'hsl(var(--muted-foreground))',
                                    transition: 'background-color 0.15s ease, color 0.15s ease',
                                })}
                            >
                                <item.icon size={16} style={{ flexShrink: 0 }} />
                                {!sidebarCollapsed && <span>{item.label}</span>}
                            </NavLink>
                        </li>
                    ))}
                </ul>
            </nav>

            {/* ── User + Logout ─────────────────────────────────── */}
            <div
                style={{
                    padding: '16px',
                    borderTop: '1px solid hsl(var(--border))',
                }}
            >
                {!sidebarCollapsed && user && (
                    <div
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '12px',
                            marginBottom: '12px',
                        }}
                    >
                        <div
                            style={{
                                width: '32px',
                                height: '32px',
                                borderRadius: '50%',
                                backgroundColor: 'hsl(var(--primary))',
                                color: 'hsl(var(--primary-foreground))',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                fontSize: '11px',
                                fontWeight: 700,
                                flexShrink: 0,
                            }}
                        >
                            {user.firstName[0]}{user.lastName[0]}
                        </div>
                        <div style={{ overflow: 'hidden' }}>
                            <p
                                style={{
                                    fontSize: '13px',
                                    fontWeight: 500,
                                    color: 'hsl(var(--foreground))',
                                    whiteSpace: 'nowrap',
                                    overflow: 'hidden',
                                    textOverflow: 'ellipsis',
                                }}
                            >
                                {user.firstName} {user.lastName}
                            </p>
                            <p
                                style={{
                                    fontSize: '11px',
                                    color: 'hsl(var(--muted-foreground))',
                                }}
                            >
                                {user.role}
                            </p>
                        </div>
                    </div>
                )}

                <button
                    onClick={handleLogout}
                    style={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: sidebarCollapsed ? 'center' : 'flex-start',
                        gap: '12px',
                        width: '100%',
                        padding: '8px 12px',
                        borderRadius: '8px',
                        border: 'none',
                        background: 'transparent',
                        cursor: 'pointer',
                        fontSize: '14px',
                        fontWeight: 500,
                        color: 'hsl(var(--muted-foreground))',
                    }}
                >
                    <LogOut size={16} style={{ flexShrink: 0 }} />
                    {!sidebarCollapsed && <span>Logout</span>}
                </button>
            </div>
        </aside>
    )
}