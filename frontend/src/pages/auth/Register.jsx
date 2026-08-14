import React, { useState } from 'react';
import { ShieldAlert, ArrowRight, Loader2, X } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import './Auth.css';

const Register = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { register, showRegisterModal, setShowRegisterModal, setShowLoginModal } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsSubmitting(true);

    const result = await register(email, password);
    if (result.success) {
      setShowRegisterModal(false);
    } else {
      setError(result.message);
      setIsSubmitting(false);
    }
  };

  const handleSwitchToLogin = (e) => {
    e.preventDefault();
    setShowRegisterModal(false);
    setShowLoginModal(true);
  };

  if (!showRegisterModal) return null;

  return (
    <div className="auth-page" onClick={() => setShowRegisterModal(false)}>
      <div className="auth-container" onClick={e => e.stopPropagation()} style={{ position: 'relative' }}>
        <button 
          onClick={() => setShowRegisterModal(false)}
          style={{ position: 'absolute', top: '-1rem', right: '-1rem', background: 'var(--color-surface)', border: '1px solid var(--color-border)', borderRadius: '50%', width: '36px', height: '36px', display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer', zIndex: 10, color: 'var(--color-text-secondary)' }}
        >
          <X size={20} />
        </button>
        
        <div className="auth-card">
          <h2>Create an account</h2>
          <p className="auth-subtitle">Save your scan history and results</p>

          {error && <div className="auth-error">{error}</div>}

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input 
                type="email" 
                id="email" 
                placeholder="name@company.com" 
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required 
              />
            </div>
            
            <div className="form-group">
              <label htmlFor="password">Password</label>
              <input 
                type="password" 
                id="password" 
                placeholder="Create a strong password" 
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required 
              />
            </div>

            <button type="submit" className="btn-primary auth-submit" disabled={isSubmitting}>
              {isSubmitting ? <Loader2 className="spinner" size={20} /> : 'Create Account'}
              {!isSubmitting && <ArrowRight size={18} />}
            </button>
          </form>

          <div className="auth-footer">
            Already have an account? <a href="#" onClick={handleSwitchToLogin}>Log in</a>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;
