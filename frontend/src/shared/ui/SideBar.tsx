import { NavLink } from "react-router-dom";
import "./Sidebar.css";

interface NavItem {
  to: string;
  label: string;
  icon: React.ReactNode;
}

const NAV_ITEMS: NavItem[] = [
  {
    to: "/",
    label: "Réserver",
    icon: (
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.75" strokeLinecap="round" strokeLinejoin="round">
        <rect x="3" y="3" width="7" height="7" rx="1" />
        <rect x="14" y="3" width="7" height="7" rx="1" />
        <rect x="3" y="14" width="7" height="7" rx="1" />
        <rect x="14" y="14" width="7" height="7" rx="1" />
      </svg>
    ),
  },
];

export default function Sidebar() {
  return (
    <aside className="sidebar">
      {/* Logo */}
      <div className="sidebar__logo">
        <div className="sidebar__logo-icon">
          <svg viewBox="0 0 32 32" fill="none">
            <rect width="32" height="32" rx="8" fill="url(#logoGrad)" />
            <path d="M9 16 L16 9 L23 16 L16 23 Z" fill="white" fillOpacity="0.9" />
            <circle cx="16" cy="16" r="3" fill="white" />
            <defs>
              <linearGradient id="logoGrad" x1="0" y1="0" x2="32" y2="32" gradientUnits="userSpaceOnUse">
                <stop offset="0%" stopColor="#6366f1" />
                <stop offset="100%" stopColor="#8b5cf6" />
              </linearGradient>
            </defs>
          </svg>
        </div>
         <span style={{ color: "#f8fafc", fontWeight: 900}}>PARK<span className="brand-highlight">OFFICE</span></span>
      </div>

      {/* Nav */}
      <nav className="sidebar__nav">
        {NAV_ITEMS.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.to === "/"}
            className={({ isActive }) =>
              `sidebar__link${isActive ? " sidebar__link--active" : ""}`
            }
          >
            <span className="sidebar__link-icon">{item.icon}</span>
            <span className="sidebar__link-label">{item.label}</span>
          </NavLink>
        ))}
      </nav>

      {/* Footer / Avatar */}
      <div className="sidebar__footer">
        <div className="sidebar__avatar">
          <div className="sidebar__avatar-img">
            <svg viewBox="0 0 40 40" fill="none">
              <rect width="40" height="40" rx="12" fill="url(#avatarGrad)" />
              <circle cx="20" cy="16" r="6" fill="white" fillOpacity="0.85" />
              <path d="M8 36c0-6.627 5.373-12 12-12s12 5.373 12 12" fill="white" fillOpacity="0.85" />
              <defs>
                <linearGradient id="avatarGrad" x1="0" y1="0" x2="40" y2="40" gradientUnits="userSpaceOnUse">
                  <stop offset="0%" stopColor="#0ea5e9" />
                  <stop offset="100%" stopColor="#6366f1" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <div className="sidebar__avatar-info">
            <span className="sidebar__avatar-name">Personne non-connectée</span>
          </div>
        </div>
      </div>
    </aside>
  );
}
