import { Outlet, Link, useLocation } from 'react-router-dom';
import { Sun, LogIn } from 'lucide-react';
import './Layout.css';

export default function Layout() {
  const location = useLocation();

  return (
    <div className="layout">
      <header className="header">
        <div className="header-container container flex items-center justify-between">
          <Link to="/" className="brand flex items-center gap-2">
            <img 
              src="/logo.png" 
              alt="Vigil Logo" 
              className="brand-logo" 
              onError={(e) => {
                e.target.style.display = 'none';
                e.target.nextSibling.style.display = 'flex';
              }}
            />
            <div className="brand-text">
              <span className="brand-name font-bold text-xl">VIGIL</span>
              <span className="brand-tagline text-xs text-secondary">Know before you <span style={{color: 'var(--brand-red)'}}>trust.</span></span>
            </div>
          </Link>
          
          <nav className="nav-links flex gap-6 font-semibold text-sm">
            <Link to="/" className={`nav-link ${location.pathname === '/' ? 'active' : ''}`}>Dashboard</Link>
            <Link to="/history" className={`nav-link ${location.pathname === '/history' ? 'active' : ''}`}>History</Link>
            <Link to="/about" className={`nav-link ${location.pathname === '/about' ? 'active' : ''}`}>About</Link>
          </nav>

          <div className="header-actions flex items-center gap-4">
            <button className="theme-toggle">
              <Sun size={20} />
            </button>
            <Link to="/login" className="flex items-center gap-2 text-sm font-semibold text-brand-blue hover:underline">
              <LogIn size={18} />
              <span>Sign In</span>
            </Link>
          </div>
        </div>
      </header>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
