import { useState } from 'react'
import { useQuery, useQueryClient } from 'react-query'
import apiClient from '@/services/api'
import { useStore } from '@/store/useStore'
import type { ApiResponse, PageResponse, User } from '@/types'

const ROLES = ['SUPER_ADMIN', 'ADMIN', 'DISPATCHER', 'DRIVER', 'VIEWER']

const ROLE_COLORS: Record<string, string> = {
  SUPER_ADMIN: '#a855f7',
  ADMIN: '#3b82f6',
  DISPATCHER: '#f97316',
  DRIVER: '#10b981',
  VIEWER: '#6b7280',
}

export default function Admin() {
  const queryClient = useQueryClient()
  const { user: currentUser } = useStore()

  const [roleFilter, setRoleFilter] = useState('')
  const [showCreateModal, setShowCreateModal] = useState(false)
  const [actionError, setActionError] = useState('')

  const isSuperAdmin = currentUser?.role === 'SUPER_ADMIN'

  const { data, isLoading } = useQuery(
    ['admin-users', roleFilter],
    async () => {
      const { data } = await apiClient.get<ApiResponse<PageResponse<User>>>('/users', {
        params: { size: 100, ...(roleFilter && { role: roleFilter }) },
      })
      return data.data
    }
  )

  const users = data?.content ?? []

  const refresh = () => queryClient.invalidateQueries(['admin-users'])

  const handleToggleStatus = async (user: User) => {
    setActionError('')
    try {
      await apiClient.patch(`/users/${user.id}/status`, { active: !user.isActive })
      refresh()
    } catch (err: any) {
      setActionError(err?.response?.data?.error ?? 'Failed to update status')
    }
  }

  const handleRoleChange = async (user: User, role: string) => {
    setActionError('')
    try {
      await apiClient.patch(`/users/${user.id}/role`, { role })
      refresh()
    } catch (err: any) {
      setActionError(err?.response?.data?.error ?? 'Failed to update role')
    }
  }

  const handleDelete = async (user: User) => {
    if (!confirm(`Delete user ${user.firstName} ${user.lastName}? This cannot be undone.`)) return
    setActionError('')
    try {
      await apiClient.delete(`/users/${user.id}`)
      refresh()
    } catch (err: any) {
      setActionError(err?.response?.data?.error ?? 'Failed to delete user')
    }
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h1 style={{ fontSize: '24px', fontWeight: 700, color: 'hsl(var(--foreground))' }}>
            Admin
          </h1>
          <p style={{ fontSize: '14px', color: 'hsl(var(--muted-foreground))' }}>
            User management and access control
          </p>
        </div>

        <button onClick={() => setShowCreateModal(true)} style={primaryButtonStyle}>
          + New user
        </button>
      </div>

      {actionError && (
        <div style={{
          padding: '10px 14px',
          borderRadius: '8px',
          backgroundColor: '#fee2e2',
          color: '#dc2626',
          fontSize: '13px',
        }}>
          {actionError}
        </div>
      )}

      {/* Role filter */}
      <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
        <FilterPill label="All" active={roleFilter === ''} onClick={() => setRoleFilter('')} />
        {ROLES.map((role) => (
          <FilterPill
            key={role}
            label={role.replace('_', ' ')}
            active={roleFilter === role}
            onClick={() => setRoleFilter(role)}
            color={ROLE_COLORS[role]}
          />
        ))}
      </div>

      {/* Users table */}
      {isLoading ? (
        <p style={{ color: 'hsl(var(--muted-foreground))' }}>Loading users...</p>
      ) : users.length === 0 ? (
        <div style={emptyStateStyle}>No users found</div>
      ) : (
        <div style={{
          borderRadius: '12px',
          border: '1px solid hsl(var(--border))',
          backgroundColor: 'hsl(var(--card))',
          overflow: 'hidden',
        }}>
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '13px' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid hsl(var(--border))' }}>
                  {['Name', 'Email', 'Phone', 'Role', 'Status', 'Last login', ''].map((h) => (
                    <th key={h} style={thStyle}>{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {users.map((user) => (
                  <tr key={user.id} style={{ borderBottom: '1px solid hsl(var(--border))' }}>
                    <td style={{ ...tdStyle, fontWeight: 600 }}>
                      {user.firstName} {user.lastName}
                      {user.id === currentUser?.id && (
                        <span style={{ marginLeft: '6px', fontSize: '11px', color: 'hsl(var(--muted-foreground))' }}>
                          (you)
                        </span>
                      )}
                    </td>
                    <td style={tdStyle}>{user.email}</td>
                    <td style={tdStyle}>{user.phone ?? '—'}</td>
                    <td style={tdStyle}>
                      {isSuperAdmin && user.id !== currentUser?.id ? (
                        <select
                          value={user.role}
                          onChange={(e) => handleRoleChange(user, e.target.value)}
                          style={{ ...selectStyle, padding: '4px 8px', fontSize: '12px' }}
                        >
                          {ROLES.map((r) => (
                            <option key={r} value={r}>{r.replace('_', ' ')}</option>
                          ))}
                        </select>
                      ) : (
                        <span style={{
                          ...badgeStyle,
                          backgroundColor: (ROLE_COLORS[user.role] ?? '#6b7280') + '20',
                          color: ROLE_COLORS[user.role] ?? '#6b7280',
                        }}>
                          {user.role.replace('_', ' ')}
                        </span>
                      )}
                    </td>
                    <td style={tdStyle}>
                      <span style={{
                        ...badgeStyle,
                        backgroundColor: user.isActive ? '#10b98120' : '#ef444420',
                        color: user.isActive ? '#10b981' : '#ef4444',
                      }}>
                        {user.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td style={tdStyle}>
                      {user.lastLoginAt
                        ? new Date(user.lastLoginAt).toLocaleDateString('fr-FR')
                        : 'Never'}
                    </td>
                    <td style={{ ...tdStyle, textAlign: 'right' }}>
                      <div style={{ display: 'flex', gap: '8px', justifyContent: 'flex-end' }}>
                        {user.id !== currentUser?.id && (
                          <>
                            <button
                              onClick={() => handleToggleStatus(user)}
                              style={smallButtonStyle}
                            >
                              {user.isActive ? 'Deactivate' : 'Activate'}
                            </button>
                            {isSuperAdmin && (
                              <button
                                onClick={() => handleDelete(user)}
                                style={{ ...smallButtonStyle, color: '#ef4444', borderColor: '#ef444440' }}
                              >
                                Delete
                              </button>
                            )}
                          </>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div style={{ padding: '12px 16px', borderTop: '1px solid hsl(var(--border))' }}>
            <span style={{ fontSize: '13px', color: 'hsl(var(--muted-foreground))' }}>
              {users.length} {users.length === 1 ? 'user' : 'users'}
            </span>
          </div>
        </div>
      )}

      {!isSuperAdmin && (
        <p style={{ fontSize: '12px', color: 'hsl(var(--muted-foreground))' }}>
          Only SUPER_ADMIN users can change roles or delete accounts.
        </p>
      )}

      {showCreateModal && (
        <CreateUserModal
          onClose={() => setShowCreateModal(false)}
          onCreated={() => {
            setShowCreateModal(false)
            refresh()
          }}
        />
      )}
    </div>
  )
}

// ─────────────────────────────────────────────────────────────
// Create user modal
// ─────────────────────────────────────────────────────────────
function CreateUserModal({ onClose, onCreated }: { onClose: () => void; onCreated: () => void }) {
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    phone: '',
    role: 'DRIVER',
  })
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setSubmitting(true)

    try {
      await apiClient.post('/users', form)
      onCreated()
    } catch (err: any) {
      setError(err?.response?.data?.error ?? 'Failed to create user')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div
      onClick={onClose}
      style={{
        position: 'fixed', inset: 0,
        backgroundColor: 'rgba(0,0,0,0.4)',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        zIndex: 50,
      }}
    >
      <form
        onClick={(e) => e.stopPropagation()}
        onSubmit={handleSubmit}
        style={{
          width: '100%', maxWidth: '420px',
          backgroundColor: 'hsl(var(--card))',
          borderRadius: '12px',
          border: '1px solid hsl(var(--border))',
          padding: '24px',
          display: 'flex', flexDirection: 'column', gap: '12px',
        }}
      >
        <h2 style={{ fontSize: '16px', fontWeight: 700, color: 'hsl(var(--foreground))', marginBottom: '4px' }}>
          New user
        </h2>

        {error && (
          <div style={{ padding: '8px 12px', borderRadius: '6px', backgroundColor: '#fee2e2', color: '#dc2626', fontSize: '12px' }}>
            {error}
          </div>
        )}

        <div style={{ display: 'flex', gap: '8px' }}>
          <Field label="First name" value={form.firstName} onChange={(v) => setForm({ ...form, firstName: v })} required />
          <Field label="Last name" value={form.lastName} onChange={(v) => setForm({ ...form, lastName: v })} required />
        </div>

        <Field label="Email" type="email" value={form.email} onChange={(v) => setForm({ ...form, email: v })} required />
        <Field label="Password" type="password" value={form.password} onChange={(v) => setForm({ ...form, password: v })} required />
        <Field label="Phone" value={form.phone} onChange={(v) => setForm({ ...form, phone: v })} />

        <div>
          <label style={labelStyle}>Role</label>
          <select
            value={form.role}
            onChange={(e) => setForm({ ...form, role: e.target.value })}
            style={selectStyle}
          >
            {ROLES.map((r) => (
              <option key={r} value={r}>{r.replace('_', ' ')}</option>
            ))}
          </select>
        </div>

        <div style={{ display: 'flex', gap: '8px', marginTop: '12px', justifyContent: 'flex-end' }}>
          <button type="button" onClick={onClose} style={smallButtonStyle}>
            Cancel
          </button>
          <button type="submit" disabled={submitting} style={primaryButtonStyle}>
            {submitting ? 'Creating...' : 'Create user'}
          </button>
        </div>
      </form>
    </div>
  )
}

function Field({
  label, value, onChange, type = 'text', required = false,
}: {
  label: string
  value: string
  onChange: (v: string) => void
  type?: string
  required?: boolean
}) {
  return (
    <div style={{ flex: 1 }}>
      <label style={labelStyle}>{label}</label>
      <input
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        required={required}
        style={{ ...selectStyle, width: '100%', boxSizing: 'border-box' }}
      />
    </div>
  )
}

function FilterPill({
  label, active, onClick, color = '#6b7280',
}: {
  label: string
  active: boolean
  onClick: () => void
  color?: string
}) {
  return (
    <button
      onClick={onClick}
      style={{
        padding: '6px 14px',
        borderRadius: '99px',
        fontSize: '12px',
        fontWeight: 500,
        border: `1px solid ${active ? color : 'hsl(var(--border))'}`,
        backgroundColor: active ? color + '20' : 'transparent',
        color: active ? color : 'hsl(var(--foreground))',
        cursor: 'pointer',
      }}
    >
      {label}
    </button>
  )
}

// ─────────────────────────────────────────────────────────────
// Styles
// ─────────────────────────────────────────────────────────────
const labelStyle: React.CSSProperties = {
  fontSize: '12px',
  color: 'hsl(var(--muted-foreground))',
  marginBottom: '4px',
  display: 'block',
}

const selectStyle: React.CSSProperties = {
  padding: '8px 12px',
  borderRadius: '8px',
  border: '1px solid hsl(var(--border))',
  backgroundColor: 'hsl(var(--input))',
  color: 'hsl(var(--foreground))',
  fontSize: '13px',
}

const primaryButtonStyle: React.CSSProperties = {
  padding: '8px 16px',
  borderRadius: '8px',
  border: 'none',
  backgroundColor: 'hsl(var(--primary))',
  color: 'hsl(var(--primary-foreground))',
  fontSize: '13px',
  fontWeight: 600,
  cursor: 'pointer',
}

const smallButtonStyle: React.CSSProperties = {
  padding: '4px 10px',
  borderRadius: '6px',
  border: '1px solid hsl(var(--border))',
  backgroundColor: 'transparent',
  color: 'hsl(var(--foreground))',
  fontSize: '12px',
  cursor: 'pointer',
}

const emptyStateStyle: React.CSSProperties = {
  padding: '40px',
  textAlign: 'center',
  color: 'hsl(var(--muted-foreground))',
  border: '1px dashed hsl(var(--border))',
  borderRadius: '12px',
  fontSize: '14px',
}

const thStyle: React.CSSProperties = {
  textAlign: 'left',
  padding: '10px 16px',
  fontSize: '12px',
  fontWeight: 600,
  textTransform: 'uppercase',
  letterSpacing: '0.05em',
  color: 'hsl(var(--muted-foreground))',
  whiteSpace: 'nowrap',
}

const tdStyle: React.CSSProperties = {
  padding: '10px 16px',
  color: 'hsl(var(--foreground))',
  whiteSpace: 'nowrap',
}

const badgeStyle: React.CSSProperties = {
  padding: '2px 8px',
  borderRadius: '99px',
  fontSize: '11px',
  fontWeight: 500,
}
