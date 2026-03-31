import { useState } from 'react';
import './ReservationModal.css';

interface Props {
  spotId: string;
  bookingDate: string; 
  onClose: () => void;
  onConfirm: (firstName: string, lastName: string, role: string) => void; 
}

export const ReservationModal = ({ spotId, bookingDate, onClose, onConfirm }: Props) => {
  const [form, setForm] = useState({ 
    firstName: '', 
    lastName: '', 
    role: 'EMPLOYEE' 
  });

  // La validation est simplifiée : juste vérifier que les noms sont remplis
  const isFormComplete = form.firstName.trim() !== '' && form.lastName.trim() !== '';

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (isFormComplete) {
      // On n'envoie plus duration ici
      onConfirm(form.firstName, form.lastName, form.role);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h3>Réserver la place {spotId}</h3>
        
        {/* On affiche la date pour laquelle on réserve */}
        <p className="modal-date-info">
          Réservation pour le : <strong>{bookingDate}</strong>
        </p>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Prénom</label>
            <input 
              type="text"
              placeholder="Ex: Alan" 
              value={form.firstName}
              onChange={e => setForm({...form, firstName: e.target.value})} 
              required 
            />
          </div>

          <div className="form-group">
            <label>Nom</label>
            <input 
              type="text"
              placeholder="Ex: Diot" 
              value={form.lastName}
              onChange={e => setForm({...form, lastName: e.target.value})} 
              required 
            />
          </div>

          <div className="form-group">
            <label>Votre profil</label>
            <select 
              value={form.role} 
              onChange={e => setForm({...form, role: e.target.value})}
            >
              <option value="EMPLOYEE">Employé</option>
              <option value="SECRETARY">Secrétaire</option>
              <option value="MANAGER">Manager</option>
            </select>
            <small className="role-hint">
              {form.role === 'MANAGER' ? 'Quota : 30 jours max' : 'Quota : 5 jours max'}
            </small>
          </div>

          <div className="modal-buttons">
            <button type="button" className="cancel-btn" onClick={onClose}>Annuler</button>
            <button 
              type="submit" 
              className="confirm-btn" 
              disabled={!isFormComplete}
            >
              Confirmer la place
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};