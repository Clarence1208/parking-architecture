import { useState, useEffect } from 'react';
import { bookingService } from '../../services/bookingService';
import { ReservationModal } from './reservationModal';
import type { ParkingSpotResponse } from '../../types/api-model';
import './parkingMap.css';

import RedCar from '../../assets/cars/purple-car.png';
import BlueCar from '../../assets/cars/blue-car.png';

const COLS = Array.from({ length: 10 }, (_, i) => i + 1);

// Utilitaire pour obtenir la date du jour au format YYYY-MM-DD
const getTodayString = () => new Date().toISOString().split('T')[0];

export const ParkingMap = () => {
  const [spots, setSpots] = useState<ParkingSpotResponse[]>([]);
  const [selectedDate, setSelectedDate] = useState<string>(getTodayString());
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);

  // Fonction de synchronisation avec le Backend utilisant la date sélectionnée
  const refreshParkingStatus = async () => {
    try {
      // On passe la date au service pour filtrer le parking
      const data = await bookingService.getParkingStatus(selectedDate);
      console.log(`Données reçues pour le ${selectedDate} :`, data);
      setSpots(data);
    } catch (error) {
      console.error("Erreur chargement parking:", error);
    }
  };

  // On recharge les places dès que la date change
  useEffect(() => {
    refreshParkingStatus();
    // On réinitialise la sélection si on change de date pour éviter les erreurs
    setSelectedId(null);
  }, [selectedDate]);

  const handleConfirm = async (firstName: string, lastName: string, role: string) => {
    if (!selectedId) return;

    try {
      // On envoie les données correspondant au BookingRequestDTO du Backend
      await bookingService.createReservation({
        spotId: selectedId,
        firstName,
        lastName,
        role: role as 'EMPLOYEE' | 'MANAGER',
        bookingDate: selectedDate // La date choisie sur le calendrier
      });

      setShowModal(false);
      setSelectedId(null);
      await refreshParkingStatus();
    } catch (error) {
      // On affiche le message d'erreur renvoyé par le GlobalExceptionHandler du Back
      alert(error instanceof Error ? error.message : "Erreur lors de la réservation");
    }
  };

  const renderRow = (letter: string) => (
    <div className={`row-container row-${letter}`} key={letter}>
      <div className="row-label">{letter}</div>
      <div className="spots-grid">
        {COLS.map((num) => {
          const id = `${letter}${num.toString().padStart(2, '0')}`;
          const spotData = spots.find(s => s.id === id);
          const isOccupied = !!spotData;
          const isMine = selectedId === id;

          return (
            <div 
              key={id} 
              className={`spot ${isOccupied ? 'occupied' : ''} ${isMine ? 'my-selection' : ''}`}
              onClick={() => {
                if (!isOccupied) {
                  setSelectedId(id);
                } else {
                  console.log(`Place ${id} occupée par : ${spotData?.reservedBy}`);
                }
              }}
            >
              <span className="spot-id">{id}</span>
              
              {(letter === 'A' || letter === 'F') && !isOccupied && !isMine && (
                <span className="electric-bolt">⚡</span>
              )}
              
              {isOccupied && (
                <div className="car-container">
                  <img src={RedCar} alt="Occupé" className="car-image" />
                  <div className="driver-name">
                    {spotData?.reservedBy}
                  </div>
                </div>
              )}

              {isMine && (
                <div className="car-container">
                  <img src={BlueCar} alt="Sélectionné" className="car-image" />
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );

  return (
    <div className="parking-layout">
      {/* Sélecteur de date pour naviguer dans le planning */}
      <div className="date-navigation">
        <label htmlFor="parking-date">Consulter le parking pour le : </label>
        <input 
          type="date" 
          id="parking-date"
          value={selectedDate}
          min={getTodayString()} // On empêche de réserver dans le passé
          onChange={(e) => setSelectedDate(e.target.value)}
          className="date-input"
        />
      </div>

      <div className="parking-container">
        <div className="parking-board">
          {/* Continuous SVG Road Overlay */}
          <svg className="road-svg" viewBox="0 0 1115 1080" preserveAspectRatio="xMidYMid meet">
            {/* Main Road Surface */}
            <path 
              d="M 50,-60 L 50,140 Q 50,180 90,180 L 1025,180 Q 1065,180 1065,220 L 1065,500 Q 1065,540 1025,540 L 90,540 Q 50,540 50,580 L 50,860 Q 50,900 90,900 L 1200,900"
              fill="none"
              stroke="#1e293b"
              strokeWidth="90"
              strokeLinecap="square"
            />
            {/* Dashed Center Line */}
            <path 
              d="M 50,-60 L 50,140 Q 50,180 90,180 L 1025,180 Q 1065,180 1065,220 L 1065,500 Q 1065,540 1025,540 L 90,540 Q 50,540 50,580 L 50,860 Q 50,900 90,900 L 1200,900"
              fill="none"
              stroke="#facc15"
              strokeWidth="4"
              strokeDasharray="15, 15"
              className="animated-road-line"
            />
          </svg>

          <div className="parking-content">
            {renderRow('A')}
            <div className="driveway"><span className="arrow-right">➡</span></div>
            <div className="parking-block-double">
              {renderRow('B')}
              {renderRow('C')}
            </div>
            <div className="driveway"><span className="arrow-left">⬅</span></div>
            <div className="parking-block-double">
              {renderRow('D')}
              {renderRow('E')}
            </div>
            <div className="driveway"><span className="arrow-right">➡</span></div>
            {renderRow('F')}
          </div>
        </div>
      </div>

      {selectedId && (
        <div className="action-bar" style={{ display: 'flex', zIndex: 1000 }}>
          <p>Place <strong>{selectedId}</strong> sélectionnée pour le <strong>{selectedDate}</strong></p>
          <button className="btn-reserve" onClick={() => setShowModal(true)}>
            Confirmer la réservation
          </button>
        </div>
      )}

      {showModal && selectedId && (
        <ReservationModal 
          spotId={selectedId} 
          bookingDate={selectedDate} // On passe la date à la modale pour info
          onClose={() => setShowModal(false)} 
          onConfirm={handleConfirm}
        />
      )}
    </div>
  );
};