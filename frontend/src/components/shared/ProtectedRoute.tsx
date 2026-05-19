import { Navigate, Outlet } from 'react-router-dom'
import { useStore } from '@/store/useStore'
import type { UserRole } from '@/types'

interface ProtectedRouteProps {
    roles?: UserRole[]
}

/**
 * Route protégée — redirige vers /login si non authentifié.
 * Si roles est défini, vérifie que l'utilisateur a le bon rôle.
 */
export function ProtectedRoute({ roles }: ProtectedRouteProps) {
    const { isAuthenticated, user } = useStore()

    // Non authentifié → login
    if (!isAuthenticated || !user) {
        return <Navigate to="/login" replace />
    }

    // Rôle insuffisant → dashboard
    if (roles && roles.length > 0 && !roles.includes(user.role)) {
        return <Navigate to="/dashboard" replace />
    }

    return <Outlet />
}