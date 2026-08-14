import React, { useState, useEffect } from 'react';
import { BookOpen, Code, Mail, ShieldAlert } from 'lucide-react';
import api from '../utils/api';
import './Footer.css';

const Footer = () => {
  const [isOnline, setIsOnline] = useState(true);

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
    <footer className="footer">
      <div className="footer-container container">
        <div className="footer-brand">
          <ShieldAlert size={20} className="primary-icon" />
          <span className="footer-logo">VIGIL</span>
          <span className="footer-tagline">Know before you <strong>trust.</strong></span>
        </div>
        
        <div className="footer-links">
          <div className="system-status" title={isOnline ? "System Online" : "System Offline"}>
            <span className={`status-dot ${isOnline ? 'online' : 'offline'}`}></span>
            {isOnline ? 'Online' : 'Degraded'}
          </div>
          <a href="#" className="footer-link"><BookOpen size={16} /> Documentation</a>
          <a href="#" className="footer-link"><Code size={16} /> GitHub</a>
          <a href="#" className="footer-link"><Mail size={16} /> Contact</a>
        </div>
        
        <div className="footer-copy">
          &copy; {new Date().getFullYear()} Vigil. All rights reserved.
        </div>
      </div>
    </footer>
  );
};

export default Footer;
