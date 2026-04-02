import './App.css';
import { BookingMap } from './features/booking/BookingMap.tsx';

function App() {
  return (
    <div className="app-container">
      <header className="app-header">
        <h1>PARK<span className="brand-highlight">OFFICE</span></h1>
      </header>
      <main className="main-content">
         <BookingMap />
      </main>
    </div>
  );
}

export default App;