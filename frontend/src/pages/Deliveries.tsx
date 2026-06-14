import { useState } from 'react'
import { useQuery, useQueryClient } from 'react-query'
import apiClient from '@/services/api'
import type { ApiResponse, PageResponse, Delivery } from '@/types'

// ─────────────────────────────────────────────────────────────
// Constantes d'affichage
// ─────────────────────────────────────────────────────────────
const STATUS_COLORS: Record<string, string> = {
  CREATED: '#6b7280',
  ASSIGNED: '#3b82f6',
  PICKED_UP: '#8b5cf6',
  IN_TRANSIT: '#f97316',
  DELIVERED: '#10b981',
  FAILED: '#ef4444',
  RETURNED: '#dc2626',
}

const STATUS_LABELS: Record<string, string> = {
  CREATED: 'Created',
  ASSIGNED: 'Assigned',
  PICKED_UP: 'Picked Up',
  IN_TRANSIT: 'In Transit',
  DELIVERED: 'Delivered',
  FAILED: 'Failed',
  RETURNED: 'Returned',
}

const PRIORITY_COLORS: Record<string, string> = {
  NORMAL: '#6b7280',
  URGENT: '#f59e0b',
  VIP: '#a855f7',
}

const STATUS_OPTIONS = Object.keys(STATUS_LABELS)
const PRIORITY_OPTIONS = ['NORMAL', 'URGENT', 'VIP']

// Transitions de statut autorisées (cote UI, le backend valide aussi)
const NEXT_STATUS: Record<string, string[]> = {
  CREATED: ['ASSIGNED'],
  ASSIGNED: ['PICKED_UP', 'FAILED'],
  PICKED_UP: ['IN_TRANSIT', 'FAILED'],
  IN_TRANSIT: ['DELIVERED', 'FAILED'],
  DELIVERED: [],
  FAILED: ['RETURNED', 'ASSIGNED'],
  RETURNED: ['ASSIGNED'],
}

