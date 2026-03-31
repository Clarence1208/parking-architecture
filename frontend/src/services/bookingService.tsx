// src/services/bookingService.ts
import { apiClient } from '../shared/api/apiClient';
import type { ParkingReservationRequest } from '../types/api-model';

export const bookingService = {
  /**
   * Envoie la réservation via le client partagé
   */
  async createReservation(data: ParkingReservationRequest): Promise<void> {
    return apiClient.post('/booking/reserve', data);
  },

  async getParkingStatus(): Promise<any[]> {
    return apiClient.get('/booking/spots'); 
  }
};