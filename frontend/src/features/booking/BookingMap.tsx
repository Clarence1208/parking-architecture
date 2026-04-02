import { useState, useEffect } from 'react';
import { bookingService } from '../../services/booking/bookingService.tsx';
import { BookingForm } from './BookingForm.tsx';
import QRScannerModal from './QRScannerModal.tsx';
import { useAuth } from '../../store/AuthContext';
import type { ParkingSpotResponse } from '../../services/booking/interfaces/bookingInterface.ts';
import './BookingMap.css';
import './QRScannerModal.css';

import RedCar from '../../assets/cars/purple-car.png';
import BlueCar from '../../assets/cars/blue-car.png';

const COLS = Array.from({ length: 10 }, (_, i) => i + 1);

// Utilitaire pour obtenir la date du jour au format YYYY-MM-DD
const getTodayString = () => new Date().toISOString().split('T')[0];

const roadLine = [
  { x: 50, y: -50 },     // 0
  { x: 50, y: 180 },     // 1
  { x: 1065, y: 180 },   // 2
  { x: 1065, y: 540 },   // 3
  { x: 50, y: 540 },     // 4
  { x: 50, y: 900 },     // 5
  { x: 1065, y: 900 },   // 6
  { x: 1065, y: 1150 }   // 7
];

interface Point { x: number; y: number; }

const getSpotConnection = (spotId: string) => {
  if (spotId === 'EXIT') {
    return {
      spotCenter: { x: 1065, y: 1250 },
      connection: { x: 1065, y: 1150 },
      roadIndexSegment: 7,
      row: 'EXIT'
    };
  }

  const row = spotId.charAt(0);
  const num = parseInt(spotId.slice(1), 10);
  const targetX = 165 + (num - 1) * 85 + 42.5; 
  let targetY = 0;
  let connY = 0;
  let segment = 0; 

  if (row === 'A') { targetY = 65; connY = 180; segment = 1; }
  else if (row === 'B') { targetY = 295; connY = 180; segment = 1; }
  else if (row === 'C') { targetY = 425; connY = 540; segment = 3; }
  else if (row === 'D') { targetY = 655; connY = 540; segment = 3; }
  else if (row === 'E') { targetY = 785; connY = 900; segment = 5; }
  else if (row === 'F') { targetY = 1015; connY = 900; segment = 5; }

  return {
    spotCenter: { x: targetX, y: targetY },
    connection: { x: targetX, y: connY },
    roadIndexSegment: segment,
    row: row
  };
};

