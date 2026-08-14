import React, { useState, useEffect } from 'react';
import { BookOpen, Code, Mail, ShieldAlert, X } from 'lucide-react';
import api from '../utils/api';
import HowItWorks from './HowItWorks';
import './Footer.css';

const Footer = () => {
  const [isOnline, setIsOnline] = useState(true);
  const [showHowItWorks, setShowHowItWorks] = useState(false);

  useEffect(() => {
    const checkHealth = async () => {
      try {
        await api.get('/health');
        setIsOnline(true);
      } catch (err) {
        setIsOnline(false);
      }
    };
    checkHealth();

    // Poll every 60 seconds
    const interval = setInterval(checkHealth, 60000);
    return () => clearInterval(interval);
  }, []);

  return (
    <>
      <footer className="footer">
        <div className="footer-container container">
          <div className="footer-brand">
            <img src="/image.png" alt="Vigil Logo" className="logo-img logo-light" style={{ height: '32px' }} />
            <img src="/dark.png" alt="Vigil Logo" className="logo-img logo-dark" style={{ height: '32px' }} />
            <span className="footer-tagline">Know before you <strong>trust.</strong></span>
          </div>

          <div className="footer-links">
            {/* <div className="system-status" title={isOnline ? "System Online" : "System Offline"}>
              <span className={`status-dot ${isOnline ? 'online' : 'offline'}`}></span>
              {isOnline ? 'Online' : 'Degraded'}
            </div> */}
            <a href="#" className="footer-link"><BookOpen size={16} /> Documentation</a>
            <button className="footer-link" onClick={() => setShowHowItWorks(true)} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', background: 'none', border: 'none', cursor: 'pointer', fontFamily: 'inherit', fontSize: 'inherit', color: 'inherit' }}><ShieldAlert size={16} /> How Vigil works</button>
            <a href="#" className="footer-link"><Code size={16} /> GitHub</a>
            {/* <a href="#" className="footer-link"><Mail size={16} /> Contact</a> */}
          </div>

          <div className="footer-copy">
            Vigil
          </div>
        </div>
      </footer>

      {showHowItWorks && (
        <div className="modal-overlay" onClick={() => setShowHowItWorks(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <button className="modal-close" onClick={() => setShowHowItWorks(false)}>
              <X size={24} />
            </button>
            <HowItWorks />
          </div>
        </div>
      )}
    </>
  );
};

export default Footer;
