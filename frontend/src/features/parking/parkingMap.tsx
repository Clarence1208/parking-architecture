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

const getWaypoints = (spotId: string) => {
  const row = spotId.charAt(0);
  const num = parseInt(spotId.slice(1), 10);
  
  const targetX = 165 + (num - 1) * 85 + 42.5; 
  let waypoints = [];
  
  waypoints.push({ x: 50, y: -50, rotate: 180 }); // start

  if (row === 'A' || row === 'B') {
     waypoints.push({ x: 50, y: 180, rotate: 180 }); 
     waypoints.push({ x: 50, y: 180, rotate: 90 }); 
     waypoints.push({ x: targetX, y: 180, rotate: 90 }); 
  } else if (row === 'C' || row === 'D') {
     waypoints.push({ x: 50, y: 180, rotate: 180 });
     waypoints.push({ x: 50, y: 180, rotate: 90 });
     waypoints.push({ x: 1065, y: 180, rotate: 90 }); 
     waypoints.push({ x: 1065, y: 180, rotate: 180 }); 
     waypoints.push({ x: 1065, y: 540, rotate: 180 });
     waypoints.push({ x: 1065, y: 540, rotate: 270 }); 
     waypoints.push({ x: targetX, y: 540, rotate: 270 });
  } else if (row === 'E' || row === 'F') {
     waypoints.push({ x: 50, y: 180, rotate: 180 });
     waypoints.push({ x: 50, y: 180, rotate: 90 });
     waypoints.push({ x: 1065, y: 180, rotate: 90 });
     waypoints.push({ x: 1065, y: 180, rotate: 180 });
     waypoints.push({ x: 1065, y: 540, rotate: 180 });
     waypoints.push({ x: 1065, y: 540, rotate: 270 });
     waypoints.push({ x: 50, y: 540, rotate: 270 });
     waypoints.push({ x: 50, y: 540, rotate: 180 }); 
     waypoints.push({ x: 50, y: 900, rotate: 180 });
     waypoints.push({ x: 50, y: 900, rotate: 90 }); 
     waypoints.push({ x: targetX, y: 900, rotate: 90 });
  }

  if (row === 'A' || row === 'C' || row === 'E') {
     const lastWP = waypoints[waypoints.length - 1];
     waypoints.push({ ...lastWP, rotate: 180 }); // turn up
     let targetY = row === 'A' ? 65 : (row === 'C' ? 425 : 785);
     waypoints.push({ x: targetX, y: targetY, rotate: 180 });
  } else {
     const lastWP = waypoints[waypoints.length - 1];
     let endRot = 0;
     if (row === 'D') endRot = 360; // 270 -> 360 is shortest
     else endRot = 0; // B: 90 -> 0. F: 90 -> 0.
     waypoints.push({ ...lastWP, rotate: endRot }); // turn down
     let targetY = row === 'B' ? 295 : (row === 'D' ? 655 : 1015);
     waypoints.push({ x: targetX, y: targetY, rotate: endRot });
  }
  
  return waypoints;
};

const generateKeyframes = (waypoints: {x: number, y: number, rotate: number}[]) => {
  let totalDist = 0;
  for (let i = 0; i < waypoints.length - 1; i++) {
    let d = Math.max(Math.abs(waypoints[i + 1].x - waypoints[i].x), Math.abs(waypoints[i + 1].y - waypoints[i].y));
    if (d === 0) d = 80; 
    totalDist += d;
  }

  let currentDist = 0;
  return waypoints.map((wp, i) => {
    if (i > 0) {
      let d = Math.max(Math.abs(wp.x - waypoints[i - 1].x), Math.abs(wp.y - waypoints[i - 1].y));
      if (d === 0) d = 80;
      currentDist += d;
    }
    const percent = ((currentDist / totalDist) * 100).toFixed(2);
    // translate goes to the exact center, so we offset by 50% of the car's own width/height
    return `${percent}% { transform: translate(${wp.x}px, ${wp.y}px) translate(-50%, -50%) rotate(${wp.rotate}deg); }`;
  }).join('\n');
};

export const ParkingMap = () => {
  const [spots, setSpots] = useState<ParkingSpotResponse[]>([]);
  const [selectedDate, setSelectedDate] = useState<string>(getTodayString());
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [animatingSpotId, setAnimatingSpotId] = useState<string | null>(null);
  const [animationStyle, setAnimationStyle] = useState<string>('');
  const [showModal, setShowModal] = useState(false);

  const handleSpotClick = (id: string, isOccupied: boolean) => {
    if (isOccupied) {
      console.log(`Place ${id} occupée.`);
      return;
    }
    if (animatingSpotId) return; // Ignore while another car is animating
    if (selectedId === id) return; // Already selected

    // Start Animation Sequence
    setAnimatingSpotId(id);
    setSelectedId(null);
    
    const waypoints = getWaypoints(id);
    const keyframesRules = generateKeyframes(waypoints);
    setAnimationStyle(`
      @keyframes driveCarToSpot {
        ${keyframesRules}
      }
      .animated-car-drive {
        animation: driveCarToSpot 2.5s linear forwards;
      }
    `);

    // Timer completes after 2.5 seconds matching the animation time
    setTimeout(() => {
      setAnimatingSpotId(null);
      setSelectedId(id);
    }, 2500);
  };

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
              onClick={() => handleSpotClick(id, isOccupied)}
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
        {animationStyle && <style>{animationStyle}</style>}
        
        <div className="parking-board">
          {animatingSpotId && (
            <div className="animated-car-container animated-car-drive">
              <img src={BlueCar} alt="Moving Car" className="car-image-moving" />
            </div>
          )}
          <div className="sign-board sign-entrance">ENTRÉE</div>
          <div className="sign-board sign-exit">SORTIE</div>
          {/* Continuous SVG Road Overlay */}
          <svg className="road-svg" viewBox="0 0 1115 1080" preserveAspectRatio="xMidYMid meet">
            {/* Main Road Surface */}
            <path 
              d="M 50,-60 L 50,140 Q 50,180 90,180 L 1025,180 Q 1065,180 1065,220 L 1065,500 Q 1065,540 1025,540 L 90,540 Q 50,540 50,580 L 50,860 Q 50,900 90,900 L 1025,900 Q 1065,900 1065,940 L 1065,1150"
              fill="none"
              stroke="#1e293b"
              strokeWidth="90"
              strokeLinecap="square"
            />
            {/* Dashed Center Line */}
            <path 
              d="M 50,-60 L 50,140 Q 50,180 90,180 L 1025,180 Q 1065,180 1065,220 L 1065,500 Q 1065,540 1025,540 L 90,540 Q 50,540 50,580 L 50,860 Q 50,900 90,900 L 1025,900 Q 1065,900 1065,940 L 1065,1150"
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