//BookingRequestDTO
export interface ParkingReservationRequest {
  spotId: string;
  startDate: string;
  endDate: string;
}

export interface ParkingSpotResponse {
  spotId: string; 
  occupied: boolean;
  reservedBy?: string;
  date?: string;
}