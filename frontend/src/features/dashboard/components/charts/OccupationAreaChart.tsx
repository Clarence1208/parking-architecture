import {
    AreaChart, Area, XAxis, YAxis, CartesianGrid,
    Tooltip, ResponsiveContainer
} from 'recharts';
import './Charts.css';

// 1. On définit l'interface pour accepter les données du service
interface OccupationAreaChartProps {
    data: {
        day: string;
        total: number;
        electric: number;
    }[];
}

// 2. On ajoute { data } dans les parenthèses pour recevoir les props
export default function OccupationAreaChart({ data }: OccupationAreaChartProps) {
    return (
        <div className="chart-card">
            <div className="chart-card__header">
                <h3>Évolution de l'occupation</h3>
                <p>Utilisation totale vs places électriques (7 derniers jours)</p>
            </div>

            <div className="chart-card__content">
                <ResponsiveContainer width="100%" height={300}>
                    {/* 3. Recharts utilise maintenant le 'data' qui vient des props */}
                    <AreaChart data={data} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                        <defs>
                            <linearGradient id="colorTotal" x1="0" y1="0" x2="0" y2="1">
                                <stop offset="5%" stopColor="#6366f1" stopOpacity={0.3}/>
                                <stop offset="95%" stopColor="#6366f1" stopOpacity={0}/>
                            </linearGradient>
                            <linearGradient id="colorElectric" x1="0" y1="0" x2="0" y2="1">
                                <stop offset="5%" stopColor="#10b981" stopOpacity={0.3}/>
                                <stop offset="95%" stopColor="#10b981" stopOpacity={0}/>
                            </linearGradient>
                        </defs>

                        <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
                        <XAxis dataKey="day" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
                        <YAxis stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
                        <Tooltip
                            contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155', borderRadius: '8px' }}
                            itemStyle={{ fontSize: '12px' }}
                        />

                        <Area
                            type="monotone"
                            dataKey="total" // Doit matcher la clé dans ton adapter
                            stroke="#6366f1"
                            strokeWidth={3}
                            fillOpacity={1}
                            fill="url(#colorTotal)"
                        />
                        <Area
                            type="monotone"
                            dataKey="electric" // Doit matcher la clé dans ton adapter
                            stroke="#10b981"
                            strokeWidth={3}
                            fillOpacity={1}
                            fill="url(#colorElectric)"
                        />
                    </AreaChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
}