import { useState } from 'react';
import './ReservationModal.css';

interface Props {
  spotId: string;
  onClose: () => void;
  onConfirm: (firstName: string, lastName: string, duration: number, role: string) => void;
}

export const ReservationModal = ({ spotId, onClose, onConfirm }: Props) => {
  const [form, setForm] = useState({ 
    firstName: '', 
    lastName: '', 
    duration: 1, 
    role: 'EMPLOYEE' // Rôle par défaut
  });

  // Détermination de la limite selon le rôle sélectionné
  const maxDays = form.role === 'MANAGER' ? 30 : 5;
  
  // Validation du formulaire
  const isDurationValid = form.duration > 0 && form.duration <= maxDays;
  const isFormComplete = form.firstName.trim() !== '' && form.lastName.trim() !== '';
  const canSubmit = isDurationValid && isFormComplete;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (canSubmit) {
      onConfirm(form.firstName, form.lastName, form.duration, form.role);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h3>Réserver la place {spotId}</h3>
        
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
              onChange={e => setForm({...form, role: e.target.value, duration: 1})} // On reset la durée si on change de rôle
            >
              <option value="EMPLOYEE">Employé (max 5j)</option>
              <option value="SECRETARY">Secrétaire (max 5j)</option>
              <option value="MANAGER">Manager (max 30j)</option>
            </select>
          </div>

          <div className="form-group">
            <label>Durée de la réservation (jours)</label>
            <input 
              type="number" 
              min="1" 
              max={maxDays}
              value={form.duration} 
              className={!isDurationValid ? 'input-error' : ''}
              onChange={e => setForm({...form, duration: parseInt(e.target.value) || 0})} 
            />
            {!isDurationValid && (
              <span className="error-text">Limite pour ce profil : {maxDays} jours.</span>
            )}
          </div>

          <div className="modal-buttons">
            <button type="button" className="cancel-btn" onClick={onClose}>Annuler</button>
            <button 
              type="submit" 
              className="confirm-btn" 
              disabled={!canSubmit}
            >
              Confirmer la place
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};