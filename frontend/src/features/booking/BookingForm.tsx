import { useState, useEffect } from 'react';
import { useAuth } from '../../store/AuthContext';
import { bookingService } from '../../services/booking/bookingService.tsx';
import './BookingForm.css';

interface Props {
  spotId: string;
  startDate: string; 
  onClose: () => void;
  onConfirm: (endDate: string) => void; 
}

export const BookingForm = ({ spotId, startDate, onClose, onConfirm }: Props) => {
  const { user } = useAuth();
  const [endDate, setEndDate] = useState<string>(startDate);
  const [remainingDays, setRemainingDays] = useState<number>(1);
  const [maxDays, setMaxDays] = useState<number>(5);

  // Récupérer les jours restants réservables pour l'utilisateur
  useEffect(() => {
    bookingService.getRemainingDays()
      .then((data) => {
        setRemainingDays(data.remainingDays);
        setMaxDays(data.maxDays);
      })
      .catch(err => console.error('Erreur chargement jours restants', err));
  }, []);

  // Calculer la date max autorisée (basée sur les jours restants)
  const computeMaxEndDate = () => {
    const start = new Date(startDate);
    start.setDate(start.getDate() + remainingDays - 1);
    return start.toISOString().split('T')[0];
  };

  // Nombre de jours sélectionnés
  const selectedDays = () => {
    const s = new Date(startDate);
    const e = new Date(endDate);
    return Math.round((e.getTime() - s.getTime()) / (1000 * 60 * 60 * 24)) + 1;
  };

  const formatDate = (dateStr: string) => {
    const d = new Date(dateStr);
    return new Intl.DateTimeFormat('fr-FR', {
      weekday: 'short',
      day: 'numeric',
      month: 'long'
    }).format(d);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h3>Réserver la place {spotId}</h3>
        
        <div className="modal-date-range">
          <div className="date-range-row">
            <div className="date-range-item">
              <label className="date-range-label">Du</label>
              <div className="date-range-value">{formatDate(startDate)}</div>
            </div>
            <div className="date-range-separator">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="m9 18 6-6-6-6"/>
              </svg>
            </div>
            <div className="date-range-item">
              <label className="date-range-label" htmlFor="end-date-input">Au</label>
              <input
                id="end-date-input"
                type="date"
                className="date-range-input"
                value={endDate}
                min={startDate}
                max={computeMaxEndDate()}
                onChange={(e) => setEndDate(e.target.value)}
              />
            </div>
          </div>

          <div className="date-range-summary">
            <span className="days-count">{selectedDays()} jour{selectedDays() > 1 ? 's' : ''}</span>
            <span className="days-limit">{remainingDays} jour{remainingDays > 1 ? 's' : ''} restant{remainingDays > 1 ? 's' : ''} sur {maxDays} ({user?.role})</span>
          </div>
        </div>

        <p className="modal-instruction" style={{ marginBottom: '20px', color: '#a0a0a0' }}>
          Êtes-vous sûr de vouloir bloquer cette place pour votre compte sur cette période ?
        </p>

        <div className="modal-buttons" style={{ display: 'flex', gap: '10px' }}>
          <button type="button" className="cancel-btn" onClick={onClose}>
            Annuler
          </button>
          <button 
            type="button" 
            className="confirm-btn" 
            onClick={() => onConfirm(endDate)}
          >
            Confirmer la réservation
          </button>
        </div>
      </div>
    </div>
  );
};