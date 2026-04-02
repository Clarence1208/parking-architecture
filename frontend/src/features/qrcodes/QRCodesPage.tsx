import { QRCodeSVG } from 'qrcode.react';
import './QRCodesPage.css';

const ROWS = ['A', 'B', 'C', 'D', 'E', 'F'] as const;
const COLS = Array.from({ length: 10 }, (_, i) => i + 1);

const ALL_SPOTS = ROWS.flatMap(row =>
  COLS.map(col => `${row}${col.toString().padStart(2, '0')}`)
);

function getCheckInUrl(spotId: string): string {
  return `${window.location.origin}/check-in?spot=${spotId}`;
}

export default function QRCodesPage() {
  const handlePrint = () => window.print();

  return (
    <div className="qr-page">
      <div className="qr-header no-print">
        <div>
          <h1>QR Codes des places</h1>
          <p>Imprimez et placez chaque QR code sur la place correspondante. Les utilisateurs pourront scanner pour confirmer leur présence.</p>
        </div>
        <button className="qr-print-btn" onClick={handlePrint}>
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <polyline points="6 9 6 2 18 2 18 9" />
            <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2" />
            <rect x="6" y="14" width="12" height="8" />
          </svg>
          Imprimer
        </button>
      </div>

      <div className="qr-grid">
        {ALL_SPOTS.map(spotId => (
          <div className="qr-card" key={spotId}>
            <div className="qr-card__code">
              <QRCodeSVG
                value={getCheckInUrl(spotId)}
                size={140}
                level="M"
                bgColor="transparent"
                fgColor="#e2e8f0"
              />
            </div>
            <div className="qr-card__label">{spotId}</div>
            <div className="qr-card__hint">Scanner pour check-in</div>
          </div>
        ))}
      </div>
    </div>
  );
}
