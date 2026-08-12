import { Link } from 'react-router-dom';
import { 
  ArrowLeft, Download, Globe, AlertTriangle, 
  Lock, Search, ShieldAlert, CheckCircle, 
  Info, Shield, Copy, X, Activity, Clock
} from 'lucide-react';
import Card from '../components/common/Card';
import Badge from '../components/common/Badge';
import Button from '../components/common/Button';
import './Analysis.css';

export default function Analysis() {
  return (
    <div className="analysis-page container">
      {/* Top Navigation */}
      <div className="analysis-nav flex items-center justify-between mb-6">
        <Link to="/" className="text-brand-blue font-semibold flex items-center gap-1 hover:underline">
          <ArrowLeft size={16} /> Back to Dashboard
        </Link>
        <div className="scan-type-toggle flex items-center gap-2">
          <span className="text-sm font-semibold text-secondary mr-2">Scan Type:</span>
          <div className="toggle-group">
            <button className="toggle-btn active">URL</button>
            <button className="toggle-btn">Email</button>
            <button className="toggle-btn">PDF</button>
          </div>
        </div>
        <div style={{width: 130}}></div> {/* Spacer for centering */}
      </div>

      {/* Main Header */}
      <div className="analysis-header flex items-center justify-between mb-8">
        <div className="flex items-center gap-4">
          <div className="header-icon-wrapper bg-blue-subtle text-brand-blue">
            <Globe size={32} />
          </div>
          <div>
            <h1 className="text-3xl font-bold flex items-center gap-2">
              http://login-verify.tk <Copy size={16} className="text-tertiary cursor-pointer hover:text-primary" />
            </h1>
            <div className="text-sm text-secondary flex items-center gap-3 mt-1">
              <span>Scanned on May 15, 2025 at 11:42 AM</span>
              <span className="bullet">&bull;</span>
              <span>ID: 6a7bec58bccb1f0dd785fafa</span>
            </div>
          </div>
        </div>
        <Button variant="outline" icon={Download}>
          Download Report
        </Button>
      </div>

      <div className="analysis-content">
        <div className="analysis-main flex-col gap-6">
          {/* AI Explanation */}
          <Card className="ai-explanation">
            <div className="flex items-center gap-2 mb-4 font-bold text-purple">
              <span className="sparkles">✨</span> AI Explanation
            </div>
            <div className="ai-box">
              <div className="ai-row"><span className="font-bold">SUMMARY:</span> This URL has a CRITICAL risk level with a score of 100/100.</div>
              <div className="ai-row mt-2"><span className="font-bold">WHY:</span> 3 antivirus engines flagged this URL as malicious.</div>
              <div className="ai-row mt-2"><span className="font-bold">ACTION:</span> Do not trust this URL under any circumstances.</div>
            </div>
          </Card>

          {/* Detected Indicators */}
          <Card>
            <div className="flex items-center gap-2 mb-6 font-bold text-lg">
              <AlertTriangle size={20} className="text-critical" /> Detected Indicators <Info size={16} className="text-tertiary ml-1" />
            </div>
            
            <div className="indicators-list flex-col gap-4">
              {/* Indicator 1 */}
              <div className="indicator-item flex items-start gap-4">
                <div className="indicator-icon bg-critical-subtle text-critical">
                  <Lock size={20} />
                </div>
                <div className="indicator-body flex-1">
                  <div className="flex items-center gap-3 mb-1">
                    <span className="font-bold">INSECURE PROTOCOL</span>
                    <Badge variant="low">LOW</Badge>
                  </div>
                  <div className="text-sm text-secondary">The URL uses an unencrypted HTTP connection.</div>
                </div>
                <div className="indicator-meta text-right">
                  <div className="text-xs text-secondary mb-1">Source</div>
                  <Badge variant="default" className="source-badge">Local</Badge>
                </div>
                <div className="indicator-points text-critical font-bold text-lg">
                  +20 <span className="text-xs font-normal">points</span>
                </div>
              </div>

              <hr className="divider" />

              {/* Indicator 2 */}
              <div className="indicator-item flex items-start gap-4">
                <div className="indicator-icon bg-medium-subtle text-medium">
                  <Search size={20} />
                </div>
                <div className="indicator-body flex-1">
                  <div className="flex items-center gap-3 mb-1">
                    <span className="font-bold">SUSPICIOUS KEYWORDS IN DOMAIN</span>
                    <Badge variant="medium">MEDIUM</Badge>
                  </div>
                  <div className="text-sm text-secondary">The domain contains suspicious keywords often used in phishing sites: login, verify. Legitimate brands do not embed these words in their domain.</div>
                </div>
                <div className="indicator-meta text-right">
                  <div className="text-xs text-secondary mb-1">Source</div>
                  <Badge variant="default" className="source-badge">Local</Badge>
                </div>
                <div className="indicator-points text-medium font-bold text-lg">
                  +20 <span className="text-xs font-normal">points</span>
                </div>
              </div>

              <hr className="divider" />

              {/* Indicator 3 */}
              <div className="indicator-item flex items-start gap-4">
                <div className="indicator-icon bg-medium-subtle text-medium">
                  <Globe size={20} />
                </div>
                <div className="indicator-body flex-1">
                  <div className="flex items-center gap-3 mb-1">
                    <span className="font-bold">SUSPICIOUS TLD</span>
                    <Badge variant="medium">MEDIUM</Badge>
                  </div>
                  <div className="text-sm text-secondary">The URL uses a Top-Level Domain (TLD) commonly associated with malicious activity.</div>
                </div>
                <div className="indicator-meta text-right">
                  <div className="text-xs text-secondary mb-1">Source</div>
                  <Badge variant="default" className="source-badge">Local</Badge>
                </div>
                <div className="indicator-points text-medium font-bold text-lg">
                  +30 <span className="text-xs font-normal">points</span>
                </div>
              </div>

              <hr className="divider" />

              {/* Indicator 4 */}
              <div className="indicator-item flex items-start gap-4">
                <div className="indicator-icon bg-critical-subtle text-critical">
                  <ShieldAlert size={20} />
                </div>
                <div className="indicator-body flex-1">
                  <div className="flex items-center gap-3 mb-1">
                    <span className="font-bold">VIRUSTOTAL DETECTION</span>
                    <Badge variant="high">HIGH</Badge>
                  </div>
                  <div className="text-sm text-secondary">3 antivirus engines flagged this URL as malicious.</div>
                </div>
                <div className="indicator-meta text-right">
                  <div className="text-xs text-secondary mb-1">Source</div>
                  <Badge variant="default" className="source-badge text-green border-green bg-green-subtle">VirusTotal</Badge>
                </div>
                <div className="indicator-points text-critical font-bold text-lg">
                  +40 <span className="text-xs font-normal">points</span>
                </div>
              </div>
            </div>

            <div className="indicators-footer mt-6 pt-4 border-t flex justify-between items-center text-sm">
              <span className="text-secondary">Total Score Calculation: Sum of indicator scores (capped at 100)</span>
              <span className="font-bold">100 / 100</span>
            </div>
          </Card>

          <div className="flex gap-6 mt-2">
            <Card className="flex-1 bg-subtle">
              <div className="flex items-center gap-2 mb-3 font-bold text-brand-blue">
                <Info size={20} /> What This Means
              </div>
              <p className="text-sm text-secondary mb-3">
                This URL exhibits multiple characteristics commonly found in malicious websites, including the use of suspicious keywords, an uncommon TLD, and detections from security vendors.
              </p>
              <p className="text-sm font-bold">It is highly recommended to avoid this URL.</p>
            </Card>
            
            <Card className="flex-1 bg-subtle justify-center">
              <div className="flex items-center gap-2 mb-4 font-bold text-brand-blue">
                <Activity size={20} /> Understanding the Score
              </div>
              <div className="score-legend flex justify-between text-xs text-center">
                <div className="legend-item flex-col items-center gap-1 flex-1">
                  <div className="legend-bar bg-low"></div>
                  <div>0-29<br/>Safe</div>
                </div>
                <div className="legend-item flex-col items-center gap-1 flex-1">
                  <div className="legend-bar bg-medium"></div>
                  <div>30-59<br/>Suspicious</div>
                </div>
                <div className="legend-item flex-col items-center gap-1 flex-1">
                  <div className="legend-bar bg-high"></div>
                  <div>60-79<br/>High Risk</div>
                </div>
                <div className="legend-item flex-col items-center gap-1 flex-1 relative">
                  <div className="legend-bar bg-critical"></div>
                  <div>80-100<br/>Critical</div>
                  <div className="marker absolute text-black" style={{top: -4, right: 0}}>▼</div>
                </div>
              </div>
            </Card>
          </div>
        </div>

        {/* Sidebar */}
        <div className="analysis-sidebar flex-col gap-6">
          {/* Risk Score Card */}
          <Card>
            <div className="flex items-center gap-2 mb-4 font-bold text-sm uppercase text-secondary tracking-wider">
              VIGIL RISK SCORE <Info size={14} className="text-tertiary" />
            </div>
            
            <div className="score-display flex items-center gap-6 mb-6">
              <div className="score-ring critical">
                <svg viewBox="0 0 36 36" className="circular-chart">
                  <path className="circle-bg"
                    d="M18 2.0845
                      a 15.9155 15.9155 0 0 1 0 31.831
                      a 15.9155 15.9155 0 0 1 0 -31.831"
                  />
                  <path className="circle"
                    strokeDasharray="100, 100"
                    d="M18 2.0845
                      a 15.9155 15.9155 0 0 1 0 31.831
                      a 15.9155 15.9155 0 0 1 0 -31.831"
                  />
                </svg>
                <div className="score-text flex-col">
                  <span className="value text-4xl font-bold">100</span>
                  <span className="max text-sm text-secondary">/100</span>
                </div>
              </div>
              
              <div>
                <div className="text-critical font-bold text-xl mb-1">CRITICAL</div>
                <div className="font-semibold text-sm mb-1">Extreme Risk Detected</div>
                <div className="text-xs text-secondary">This content is extremely suspicious and likely malicious.</div>
              </div>
            </div>

            <div className="bg-critical-subtle text-critical text-sm font-semibold p-2 rounded text-center border border-critical-light">
              <AlertTriangle size={14} className="inline mr-1" /> 4 indicators detected
            </div>
          </Card>

          {/* Scan Overview */}
          <Card>
            <div className="font-bold mb-4">Scan Overview</div>
            
            <div className="overview-list flex-col gap-4 text-sm">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2 text-secondary">
                  <Globe size={16} /> Scan Type
                </div>
                <div className="font-medium">URL Analysis</div>
              </div>
              
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2 text-secondary">
                  <Activity size={16} /> Status
                </div>
                <Badge variant="low" className="rounded-full px-3 text-[10px]">COMPLETED</Badge>
              </div>

              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2 text-secondary">
                  <FileText size={16} /> Scanned At
                </div>
                <div className="font-medium">May 15, 2025 at 11:42 AM</div>
              </div>

              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2 text-secondary">
                  <Clock size={16} /> Processing Time
                </div>
                <div className="font-medium">3.42 seconds</div>
              </div>

              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2 text-secondary">
                  <LinkIcon size={16} /> Scan ID
                </div>
                <div className="font-medium flex items-center gap-1">
                  6a7bec58bccb1... <Copy size={12} className="text-tertiary cursor-pointer" />
                </div>
              </div>
            </div>
          </Card>

          {/* Recommended Action */}
          <Card>
            <div className="flex items-center gap-2 mb-4 font-bold text-critical">
              <ShieldAlert size={20} /> Recommended Action
            </div>

            <div className="bg-critical-subtle border border-critical-light p-3 rounded text-critical text-sm font-bold flex gap-2 items-start mb-4">
              <AlertTriangle size={16} className="shrink-0 mt-0.5" />
              Do not trust this URL under any circumstances.
            </div>

            <ul className="text-sm text-secondary flex-col gap-3 ml-1">
              <li className="flex items-start gap-2">
                <X size={16} className="text-critical shrink-0 mt-0.5" /> Do not click any links on this page
              </li>
              <li className="flex items-start gap-2">
                <X size={16} className="text-critical shrink-0 mt-0.5" /> Do not enter any personal information
              </li>
              <li className="flex items-start gap-2">
                <X size={16} className="text-critical shrink-0 mt-0.5" /> Do not download any files
              </li>
              <li className="flex items-start gap-2">
                <X size={16} className="text-critical shrink-0 mt-0.5" /> If visited accidentally, close the page immediately
              </li>
              <li className="flex items-start gap-2">
                <X size={16} className="text-critical shrink-0 mt-0.5" /> Consider reporting this URL as malicious
              </li>
            </ul>
          </Card>
        </div>
      </div>
    </div>
  );
}
