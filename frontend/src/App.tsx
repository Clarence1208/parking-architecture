import './App.css';
import { ParkingMap } from './features/parking/parkingMap';

function App() {
  return (
    <div className="app-container">
      <header className="app-header">
        <h1>PARK<span className="brand-highlight">OFFICE</span></h1>
      </header>
      <main className="main-content">
         <ParkingMap />
      </main>
    </div>
  );
}

export default App;