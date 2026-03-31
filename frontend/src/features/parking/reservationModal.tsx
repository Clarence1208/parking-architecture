import { useState } from 'react';
import './ReservationModal.css';

interface Props {
  spotId: string;
  onClose: () => void;
  onConfirm: (firstName: string, lastName: string, duration: number) => void;
}

export const ReservationModal = ({ spotId, onClose, onConfirm }: Props) => {
  const [form, setForm] = useState({ firstName: '', lastName: '', duration: 1 });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onConfirm(form.firstName, form.lastName, form.duration);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      {/* On ajoute e.stopPropagation() pour éviter que cliquer dans la fenêtre ferme la modale */}
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h3>Réserver la place {spotId}</h3>
        
        <form onSubmit={handleSubmit}>
          <input 
            type="text"
            placeholder="Prénom" 
            value={form.firstName}
            onChange={e => setForm({...form, firstName: e.target.value})} 
            required 
          />
          <input 
            type="text"
            placeholder="Nom" 
            value={form.lastName}
            onChange={e => setForm({...form, lastName: e.target.value})} 
            required 
          />
          <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
            <label style={{ color: '#94a3b8', fontSize: '0.8rem' }}>Durée de la réservation (jours)</label>
            <input 
              type="number" 
              min="1" 
              max="7"
              value={form.duration} 
              onChange={e => setForm({...form, duration: parseInt(e.target.value)})} 
            />
          </div>

          <div className="modal-buttons">
            <button type="button" className="cancel-btn" onClick={onClose}>Annuler</button>
            <button type="submit" className="confirm-btn">Confirmer la place</button>
          </div>
        </form>
      </div>
    </div>
  );
};