// ─────────────────────────────────────────────────────────────
// DeliveryOS — Global Type Definitions
// ─────────────────────────────────────────────────────────────

// ── Enums ────────────────────────────────────────────────────

export type UserRole =
    | 'SUPER_ADMIN'
    | 'ADMIN'
    | 'DISPATCHER'
    | 'DRIVER'
    | 'VIEWER'

export type DeliveryStatus =
    | 'CREATED'
    | 'ASSIGNED'
    | 'PICKED_UP'
    | 'IN_TRANSIT'
    | 'DELIVERED'
    | 'FAILED'
    | 'RETURNED'

export type DeliveryPriority = 'NORMAL' | 'URGENT' | 'VIP'

export type TourStatus = 'PLANNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'

export type VehicleType =
    | 'CAR'
    | 'VAN'
    | 'BIKE'
    | 'CARGO_BIKE'
    | 'ELECTRIC_CAR'
    | 'ELECTRIC_VAN'
    | 'MOTORCYCLE'

export type FuelType = 'DIESEL' | 'PETROL' | 'ELECTRIC' | 'HYBRID'

// ── User ─────────────────────────────────────────────────────

export interface User {
    id: string
    role: UserRole
    firstName: string
    lastName: string
    email: string
    phone?: string
    avatarUrl?: string
    isActive: boolean
    mfaEnabled: boolean
    lastLoginAt?: string
    createdAt: string
    updatedAt: string
}

export interface AuthTokens {
    accessToken: string
    refreshToken: string
    expiresIn: number
    tokenType: 'Bearer'
}

export interface AuthResponse extends AuthTokens {
    user: User
}

// ── Delivery ──────────────────────────────────────────────────

export interface Delivery {
    id: string
    trackingCode: string
    status: DeliveryStatus
    recipientName: string
    recipientPhone?: string
    recipientEmail?: string
    address: string
    city?: string
    postalCode?: string
    latitude?: number
    longitude?: number
    weightKg?: number
    volumeM3?: number
    priority: DeliveryPriority
    timeWindowStart?: string
    timeWindowEnd?: string
    scheduledDate?: string
    notes?: string
    qrCodeUrl?: string
    proofPhotoUrl?: string
    signatureUrl?: string
    deliveredAt?: string
    failedReason?: string
    attemptCount: number
    createdAt: string
    updatedAt: string
}

// ── Vehicle ───────────────────────────────────────────────────

export interface Vehicle {
    id: string
    plateNumber: string
    type: VehicleType
    brand?: string
    model?: string
    year?: number
    capacityKg: number
    capacityM3: number
    co2PerKm: number
    fuelType?: FuelType
    mileageKm: number
    lastRevisionDate?: string
    nextRevisionKm?: number
    isAvailable: boolean
    createdAt: string
    updatedAt: string
}

// ── Tour ──────────────────────────────────────────────────────

export interface TourStop {
    id: string
    deliveryId: string
    delivery?: Delivery
    stopOrder: number
    eta?: string
    actualArrival?: string
    distanceFromPrevKm?: number
    co2FromPrevKg?: number
}

export interface Tour {
    id: string
    driverId?: string
    driver?: User
    vehicleId?: string
    vehicle?: Vehicle
    date: string
    status: TourStatus
    totalDistanceKm?: number
    totalCo2Kg?: number
    estimatedDurationMin?: number
    startedAt?: string
    completedAt?: string
    aiOptimized: boolean
    optimizationGainKm?: number
    optimizationGainPct?: number
    stops: TourStop[]
    createdAt: string
    updatedAt: string
}

// ── GPS ───────────────────────────────────────────────────────

export interface GpsPosition {
    driverId: string
    latitude: number
    longitude: number
    speedKmh?: number
    heading?: number
    recordedAt: string
}

// ── Analytics ─────────────────────────────────────────────────

export interface DashboardKpis {
    deliveryRate: number
    totalDeliveries: number
    deliveredCount: number
    failedCount: number
    averageDelayMin: number
    co2PerDelivery: number
    kmPerDriverPerDay: number
    firstAttemptDeliveryRate: number
    period: string
}

// ── Notification ─────────────────────────────────────────────

export interface Notification {
    id: string
    userId: string
    type: 'ALERT' | 'INFO' | 'WARNING' | 'SUCCESS'
    channel: 'IN_APP' | 'EMAIL' | 'SMS'
    title: string
    message: string
    isRead: boolean
    readAt?: string
    createdAt: string
}

// ── API Wrappers ──────────────────────────────────────────────

export interface ApiResponse<T> {
    success: boolean
    data: T
    message?: string
    error?: string
    timestamp: string
}

export interface PageResponse<T> {
    content: T[]
    page: number
    size: number
    totalElements: number
    totalPages: number
    first: boolean
    last: boolean
}

export interface ApiError {
    status: number
    error: string
    message: string
    timestamp: string
    path: string
}

// ── Forms ─────────────────────────────────────────────────────

export interface LoginForm {
    email: string
    password: string
    totpCode?: string
}

export interface CreateDeliveryForm {
    recipientName: string
    recipientPhone?: string
    recipientEmail?: string
    address: string
    city: string
    postalCode: string
    weightKg?: number
    volumeM3?: number
    priority: DeliveryPriority
    timeWindowStart?: string
    timeWindowEnd?: string
    scheduledDate: string
    notes?: string
}