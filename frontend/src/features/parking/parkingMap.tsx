import { useState, useEffect } from 'react';
import { bookingService } from '../../services/bookingService';
import { ReservationModal } from './reservationModal';
import type { ParkingSpot } from '../../types/api-model';
import './parkingMap.css';

import RedCar from '../../assets/cars/red-car.png';
import BlueCar from '../../assets/cars/blue-car2.png';

const COLS = Array.from({ length: 10 }, (_, i) => i + 1);

export const ParkingMap = () => {
  const [spots, setSpots] = useState<ParkingSpot[]>([]);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    // On charge les données au montage
    bookingService.getParkingStatus().then(data => setSpots(data));
  }, []);

  const handleConfirm = async (firstName: string, lastName: string, duration: number) => {
    if (!selectedId) return;

    await bookingService.createReservation({
      spotId: selectedId,
      firstName,
      lastName,
      durationDays: duration
    });

    setShowModal(false);
    setSelectedId(null);
    // On rafraîchit les données après réservation
    const updatedSpots = await bookingService.getParkingStatus();
    setSpots(updatedSpots);
  };

  const renderRow = (letter: string) => (
    <div className={`row-container row-${letter}`} key={letter}>
      <div className="row-label">{letter}</div>
      <div className="spots-grid">
        {COLS.map((num) => {
          const id = `${letter}${num.toString().padStart(2, '0')}`;
          
          // --- UTILISATION DE 'spots' ICI POUR LE BUILD ---
          const spotData = spots.find(s => s.id === id);
          const isOccupied = spotData?.isOccupied || false;
          const isMine = selectedId === id;

          return (
            <div 
              key={id} 
              className={`spot ${isOccupied ? 'occupied' : ''} ${isMine ? 'my-selection' : ''}`}
              onClick={() => !isOccupied && setSelectedId(id)}
            >
              <span className="spot-id">{id}</span>
              
              {(letter === 'A' || letter === 'F') && !isOccupied && !isMine && (
                <span className="electric-bolt">⚡</span>
              )}
              
              {isOccupied && <img src={RedCar} alt="Occupied" className="car-image" />}
              {isMine && <img src={BlueCar} alt="Selected" className="car-image" />}
            </div>
          );
        })}
      </div>
    </div>
  );

  return (
    <div className="parking-layout">
      <div className="parking-container">
        {renderRow('A')}
        <div className="driveway">➡</div>
        <div className="parking-block-double">
          {renderRow('B')}
          {renderRow('C')}
        </div>
        <div className="driveway">⬅</div>
        <div className="parking-block-double">
          {renderRow('D')}
          {renderRow('E')}
        </div>
        <div className="driveway">➡</div>
        {renderRow('F')}
      </div>

        {selectedId && (
        <div className="action-bar">
            <p>Place sélectionnée : <strong>{selectedId}</strong></p>
            <button className="btn-reserve" onClick={() => setShowModal(true)}>
            Réserver
            </button>
        </div>
        )}

      {showModal && selectedId && (
        <ReservationModal 
          spotId={selectedId} 
          onClose={() => setShowModal(false)} 
          onConfirm={handleConfirm}
        />
      )}
    </div>
  );
};