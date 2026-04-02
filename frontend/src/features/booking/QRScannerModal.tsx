import { useEffect, useRef, useState, useCallback } from 'react';
import { Html5Qrcode } from 'html5-qrcode';
import { bookingService } from '../../services/booking/bookingService';
import type { CheckInResponse } from '../../services/booking/interfaces/bookingInterface';

interface QRScannerModalProps {
  onClose: () => void;
  onCheckInSuccess: () => void;
}

type ScanState = 'scanning' | 'processing' | 'success' | 'error';

const SCANNER_ID = 'qr-scanner-region';

/**
 * Synchronously stops every camera track attached to video elements
 * inside the scanner container. This is the only reliable way to
 * release the camera — html5-qrcode's async stop() races with
 * React's synchronous unmount.
 */
function killCameraTracks() {
  document.getElementById(SCANNER_ID)
    ?.querySelectorAll('video')
    .forEach(video => {
      const stream = video.srcObject as MediaStream | null;
      if (stream) {
        stream.getTracks().forEach(track => track.stop());
        video.srcObject = null;
      }
    });
}

export default function QRScannerModal({ onClose, onCheckInSuccess }: QRScannerModalProps) {
  const [state, setState] = useState<ScanState>('scanning');
  const [result, setResult] = useState<CheckInResponse | null>(null);
  const [error, setError] = useState('');
  const scannerRef = useRef<Html5Qrcode | null>(null);
  const processedRef = useRef(false);

  const stopAndClear = useCallback(() => {
    killCameraTracks();

    const scanner = scannerRef.current;
    if (scanner) {
      try { scanner.stop().catch(() => {}); } catch { /* ignore */ }
      try { scanner.clear(); } catch { /* ignore */ }
      scannerRef.current = null;
    }
  }, []);

  const startScanner = useCallback(() => {
    const el = document.getElementById(SCANNER_ID);
    if (!el) return;
    el.innerHTML = '';

    const scanner = new Html5Qrcode(SCANNER_ID);
    scannerRef.current = scanner;
    processedRef.current = false;

    scanner.start(
      { facingMode: 'environment' },
      { fps: 10, qrbox: { width: 250, height: 250 } },
      (decodedText) => {
        if (processedRef.current) return;
        processedRef.current = true;
        handleScan(decodedText);
      },
      () => {}
    ).catch(() => {
      setError("Impossible d'accéder à la caméra. Vérifiez les permissions.");
      setState('error');
    });
  }, []);

  useEffect(() => {
    startScanner();
    // Synchronous cleanup — kills the camera immediately on unmount
    return stopAndClear;
  }, [startScanner, stopAndClear]);

  const handleScan = async (decodedText: string) => {
    stopAndClear();

    const spotId = extractSpotId(decodedText);
    if (!spotId) {
      setError("QR code invalide. Scannez le QR code de votre place de parking.");
      setState('error');
      return;
    }

    setState('processing');

    try {
      const res = await bookingService.checkIn(spotId);
      setResult(res);
      setState('success');
    } catch (err: any) {
      setError(err.message || 'Erreur lors du check-in.');
      setState('error');
    }
  };

  const extractSpotId = (text: string): string | null => {
    try {
      const url = new URL(text);
      return url.searchParams.get('spot');
    } catch {
      const match = text.match(/[A-F]\d{2}/i);
      return match ? match[0].toUpperCase() : null;
    }
  };

  const handleSuccessClose = () => {
    stopAndClear();
    onCheckInSuccess();
    onClose();
  };

  const handleRetry = () => {
    stopAndClear();
    setError('');
    setState('scanning');
    requestAnimationFrame(() => startScanner());
  };

  const handleClose = () => {
    stopAndClear();
    onClose();
  };

  return (
    <div className="qr-modal-overlay" onClick={handleClose}>
      <div className="qr-modal" onClick={e => e.stopPropagation()}>
        <button className="qr-modal__close" onClick={handleClose} title="Fermer">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <line x1="18" y1="6" x2="6" y2="18" /><line x1="6" y1="6" x2="18" y2="18" />
          </svg>
        </button>

        {state === 'scanning' && (
          <>
            <h2 className="qr-modal__title">Scannez le QR code de votre place</h2>
            <p className="qr-modal__subtitle">Pointez la caméra vers le QR code collé sur votre place de parking</p>
          </>
        )}

        {state === 'processing' && (
          <div className="qr-modal__status">
            <div className="qr-modal__spinner" />
            <h2 className="qr-modal__title">Vérification en cours...</h2>
          </div>
        )}

        {state === 'success' && result && (
          <div className="qr-modal__status">
            <div className="qr-modal__icon qr-modal__icon--success">&#x2705;</div>
            <h2 className="qr-modal__title qr-modal__title--success">Check-in confirmé !</h2>
            <p className="qr-modal__detail">Place <strong>{result.spotId}</strong> — {result.startDate} → {result.endDate}</p>
            <button className="qr-modal__btn" onClick={handleSuccessClose}>Fermer</button>
          </div>
        )}

        {state === 'error' && (
          <div className="qr-modal__status">
            <div className="qr-modal__icon qr-modal__icon--error">&#x274C;</div>
            <h2 className="qr-modal__title qr-modal__title--error">Échec du check-in</h2>
            <p className="qr-modal__detail">{error}</p>
            <div className="qr-modal__actions">
              <button className="qr-modal__btn" onClick={handleRetry}>Réessayer</button>
              <button className="qr-modal__btn qr-modal__btn--secondary" onClick={handleClose}>Annuler</button>
            </div>
          </div>
        )}

        <div
          className={state === 'scanning' ? 'qr-modal__scanner-wrapper' : undefined}
          style={state !== 'scanning' ? { display: 'none' } : undefined}
        >
          <div id={SCANNER_ID} />
        </div>
      </div>
    </div>
  );
}
