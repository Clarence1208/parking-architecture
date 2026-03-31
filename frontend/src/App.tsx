import './App.css';
import { HelloButtons } from './features/hello/HelloButtons';

function App() {
  return (
    <div className="app-container">
      <header className="app-header">
        <h1>
          PARK<span className="brand-highlight">OFFICE</span>
        </h1>
      </header>

      <main className="main-content">
        <HelloButtons />
      </main>
    </div>
  );
}

export default App;