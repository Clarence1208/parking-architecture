import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend } from 'recharts';
import './Charts.css';

const data = [
    { name: 'Élec. Occupées', value: 18, color: '#f59e0b' }, // Orange
    { name: 'Élec. Libres', value: 7, color: '#10b981' },    // Vert
    { name: 'Classiques', value: 25, color: '#6366f1' },     // Indigo
];

export default function ElectricDonutChart() {
    return (
        <div className="chart-card">
            <div className="chart-card__header">
                <h3>Focus Électrique</h3>
                <p>Répartition et disponibilité des bornes</p>
            </div>

            <div className="chart-card__content">
                <ResponsiveContainer width="100%" height={300}>
                    <PieChart>
                        <Pie
                            data={data}
                            cx="50%"
                            cy="50%"
                            innerRadius={60}   // C'est ce qui crée le trou du Donut
                            outerRadius={80}
                            paddingAngle={5}
                            dataKey="value"
                        >
                            {data.map((entry, index) => (
                                <Cell key={`cell-${index}`} fill={entry.color} stroke="none" />
                            ))}
                        </Pie>
                        <Tooltip
                            contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155', borderRadius: '8px', color: '#fff' }}
                            itemStyle={{ color: '#fff' }}
                        />
                        <Legend
                            verticalAlign="bottom"
                            height={36}
                            iconType="circle"
                            formatter={(value) => <span style={{ color: '#94a3b8', fontSize: '12px' }}>{value}</span>}
                        />
                    </PieChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
}