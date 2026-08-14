import React, { useState, useEffect } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { Clock, ShieldAlert, ArrowRight, Loader2, Link2, FileText, Mail, ChevronRight } from 'lucide-react';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import api from '../utils/api';
import { useAuth } from '../context/AuthContext';
import './History.css';

const History = () => {
  const { user, loading: authLoading, setShowLoginModal, setShowRegisterModal } = useAuth();
  const [scans, setScans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (authLoading) return;

    if (!user) {
      setLoading(false);
      return;
    }

    const fetchHistory = async () => {
      try {
        setLoading(true);
        const response = await api.get('/scans');
        setScans(response.data || []);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to fetch scan history.');
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, [user, authLoading]);

  // Removed redirect, we'll handle !user in the render

  const renderTarget = (scan) => {
    if (scan.url) return { icon: <Link2 size={16} />, text: scan.url, type: 'URL' };
    if (scan.fileName) return { icon: <FileText size={16} />, text: scan.fileName, type: 'Document' };
    if (scan.emailSubject) return { icon: <Mail size={16} />, text: scan.emailSubject, type: 'Email' };
    return { icon: <ShieldAlert size={16} />, text: 'Unknown', type: 'Unknown' };
  };

  const getStatusColor = (scan) => {
    const isSafe = scan.severity === 'SAFE' || scan.threatScore?.severity === 'SAFE';
    const isRisk = scan.severity === 'CRITICAL' || scan.severity === 'HIGH' || scan.threatScore?.severity === 'CRITICAL' || scan.threatScore?.severity === 'HIGH';
    return isRisk ? 'var(--color-risk)' : (isSafe ? 'var(--color-safe)' : 'var(--color-warning, #f5a623)');
  };

  return (
    <div className="dashboard-layout">
      <Navbar />
      <main className="dashboard-main container">
        
        <div className="history-header">
          <h1>Scan History</h1>
          <p>Review your past security verifications and threat analysis reports.</p>
        </div>

        {loading || authLoading ? (
          <div className="history-loading">
            <Loader2 size={48} className="spinner primary-icon" />
            <p>Loading your history...</p>
          </div>
        ) : !user ? (
          <div className="history-empty">
            <ShieldAlert size={48} className="empty-icon" style={{ color: 'var(--color-primary)' }} />
            <h2>Login Required</h2>
            <p>Your scan history is tied to your account for privacy. Log in or create a free account to view and save your past security scans.</p>
            <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem', justifyContent: 'center' }}>
              <button onClick={() => setShowLoginModal(true)} className="btn-primary-outline">Log in</button>
              <button onClick={() => setShowRegisterModal(true)} className="btn-primary">Create Account</button>
            </div>
          </div>
        ) : error ? (
          <div className="history-error">
            <ShieldAlert size={32} color="var(--color-risk)" />
            <p>{error}</p>
          </div>
        ) : scans.length === 0 ? (
          <div className="history-empty">
            <Clock size={48} className="empty-icon" />
            <h2>No scans found</h2>
            <p>You haven't run any security checks yet. Head back to the dashboard to scan a URL, Email, or PDF.</p>
            <Link to="/" className="btn-primary" style={{ marginTop: '1.5rem', display: 'inline-flex' }}>
              Run your first scan <ArrowRight size={18} />
            </Link>
          </div>
        ) : (
          <div className="history-list">
            <div className="history-list-header">
              <div>Target</div>
              <div>Date</div>
              <div>Type</div>
              <div>Result</div>
              <div></div>
            </div>
            
            {scans.map((scan) => {
              const targetInfo = renderTarget(scan);
              const statusColor = getStatusColor(scan);
              const severity = scan.severity || scan.threatScore?.severity || 'UNKNOWN';
              const date = new Date(scan.createdAt).toLocaleDateString(undefined, { 
                year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
              });

              return (
                <Link to={`/scan/${scan.id}`} key={scan.id} className="history-item">
                  <div className="history-target">
                    <span className="target-icon">{targetInfo.icon}</span>
                    <span className="target-text">{targetInfo.text}</span>
                  </div>
                  <div className="history-date">{date}</div>
                  <div className="history-type">
                    <span className="type-badge">{targetInfo.type}</span>
                  </div>
                  <div className="history-result">
                    <span className="result-badge" style={{ backgroundColor: `${statusColor}22`, color: statusColor }}>
                      {severity}
                    </span>
                  </div>
                  <div className="history-action">
                    <ChevronRight size={20} className="action-icon" />
                  </div>
                </Link>
              );
            })}
          </div>
        )}

      </main>
      <Footer />
    </div>
  );
};

export default History;
