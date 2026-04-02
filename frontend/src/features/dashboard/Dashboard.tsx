import './Dashboard.css';
import StatsCard from './components/StatsCard';
import OccupationAreaChart from "./components/charts/OccupationAreaChart.tsx";
import ElectricDonutChart from "./components/charts/ElectricDonutChart.tsx";
import ZoneCapacityTable from "./components/charts/ZoneCapacityTable.tsx";

export default function Dashboard() {
    return (
        <div className="dashboard-container">
            <header className="dashboard-header">
                <div>
                    <h1>Tableau de bord</h1>
                    <p className="subtitle">Aperçu de l'utilisation du parking en temps réel</p>
                </div>
                <div className="dashboard-actions">
                    <button className="btn-refresh">Actualiser</button>
                </div>
            </header>

            <section className="stats-grid">
                <StatsCard
                    title="Occupation Globale"
                    value="78%"
                    color="#6366f1"
                    trend={{ value: 12, isPositive: true }}
                    icon={<svg>...</svg>} // Mets une icône ici
                />
                <StatsCard
                    title="Places Libres"
                    value="14"
                    color="#0ea5e9"
                    icon={<svg>...</svg>}
                />
                <StatsCard
                    title="Occupation Électrique"
                    value="92%"
                    color="#10b981"
                    trend={{ value: 5, isPositive: true }}
                    icon={<svg>...</svg>}
                />
                <StatsCard
                    title="Temps Moyen"
                    value="4h 20m"
                    color="#f59e0b"
                    icon={<svg>...</svg>}
                />
            </section>

            {/* 2. Section des graphiques (Main Content) */}
            <section className="charts-grid">
                <div className="main-chart-area">
                    <OccupationAreaChart />
                </div>
                <div className="side-chart-area">
                    <ElectricDonutChart />
                </div>
            </section>

            {/* 3. Section Basse (Tableaux ou détails) */}
            <section className="bottom-grid-area">
                <ZoneCapacityTable />
            </section>
        </div>
    );
}