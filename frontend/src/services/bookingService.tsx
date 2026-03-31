import type { ParkingReservationRequest, ParkingSpot } from '../types/api-model';

export const bookingService = {
  // Récupérer l'état initial du parking
  async getParkingStatus(): Promise<ParkingSpot[]> {
    // Simulation d'appel API GET /api/booking/spots
    return [
      { id: 'A05', isOccupied: true, reservedBy: 'Jean Dupont' },
      { id: 'B02', isOccupied: true, reservedBy: 'Alice Martin' },
    ];
  },

  // Envoyer la réservation au backend
  async createReservation(data: ParkingReservationRequest): Promise<void> {
    console.log("Appel API POST /api/booking/reserve avec :", data);
    return new Promise((resolve) => setTimeout(resolve, 1000));
  }
};