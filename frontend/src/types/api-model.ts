//BookingRequestDTO
export interface ParkingReservationRequest {
  spotId: string;
  firstName: string;
  lastName: string;
  role: 'EMPLOYEE' | 'MANAGER'; 
  bookingDate: string;
}

//BookingResponseDTO
export interface ParkingSpotResponse {
  id: string; 
  isOccupied: boolean;
  reservedBy?: string;
  date?: string;
}