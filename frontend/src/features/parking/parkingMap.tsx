import { useState, useEffect } from 'react';
import { bookingService } from '../../services/bookingService';
import { ReservationModal } from './reservationModal';
import type { ParkingSpot } from '../../types/api-model';
import './parkingMap.css';

import RedCar from '../../assets/cars/purple-car.png';
import BlueCar from '../../assets/cars/blue-car.png';

const COLS = Array.from({ length: 10 }, (_, i) => i + 1);

export const ParkingMap = () => {
  const [spots, setSpots] = useState<ParkingSpot[]>([]);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);

  // Fonction de synchronisation avec le Backend
  const refreshParkingStatus = async () => {
    try {
      const data = await bookingService.getParkingStatus();
      console.log("Données reçues du Backend :", data);
      setSpots(data);
    } catch (error) {
      console.error("Erreur chargement parking:", error);
    }
  };

  useEffect(() => {
    refreshParkingStatus();
  }, []);

const handleConfirm = async (firstName: string, lastName: string, duration: number, role: string) => {
  if (!selectedId) return;

  try {
    // On envoie l'objet JSON qui correspond au record 'Booking' du Java
    await bookingService.createReservation({
      spotId: selectedId,
      firstName,
      lastName,
      durationDays: duration,
      role: role // <--- Nouveau champ indispensable !
    });

    setShowModal(false);
    setSelectedId(null);
    await refreshParkingStatus();
  } catch (error) {
    alert("Erreur : Vérifiez la durée autorisée pour votre rôle.");
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
          console.log(`Sélection de la place libre : ${id}`);
          setSelectedId(id);
        } else {
          console.log(`Place ${id} occupée par : ${spotData?.reservedBy}`);
        }
      }}
    >
      <span className="spot-id">{id}</span>
      
      {/* Électrique pour A et F si libre */}
      {(letter === 'A' || letter === 'F') && !isOccupied && !isMine && (
        <span className="electric-bolt">⚡</span>
      )}
      
      {/* Affichage des voitures occupées avec le nom du conducteur */}
      {isOccupied && (
        <div className="car-container">
          <img src={RedCar} alt="Occupé" className="car-image" />
          <div className="driver-name">
            {spotData?.reservedBy}
          </div>
        </div>
      )}

      {/* Affichage de la voiture bleue pour la sélection en cours */}
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

      {/* Barre d'action : ne s'affiche que si selectedId n'est pas null */}
      {selectedId && (
        <div className="action-bar" style={{ display: 'flex', zIndex: 1000 }}>
          <p>Place sélectionnée : <strong>{selectedId}</strong></p>
          <button className="btn-reserve" onClick={() => setShowModal(true)}>
            Réserver maintenant
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