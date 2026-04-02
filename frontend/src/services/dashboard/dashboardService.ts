import { apiClient } from '../api/apiClient.ts';
import type {
    DashboardDataResponse,
    ElectricDistributionResponse,
    OccupationHistoryResponse
} from './interfaces/dashboardInterface.ts';

export const dashboardService = {
    /**
     * Appels API : Récupère l'objet global contenant toutes les sections
     */
    async getDashboardData(): Promise<DashboardDataResponse> {
        return apiClient.get<DashboardDataResponse>('/dashboard/summary');
    },

    /**
     * ADAPTER : Transforme l'objet distribution (Java) en tableau (Recharts)
     */
    formatDonutData(dist: ElectricDistributionResponse) {
        return [
            { name: 'Élec. Occupées', value: dist.occupiedElectric, color: '#f59e0b' },
            { name: 'Élec. Libres', value: dist.availableElectric, color: '#10b981' },
            { name: 'Classiques', value: dist.classicSpots, color: '#6366f1' },
        ];
    },

    /**
     * ADAPTER : Mappe les champs Java vers les clés utilisées dans l'AreaChart
     */
    formatHistoryData(history: OccupationHistoryResponse[]) {
        return history.map(item => ({
            day: item.day,
            total: item.totalOccupied,
            electric: item.electricOccupied
        }));
    }
};