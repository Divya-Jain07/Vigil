import React, { useState, useRef } from 'react';
import { Link2, Mail, FileText, ArrowRight, Globe, Shield, Search, Lock, Loader2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import api from '../utils/api';
import './VerificationPanel.css';

const VerificationPanel = () => {
  const [activeTab, setActiveTab] = useState('url');
  const [url, setUrl] = useState('');
  const [isScanning, setIsScanning] = useState(false);
  const [error, setError] = useState('');
  const fileInputRef = useRef(null);
  const navigate = useNavigate();

  const handleUrlSubmit = async (e) => {
    e.preventDefault();
    if (!url) return;
    
    setIsScanning(true);
    setError('');
    
    try {
      const response = await api.post('/scans/url', { url });
      if (response.data.success) {
        navigate(`/scan/${response.data.data.id}`, { state: { scanResult: response.data.data } });
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to scan URL. Please try again.');
    } finally {
      setIsScanning(false);
    }
  };

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setIsScanning(true);
    setError('');

    try {
      const token = localStorage.getItem('vigil_token');
      const headers = {};
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
      
      let response;

      if (activeTab === 'email') {
        // The backend EmailScanController expects JSON, not a file upload
        // We need to read the .eml file text and extract basic fields
        const text = await file.text();
        
        // Very basic naive .eml parser for the purpose of passing to our backend
        const fromMatch = text.match(/^From:\s*(.*)$/im);
        const subjectMatch = text.match(/^Subject:\s*(.*)$/im);
        const replyToMatch = text.match(/^Reply-To:\s*(.*)$/im);
        
        const payload = {
          sender: fromMatch ? fromMatch[1].trim() : 'unknown@example.com',
          subject: subjectMatch ? subjectMatch[1].trim() : file.name,
          replyTo: replyToMatch ? replyToMatch[1].trim() : null,
          body: text // Send raw text so backend can extract URLs
        };

        response = await api.post('/scans/email', payload);
      } else {
        // PDF uses multipart/form-data
        const formData = new FormData();
        formData.append('file', file);
        response = await axios.post('http://localhost:8080/api/v1/scans/pdf', formData, { headers });
      }
      
      if (response.data.success) {
        navigate(`/scan/${response.data.data.id}`, { state: { scanResult: response.data.data } });
      }
    } catch (err) {
      setError(err.response?.data?.message || `Failed to scan ${activeTab.toUpperCase()}. Please try again.`);
    } finally {
      setIsScanning(false);
    }
  };

  const triggerFileInput = () => {
    fileInputRef.current?.click();
  };

  return (
    <div className="verification-panel">
      <div className="tabs">
        <button 
          className={`tab-btn ${activeTab === 'url' ? 'active' : ''}`}
          onClick={() => setActiveTab('url')}
        >
          <Link2 size={18} />
          <span>Check a URL</span>
        </button>
        <button 
          className={`tab-btn ${activeTab === 'email' ? 'active' : ''}`}
          onClick={() => setActiveTab('email')}
        >
          <Mail size={18} />
          <span>Check an Email (.eml)</span>
        </button>
        <button 
          className={`tab-btn ${activeTab === 'pdf' ? 'active' : ''}`}
          onClick={() => setActiveTab('pdf')}
        >
          <FileText size={18} />
          <span>Check a Document (PDF)</span>
        </button>
      </div>

      <div className="panel-content">
        {error && <div style={{ color: 'var(--color-risk)', marginBottom: '1rem', padding: '0.5rem', backgroundColor: 'rgba(231,109,140,0.1)', borderRadius: '4px', textAlign: 'center', fontSize: '0.9rem' }}>{error}</div>}
        
        {activeTab === 'url' && (
          <form className="tab-content" onSubmit={handleUrlSubmit}>
            <div className="content-header">
              <div className="icon-wrapper">
                <Link2 size={24} className="primary-icon" />
              </div>
              <div className="header-text">
                <h2>Check a URL</h2>
                <p>Enter any URL to analyze for potential threats and risks.</p>
              </div>
            </div>
            
            <div className="input-group">
              <div className="input-wrapper">
                <Link2 size={18} className="input-icon" />
                <input 
                  type="url" 
                  placeholder="https://example.com" 
                  className="url-input" 
                  value={url}
                  onChange={(e) => setUrl(e.target.value)}
                  required
                  disabled={isScanning}
                />
              </div>
              <button type="submit" className="btn-primary check-btn" disabled={isScanning}>
                {isScanning ? <Loader2 size={18} className="spinner" /> : 'Check with Vigil'}
                {!isScanning && <ArrowRight size={18} />}
              </button>
            </div>
          </form>
        )}

        {activeTab === 'email' && (
          <div className="tab-content">
            <div className="content-header">
              <div className="icon-wrapper">
                <Mail size={24} className="primary-icon" />
              </div>
              <div className="header-text">
                <h2>Check an Email</h2>
                <p>Upload an .eml file to scan for phishing attempts and malicious attachments.</p>
              </div>
            </div>
            
            <div className="upload-zone" onClick={triggerFileInput} style={{ cursor: 'pointer' }}>
              <input 
                type="file" 
                accept=".eml" 
                style={{ display: 'none' }} 
                ref={fileInputRef}
                onChange={handleFileUpload}
                disabled={isScanning}
              />
              {isScanning ? (
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '1rem' }}>
                  <Loader2 size={32} className="upload-icon spinner" style={{ color: 'var(--color-primary)' }} />
                  <p>Scanning Email...</p>
                </div>
              ) : (
                <>
                  <Mail size={32} className="upload-icon" />
                  <p>Click to browse or drag and drop</p>
                  <span className="upload-or">.eml files only</span>
                  <button type="button" className="btn-outline">Browse files</button>
                </>
              )}
            </div>
          </div>
        )}

        {activeTab === 'pdf' && (
          <div className="tab-content">
            <div className="content-header">
              <div className="icon-wrapper">
                <FileText size={24} className="primary-icon" />
              </div>
              <div className="header-text">
                <h2>Check a Document</h2>
                <p>Upload a PDF to detect hidden scripts, exploits, and anomalies.</p>
              </div>
            </div>
            
            <div className="upload-zone" onClick={triggerFileInput} style={{ cursor: 'pointer' }}>
              <input 
                type="file" 
                accept=".pdf" 
                style={{ display: 'none' }} 
                ref={fileInputRef}
                onChange={handleFileUpload}
                disabled={isScanning}
              />
              {isScanning ? (
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '1rem' }}>
                  <Loader2 size={32} className="upload-icon spinner" style={{ color: 'var(--color-primary)' }} />
                  <p>Scanning PDF...</p>
                </div>
              ) : (
                <>
                  <FileText size={32} className="upload-icon" />
                  <p>Click to browse or drag and drop</p>
                  <span className="upload-or">.pdf files only</span>
                  <button type="button" className="btn-outline">Browse files</button>
                </>
              )}
            </div>
          </div>
        )}
      </div>

      <div className="what-we-check">
        <h3>What we check</h3>
        <div className="check-items">
          <div className="check-item">
            <div className="check-icon"><Globe size={20} /></div>
            <div className="check-text">
              <h4>Reputation</h4>
              <p>Check domain and reputation data</p>
            </div>
          </div>
          <div className="check-divider"></div>
          <div className="check-item">
            <div className="check-icon"><Shield size={20} /></div>
            <div className="check-text">
              <h4>Threats</h4>
              <p>Look for known threats and malware</p>
            </div>
          </div>
          <div className="check-divider"></div>
          <div className="check-item">
            <div className="check-icon"><Search size={20} /></div>
            <div className="check-text">
              <h4>Content</h4>
              <p>Analyze content and suspicious patterns</p>
            </div>
          </div>
          <div className="check-divider"></div>
          <div className="check-item">
            <div className="check-icon"><Lock size={20} /></div>
            <div className="check-text">
              <h4>Security</h4>
              <p>Verify SSL/TLS and security configuration</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default VerificationPanel;
