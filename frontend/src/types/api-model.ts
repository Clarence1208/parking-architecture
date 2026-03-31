export interface ParkingReservationRequest {
  spotId: string;
  firstName: string;
  lastName: string;
  durationDays: number;
  role: string;
}

export interface ParkingSpot {
  id: string;
  isOccupied: boolean;
  reservedBy?: string; // Nom complet pour l'affichage
}