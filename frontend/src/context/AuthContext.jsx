import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../utils/api';

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showRegisterModal, setShowRegisterModal] = useState(false);

  useEffect(() => {
    // Check if token and user data exist in local storage on mount
    const token = localStorage.getItem('vigil_token');
    const storedUser = localStorage.getItem('vigil_user');

    if (token && storedUser) {
      try {
        setUser(JSON.parse(storedUser));
        window.postMessage({ type: 'VIGIL_AUTH_UPDATE', token: token }, '*');
      } catch (e) {
        console.error('Failed to parse user from local storage', e);
        logout();
      }
    } else {
      window.postMessage({ type: 'VIGIL_AUTH_UPDATE', token: null }, '*');
    }
    
    setLoading(false);

    // Listen for unauthorized events from axios interceptor
    const handleUnauthorized = () => {
      setUser(null);
    };
    
    window.addEventListener('unauthorized', handleUnauthorized);
    return () => window.removeEventListener('unauthorized', handleUnauthorized);
  }, []);

  const login = async (email, password) => {
    try {
      const response = await api.post('/auth/login', { email, password });
      if (response.data.success && response.data.token) {
        const token = response.data.token;
        // The backend auth doesn't currently return the user's name, so we use email
        const userData = { email, name: email.split('@')[0] };
        
        localStorage.setItem('vigil_token', token);
        localStorage.setItem('vigil_user', JSON.stringify(userData));
        
        setUser(userData);
        window.postMessage({ type: 'VIGIL_AUTH_UPDATE', token: token }, '*');
        return { success: true };
      }
      return { success: false, message: response.data.message || 'Login failed' };
    } catch (error) {
      return { 
        success: false, 
        message: error.response?.data?.message || 'An error occurred during login' 
      };
    }
  };

  const register = async (email, password) => {
    try {
      const response = await api.post('/auth/register', { email, password });
      if (response.data.success) {
        // Automatically login after successful registration if token is provided,
        // otherwise require manual login. (The current backend returns token on register).
        if (response.data.token) {
           const token = response.data.token;
           const userData = { email, name: email.split('@')[0] };
           localStorage.setItem('vigil_token', token);
           localStorage.setItem('vigil_user', JSON.stringify(userData));
           setUser(userData);
           window.postMessage({ type: 'VIGIL_AUTH_UPDATE', token: token }, '*');
        }
        return { success: true };
      }
      return { success: false, message: response.data.message || 'Registration failed' };
    } catch (error) {
      return { 
        success: false, 
        message: error.response?.data?.message || 'An error occurred during registration' 
      };
    }
  };

  const logout = () => {
    localStorage.removeItem('vigil_token');
    localStorage.removeItem('vigil_user');
    setUser(null);
    window.postMessage({ type: 'VIGIL_AUTH_UPDATE', token: null }, '*');
  };

  return (
    <AuthContext.Provider value={{ 
      user, login, register, logout, loading,
      showLoginModal, setShowLoginModal,
      showRegisterModal, setShowRegisterModal
    }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};
