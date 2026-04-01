import './ReservationModal.css';

interface Props {
  spotId: string;
  bookingDate: string; 
  onClose: () => void;
  onConfirm: () => void; 
}

export const ReservationModal = ({ spotId, bookingDate, onClose, onConfirm }: Props) => {
  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h3>Réserver la place {spotId}</h3>
        
        {/* On affiche la date pour laquelle on réserve */}
        <p className="modal-date-info">
          Réservation pour le : <strong>{bookingDate}</strong>
        </p>

        <p className="modal-instruction" style={{ marginBottom: '20px', color: '#a0a0a0' }}>
          Êtes-vous sûr de vouloir bloquer cette place pour votre compte sur cette journée entière ?
        </p>

        <div className="modal-buttons" style={{ display: 'flex', gap: '10px' }}>
          <button type="button" className="cancel-btn" onClick={onClose} style={{ flex: 1, padding: '12px', background: '#334155', border: 'none', borderRadius: '4px', cursor: 'pointer', color: '#fff', fontWeight: 'bold' }}>
            Annuler
          </button>
          <button 
            type="button" 
            className="confirm-btn" 
            onClick={onConfirm}
            style={{ flex: 1, padding: '12px', background: '#eab308', border: 'none', borderRadius: '4px', cursor: 'pointer', color: '#000', fontWeight: 'bold' }}
          >
            Confirmer la réservation
          </button>
        </div>
      </div>
    </div>
  );
};