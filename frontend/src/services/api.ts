import axios, {
    type AxiosInstance,
    type AxiosError,
    type InternalAxiosRequestConfig,
} from 'axios'
import { useStore } from '@/store/useStore'
import type { AuthTokens, ApiError } from '@/types'

// ─────────────────────────────────────────────────────────────
// Axios Instance
// ─────────────────────────────────────────────────────────────
const apiClient: AxiosInstance = axios.create({
    baseURL: '/api',
    timeout: 30_000,
    headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
    },
})

// ─────────────────────────────────────────────────────────────
// Request Interceptor — Attache le Bearer Token
// ─────────────────────────────────────────────────────────────
apiClient.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const { accessToken } = useStore.getState()
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`
        }
        return config
    },
    (error) => Promise.reject(error)
)

// ─────────────────────────────────────────────────────────────
// Response Interceptor — Refresh Token sur 401
// ─────────────────────────────────────────────────────────────
let isRefreshing = false
let failedQueue: Array<{
    resolve: (value: string) => void
    reject: (reason: unknown) => void
}> = []

const processQueue = (error: unknown, token: string | null = null) => {
    failedQueue.forEach((prom) => {
        if (error) {
            prom.reject(error)
        } else {
            prom.resolve(token!)
        }
    })
    failedQueue = []
}

apiClient.interceptors.response.use(
    (response) => response,
    async (error: AxiosError<ApiError>) => {
        const originalRequest = error.config as InternalAxiosRequestConfig & {
            _retry?: boolean
        }

        if (error.response?.status === 401 && !originalRequest._retry) {
            if (isRefreshing) {
                return new Promise((resolve, reject) => {
                    failedQueue.push({ resolve, reject })
                }).then((token) => {
                    originalRequest.headers.Authorization = `Bearer ${token}`
                    return apiClient(originalRequest)
                })
            }

            originalRequest._retry = true
            isRefreshing = true

            const { refreshToken, setAccessToken, logout } = useStore.getState()

            if (!refreshToken) {
                logout()
                window.location.href = '/login'
                return Promise.reject(error)
            }

            try {
                const { data } = await axios.post<AuthTokens>('/api/auth/refresh', {
                    refreshToken,
                })

                setAccessToken(data.accessToken)
                processQueue(null, data.accessToken)

                originalRequest.headers.Authorization = `Bearer ${data.accessToken}`
                return apiClient(originalRequest)
            } catch (refreshError) {
                processQueue(refreshError, null)
                logout()
                window.location.href = '/login'
                return Promise.reject(refreshError)
            } finally {
                isRefreshing = false
            }
        }

        return Promise.reject(error)
    }
)

export default apiClient