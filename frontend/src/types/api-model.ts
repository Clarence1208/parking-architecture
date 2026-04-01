//BookingRequestDTO
export interface ParkingReservationRequest {
  spotId: string;
  bookingDate: string;
}

export interface ParkingSpotResponse {
  spotId: string; 
  occupied: boolean;
  reservedBy?: string;
  date?: string;
}