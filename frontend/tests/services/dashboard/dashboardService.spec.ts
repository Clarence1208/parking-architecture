import { describe, it, expect } from 'vitest';
import {
    ElectricDistributionResponse,
    OccupationHistoryResponse
} from "../../../src/services/dashboard/interfaces/dashboardInterface";
import {dashboardService} from "../../../src/services/dashboard/dashboardService";


describe('DashboardService Logic (Adapters)', () => {

    describe('formatDonutData', () => {
        it('should transform the flat Java DTO into a Recharts compatible array', () => {
            // 1. On prépare une donnée "bidon" qui ressemble à ce que le Back enverrait
            const mockDist: ElectricDistributionResponse = {
                occupiedElectric: 10,
                availableElectric: 5,
                classicSpots: 20
            };

            // 2. On exécute la fonction de transformation
            const result = dashboardService.formatDonutData(mockDist);

            // 3. On vérifie le résultat
            expect(result).toHaveLength(3);
            expect(result[0]).toEqual({ name: 'Élec. Occupées', value: 10, color: '#f59e0b' });
            expect(result[1]).toEqual({ name: 'Élec. Libres', value: 5, color: '#10b981' });
            expect(result[2]).toEqual({ name: 'Classiques', value: 20, color: '#6366f1' });
        });
    });

    describe('formatHistoryData', () => {
        it('should map Java field names (totalOccupied) to Chart field names (total)', () => {
            const mockHistory: OccupationHistoryResponse[] = [
                { day: 'Lun', totalOccupied: 50, electricOccupied: 10 },
                { day: 'Mar', totalOccupied: 60, electricOccupied: 15 }
            ];

            const result = dashboardService.formatHistoryData(mockHistory);

            expect(result).toHaveLength(2);
            // On vérifie que la clé a bien été renommée
            expect(result[0]).toHaveProperty('total', 50);
            expect(result[0]).toHaveProperty('electric', 10);
            expect(result[0]).not.toHaveProperty('totalOccupied');
        });

        it('should return an empty array if history is empty', () => {
            const result = dashboardService.formatHistoryData([]);
            expect(result).toEqual([]);
        });
    });
});