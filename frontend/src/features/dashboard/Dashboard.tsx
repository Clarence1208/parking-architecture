import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../store/AuthContext.tsx';
import './Dashboard.css';
import StatsCard from './components/StatsCard';
import OccupationAreaChart from "./components/charts/OccupationAreaChart.tsx";
import ElectricDonutChart from "./components/charts/ElectricDonutChart.tsx";
import { dashboardService } from '../../services/dashboard/dashboardService';
import type { DashboardDataResponse } from '../../services/dashboard/interfaces/dashboardInterface';

export default function Dashboard() {
    const { user } = useAuth();
    const navigate = useNavigate();
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
        // Garde de sécurité : redirection si pas manager
        if (user && user.role !== 'MANAGER') {
            navigate('/');
            return;
        }

        loadDashboardData();
    }, [user, navigate]);

    // On ne rend rien si l'utilisateur n'a pas le bon rôle pendant la redirection
    if (user?.role !== 'MANAGER') {
        return null;
    }

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
                    value={`${Math.round((data.stats.occupiedSpots / data.stats.totalSpots) * 100)}%`}
                    color="#6366f1"
                    trend={{
                        value: data.stats.occupancyTrend,
                        isPositive: data.stats.isTrendPositive
                    }}
                    icon={
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M21.21 15.89A10 10 0 1 1 8 2.83M22 12A10 10 0 0 0 12 2v10z"/>
                        </svg>
                    }
                />
                <StatsCard
                    title="Places Libres"
                    value={data.stats.totalSpots - data.stats.occupiedSpots}
                    color="#0ea5e9"
                    icon={
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                            <polyline points="9 22 9 12 15 12 15 22"/>
                        </svg>
                    }
                />
                <StatsCard
                    title="Bornes Élec. Libres"
                    value={data.stats.availableElectricSpots}
                    color="#10b981"
                    icon={
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M18.36 6.64a9 9 0 1 1-12.73 0"/>
                            <line x1="12" y1="2" x2="12" y2="12"/>
                        </svg>
                    }
                />
                <StatsCard
                    title="Total Places"
                    value={data.stats.totalSpots}
                    color="#f59e0b"
                    icon={
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <rect x="2" y="3" width="20" height="14" rx="2" ry="2"/>
                            <line x1="8" y1="21" x2="16" y2="21"/>
                            <line x1="12" y1="17" x2="12" y2="21"/>
                        </svg>
                    }
                />
            </section>

            <section className="charts-grid">
                <div className="main-chart-area">
                    <OccupationAreaChart data={dashboardService.formatHistoryData(data.history)} />
                </div>
                <div className="side-chart-area">
                    <ElectricDonutChart data={dashboardService.formatDonutData(data.distribution)} />
                </div>
            </section>
        </div>
    );
}