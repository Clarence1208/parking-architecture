import {
    BarChart, Bar, XAxis, YAxis, CartesianGrid,
    Tooltip, ResponsiveContainer, Legend, Cell
} from 'recharts';
import './Charts.css';

const data = [
    { name: 'Zone A', occupied: 18, total: 20, electric: 5 },
    { name: 'Zone B', occupied: 5,  total: 15, electric: 2 },
    { name: 'Zone C', occupied: 9,  total: 10, electric: 10 },
    { name: 'Zone D', occupied: 12, total: 25, electric: 0 },
];

// On calcule la place restante pour faire une barre de "fond"
const chartData = data.map(d => ({
    ...d,
    remaining: d.total - d.occupied
}));

export default function ZoneCapacityChart() {
    return (
        <div className="chart-card">
            <div className="chart-card__header">
                <h3>Occupation par Zones</h3>
                <p>Comparaison de la saturation des secteurs</p>
            </div>

            <div className="chart-card__content">
                <ResponsiveContainer width="100%" height={300}>
                    <BarChart
                        data={chartData}
                        layout="vertical" // C'est ce qui rend les barres horizontales
                        margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                        barSize={20}
                    >
                        <CartesianGrid strokeDasharray="3 3" stroke="#334155" horizontal={false} />
                        <XAxis type="number" stroke="#94a3b8" fontSize={12} hide />
                        <YAxis
                            dataKey="name"
                            type="category"
                            stroke="#f8fafc"
                            fontSize={12}
                            tickLine={false}
                            axisLine={false}
                            width={70}
                        />
                        <Tooltip
                            cursor={{fill: 'transparent'}}
                            contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155', borderRadius: '8px' }}
                        />
                        <Legend verticalAlign="top" align="right" iconType="circle" />

                        {/* Barre de fond (Capacité totale) */}
                        <Bar dataKey="total" stackId="a" fill="#334155" radius={[0, 4, 4, 0]} name="Capacité Totale" />

                        {/* Barre d'occupation (Par dessus) */}
                        <Bar dataKey="occupied" stackId="a" fill="#6366f1" radius={[0, 4, 4, 0]} name="Occupé">
                            {chartData.map((entry, index) => (
                                // Si l'occupation est > 80%, on met la barre en orange
                                <Cell key={`cell-${index}`} fill={entry.occupied / entry.total > 0.8 ? '#f59e0b' : '#6366f1'} />
                            ))}
                        </Bar>
                    </BarChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
}