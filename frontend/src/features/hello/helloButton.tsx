import { useState } from 'react';
import { helloService } from '../../services/helloService';
// Import de ton atome UI partagé
import { Button } from '../../shared/ui/Button';

export const HelloButton = () => {
  const [message, setMessage] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);

  const handleHello = async () => {
    setIsLoading(true);
    try {
      const data = await helloService.sayHello();
      setMessage(data);
    } catch (error) {
      console.error(error);
      setMessage('Erreur de connexion au backend Java');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="p-4 border rounded-lg shadow-sm bg-white">
      <Button 
        onClick={handleHello} 
        disabled={isLoading}
      >
        {isLoading ? 'Appel en cours...' : 'Appeler HelloController'}
      </Button>
      
      {message && (
        <p className="mt-4 p-2 bg-blue-50 text-blue-700 rounded border border-blue-200">
          Réponse du serveur : <strong>{message}</strong>
        </p>
      )}
    </div>
  );
};