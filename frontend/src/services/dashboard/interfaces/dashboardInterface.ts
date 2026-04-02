/**
 * DTO pour les compteurs rapides (StatsCard)
 */
export interface DashboardStatsResponse {
    totalSpots: number;
    occupiedSpots: number;
    availableElectricSpots: number;
    occupancyTrend: number; // ex: 5.4
    isTrendPositive: boolean;
}

/**
 * DTO pour le graphique Donut (ElectricDonutChart)
 */
export interface ElectricDistributionResponse {
    occupiedElectric: number;
    availableElectric: number;
    classicSpots: number;
}

/**
 * DTO pour le graphique d'évolution (OccupationAreaChart)
 */
export interface OccupationHistoryResponse {
    day: string;          // "Lun", "Mar"...
    totalOccupied: number;
    electricOccupied: number;
}

/**
 * DTO Global pour le Dashboard (Optionnel, si tu fais un seul appel API)
 */
export interface DashboardDataResponse {
    stats: DashboardStatsResponse;
    distribution: ElectricDistributionResponse;
    history: OccupationHistoryResponse[];
}