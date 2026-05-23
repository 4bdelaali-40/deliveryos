import { Zap, RotateCcw, Loader } from 'lucide-react'

interface MapControlsProps {
    onOptimize: () => void
    onReset: () => void
    isOptimizing: boolean
    isOptimized: boolean
    totalDistanceKm?: number
    totalCo2Kg?: number
}

export function MapControls({
                                onOptimize,
                                onReset,
                                isOptimizing,
                                isOptimized,
                                totalDistanceKm,
                                totalCo2Kg,
                            }: MapControlsProps) {
    return (
        <div
            style={{
                position: 'absolute',
                top: '16px',
                right: '16px',
                zIndex: 1000,
                display: 'flex',
                flexDirection: 'column',
                gap: '8px',
            }}
        >
            {/* Optimize button */}
            <button
                onClick={onOptimize}
                disabled={isOptimizing}
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '8px',
                    padding: '10px 16px',
                    borderRadius: '8px',
                    border: 'none',
                    backgroundColor: isOptimizing ? '#6b7280' : '#3b82f6',
                    color: 'white',
                    fontSize: '14px',
                    fontWeight: 600,
                    cursor: isOptimizing ? 'not-allowed' : 'pointer',
                    boxShadow: '0 2px 8px rgba(0,0,0,0.2)',
                }}
            >
                {isOptimizing ? (
                    <Loader size={16} style={{ animation: 'spin 1s linear infinite' }} />
                ) : (
                    <Zap size={16} />
                )}
                {isOptimizing ? 'Optimizing...' : 'Optimize Routes'}
            </button>

            {/* Reset button */}
            {isOptimized && (
                <button
                    onClick={onReset}
                    style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '8px',
                        padding: '10px 16px',
                        borderRadius: '8px',
                        border: 'none',
                        backgroundColor: '#6b7280',
                        color: 'white',
                        fontSize: '14px',
                        fontWeight: 600,
                        cursor: 'pointer',
                        boxShadow: '0 2px 8px rgba(0,0,0,0.2)',
                    }}
                >
                    <RotateCcw size={16} />
                    Reset
                </button>
            )}

            {/* Stats */}
            {isOptimized && totalDistanceKm && (
                <div
                    style={{
                        padding: '10px 16px',
                        borderRadius: '8px',
                        backgroundColor: 'white',
                        boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                        fontSize: '13px',
                    }}
                >
                    <p style={{ fontWeight: 600, marginBottom: '4px', color: '#111827' }}>
                        Optimization Result
                    </p>
                    <p style={{ color: '#6b7280' }}>
                        Distance: {totalDistanceKm.toFixed(1)} km
                    </p>
                    {totalCo2Kg && (
                        <p style={{ color: '#6b7280' }}>
                            CO2: {(totalCo2Kg * 1000).toFixed(0)} g
                        </p>
                    )}
                </div>
            )}
        </div>
    )
}