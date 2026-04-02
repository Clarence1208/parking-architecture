import { apiClient } from '../api/apiClient.ts';
import type { ParkingReservationRequest, ParkingSpotResponse, RemainingDaysResponse, UserBookingResponse} from './interfaces/bookingInterface.ts';


export const bookingService = {
  /**
   * Envoie la réservation au format BookingRequestDTO
   */
  async createReservation(data: ParkingReservationRequest): Promise<void> {
    return apiClient.post('/booking/reserve', data);
  },

  /**
   * Récupère l'état pour une date précise
   * @param date au format "YYYY-MM-DD"
   */
  async getParkingStatus(date?: string): Promise<ParkingSpotResponse[]> {
    // Si date est présent, on l'ajoute en query param : /booking/spots?date=2026-03-31
    const url = date ? `/booking/spots?date=${date}` : '/booking/spots';
    return apiClient.get(url); 
  },

  /**
   * Récupère le nombre de jours restants réservables pour l'utilisateur connecté
   */
  async getRemainingDays(): Promise<RemainingDaysResponse> {
    return apiClient.get('/booking/remaining-days');
  },

    async cancelReservation(bookingId: number): Promise<void> {
        return apiClient.delete(`/booking/${bookingId}`);
    },

    async getMyBookings(): Promise<UserBookingResponse[]> {
        return apiClient.get<UserBookingResponse[]>('/booking/my-bookings');
    },
};