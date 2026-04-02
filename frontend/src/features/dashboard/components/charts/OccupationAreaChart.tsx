import {
    AreaChart, Area, XAxis, YAxis, CartesianGrid,
    Tooltip, ResponsiveContainer
} from 'recharts';
import './Charts.css';

const data = [
    { day: 'Lun', total: 45, electric: 12 },
    { day: 'Mar', total: 52, electric: 15 },
    { day: 'Mer', total: 48, electric: 10 },
    { day: 'Jeu', total: 61, electric: 22 },
    { day: 'Ven', total: 55, electric: 18 },
    { day: 'Sam', total: 32, electric: 8 },
    { day: 'Dim', total: 25, electric: 5 },
];

export default function OccupationAreaChart() {
    return (
        <div className="chart-card">
            <div className="chart-card__header">
                <h3>Évolution de l'occupation</h3>
                <p>Utilisation totale vs places électriques (7 derniers jours)</p>
            </div>

            <div className="chart-card__content">
                <ResponsiveContainer width="100%" height={300}>
                    <AreaChart data={data} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                        <defs>
                            {/* Dégradé pour l'occupation totale (Indigo) */}
                            <linearGradient id="colorTotal" x1="0" y1="0" x2="0" y2="1">
                                <stop offset="5%" stopColor="#6366f1" stopOpacity={0.3}/>
                                <stop offset="95%" stopColor="#6366f1" stopOpacity={0}/>
                            </linearGradient>
                            {/* Dégradé pour l'électrique (Vert) */}
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
                            dataKey="total"
                            stroke="#6366f1"
                            strokeWidth={3}
                            fillOpacity={1}
                            fill="url(#colorTotal)"
                        />
                        <Area
                            type="monotone"
                            dataKey="electric"
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