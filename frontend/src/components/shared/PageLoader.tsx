export function PageLoader() {
    return (
        <div className="flex h-screen w-screen items-center justify-center"
             style={{ backgroundColor: 'hsl(var(--background))' }}>
            <div className="flex flex-col items-center gap-4">
                {/* Spinner */}
                <div
                    className="h-10 w-10 animate-spin rounded-full border-4 border-t-transparent"
                    style={{
                        borderColor: 'hsl(var(--primary))',
                        borderTopColor: 'transparent',
                    }}
                />
                {/* Label */}
                <p style={{ color: 'hsl(var(--muted-foreground))', fontSize: '14px' }}>
                    Loading DeliveryOS…
                </p>
            </div>
        </div>
    )
}