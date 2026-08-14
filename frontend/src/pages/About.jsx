import React from 'react';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import { 
  ShieldCheck, Shield, CheckCircle, MessageSquare, 
  Search, Share2, BarChart2, Link as LinkIcon, 
  Mail, FileText, ArrowRight
} from 'lucide-react';
import './About.css';

const About = () => {
  return (
    <div className="dashboard-layout">
      <Navbar />
      <main className="about-main container">
        
        {/* Hero Section */}
        <section className="about-hero-section">
          <div className="about-hero-text">
            <h1>Know before<br/>you trust.</h1>
            <p>
              Vigil is a security analysis platform that helps you identify potentially malicious URLs, emails, and PDF documents before you interact with them.
            </p>
          </div>
          <div className="about-hero-visual">
            <div className="visual-inputs">
              <div className="visual-card"><LinkIcon size={16} /> URL</div>
              <div className="visual-card"><Mail size={16} /> Email (.eml)</div>
              <div className="visual-card"><FileText size={16} /> PDF Document</div>
            </div>
            <div className="visual-arrows">
              <span>----&gt;</span>
              <span>----&gt;</span>
              <span>----&gt;</span>
            </div>
            <div className="visual-core">
              <div className="visual-shield">
                <ShieldCheck size={48} className="shield-icon" />
                <span>VIGIL</span>
              </div>
            </div>
            <div className="visual-arrows">
              <span>----&gt;</span>
              <span>----&gt;</span>
              <span>----&gt;</span>
            </div>
            <div className="visual-outputs">
              <div className="visual-badge safe"><CheckCircle size={16} /> Risk Analysis</div>
              <div className="visual-badge safe"><CheckCircle size={16} /> Risk Analysis</div>
              <div className="visual-badge safe"><CheckCircle size={16} /> Risk Analysis</div>
            </div>
          </div>
        </section>

        {/* What is Vigil */}
        <section className="about-what-is">
          <div className="what-is-card">
            <div className="what-is-icon">
              <ShieldCheck size={64} />
            </div>
            <div className="what-is-text">
              <h2>What is Vigil?</h2>
              <p>
                Vigil combines local security analysis with external threat intelligence and AI-assisted reasoning to identify suspicious patterns, explain potential risks, and provide actionable recommendations.
              </p>
            </div>
          </div>
        </section>

        {/* How it works */}
        <section className="about-pipeline-section">
          <h2>How Vigil analyzes threats</h2>
          <div className="about-pipeline">
            <div className="pipeline-step">
              <div className="step-circle"><Search size={24} /></div>
              <div className="step-num">01</div>
              <h3>Inspect</h3>
              <p>Vigil examines the submitted URL, email, or document for suspicious characteristics.</p>
            </div>
            <div className="pipeline-line"></div>
            <div className="pipeline-step">
              <div className="step-circle"><Share2 size={24} /></div>
              <div className="step-num">02</div>
              <h3>Correlate</h3>
              <p>Relevant information is compared against external threat intelligence sources.</p>
            </div>
            <div className="pipeline-line"></div>
            <div className="pipeline-step">
              <div className="step-circle"><BarChart2 size={24} /></div>
              <div className="step-num">03</div>
              <h3>Assess</h3>
              <p>Individual findings are combined to calculate an overall threat score.</p>
            </div>
            <div className="pipeline-line"></div>
            <div className="pipeline-step">
              <div className="step-circle"><MessageSquare size={24} /></div>
              <div className="step-num">04</div>
              <h3>Explain</h3>
              <p>Gemini converts the technical findings into a human readable explanation and action.</p>
            </div>
          </div>
        </section>

        {/* What can you check */}
        <section className="about-checks">
          <h2>What can you check?</h2>
          <div className="checks-grid">
            <div className="check-card">
              <div className="check-icon url-icon"><LinkIcon size={24} /></div>
              <div className="check-content">
                <h3>URL</h3>
                <p>Analyze links for suspicious protocols, domains, keywords, reputation signals, and other indicators.</p>
              </div>
            </div>
            <div className="check-card">
              <div className="check-icon email-icon"><Mail size={24} /></div>
              <div className="check-content">
                <h3>Email (.eml)</h3>
                <p>Analyze .eml files for suspicious sender information, reply-to mismatches, phishing language, and malicious links.</p>
              </div>
            </div>
            <div className="check-card">
              <div className="check-icon pdf-icon"><FileText size={24} /></div>
              <div className="check-content">
                <h3>PDF Document</h3>
                <p>Analyze PDF documents for suspicious links, embedded content, and other potentially dangerous indicators.</p>
              </div>
            </div>
          </div>
        </section>

        {/* Differentiators */}
        <section className="about-diff">
          <h2>What makes Vigil different?</h2>
          <p className="diff-subtitle">Detection is only half the problem. Understanding the risk matters too.</p>
          <div className="diff-grid">
            <div className="diff-item">
              <div className="diff-icon"><Shield size={24} /></div>
              <div className="diff-content">
                <h3>Evidence</h3>
                <p>See exactly which indicators contributed to the risk score.</p>
              </div>
            </div>
            <div className="diff-item">
              <div className="diff-icon"><MessageSquare size={24} /></div>
              <div className="diff-content">
                <h3>Explanation</h3>
                <p>Understand why Vigil considers something suspicious instead of receiving only a score.</p>
              </div>
            </div>
            <div className="diff-item">
              <div className="diff-icon"><CheckCircle size={24} /></div>
              <div className="diff-content">
                <h3>Action</h3>
                <p>Get a clear recommendation about what to do next.</p>
              </div>
            </div>
          </div>
        </section>

        {/* Footer Cards */}
        <section className="about-footer-cards">
          <div className="built-with-card">
            <h3>Built with</h3>
            <div className="tech-stack">
              <div className="tech-item">
                <div className="tech-icon react-icon">⚛</div>
                <span>React</span>
              </div>
              <div className="tech-item">
                <div className="tech-icon spring-icon">🌿</div>
                <span>Spring Boot</span>
              </div>
              <div className="tech-item">
                <div className="tech-icon mongo-icon">🍃</div>
                <span>MongoDB</span>
              </div>
              <div className="tech-item">
                <div className="tech-icon gemini-icon">✨</div>
                <span>Gemini</span>
              </div>
              <div className="tech-item">
                <div className="tech-icon vt-icon">Σ</div>
                <span>VirusTotal</span>
              </div>
            </div>
          </div>

          <div className="important-card">
            <div className="important-icon">
              <ShieldCheck size={28} />
            </div>
            <div className="important-content">
              <h3>Important</h3>
              <p>Vigil is an analysis and awareness tool. A scan result should not be treated as an absolute guarantee that a URL, email, or document is safe or malicious. Always exercise caution when interacting with unknown content.</p>
            </div>
          </div>
        </section>

      </main>
      <Footer />
    </div>
  );
};

export default About;
