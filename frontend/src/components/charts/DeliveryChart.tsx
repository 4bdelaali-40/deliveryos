import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
} from 'recharts'

interface DeliveryStatusChartProps {
  delivered: number
  failed: number
  inTransit: number
  created: number
}

const COLORS = {
  delivered: '#10b981',
  failed: '#ef4444',
  inTransit: '#3b82f6',
  created: '#6b7280',
}

export function DeliveryStatusChart({
  delivered,
  failed,
  inTransit,
  created,
}: DeliveryStatusChartProps) {
  const data = [
    { name: 'Delivered', value: delivered, color: COLORS.delivered },
    { name: 'In Transit', value: inTransit, color: COLORS.inTransit },
    { name: 'Created', value: created, color: COLORS.created },
    { name: 'Failed', value: failed, color: COLORS.failed },
  ]

  return (
    <div
      style={{
        padding: '20px',
        borderRadius: '12px',
        border: '1px solid hsl(var(--border))',
        backgroundColor: 'hsl(var(--card))',
      }}
    >
      <p
        style={{
          fontSize: '14px',
          fontWeight: 600,
          color: 'hsl(var(--foreground))',
          marginBottom: '16px',
        }}
      >
        Delivery Status Distribution
      </p>
      <ResponsiveContainer width="100%" height={250}>
        <PieChart>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            innerRadius={60}
            outerRadius={100}
            paddingAngle={3}
            dataKey="value"
          >
            {data.map((entry) => (
              <Cell key={entry.name} fill={entry.color} />
            ))}
          </Pie>
          <Tooltip />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    </div>
  )
}