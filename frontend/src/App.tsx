import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from 'react-query'
import { Toaster } from 'react-hot-toast'
import { lazy, Suspense } from 'react'
import { useStore } from '@/store/useStore'
import { ProtectedRoute } from '@/components/shared/ProtectedRoute'
import { AppLayout } from '@/components/layout/AppLayout'
import { PageLoader } from '@/components/shared/PageLoader'

// ── Pages (lazy loaded) ──────────────────────────────────────
const Login          = lazy(() => import('@/pages/Login'))
const Dashboard      = lazy(() => import('@/pages/Dashboard'))
const Deliveries     = lazy(() => import('@/pages/Deliveries'))
const Tours          = lazy(() => import('@/pages/Tours'))
const Tracking       = lazy(() => import('@/pages/Tracking'))
const Analytics      = lazy(() => import('@/pages/Analytics'))
const Fleet          = lazy(() => import('@/pages/Fleet'))
const Drivers        = lazy(() => import('@/pages/Drivers'))
const CarbonDashboard = lazy(() => import('@/pages/CarbonDashboard'))
const Admin          = lazy(() => import('@/pages/Admin'))

// ── React Query Client ───────────────────────────────────────
const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            staleTime: 1000 * 60 * 5,
            retry: 2,
            refetchOnWindowFocus: false,
        },
        mutations: {
            retry: 1,
        },
    },
})

// ── App ──────────────────────────────────────────────────────
function App() {
    const { isAuthenticated } = useStore()

    return (
        <QueryClientProvider client={queryClient}>
            <BrowserRouter>
                <Suspense fallback={<PageLoader />}>
                    <Routes>

                        {/* Public */}
                        <Route
                            path="/login"
                            element={
                                isAuthenticated
                                    ? <Navigate to="/dashboard" replace />
                                    : <Login />
                            }
                        />

                        {/* Protégé — tous les rôles */}
                        <Route element={<ProtectedRoute />}>
                            <Route element={<AppLayout />}>
                                <Route index element={<Navigate to="/dashboard" replace />} />
                                <Route path="/dashboard"  element={<Dashboard />} />
                                <Route path="/deliveries/*" element={<Deliveries />} />
                                <Route path="/tours/*"    element={<Tours />} />
                                <Route path="/tracking"   element={<Tracking />} />
                                <Route path="/analytics"  element={<Analytics />} />
                                <Route path="/carbon"     element={<CarbonDashboard />} />

                                {/* ADMIN + DISPATCHER */}
                                <Route
                                    element={
                                        <ProtectedRoute
                                            roles={['SUPER_ADMIN', 'ADMIN', 'DISPATCHER']}
                                        />
                                    }
                                >
                                    <Route path="/fleet/*"   element={<Fleet />} />
                                    <Route path="/drivers/*" element={<Drivers />} />
                                </Route>

                                {/* SUPER_ADMIN + ADMIN uniquement */}
                                <Route
                                    element={
                                        <ProtectedRoute roles={['SUPER_ADMIN', 'ADMIN']} />
                                    }
                                >
                                    <Route path="/admin/*" element={<Admin />} />
                                </Route>
                            </Route>
                        </Route>

                        {/* 404 */}
                        <Route path="*" element={<Navigate to="/dashboard" replace />} />

                    </Routes>
                </Suspense>

                <Toaster
                    position="top-right"
                    toastOptions={{
                        duration: 4000,
                        style: {
                            borderRadius: '8px',
                            fontSize: '14px',
                        },
                    }}
                />
            </BrowserRouter>
        </QueryClientProvider>
    )
}

export default App