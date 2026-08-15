import React, { useState, useEffect, useRef } from 'react';
import { Sun, Moon, ChevronDown, LogOut, User } from 'lucide-react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

const Navbar = () => {
  const { user, logout, setShowLoginModal, setShowRegisterModal } = useAuth();
  const [theme, setTheme] = useState(localStorage.getItem('vigil_theme') || 'light');
  const [showDropdown, setShowDropdown] = useState(false);
  const dropdownRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('vigil_theme', theme);
  }, [theme]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const toggleTheme = () => {
    setTheme(prev => prev === 'light' ? 'dark' : 'light');
  };

  const handleLogout = () => {
    logout();
    setShowDropdown(false);
    navigate('/');
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
            <div className="user-profile-container" ref={dropdownRef} style={{ position: 'relative' }}>
              <button 
                className="user-profile" 
                onClick={() => setShowDropdown(!showDropdown)}
                style={{ background: 'none', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.5rem', fontFamily: 'inherit' }}
              >
                <div className="avatar">
                  {user.name ? user.name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() : 'U'}
                </div>
                <span className="user-name" style={{ color: 'var(--color-text-main)', fontWeight: 500 }}>{user.name}</span>
                <ChevronDown size={16} style={{ color: 'var(--color-text-secondary)' }} />
              </button>
              
              {showDropdown && (
                <div className="profile-dropdown" style={{
                  position: 'absolute',
                  top: '100%',
                  right: 0,
                  marginTop: '0.5rem',
                  background: 'var(--color-surface)',
                  border: '1px solid var(--color-border)',
                  borderRadius: 'var(--border-radius-md)',
                  boxShadow: 'var(--shadow-md)',
                  minWidth: '160px',
                  zIndex: 100,
                  display: 'flex',
                  flexDirection: 'column',
                  overflow: 'hidden'
                }}>
                  <Link 
                    to="/profile"
                    onClick={() => setShowDropdown(false)}
                    style={{ 
                      padding: '0.75rem 1rem', 
                      color: 'var(--color-text-main)', 
                      textDecoration: 'none',
                      fontSize: '0.9rem', 
                      width: '100%',
                      display: 'flex',
                      alignItems: 'center',
                      gap: '0.5rem',
                      borderBottom: '1px solid var(--color-border)'
                    }}
                    onMouseOver={(e) => e.currentTarget.style.backgroundColor = 'var(--color-neutral-light)'}
                    onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                  >
                    <User size={16} /> Profile & Settings
                  </Link>
                  <button 
                    onClick={handleLogout}
                    style={{ 
                      padding: '0.75rem 1rem', 
                      color: 'var(--color-risk)', 
                      background: 'none', 
                      border: 'none', 
                      cursor: 'pointer', 
                      textAlign: 'left', 
                      fontSize: '0.9rem', 
                      width: '100%',
                      display: 'flex',
                      alignItems: 'center',
                      gap: '0.5rem',
                      fontFamily: 'inherit'
                    }}
                    onMouseOver={(e) => e.currentTarget.style.backgroundColor = 'var(--color-neutral-light)'}
                    onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                  >
                    <LogOut size={16} /> Log out
                  </button>
                </div>
              )}
            </div>
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
