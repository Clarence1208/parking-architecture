import { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useAuth } from '../../store/AuthContext';
import { authService } from '../../services/auth/authService';
import { bookingService } from '../../services/booking/bookingService';
import type { CheckInResponse } from '../../services/booking/interfaces/bookingInterface';
import './CheckInPage.css';

type Status = 'idle' | 'loading' | 'success' | 'error';

export default function CheckInPage() {
  const [searchParams] = useSearchParams();
  const spotId = searchParams.get('spot');

  const { isAuthenticated, login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [authError, setAuthError] = useState('');

  const [status, setStatus] = useState<Status>('idle');
  const [result, setResult] = useState<CheckInResponse | null>(null);
  const [errorMsg, setErrorMsg] = useState('');

  const performCheckIn = useCallback(async () => {
    if (!spotId) return;
    setStatus('loading');
    try {
      const res = await bookingService.checkIn(spotId);
      setResult(res);
      setStatus('success');
    } catch (err: any) {
      setErrorMsg(err.message || 'Erreur lors du check-in.');
      setStatus('error');
    }
  }, [spotId]);

  useEffect(() => {
    if (isAuthenticated && spotId && status === 'idle') {
      performCheckIn();
    }
  }, [isAuthenticated, spotId, status, performCheckIn]);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setAuthError('');
    try {
      const res = await authService.login({ email, password });
      login(res.token, { email: res.email, role: res.role });
    } catch (err: any) {
      setAuthError(err.message || 'Identifiants invalides.');
    }
  };

  if (!spotId) {
    return (
      <div className="checkin-container">
        <div className="checkin-card checkin-card--error">
          <div className="checkin-icon">&#x26A0;</div>
          <h1>Lien invalide</h1>
          <p>Aucune place de parking n'est spécifiée dans ce QR code.</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <div className="checkin-container">
        <div className="checkin-card">
          <div className="checkin-spot-badge">{spotId}</div>
          <h1>Check-in parking</h1>
          <p className="checkin-subtitle">Connectez-vous pour confirmer votre présence sur la place <strong>{spotId}</strong></p>

          {authError && <div className="checkin-alert checkin-alert--error">{authError}</div>}

          <form onSubmit={handleLogin} className="checkin-form">
            <div className="checkin-field">
              <label htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                required
                placeholder="vous@exemple.com"
              />
            </div>
            <div className="checkin-field">
              <label htmlFor="password">Mot de passe</label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                required
                placeholder="••••••••"
              />
            </div>
            <button type="submit" className="checkin-btn">Se connecter & check-in</button>
          </form>
        </div>
      </div>
    );
  }

  if (status === 'loading') {
    return (
      <div className="checkin-container">
        <div className="checkin-card">
          <div className="checkin-spinner" />
          <h1>Check-in en cours...</h1>
          <p>Vérification de votre réservation pour la place <strong>{spotId}</strong></p>
        </div>
      </div>
    );
  }

  if (status === 'success' && result) {
    return (
      <div className="checkin-container">
        <div className="checkin-card checkin-card--success">
          <div className="checkin-icon">&#x2705;</div>
          <h1>Check-in confirmé</h1>
          <div className="checkin-details">
            <div className="checkin-detail-row">
              <span>Place</span>
              <strong>{result.spotId}</strong>
            </div>
            <div className="checkin-detail-row">
              <span>Période</span>
              <strong>{result.startDate} → {result.endDate}</strong>
            </div>
          </div>
          <p className="checkin-success-msg">Vous êtes bien enregistré sur votre place. Bonne journée !</p>
        </div>
      </div>
    );
  }

  if (status === 'error') {
    return (
      <div className="checkin-container">
        <div className="checkin-card checkin-card--error">
          <div className="checkin-icon">&#x274C;</div>
          <h1>Check-in échoué</h1>
          <p>{errorMsg}</p>
          <button className="checkin-btn checkin-btn--retry" onClick={() => { setStatus('idle'); }}>
            Réessayer
          </button>
        </div>
      </div>
    );
  }

  return null;
}
