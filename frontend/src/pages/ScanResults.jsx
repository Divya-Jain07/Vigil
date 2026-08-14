import React, { useState, useEffect } from 'react';
import { useParams, useLocation, Link, useNavigate } from 'react-router-dom';
import { 
  AlertTriangle, CheckCircle, ShieldAlert, FileText, Mail, 
  Link2, ArrowLeft, Loader2, Info, Copy, Sparkles, Code, Globe, Lock, Search, ChevronDown, Flag
} from 'lucide-react';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import api from '../utils/api';
import { useAuth } from '../context/AuthContext';
import './ScanResults.css';

const ScanResults = () => {
  const { id } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useAuth();
  
  const [scan, setScan] = useState(location.state?.scanResult || null);
  const [loading, setLoading] = useState(!scan);
  const [error, setError] = useState('');
  const [copied, setCopied] = useState(false);
  const [expandedIndicators, setExpandedIndicators] = useState({});
  const [activeFilter, setActiveFilter] = useState('All');

  useEffect(() => {
    if (scan && scan.id === id) {
      setLoading(false);
      return;
    }

    const fetchScan = async () => {
      try {
        setLoading(true);
        const response = await api.get(`/scans/${id}`);
        setScan(response.data);
      } catch (err) {
        if (err.response?.status === 401 || err.response?.status === 403) {
          setError('Authentication required to view this past scan. Please log in.');
        } else {
          setError('Scan not found or an error occurred while fetching the results.');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchScan();
  }, [id, scan, user]);

  const copyToClipboard = () => {
    navigator.clipboard.writeText(id);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const toggleIndicator = (index) => {
    setExpandedIndicators(prev => ({
      ...prev,
      [index]: !prev[index]
    }));
  };

  if (loading) {
    return (
      <div className="dashboard-layout">
        <Navbar />
        <div className="dashboard-main container loading-state">
          <Loader2 size={48} className="spinner primary-icon" />
          <p>Loading scan results...</p>
        </div>
        <Footer />
      </div>
    );
  }

  if (error || !scan) {
    return (
      <div className="dashboard-layout">
        <Navbar />
        <div className="dashboard-main container error-state">
          <ShieldAlert size={64} className="error-icon" />
          <h2>Cannot load results</h2>
          <p>{error || 'Scan not found.'}</p>
          {!user && error.includes('log in') ? (
            <Link to="/login" className="btn-primary" style={{ marginTop: '1rem', display: 'inline-flex' }}>Log in</Link>
          ) : (
            <Link to="/" className="btn-primary" style={{ marginTop: '1rem', display: 'inline-flex' }}>Return to Dashboard</Link>
          )}
        </div>
        <Footer />
      </div>
    );
  }

  // Formatting scan date
  const scanDate = scan.createdAt ? new Date(scan.createdAt) : new Date();
  const formattedDate = scanDate.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  const formattedTime = scanDate.toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit', hour12: true });

  // Data extraction and derivation
  const severityStr = scan.severity || scan.threatScore?.severity || 'UNKNOWN';
  const scoreValue = scan.score ?? scan.threatScore?.score ?? 0;
  
  const isSafe = severityStr === 'SAFE' || severityStr === 'LOW';
  const isRisk = severityStr === 'CRITICAL' || severityStr === 'HIGH';
  const isMedium = severityStr === 'MEDIUM';

  const indicators = scan.indicators || [];
  
  // Filtering
  const highIndicators = indicators.filter(i => i.severity === 'CRITICAL' || i.severity === 'HIGH');
  const mediumIndicators = indicators.filter(i => i.severity === 'MEDIUM');
  const lowIndicators = indicators.filter(i => i.severity === 'LOW');
  
  let filteredIndicators = indicators;
  if (activeFilter === 'High') filteredIndicators = highIndicators;
  if (activeFilter === 'Medium') filteredIndicators = mediumIndicators;
  if (activeFilter === 'Low') filteredIndicators = lowIndicators;

  // Deriving "Why?" and "Recommended Action"
  const derivedWhy = indicators.length > 0 
    ? indicators.slice(0, 2).map(i => i.message).join(' ')
    : 'No specific suspicious patterns were detected in this target.';
    
  let recommendedAction = 'Exercise standard caution when interacting with digital content.';
  if (isRisk) {
    recommendedAction = 'Do not click on any links or provide any personal information. Delete this file or email immediately if you did not expect to receive it.';
  } else if (isMedium) {
    recommendedAction = 'Proceed with caution. Verify the sender or source before trusting any embedded links or attachments.';
  } else if (isSafe) {
    recommendedAction = 'This target appears safe, but always remain vigilant and verify unexpected requests.';
  }

  // Theming based on severity
  const getSeverityStyles = (sev) => {
    if (sev === 'CRITICAL' || sev === 'HIGH') return { color: 'var(--color-risk)', bg: 'rgba(231,109,140,0.1)' };
    if (sev === 'MEDIUM') return { color: 'var(--color-warning, #f5a623)', bg: 'rgba(245,166,35,0.1)' };
    return { color: 'var(--color-safe)', bg: 'rgba(32,178,170,0.1)' };
  };

  const mainStyles = getSeverityStyles(severityStr);

  const getIndicatorIcon = (type) => {
    const t = type.toLowerCase();
    if (t.includes('link') || t.includes('url')) return <Link2 size={18} />;
    if (t.includes('javascript') || t.includes('script') || t.includes('code')) return <Code size={18} />;
    if (t.includes('ip') || t.includes('domain') || t.includes('reputation')) return <Globe size={18} />;
    if (t.includes('ssl') || t.includes('cert')) return <Lock size={18} />;
    if (t.includes('malware') || t.includes('phishing')) return <ShieldAlert size={18} />;
    return <Search size={18} />;
  };

  const getTargetTitle = () => {
    if (scan.url && scan.url.startsWith('PDF:')) return scan.url;
    if (scan.fileName) return `PDF: ${scan.fileName}`;
    if (scan.url && scan.url.startsWith('Email:')) return scan.url;
    if (scan.emailSubject) return `Email: ${scan.emailSubject}`;
    if (scan.url) return `URL: ${scan.url}`;
    return 'Unknown Target';
  };
  
  const targetType = getTargetTitle().split(':')[0];

  return (
    <div className="dashboard-layout">
      <Navbar />
      <main className="scan-results-main container">
        
        {/* Header Section */}
        <div className="sr-header-top">
          <button onClick={() => navigate('/')} className="sr-back-btn">
            <ArrowLeft size={16} /> Back to Dashboard
          </button>
        </div>

        <div className="sr-title-section">
          <div className="sr-status-badge">
            <CheckCircle size={16} /> ANALYSIS COMPLETE
          </div>
          <h1>{getTargetTitle()}</h1>
          
          <div className="sr-meta-row">
            <div className="sr-meta-left">
              <span className="sr-id">Scan ID: {id}</span>
              <button onClick={copyToClipboard} className="sr-copy-btn" title="Copy ID">
                {copied ? <CheckCircle size={14} color="var(--color-safe)" /> : <Copy size={14} />}
              </button>
              <span className="sr-divider">|</span>
              <span className="sr-status-tag">Status: <span className="status-completed">COMPLETED</span></span>
            </div>
            <div className="sr-meta-right">
              <span className="sr-timestamp">
                <Search size={14} /> Scanned at: {formattedDate} &middot; {formattedTime}
              </span>
            </div>
          </div>
        </div>

        {/* Main Grid Layout */}
        <div className="sr-grid">
          
          {/* LEFT COLUMN */}
          <div className="sr-col-left">
            
            {/* Score Card */}
            <div className="sr-card score-card-large">
              <div className="score-card-content">
                <div className="score-donut-wrapper">
                  <div className="score-donut" style={{ '--score': scoreValue, '--color': mainStyles.color }}>
                    <div className="donut-inner">
                      <span className="donut-value" style={{ color: mainStyles.color }}>{scoreValue}</span>
                      <span className="donut-max">/ 100</span>
                    </div>
                  </div>
                </div>
                <div className="score-details">
                  <div className="severity-badge" style={{ backgroundColor: mainStyles.bg, color: mainStyles.color }}>
                    <ShieldAlert size={16} /> {severityStr}
                  </div>
                  <h2>{severityStr === 'CRITICAL' ? 'Critical Risk' : severityStr === 'HIGH' ? 'High Risk' : severityStr === 'MEDIUM' ? 'Medium Risk' : 'Safe to Trust'}</h2>
                  <p>{isRisk ? 'This file is extremely dangerous and should not be trusted.' : isMedium ? 'This target exhibits suspicious traits and should be handled with caution.' : 'This target appears safe based on our analysis.'}</p>
                </div>
              </div>
              <div className="score-card-footer">
                <FileText size={16} /> <strong>{indicators.length}</strong> Findings Detected
              </div>
            </div>

            {/* Findings Card */}
            <div className="sr-card findings-card">
              <div className="findings-header">
                <h3>FINDINGS</h3>
                <div className="findings-filters">
                  <button className={`filter-btn ${activeFilter === 'All' ? 'active' : ''}`} onClick={() => setActiveFilter('All')}>All ({indicators.length})</button>
                  <button className={`filter-btn ${activeFilter === 'High' ? 'active' : ''}`} onClick={() => setActiveFilter('High')}>High ({highIndicators.length})</button>
                  <button className={`filter-btn ${activeFilter === 'Medium' ? 'active' : ''}`} onClick={() => setActiveFilter('Medium')}>Medium ({mediumIndicators.length})</button>
                  <button className={`filter-btn ${activeFilter === 'Low' ? 'active' : ''}`} onClick={() => setActiveFilter('Low')}>Low ({lowIndicators.length})</button>
                </div>
              </div>

              <div className="findings-list">
                {filteredIndicators.length > 0 ? (
                  filteredIndicators.map((ind, idx) => {
                    const indStyle = getSeverityStyles(ind.severity || 'LOW');
                    const isExpanded = expandedIndicators[idx];
                    
                    return (
                      <div key={idx} className={`finding-row ${isExpanded ? 'expanded' : ''}`} style={{ borderLeftColor: indStyle.color }}>
                        <div className="finding-header" onClick={() => toggleIndicator(idx)}>
                          <div className="finding-icon" style={{ color: indStyle.color, backgroundColor: indStyle.bg }}>
                            {getIndicatorIcon(ind.type)}
                          </div>
                          <div className="finding-title-area">
                            <div className="finding-badges">
                              <span className="finding-severity" style={{ color: indStyle.color, backgroundColor: indStyle.bg }}>
                                {ind.severity || 'LOW'}
                              </span>
                              <span className="finding-type">{ind.type}</span>
                            </div>
                            <span className="finding-score-mobile" style={{ color: indStyle.color }}>+{ind.score || 0}</span>
                          </div>
                          
                          <div className="finding-actions">
                            <span className="finding-score" style={{ color: indStyle.color }}>+{ind.score || 0}</span>
                            <ChevronDown size={18} className={`expand-icon ${isExpanded ? 'rotated' : ''}`} />
                          </div>
                        </div>
                        
                        <div className="finding-body">
                          <p>{ind.message || ind.description}</p>
                          <div className="finding-source">
                            Source: {ind.source || 'Vigil Local Engine'}
                          </div>
                        </div>
                      </div>
                    );
                  })
                ) : (
                  <div className="no-findings">
                    No findings in this category.
                  </div>
                )}
              </div>
            </div>
            
          </div>

          {/* RIGHT COLUMN */}
          <div className="sr-col-right">
            
            {/* Vigil's Assessment */}
            <div className="sr-card assessment-card">
              <div className="card-header-with-icon">
                <Sparkles size={18} className="primary-icon" />
                <h3>VIGIL'S ASSESSMENT</h3>
              </div>
              
              <div className="assessment-sections">
                <div className="assessment-item">
                  <div className="assessment-icon">
                    <FileText size={20} />
                  </div>
                  <div className="assessment-content">
                    <h4>Summary</h4>
                    <p>{scan.explanation || 'No summary available for this scan.'}</p>
                  </div>
                </div>

                <div className="assessment-item">
                  <div className="assessment-icon">
                    <Search size={20} />
                  </div>
                  <div className="assessment-content">
                    <h4>Why?</h4>
                    <p>{derivedWhy}</p>
                  </div>
                </div>

                <div className="assessment-item">
                  <div className="assessment-icon">
                    <ShieldAlert size={20} />
                  </div>
                  <div className="assessment-content">
                    <h4>Recommended Action</h4>
                    <p>{recommendedAction}</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Analysis Details */}
            <div className="sr-card details-card">
              <div className="card-header-with-icon">
                <h3>ANALYSIS DETAILS</h3>
              </div>
              
              <div className="details-list">
                <div className="detail-row">
                  <div className="detail-label"><FileText size={16} /> Type</div>
                  <div className="detail-value">{targetType} Target</div>
                </div>
                <div className="detail-row">
                  <div className="detail-label"><CheckCircle size={16} /> Status</div>
                  <div className="detail-value"><span className="status-completed-badge">COMPLETED</span></div>
                </div>
                <div className="detail-row">
                  <div className="detail-label"><Search size={16} /> Scan Time</div>
                  <div className="detail-value">{formattedDate} &middot; {formattedTime}</div>
                </div>
                <div className="detail-row">
                  <div className="detail-label"><Flag size={16} /> Total Findings</div>
                  <div className="detail-value">{indicators.length}</div>
                </div>
                <div className="detail-row">
                  <div className="detail-label"><ShieldAlert size={16} /> Threat Score</div>
                  <div className="detail-value" style={{ color: mainStyles.color, fontWeight: '700' }}>{scoreValue} / 100</div>
                </div>
              </div>
            </div>

          </div>

        </div>
      </main>
      <Footer />
    </div>
  );
};

export default ScanResults;
