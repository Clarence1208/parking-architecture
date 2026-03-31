export interface ParkingReservationRequest {
  spotId: string;
  firstName: string;
  lastName: string;
  durationDays: number;
}

export interface ParkingSpot {
  id: string;
  isOccupied: boolean;
  reservedBy?: string; // Nom complet pour l'affichage
}