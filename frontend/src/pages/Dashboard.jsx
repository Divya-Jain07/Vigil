import { Link } from 'react-router-dom';
import { Link as LinkIcon, Mail, FileText, Upload, ArrowRight, Shield, ShieldCheck, Lock, CheckCircle, HelpCircle, Target, BarChart2, Clock } from 'lucide-react';
import Card from '../components/common/Card';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import './Dashboard.css';

export default function Dashboard() {
  return (
    <div className="dashboard w-full px-4 md:px-8 lg:px-12">
      {/* Hero Section */}
      <div className="hero-section text-center">
        <h1 className="hero-title text-4xl font-bold">
          Know before you <span style={{color: 'var(--brand-red)'}}>trust.</span>
        </h1>
        <p className="hero-subtitle text-secondary text-lg">
          Analyze suspicious URLs, emails and documents<br />
          with multiple layers of security checks.
        </p>
      </div>

      <div className="info-section mb-6">
        <div className="grid-4">
          <div className="info-item flex items-center gap-4">
            <div className="icon-wrapper red">
              <Target size={28} />
            </div>
            <div>
              <div className="font-bold text-sm mb-1">Real-time Analysis</div>
              <div className="text-xs text-secondary leading-relaxed">Scan in seconds and get instant results</div>
            </div>
          </div>
          <div className="info-divider"></div>
          <div className="info-item flex items-center gap-4">
            <div className="icon-wrapper blue">
              <BarChart2 size={28} />
            </div>
            <div>
              <div className="font-bold text-sm mb-1">Detailed Reports</div>
              <div className="text-xs text-secondary leading-relaxed">Comprehensive breakdowns with actionable insights</div>
            </div>
          </div>
          <div className="info-divider"></div>
          <div className="info-item flex items-center gap-4">
            <div className="icon-wrapper green">
              <Clock size={28} />
            </div>
            <div>
              <div className="font-bold text-sm mb-1">Scan History</div>
              <div className="text-xs text-secondary leading-relaxed">Save and review your previous scans</div>
            </div>
          </div>
          <div className="info-divider"></div>
          <div className="info-item flex items-center gap-4">
            <div className="icon-wrapper purple">
              <Shield size={28} />
            </div>
            <div>
              <div className="font-bold text-sm mb-1">Privacy First</div>
              <div className="text-xs text-secondary leading-relaxed">Your data is encrypted and never stored</div>
            </div>
          </div>
        </div>
      </div>

      {/* Free Account Banner */}
      <Card className="signup-banner mb-12 flex justify-between items-center bg-white shadow-sm border border-light">
        <div className="flex items-center gap-5">
          <div className="icon-wrapper-large">
            <ShieldCheck size={28} className="text-brand-blue" />
          </div>
          <div>
            <h3 className="font-bold text-lg mb-1">Scan without an account</h3>
            <p className="text-sm text-secondary">Create a free account to save your scan history, manage reports and get notified about threats.</p>
          </div>
        </div>
        <Link to="/login">
          <Button variant="outline" className="text-blue border-blue hover-blue px-6 py-2.5">
            Create Free Account
          </Button>
        </Link>
      </Card>

      {/* Scanner Columns */}
      <div className="scanners-grid">
        {/* URL Scanner */}
        <Card variant="url" className="scanner-card">
          <div className="scanner-header flex items-center gap-3">
            <div className="scanner-icon icon-url">
              <LinkIcon size={24} />
            </div>
            <div>
              <h2 className="font-bold text-lg" style={{color: 'var(--accent-url-border)'}}>URL SCANNER</h2>
              <p className="text-xs text-secondary">Check websites and links for suspicious activity.</p>
            </div>
          </div>
          
          <div className="scanner-body flex-col gap-4 mt-6">
            <Input 
              icon={LinkIcon} 
              placeholder="https://example.com" 
            />
            <Button variant="url" fullWidth icon={ArrowRight}>
              Analyze URL
            </Button>
          </div>

          <div className="scanner-checks mt-6">
            <div className="text-sm font-semibold mb-2">Checks include:</div>
            <ul className="check-list text-sm text-secondary">
              <li><CheckCircle size={16} className="text-url" /> Domain & Reputation Analysis</li>
              <li><CheckCircle size={16} className="text-url" /> Suspicious Patterns Detection</li>
              <li><CheckCircle size={16} className="text-url" /> Threat Intelligence Lookup</li>
              <li><CheckCircle size={16} className="text-url" /> SSL & Security Checks</li>
            </ul>
          </div>

          <div className="scanner-footer mt-auto pt-6">
            <a href="#" className="flex items-center gap-1 text-sm text-url hover:underline">
              <HelpCircle size={16} /> How URL scanning works
            </a>
          </div>
        </Card>

        {/* Email Scanner */}
        <Card variant="email" className="scanner-card">
          <div className="scanner-header flex items-center gap-3">
            <div className="scanner-icon icon-email">
              <Mail size={24} />
            </div>
            <div>
              <h2 className="font-bold text-lg" style={{color: 'var(--brand-blue)'}}>EMAIL SCANNER</h2>
              <p className="text-xs text-secondary">Analyze email content for phishing and suspicious patterns.</p>
            </div>
          </div>
          
          <div className="scanner-body flex-col gap-4 mt-6">
            <div className="upload-zone text-center border-email">
              <Upload size={32} className="text-email mx-auto mb-2" />
              <div className="text-sm">Drag & drop your .eml here</div>
              <div className="text-sm">or <span className="text-email font-semibold cursor-pointer">click to browse</span></div>
              <div className="text-xs text-secondary mt-1">Supports .eml and .msg files</div>
            </div>
            <Button variant="email" fullWidth icon={ArrowRight}>
              Upload & Analyze Email
            </Button>
          </div>

          <div className="scanner-checks mt-6">
            <div className="text-sm font-semibold mb-2">Checks include:</div>
            <ul className="check-list text-sm text-secondary">
              <li><CheckCircle size={16} className="text-email" /> Sender Reputation</li>
              <li><CheckCircle size={16} className="text-email" /> Phishing Indicators</li>
              <li><CheckCircle size={16} className="text-email" /> Suspicious Links Detection</li>
              <li><CheckCircle size={16} className="text-email" /> Email Content Analysis</li>
            </ul>
          </div>

          <div className="scanner-footer mt-auto pt-6">
            <a href="#" className="flex items-center gap-1 text-sm text-email hover:underline">
              <HelpCircle size={16} /> How email scanning works
            </a>
          </div>
        </Card>

        {/* PDF Scanner */}
        <Card variant="pdf" className="scanner-card">
          <div className="scanner-header flex items-center gap-3">
            <div className="scanner-icon icon-pdf">
              <FileText size={24} />
            </div>
            <div>
              <h2 className="font-bold text-lg" style={{color: 'var(--accent-pdf-border)'}}>PDF SCANNER</h2>
              <p className="text-xs text-secondary">Upload documents to analyze content and extracted links.</p>
            </div>
          </div>
          
          <div className="scanner-body flex-col gap-4 mt-6">
            <div className="upload-zone text-center border-pdf">
              <Upload size={32} className="text-pdf mx-auto mb-2" />
              <div className="text-sm">Drag & drop your PDF here</div>
              <div className="text-sm">or <span className="text-pdf font-semibold cursor-pointer">click to browse</span></div>
              <div className="text-xs text-secondary mt-1">Max file size: 25 MB</div>
            </div>
            <Button variant="pdf" fullWidth icon={ArrowRight}>
              Upload & Analyze PDF
            </Button>
          </div>

          <div className="scanner-checks mt-6">
            <div className="text-sm font-semibold mb-2">Checks include:</div>
            <ul className="check-list text-sm text-secondary">
              <li><CheckCircle size={16} className="text-pdf" /> Metadata Analysis</li>
              <li><CheckCircle size={16} className="text-pdf" /> Extracted Links Scan</li>
              <li><CheckCircle size={16} className="text-pdf" /> Content Risk Detection</li>
              <li><CheckCircle size={16} className="text-pdf" /> Document Structure Analysis</li>
            </ul>
          </div>

          <div className="scanner-footer mt-auto pt-6">
            <a href="#" className="flex items-center gap-1 text-sm text-pdf hover:underline">
              <HelpCircle size={16} /> How PDF scanning works
            </a>
          </div>
        </Card>
      </div>

      </div>
    </div>
  );
}
