import { useState } from 'react';
import { helloService } from '../../services/helloService';
export const HelloButtons = () => {
  const [message, setMessage] = useState<string>('');
  const [loading, setLoading] = useState<string | null>(null);

  const handleCall = async (type: string, call: () => Promise<any>) => {
    setLoading(type);
    try {
      const data = await call();
      setMessage(`[${type}] : ${data}`);
    } catch (e) {
      setMessage(`Erreur critique sur la route ${type}`);
    } finally {
      setLoading(null);
    }
  };

  return (
    <div className="hello-feature-container">
      <div className="buttons-group">
        <button 
          className="app-button"
          onClick={() => handleCall('JAVA', helloService.sayHello)} 
          disabled={!!loading}
        >
          {loading === 'JAVA' ? '...' : 'Hello backend'}
        </button>

        <button 
          className="app-button"
          onClick={() => handleCall('DB', helloService.sayHelloDb)} 
          disabled={!!loading}
        >
          {loading === 'DB' ? '...' : 'Hello DB'}
        </button>

        <button 
          className="app-button"
          onClick={() => handleCall('REDIS', helloService.sayHelloRedis)} 
          disabled={!!loading}
        >
          {loading === 'REDIS' ? '...' : 'Hello Redis'}
        </button>
      </div>

      {}
      {message && (
        <div className="status-message">
          {message}
        </div>
      )}
    </div>
  );
};