// ─────────────────────────────────────────────────────────────
// Composant principal
// ─────────────────────────────────────────────────────────────
export default function Deliveries() {
  const queryClient = useQueryClient()

  const [page, setPage] = useState(0)
  const [statusFilter, setStatusFilter] = useState<string>('')
  const [priorityFilter, setPriorityFilter] = useState<string>('')
  const [cityFilter, setCityFilter] = useState<string>('')
  const [selected, setSelected] = useState<Delivery | null>(null)
  const pageSize = 10

  const { data, isLoading, isFetching } = useQuery(
    ['deliveries', page, statusFilter, priorityFilter, cityFilter],
    async () => {
      const { data } = await apiClient.get<ApiResponse<PageResponse<Delivery>>>(
        '/deliveries',
        {
          params: {
            page,
            size: pageSize,
            sortBy: 'createdAt',
            sortDir: 'desc',
            ...(statusFilter && { status: statusFilter }),
            ...(priorityFilter && { priority: priorityFilter }),
            ...(cityFilter && { city: cityFilter }),
          },
        }
      )
      return data.data
    },
    { keepPreviousData: true }
  )

  const deliveries = data?.content ?? []
  const totalElements = data?.totalElements ?? 0
  const totalPages = data?.totalPages ?? 0

  const handleStatusChange = async (delivery: Delivery, newStatus: string) => {
    try {
      await apiClient.patch(`/deliveries/${delivery.id}/status`, { status: newStatus })
      queryClient.invalidateQueries(['deliveries'])
      if (selected?.id === delivery.id) {
        setSelected({ ...delivery, status: newStatus as Delivery['status'] })
      }
    } catch (err) {
      console.error('Failed to update status', err)
    }
  }

  const resetFilters = () => {
    setStatusFilter('')
    setPriorityFilter('')
    setCityFilter('')
    setPage(0)
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h1 style={{ fontSize: '24px', fontWeight: 700, color: 'hsl(var(--foreground))' }}>
            Deliveries
          </h1>
          <p style={{ fontSize: '14px', color: 'hsl(var(--muted-foreground))' }}>
            {totalElements} {totalElements === 1 ? 'delivery' : 'deliveries'} total
          </p>
        </div>
      </div>

      {/* Filters */}
      <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', alignItems: 'center' }}>
        <select
          value={statusFilter}
          onChange={(e) => { setStatusFilter(e.target.value); setPage(0) }}
          style={selectStyle}
        >
          <option value="">All statuses</option>
          {STATUS_OPTIONS.map((s) => (
            <option key={s} value={s}>{STATUS_LABELS[s]}</option>
          ))}
        </select>

        <select
          value={priorityFilter}
          onChange={(e) => { setPriorityFilter(e.target.value); setPage(0) }}
          style={selectStyle}
        >
          <option value="">All priorities</option>
          {PRIORITY_OPTIONS.map((p) => (
            <option key={p} value={p}>{p}</option>
          ))}
        </select>

        <input
          type="text"
          placeholder="Filter by city..."
          value={cityFilter}
          onChange={(e) => { setCityFilter(e.target.value); setPage(0) }}
          style={{ ...selectStyle, minWidth: '180px' }}
        />

        {(statusFilter || priorityFilter || cityFilter) && (
          <button onClick={resetFilters} style={resetButtonStyle}>
            Clear filters
          </button>
        )}

        {isFetching && (
          <span style={{ fontSize: '12px', color: 'hsl(var(--muted-foreground))' }}>
            Refreshing...
          </span>
        )}
      </div>

      {/* Table */}
      {isLoading ? (
        <p style={{ color: 'hsl(var(--muted-foreground))' }}>Loading deliveries...</p>
      ) : deliveries.length === 0 ? (
        <div style={emptyStateStyle}>No deliveries match the current filters</div>
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
                  {['Tracking', 'Recipient', 'Address', 'Priority', 'Status', 'Scheduled', ''].map((h) => (
                    <th key={h} style={thStyle}>{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {deliveries.map((delivery) => (
                  <tr
                    key={delivery.id}
                    onClick={() => setSelected(delivery)}
                    style={{
                      borderBottom: '1px solid hsl(var(--border))',
                      cursor: 'pointer',
                      transition: 'background-color 0.15s',
                    }}
                    onMouseEnter={(e) => e.currentTarget.style.backgroundColor = 'hsl(var(--muted))'}
                    onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                  >
                    <td style={{ ...tdStyle, fontFamily: 'monospace', fontWeight: 600 }}>
                      {delivery.trackingCode}
                    </td>
                    <td style={tdStyle}>{delivery.recipientName}</td>
                    <td style={tdStyle}>
                      {delivery.address}, {delivery.city} {delivery.postalCode}
                    </td>
                    <td style={tdStyle}>
                      <span style={{ ...badgeStyle, backgroundColor: PRIORITY_COLORS[delivery.priority] + '20', color: PRIORITY_COLORS[delivery.priority] }}>
                        {delivery.priority}
                      </span>
                    </td>
                    <td style={tdStyle}>
                      <span style={{ ...badgeStyle, backgroundColor: STATUS_COLORS[delivery.status] + '20', color: STATUS_COLORS[delivery.status] }}>
                        {STATUS_LABELS[delivery.status]}
                      </span>
                    </td>
                    <td style={tdStyle}>
                      {delivery.scheduledDate
                        ? new Date(delivery.scheduledDate).toLocaleDateString('fr-FR')
                        : '—'}
                    </td>
                    <td style={{ ...tdStyle, textAlign: 'right' }}>
                      {NEXT_STATUS[delivery.status]?.length > 0 && (
                        <select
                          value=""
                          onClick={(e) => e.stopPropagation()}
                          onChange={(e) => {
                            e.stopPropagation()
                            if (e.target.value) handleStatusChange(delivery, e.target.value)
                          }}
                          style={{ ...selectStyle, padding: '4px 8px', fontSize: '12px' }}
                        >
                          <option value="">Update status...</option>
                          {NEXT_STATUS[delivery.status].map((s) => (
                            <option key={s} value={s}>{STATUS_LABELS[s]}</option>
                          ))}
                        </select>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          <div style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '12px 16px',
            borderTop: '1px solid hsl(var(--border))',
          }}>
            <span style={{ fontSize: '13px', color: 'hsl(var(--muted-foreground))' }}>
              Page {page + 1} of {Math.max(totalPages, 1)}
            </span>
            <div style={{ display: 'flex', gap: '8px' }}>
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                style={paginationButtonStyle(page === 0)}
              >
                Previous
              </button>
              <button
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
                style={paginationButtonStyle(page >= totalPages - 1)}
              >
                Next
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Detail drawer */}
      {selected && (
        <DeliveryDetail delivery={selected} onClose={() => setSelected(null)} />
      )}
    </div>
  )
}

// ─────────────────────────────────────────────────────────────
// Detail panel (overlay)
// ─────────────────────────────────────────────────────────────
function DeliveryDetail({ delivery, onClose }: { delivery: Delivery; onClose: () => void }) {
  return (
    <div
      onClick={onClose}
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(0,0,0,0.4)',
        display: 'flex',
        justifyContent: 'flex-end',
        zIndex: 50,
      }}
    >
      <div
        onClick={(e) => e.stopPropagation()}
        style={{
          width: '100%',
          maxWidth: '420px',
          height: '100%',
          backgroundColor: 'hsl(var(--card))',
          padding: '24px',
          overflowY: 'auto',
          boxShadow: '-4px 0 16px rgba(0,0,0,0.1)',
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '20px' }}>
          <div>
            <h2 style={{ fontSize: '18px', fontWeight: 700, color: 'hsl(var(--foreground))', fontFamily: 'monospace' }}>
              {delivery.trackingCode}
            </h2>
            <span style={{
              ...badgeStyle,
              marginTop: '6px',
              display: 'inline-block',
              backgroundColor: STATUS_COLORS[delivery.status] + '20',
              color: STATUS_COLORS[delivery.status],
            }}>
              {STATUS_LABELS[delivery.status]}
            </span>
          </div>
          <button onClick={onClose} style={{
            border: 'none',
            background: 'none',
            fontSize: '20px',
            cursor: 'pointer',
            color: 'hsl(var(--muted-foreground))',
          }}>
            ×
          </button>
        </div>

        <DetailSection title="Recipient">
          <DetailRow label="Name" value={delivery.recipientName} />
          <DetailRow label="Phone" value={delivery.recipientPhone} />
          <DetailRow label="Email" value={delivery.recipientEmail} />
        </DetailSection>

        <DetailSection title="Address">
          <DetailRow label="Street" value={delivery.address} />
          <DetailRow label="City" value={`${delivery.city} ${delivery.postalCode}`} />
          {delivery.latitude && delivery.longitude && (
            <DetailRow label="Coordinates" value={`${delivery.latitude.toFixed(5)}, ${delivery.longitude.toFixed(5)}`} />
          )}
        </DetailSection>

        <DetailSection title="Package">
          <DetailRow label="Weight" value={`${delivery.weightKg} kg`} />
          <DetailRow label="Volume" value={`${delivery.volumeM3} m³`} />
          <DetailRow label="Priority" value={delivery.priority} />
        </DetailSection>

        <DetailSection title="Schedule">
          <DetailRow
            label="Scheduled date"
            value={delivery.scheduledDate ? new Date(delivery.scheduledDate).toLocaleDateString('fr-FR') : '—'}
          />
          <DetailRow
            label="Time window"
            value={delivery.timeWindowStart && delivery.timeWindowEnd
              ? `${delivery.timeWindowStart} - ${delivery.timeWindowEnd}`
              : '—'}
          />
          {delivery.deliveredAt && (
            <DetailRow
              label="Delivered at"
              value={new Date(delivery.deliveredAt).toLocaleString('fr-FR')}
            />
          )}
          <DetailRow label="Attempts" value={String(delivery.attemptCount ?? 1)} />
        </DetailSection>
      </div>
    </div>
  )
}

function DetailSection({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <div style={{ marginBottom: '20px' }}>
      <p style={{
        fontSize: '12px',
        fontWeight: 600,
        textTransform: 'uppercase',
        letterSpacing: '0.05em',
        color: 'hsl(var(--muted-foreground))',
        marginBottom: '8px',
      }}>
        {title}
      </p>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
        {children}
      </div>
    </div>
  )
}

function DetailRow({ label, value }: { label: string; value?: string | null }) {
  if (!value) return null
  return (
    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px' }}>
      <span style={{ color: 'hsl(var(--muted-foreground))' }}>{label}</span>
      <span style={{ color: 'hsl(var(--foreground))', fontWeight: 500, textAlign: 'right' }}>{value}</span>
    </div>
  )
}

// ─────────────────────────────────────────────────────────────
// Styles partagés
// ─────────────────────────────────────────────────────────────
const selectStyle: React.CSSProperties = {
  padding: '8px 12px',
  borderRadius: '8px',
  border: '1px solid hsl(var(--border))',
  backgroundColor: 'hsl(var(--card))',
  color: 'hsl(var(--foreground))',
  fontSize: '14px',
}

const resetButtonStyle: React.CSSProperties = {
  padding: '8px 12px',
  borderRadius: '8px',
  border: '1px solid hsl(var(--border))',
  backgroundColor: 'transparent',
  color: 'hsl(var(--muted-foreground))',
  fontSize: '13px',
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

function paginationButtonStyle(disabled: boolean): React.CSSProperties {
  return {
    padding: '6px 12px',
    borderRadius: '6px',
    border: '1px solid hsl(var(--border))',
    backgroundColor: 'transparent',
    color: disabled ? 'hsl(var(--muted-foreground))' : 'hsl(var(--foreground))',
    fontSize: '13px',
    cursor: disabled ? 'not-allowed' : 'pointer',
    opacity: disabled ? 0.5 : 1,
  }
}
