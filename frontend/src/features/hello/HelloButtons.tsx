import { useState } from 'react';
import { helloService } from '../../services/helloService';
import { Button } from '../../shared/ui/Button';

export const HelloButtons = () => {
  const [message, setMessage] = useState<string>('');
  const [loading, setLoading] = useState<string | null>(null);

  const handleCall = async (type: 'API' | 'DB' | 'REDIS', call: () => Promise<any>) => {
    setLoading(type);
    try {
      const data = await call();
      setMessage(`[${type}] : ${data}`);
    } catch (e) {
      setMessage(`Erreur sur ${type}`);
    } finally {
      setLoading(null);
    }
  };

  return (
    <div className="flex flex-col items-center gap-12">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        <Button onClick={() => handleCall('API', helloService.sayHello)} disabled={!!loading}>
          {loading === 'API' ? '...' : 'Test Java'}
        </Button>

        <Button onClick={() => handleCall('DB', helloService.sayHelloDb)} disabled={!!loading}>
          {loading === 'DB' ? '...' : 'Test Postgres'}
        </Button>

        <Button onClick={() => handleCall('REDIS', helloService.sayHelloRedis)} disabled={!!loading}>
          {loading === 'REDIS' ? '...' : 'Test Redis'}
        </Button>
      </div>

      {message && (
        <div className="animate-bounce text-xl font-bold text-blue-600 bg-blue-50 px-8 py-4 rounded-2xl border-2 border-blue-200">
          {message}
        </div>
      )}
    </div>
  );
};