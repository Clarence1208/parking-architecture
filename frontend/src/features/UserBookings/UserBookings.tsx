import { useEffect, useState } from 'react';
import { bookingService } from '../../services/booking/bookingService.ts';
import './UserBookings.css';
import type {UserBookingResponse} from "../../services/booking/interfaces/bookingInterface.ts";

export default function UserBookings() {
  const [bookings, setBookings] = useState<UserBookingResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchBookings = async () => {
      try {
        setLoading(true);
        const data = await bookingService.getMyBookings();
        setBookings(data);
      } catch (err) {
        setError("Impossible de récupérer vos réservations.");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchBookings();
  }, []);

  const handleCancel = async (bookingId: number) => {
    const confirmCancel = window.confirm("Voulez-vous vraiment annuler cette réservation ?");

    if (confirmCancel) {
      try {
        await bookingService.cancelReservation(bookingId);

        setBookings(prev => prev.filter(b => b.id !== bookingId));

        alert("Réservation annulée !");
      } catch (err) {
        console.error("Erreur lors de l'annulation:", err);
        alert("Une erreur est survenue lors de l'annulation.");
      }
    }
  };
  if (loading) return <div className="p-8">Chargement de vos réservations...</div>;
  if (error) return <div className="p-8 text-red-500">{error}</div>;

  return (
    <div className="user-bookings">
      <header className="user-bookings__header">
        <h1>Mes réservations</h1>
      </header>

      <div className="user-bookings__content">
        {bookings.length === 0 ? (
          <div className="empty-state">
            <p>Vous n'avez aucune réservation en cours.</p>
          </div>
        ) : (
          <table className="bookings-table">
            <thead>
              <tr>
                <th>Place</th>
                <th>Date de début</th>
                <th>Date de fin</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {bookings.map((booking) => (
                <tr key={booking.id}>
                  <td className="font-bold">{booking.spotId}</td>
                  <td>{new Date(booking.startDate).toLocaleDateString()}</td>
                  <td>{new Date(booking.endDate).toLocaleDateString()}</td>
                  <td>
                    <button 
                      className="btn-cancel"
                      onClick={() => { void handleCancel(booking.id)}}
                    >
                      Annuler
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}