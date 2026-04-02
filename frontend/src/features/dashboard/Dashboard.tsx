import { useEffect, useState } from 'react';
import './Dashboard.css';
import StatsCard from './components/StatsCard';
import OccupationAreaChart from "./components/charts/OccupationAreaChart.tsx";
import ElectricDonutChart from "./components/charts/ElectricDonutChart.tsx";
import { dashboardService } from '../../services/dashboard/dashboardService';
import type { DashboardDataResponse } from '../../services/dashboard/interfaces/dashboardInterface';

export default function Dashboard() {
    const [data, setData] = useState<DashboardDataResponse | null>(null);
    const [loading, setLoading] = useState(true);

    const loadDashboardData = async () => {
        setLoading(true);
        try {
            const response = await dashboardService.getDashboardData();
            setData(response);
        } catch (error) {
            console.error("Erreur lors de la récupération des données :", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadDashboardData();
    }, []);

    if (loading || !data) {
        return <div className="dashboard-loader">Chargement des données...</div>;
    }

    return (
        <div className="dashboard-container">
            <header className="dashboard-header">
                <div>
                    <h1>Tableau de bord</h1>
                    <p className="subtitle">Aperçu de l'utilisation du parking en temps réel</p>
                </div>
                <div className="dashboard-actions">
                    <button className="btn-refresh" onClick={loadDashboardData}>
                        Actualiser
                    </button>
                </div>
            </header>

            <section className="stats-grid">
                <StatsCard
                    title="Occupation Globale"
                    // On calcule le % si le back ne le donne pas direct, ou on affiche la valeur brute
                    value={`${Math.round((data.stats.occupiedSpots / data.stats.totalSpots) * 100)}%`}
                    color="#6366f1"
                    trend={{ value: data.stats.occupancyTrend, isPositive: data.stats.isTrendPositive }}
                    icon={<svg>...</svg>}
                />
                <StatsCard
                    title="Places Libres"
                    value={data.stats.totalSpots - data.stats.occupiedSpots}
                    color="#0ea5e9"
                    icon={<svg>...</svg>}
                />
                <StatsCard
                    title="Bornes Élec. Libres"
                    value={data.stats.availableElectricSpots}
                    color="#10b981"
                    icon={<svg>...</svg>}
                />
                <StatsCard
                    title="Total Places"
                    value={data.stats.totalSpots}
                    color="#f59e0b"
                    icon={<svg>...</svg>}
                />
            </section>

            <section className="charts-grid">
                <div className="main-chart-area">
                    {/* On utilise l'adapter pour transformer l'historique */}
                    <OccupationAreaChart data={dashboardService.formatHistoryData(data.history)} />
                </div>
                <div className="side-chart-area">
                    {/* On utilise l'adapter pour le donut */}
                    <ElectricDonutChart data={dashboardService.formatDonutData(data.distribution)} />
                </div>
            </section>
        </div>
    );
}