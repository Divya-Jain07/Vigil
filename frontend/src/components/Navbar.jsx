import React, { useState, useEffect } from 'react';
import { Sun, Moon, ChevronDown } from 'lucide-react';
import { Link, NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

const Navbar = () => {
  const { user, logout, setShowLoginModal, setShowRegisterModal } = useAuth();
  const [theme, setTheme] = useState(localStorage.getItem('vigil_theme') || 'light');

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('vigil_theme', theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme(prev => prev === 'light' ? 'dark' : 'light');
  };

  return (
    <nav className="navbar">
      <div className="navbar-container container">
        <div className="navbar-left">
          <Link to="/" className="navbar-logo">
            <img src="/image.png" alt="Vigil Logo" className="logo-img logo-light" />
            <img src="/dark.png" alt="Vigil Logo" className="logo-img logo-dark" />
          </Link>
          <div className="navbar-links">
            <NavLink to="/" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>Dashboard</NavLink>
            <NavLink to="/history" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>History</NavLink>
            <NavLink to="/about" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>About</NavLink>
          </div>
        </div>
        <div className="navbar-right">
          <button className="icon-button" onClick={toggleTheme} title="Toggle dark mode">
            {theme === 'light' ? <Sun size={20} /> : <Moon size={20} />}
          </button>
          {user ? (
            <NavLink to="/profile" className="user-profile" title="View Profile & Settings" style={{ textDecoration: 'none' }}>
              <div className="avatar">
                {user.name ? user.name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() : 'U'}
              </div>
              <span className="user-name">{user.name}</span>
            </NavLink>
          ) : (
            <div className="auth-buttons">
              <button onClick={() => setShowLoginModal(true)} className="btn-nav-login">Log in</button>
              <button onClick={() => setShowRegisterModal(true)} className="btn-nav-signup">Sign up</button>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
