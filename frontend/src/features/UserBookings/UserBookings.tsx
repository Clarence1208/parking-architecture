import { useEffect, useState } from 'react';
import { bookingService } from '../../services/booking/bookingService.ts';
import './UserBookings.css';
import type { UserBookingResponse } from "../../services/booking/interfaces/bookingInterface.ts";

export default function UserBookings() {
  const [bookings, setBookings] = useState<UserBookingResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchBookings = async () => {
      try {
        setLoading(true);
        const data = await bookingService.getMyBookings();

        // Tri : On met les réservations futures/non-checkées en premier
        const sorted = data.sort((a, b) =>
            new Date(b.startDate).getTime() - new Date(a.startDate).getTime()
        );

        setBookings(sorted);
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

  // Fonction utilitaire pour déterminer si une réservation est passée
  const isPastBooking = (endDate: string) => {
    const end = new Date(endDate);
    end.setHours(23, 59, 59, 999); // On considère la journée entière
    return end < new Date();
  };

  if (loading) return <div className="p-8">Chargement de vos réservations...</div>;
  if (error) return <div className="p-8 text-red-500">{error}</div>;

  return (
      <div className="user-bookings">
        <header className="user-bookings__header">
          <h1>Mes réservations</h1>
          <p className="subtitle">Retrouvez l'historique et gérez vos accès au parking</p>
        </header>

        <div className="user-bookings__content">
          {bookings.length === 0 ? (
              <div className="empty-state">
                <p>Vous n'avez aucune réservation enregistrée.</p>
              </div>
          ) : (
              <table className="bookings-table">
                <thead>
                <tr>
                  <th>Place</th>
                  <th>Date de début</th>
                  <th>Date de fin</th>
                  <th>État / Actions</th>
                </tr>
                </thead>
                <tbody>
                {bookings.map((booking) => {
                  const past = isPastBooking(booking.endDate);

                  return (
                      <tr key={booking.id} className={past ? 'row-past' : ''}>
                        <td className="font-bold">{booking.spotId}</td>
                        <td>{new Date(booking.startDate).toLocaleDateString()}</td>
                        <td>{new Date(booking.endDate).toLocaleDateString()}</td>
                        <td>
                          {booking.checkedIn ? (
                              <span className="badge badge--success">
                           <span className="icon">✓</span> Vérifié
                        </span>
                          ) : past ? (
                              <span className="badge badge--past">Terminé</span>
                          ) : (
                              <button
                                  className="btn-cancel"
                                  onClick={() => { void handleCancel(booking.id)}}
                              >
                                Annuler
                              </button>
                          )}
                        </td>
                      </tr>
                  );
                })}
                </tbody>
              </table>
          )}
        </div>
      </div>
  );
}