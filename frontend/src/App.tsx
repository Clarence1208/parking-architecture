import { HelloButtons } from './features/hello/HelloButtons';

function App() {
  return (
    <div className="min-h-screen bg-white flex flex-col font-sans">


      <main className="flex-grow flex items-center justify-center p-6">
        <HelloButtons />
      </main>
    </div>
  );
}

export default App;