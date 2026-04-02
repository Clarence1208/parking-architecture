import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend } from 'recharts';
import './Charts.css';

// 1. On définit l'interface pour les données que le dashboard va envoyer
interface ElectricDonutChartProps {
    data: {
        name: string;
        value: number;
        color: string;
    }[];
}

// 2. On récupère { data } dans les arguments de la fonction
export default function ElectricDonutChart({ data }: ElectricDonutChartProps) {
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
                            data={data} // 3. Utilise maintenant les données dynamiques
                            cx="50%"
                            cy="50%"
                            innerRadius={60}
                            outerRadius={80}
                            paddingAngle={5}
                            dataKey="value"
                        >
                            {/* On boucle sur 'data' pour générer les Cellules colorées */}
                            {data.map((entry, index) => (
                                <Cell key={`cell-${index}`} fill={entry.color} stroke="none" />
                            ))}
                        </Pie>
                        <Tooltip
                            contentStyle={{
                                backgroundColor: '#1e293b',
                                border: '1px solid #334155',
                                borderRadius: '8px',
                                color: '#fff'
                            }}
                            itemStyle={{ color: '#fff' }}
                        />
                        <Legend
                            verticalAlign="bottom"
                            height={36}
                            iconType="circle"
                            formatter={(value) => (
                                <span style={{ color: '#94a3b8', fontSize: '12px' }}>{value}</span>
                            )}
                        />
                    </PieChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
}