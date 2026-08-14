import React from 'react';
import { Link } from 'react-router-dom';
import Navbar from '../components/Navbar';
import VerificationPanel from '../components/VerificationPanel';
import Footer from '../components/Footer';
import { useAuth } from '../context/AuthContext';
import './Dashboard.css';

const Dashboard = () => {
  const { user, setShowLoginModal, setShowRegisterModal } = useAuth();

  return (
    <div className="dashboard-layout">
      <Navbar />
      
      <main className="dashboard-main container">
        <header className="dashboard-header">
          <h1>What do you want to verify?</h1>
          <p>Check a link, email or document before you interact with it.</p>
        </header>

        <div className="dashboard-grid">
          <div className="dashboard-left">
            <VerificationPanel />
            {!user && (
              <div className="account-prompt">
                <div className="prompt-icon">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                </div>
                <div className="prompt-content">
                  <h3>Scan without an account</h3>
                  <p>Run scans instantly. Create an account to save your scan history and access detailed reports anytime.</p>
                </div>
                <div className="prompt-actions">
                  <button onClick={() => setShowRegisterModal(true)} className="btn-primary-outline">Create Free Account</button>
                  <button onClick={() => setShowLoginModal(true)} className="btn-outline">Log in</button>
                </div>
              </div>
            )}
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
};

export default Dashboard;
