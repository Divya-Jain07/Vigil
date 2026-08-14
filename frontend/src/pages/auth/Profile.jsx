import React, { useState } from 'react';
import { Navigate } from 'react-router-dom';
import { User, KeyRound, Loader2, LogOut, CheckCircle, ShieldAlert } from 'lucide-react';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import api from '../../utils/api';
import { useAuth } from '../../context/AuthContext';
import './Profile.css';

const Profile = () => {
  const { user, loading: authLoading, logout } = useAuth();
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  // If not authenticated, redirect to login
  if (!authLoading && !user) {
    return <Navigate to="/login" replace />;
  }

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    if (!currentPassword || !newPassword) return;

    if (newPassword.length < 8) {
      setMessage({ type: 'error', text: 'New password must be at least 8 characters long.' });
      return;
    }

    setIsSubmitting(true);
    setMessage({ type: '', text: '' });

    try {
      const response = await api.put('/auth/change-password', {
        currentPassword,
        newPassword
      });

      if (response.data.success) {
        setMessage({ type: 'success', text: 'Password successfully updated.' });
        setCurrentPassword('');
        setNewPassword('');
      }
    } catch (error) {
      setMessage({ 
        type: 'error', 
        text: error.response?.data?.message || 'Failed to change password. Please check your current password.' 
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (authLoading || !user) {
    return (
      <div className="dashboard-layout">
        <Navbar />
        <main className="dashboard-main container" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
          <Loader2 size={48} className="spinner primary-icon" />
        </main>
        <Footer />
      </div>
    );
  }

  return (
    <div className="dashboard-layout">
      <Navbar />
      <main className="dashboard-main container">
        <div className="profile-header">
          <h1>Account Settings</h1>
          <p>Manage your account preferences and security.</p>
        </div>

        <div className="profile-grid">
          {/* User Info Card */}
          <div className="profile-card">
            <div className="profile-avatar-large">
              {user.name ? user.name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() : 'U'}
            </div>
            <div className="profile-details">
              <h2>{user.name}</h2>
              <p className="profile-email"><User size={16} /> {user.email}</p>
            </div>
            <button className="btn-outline logout-btn" onClick={logout}>
              <LogOut size={18} />
              Log Out
            </button>
          </div>

          {/* Change Password Card */}
          <div className="profile-card settings-card">
            <div className="settings-header">
              <KeyRound size={24} className="primary-icon" />
              <h2>Change Password</h2>
            </div>
            <p className="settings-desc">Update your password to keep your account secure.</p>

            {message.text && (
              <div className={`settings-message ${message.type}`}>
                {message.type === 'success' ? <CheckCircle size={20} /> : <ShieldAlert size={20} />}
                {message.text}
              </div>
            )}

            <form onSubmit={handlePasswordChange} className="settings-form">
              <div className="form-group">
                <label htmlFor="currentPassword">Current Password</label>
                <input 
                  type="password" 
                  id="currentPassword" 
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                  required 
                  disabled={isSubmitting}
                />
              </div>
              <div className="form-group">
                <label htmlFor="newPassword">New Password</label>
                <input 
                  type="password" 
                  id="newPassword" 
                  placeholder="At least 8 characters"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  required 
                  disabled={isSubmitting}
                  minLength={8}
                />
              </div>
              <button type="submit" className="btn-primary" disabled={isSubmitting} style={{ marginTop: '0.5rem', alignSelf: 'flex-start' }}>
                {isSubmitting ? <Loader2 size={18} className="spinner" /> : 'Update Password'}
              </button>
            </form>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default Profile;
