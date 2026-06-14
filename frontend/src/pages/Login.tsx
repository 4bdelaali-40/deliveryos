import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useStore } from '@/store/useStore'
import type { User } from '@/types'

export default function Login() {
  const navigate = useNavigate()
  const { setAuth } = useStore()
  const [email, setEmail] = useState('admin@deliveryos.fr')
  const [password, setPassword] = useState('password123')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      })

      if (!response.ok) {
        throw new Error('Identifiants invalides')
      }

      const data = await response.json()

      // Déballage du wrapper ApiResponse { success, data: { ... } }
      const payload = data.data ?? data

      const user: User = {
        id: payload.userId,
        role: payload.role,
        firstName: payload.firstName,
        lastName: payload.lastName,
        email: payload.email,
        isActive: true,
        mfaEnabled: payload.mfaEnabled,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      }

      const tokens = {
        accessToken: payload.accessToken,
        refreshToken: payload.refreshToken,
        expiresIn: payload.expiresIn,
        tokenType: 'Bearer' as const,
      }

      setAuth(user, tokens)
      navigate('/dashboard')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erreur de connexion')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div
      style={{
        display: 'flex',
        height: '100vh',
        width: '100vw',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: 'hsl(var(--background))',
      }}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '400px',
          padding: '32px',
          borderRadius: '12px',
          border: '1px solid hsl(var(--border))',
          backgroundColor: 'hsl(var(--card))',
          boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
        }}
      >
        {/* Logo */}
        <h1
          style={{
            fontSize: '24px',
            fontWeight: 700,
            marginBottom: '4px',
            color: 'hsl(var(--foreground))',
          }}
        >
          Delivery<span style={{ color: 'hsl(var(--primary))' }}>OS</span>
        </h1>

        <p
          style={{
            fontSize: '14px',
            color: 'hsl(var(--muted-foreground))',
            marginBottom: '32px',
          }}
        >
          Enterprise delivery management platform
        </p>

        {/* Form */}
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {error && (
            <div style={{
              padding: '12px',
              borderRadius: '8px',
              backgroundColor: 'hsl(var(--destructive))',
              color: 'hsl(var(--destructive-foreground))',
              fontSize: '13px',
            }}>
              {error}
            </div>
          )}

          <div>
            <label style={{ fontSize: '13px', color: 'hsl(var(--muted-foreground))', marginBottom: '4px', display: 'block' }}>
              Email
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              style={{
                width: '100%',
                padding: '8px 12px',
                borderRadius: '6px',
                border: '1px solid hsl(var(--border))',
                backgroundColor: 'hsl(var(--input))',
                color: 'hsl(var(--foreground))',
                fontSize: '13px',
                boxSizing: 'border-box',
              }}
            />
          </div>

          <div>
            <label style={{ fontSize: '13px', color: 'hsl(var(--muted-foreground))', marginBottom: '4px', display: 'block' }}>
              Password
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              style={{
                width: '100%',
                padding: '8px 12px',
                borderRadius: '6px',
                border: '1px solid hsl(var(--border))',
                backgroundColor: 'hsl(var(--input))',
                color: 'hsl(var(--foreground))',
                fontSize: '13px',
                boxSizing: 'border-box',
              }}
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            style={{
              padding: '10px 16px',
              borderRadius: '6px',
              border: 'none',
              backgroundColor: 'hsl(var(--primary))',
              color: 'hsl(var(--primary-foreground))',
              fontSize: '13px',
              fontWeight: 600,
              cursor: loading ? 'not-allowed' : 'pointer',
              opacity: loading ? 0.6 : 1,
            }}
          >
            {loading ? 'Connexion...' : 'Se connecter'}
          </button>
        </form>

        <p style={{ fontSize: '12px', color: 'hsl(var(--muted-foreground))', marginTop: '16px', textAlign: 'center' }}>
          Tester avec: admin@deliveryos.fr / password123
        </p>
      </div>
    </div>
  )
}