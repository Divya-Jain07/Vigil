import React from 'react';
import { Sun, ChevronDown, ShieldAlert } from 'lucide-react';
import { Link, NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

const Navbar = () => {
  const { user, logout } = useAuth();

  return (
    <nav className="navbar">
      <div className="navbar-container container">
        <div className="navbar-left">
          <Link to="/" className="navbar-logo">
            <ShieldAlert className="logo-icon" size={28} />
            <span className="logo-text">VIGIL</span>
          </Link>
          <div className="navbar-links">
            <NavLink to="/" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>Dashboard</NavLink>
            <NavLink to="/history" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>History</NavLink>
            <NavLink to="/about" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>About</NavLink>
          </div>
        </div>
        <div className="navbar-right">
          <button className="icon-button">
            <Sun size={20} />
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
              <Link to="/login" className="btn-nav-login">Log in</Link>
              <Link to="/register" className="btn-nav-signup">Sign up</Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