const getWaypoints = (startId: string | null, targetId: string) => {
  const end = getSpotConnection(targetId);
  const rawPoints: Point[] = [];

  if (!startId) {
     rawPoints.push(roadLine[0]);
     for (let i = 1; i <= end.roadIndexSegment; i++) {
        rawPoints.push(roadLine[i]);
     }
     rawPoints.push(end.connection);
     rawPoints.push(end.spotCenter);
  } else {
     const start = getSpotConnection(startId);
     rawPoints.push(start.spotCenter);
     rawPoints.push(start.connection);
     
     if (start.roadIndexSegment === end.roadIndexSegment) {
        rawPoints.push(end.connection);
     } else if (start.roadIndexSegment < end.roadIndexSegment) {
        for (let i = start.roadIndexSegment + 1; i <= end.roadIndexSegment; i++) {
           rawPoints.push(roadLine[i]);
        }
        rawPoints.push(end.connection);
     } else {
        for (let i = start.roadIndexSegment; i > end.roadIndexSegment; i--) {
           rawPoints.push(roadLine[i]);
        }
        rawPoints.push(end.connection);
     }
     rawPoints.push(end.spotCenter);
  }

  // Remove consecutive duplicates
  const points: Point[] = [];
  for (const p of rawPoints) {
     if (points.length > 0) {
        const prev = points[points.length - 1];
        if (prev.x === p.x && prev.y === p.y) continue;
     }
     points.push(p);
  }

  const waypoints: { x: number, y: number, rotate: number }[] = [];
  for (let i = 0; i < points.length; i++) {
     const curr = points[i];
     if (i === 0) {
        const next = points[i+1];
        let angle = Math.round(Math.atan2(next.y - curr.y, next.x - curr.x) * (180 / Math.PI) + 90);
        waypoints.push({ x: curr.x, y: curr.y, rotate: (angle + 360) % 360 });
     } else {
        const prevWP = waypoints[waypoints.length - 1];
        waypoints.push({ x: curr.x, y: curr.y, rotate: prevWP.rotate });
        
        if (i < points.length - 1) {
           const next = points[i+1];
           let normAngle;
           
           if (i === points.length - 2) {
              if (end.row === 'EXIT') {
                 normAngle = 180;
              } else {
                 normAngle = (end.row === 'A' || end.row === 'C' || end.row === 'E') ? 180 : 0;
              }
           } else {
              let angle = Math.round(Math.atan2(next.y - curr.y, next.x - curr.x) * (180 / Math.PI) + 90);
              normAngle = (angle + 360) % 360;
           }
           
           let diff = normAngle - ((prevWP.rotate % 360 + 360) % 360);
           if (diff > 180) diff -= 360;
           if (diff < -180) diff += 360;
           
           waypoints.push({ x: curr.x, y: curr.y, rotate: prevWP.rotate + diff });
        }
     }
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
  const rules = waypoints.map((wp, i) => {
    if (i > 0) {
      let d = Math.max(Math.abs(wp.x - waypoints[i - 1].x), Math.abs(wp.y - waypoints[i - 1].y));
      if (d === 0) d = 80;
      currentDist += d;
    }
    const percent = ((currentDist / totalDist) * 100).toFixed(2);
    return `${percent}% { transform: translate(${wp.x}px, ${wp.y}px) translate(-50%, -50%) rotate(${wp.rotate}deg); }`;
  }).join('\n');
  
  return { rules, totalDist };
};

export const BookingMap = () => {
  const { user } = useAuth();
  const [spots, setSpots] = useState<ParkingSpotResponse[]>([]);
  const [selectedDate, setSelectedDate] = useState<string>(getTodayString());
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [animatingSpotId, setAnimatingSpotId] = useState<string | null>(null);
  const [animationStyle, setAnimationStyle] = useState<string>('');
  const [showModal, setShowModal] = useState(false);
  const [showScanner, setShowScanner] = useState(false);

  const adjustDate = (days: number) => {
    const d = new Date(selectedDate);
    d.setDate(d.getDate() + days);
    const newDateStr = d.toISOString().split('T')[0];
    if (newDateStr >= getTodayString()) {
      setSelectedDate(newDateStr);
    }
  };

  const formatDate = (dateStr: string) => {
    const d = new Date(dateStr);
    return new Intl.DateTimeFormat('fr-FR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long'
    }).format(d);
  };

  const hasReservationToday = spots.some(s => s.occupied && s.reservedBy === user?.email);

  const isToday = selectedDate === getTodayString();
  const myTodaySpot = spots.find(s => s.occupied && s.reservedBy === user?.email);
  const canCheckIn = isToday && !!myTodaySpot && !myTodaySpot.checkedIn;
  const alreadyCheckedIn = isToday && !!myTodaySpot && !!myTodaySpot.checkedIn;

  const handleSpotClick = (id: string, isOccupied: boolean) => {
    if (isOccupied) {
      console.log(`Place ${id} occupée.`);
      return;
    }
    if (hasReservationToday) {
      alert("Vous avez déjà une réservation sur cette date. Vous ne pouvez pas réserver plusieurs places.");
      return;
    }
    if (animatingSpotId) return; // Ignore while another car is animating
    if (selectedId === id) return; // Already selected

    // Start Animation Sequence
    const previousId = selectedId;
    setAnimatingSpotId(id);
    setSelectedId(null);
    
    const waypoints = getWaypoints(previousId, id);
    const { rules, totalDist } = generateKeyframes(waypoints);
    
    // Dynamically adjust duration based on distance traveled so it doesn't zoom
    const durationMs = Math.max(1500, Math.min((totalDist / 400) * 1000, 3500));
    const durationSec = (durationMs / 1000).toFixed(2);

    setAnimationStyle(`
      @keyframes driveCarToSpot {
        ${rules}
      }
      .animated-car-drive {
        animation: driveCarToSpot ${durationSec}s linear forwards;
      }
    `);

    setTimeout(() => {
      setAnimatingSpotId(null);
      setSelectedId(id);
    }, Math.floor(durationMs));
  };

  const handleExitClick = () => {
    if (!selectedId || animatingSpotId) return;

    const previousId = selectedId;
    setAnimatingSpotId('EXIT');
    setSelectedId(null);
    setShowModal(false); // Hide reservation panel if open
    
    const waypoints = getWaypoints(previousId, 'EXIT');
    const { rules, totalDist } = generateKeyframes(waypoints);
    
    const durationMs = Math.max(1500, Math.min((totalDist / 400) * 1000, 3500));
    const durationSec = (durationMs / 1000).toFixed(2);

    setAnimationStyle(`
      @keyframes driveCarToSpot {
        ${rules}
      }
      .animated-car-drive {
        animation: driveCarToSpot ${durationSec}s linear forwards;
      }
    `);

    setTimeout(() => {
      setAnimatingSpotId(null);
    }, Math.floor(durationMs));
  };
    const handleCancel = async (e: React.MouseEvent, bookingId: number) => {
    e.stopPropagation();
    if (!window.confirm("Voulez-vous vraiment annuler cette réservation ?")) return;

    try {
        await bookingService.cancelReservation(bookingId);
        await refreshParkingStatus();
        setSelectedId(null);
    } catch (error) {
        alert(error instanceof Error ? error.message : "Erreur lors de l'annulation");
    }
    };
  const refreshParkingStatus = async () => {
    try {
      // On passe la date au service pour filtrer le booking
      const data = await bookingService.getParkingStatus(selectedDate);
      console.log(`Données reçues pour le ${selectedDate} :`, data);
      setSpots(data);
    } catch (error) {
      console.error("Erreur chargement booking:", error);
    }
  };

  // On recharge les places dès que la date change
  useEffect(() => {
    refreshParkingStatus();
    // On réinitialise la sélection si on change de date pour éviter les erreurs
    setSelectedId(null);
  }, [selectedDate]);

  const handleConfirm = async (endDate: string) => {
    if (!selectedId) return;

    try {
      // On envoie les données correspondant au BookingRequestDTO du Backend
      await bookingService.createReservation({
        spotId: selectedId,
        startDate: selectedDate, // La date choisie sur le calendrier
        endDate: endDate         // La date de fin choisie dans la modale
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
          const spotData = spots.find(s => s.spotId === id);
          const isOccupied = !!spotData && spotData.occupied;
          const isMine = selectedId === id;
          const isMyReservation = isOccupied && spotData?.reservedBy === user?.email;
          const canCancel = isOccupied && (isMyReservation || user?.role === 'SECRETARY');

          return (
            <div 
              key={id} 
              className={`spot ${isOccupied ? 'occupied' : ''} ${isMine ? 'my-selection' : ''} ${isMyReservation ? 'my-reservation' : ''}`}
              onClick={() => handleSpotClick(id, isOccupied)}
            >
              <span className="spot-id">{id}</span>
              
              {(letter === 'A' || letter === 'F') && !isOccupied && !isMine && (
                <span className="electric-bolt">⚡</span>
              )}
              
              {/* UNE SEULE CONDITION POUR LA VOITURE : Empêche le doublon de ton image */}
              {isOccupied ? (
                <div className="car-container">
                  <img src={isMyReservation ? BlueCar : RedCar} alt="Voiture" className="car-image" />
                  {isMyReservation && <span className="spot-badge">{id}</span>}
                  <div className="driver-name">
                    <span>{isMyReservation ? "Ma place" : spotData?.reservedBy}</span>
                    {canCancel && spotData?.bookingId && (
                      <button 
                        className="btn-cancel-hover" 
                        onClick={(e) => handleCancel(e, spotData.bookingId!)}
                      >
                        ANNULER
                      </button>
                    )}
                  </div>
                </div>
              ) : isMine ? (
                <div className="car-container">
                  <img src={BlueCar} alt="Sélectionné" className="car-image" />
                  <span className="spot-badge">{id}</span>
                </div>
              ) : null}
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
        <button 
          className="date-arrow" 
          onClick={() => adjustDate(-1)}
          disabled={selectedDate <= getTodayString()}
          title="Jour précédent"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="m15 18-6-6 6-6"/></svg>
        </button>
        
        <div className="date-display">
          <span className="date-text">{formatDate(selectedDate)}</span>
          <label className="date-calendar-icon" htmlFor="parking-date" title="Choisir une date">
             <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect width="18" height="18" x="3" y="4" rx="2" ry="2"/><line x1="16" x2="16" y1="2" y2="6"/><line x1="8" x2="8" y1="2" y2="6"/><line x1="3" x2="21" y1="10" y2="10"/></svg>
          </label>
          <input 
             type="date" 
             id="parking-date"
             value={selectedDate}
             min={getTodayString()}
             onChange={(e) => setSelectedDate(e.target.value)}
             className="hidden-date-input"
          />
        </div>

        <button 
          className="date-arrow" 
          onClick={() => adjustDate(1)}
          title="Jour suivant"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="m9 18 6-6-6-6"/></svg>
        </button>
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
          <div 
            className={`sign-board sign-exit ${selectedId ? 'clickable-exit' : ''}`}
            onClick={handleExitClick}
          >
            SORTIE
          </div>
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
        <BookingForm
          spotId={selectedId} 
          startDate={selectedDate}
          onClose={() => setShowModal(false)} 
          onConfirm={handleConfirm}
        />
      )}

      {canCheckIn && !selectedId && (
        <div className="action-bar checkin-bar" style={{ display: 'flex', zIndex: 1000 }}>
          <p>Place <strong>{myTodaySpot?.spotId}</strong> — en attente de check-in</p>
          <button className="btn-checkin" onClick={() => setShowScanner(true)}>
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <rect x="2" y="2" width="6" height="6" rx="1" />
              <rect x="16" y="2" width="6" height="6" rx="1" />
              <rect x="2" y="16" width="6" height="6" rx="1" />
              <path d="M16 16h2v2h-2zM20 16h2v2h-2zM16 20h2v2h-2zM20 20h2v2h-2z" />
            </svg>
            Scanner le QR code
          </button>
        </div>
      )}

      {alreadyCheckedIn && !selectedId && (
        <div className="action-bar checkedin-bar" style={{ display: 'flex', zIndex: 1000 }}>
          <p>&#x2705; Place <strong>{myTodaySpot?.spotId}</strong> — check-in effectué</p>
        </div>
      )}

      {showScanner && (
        <QRScannerModal
          onClose={() => setShowScanner(false)}
          onCheckInSuccess={refreshParkingStatus}
        />
      )}
    </div>
  );
};