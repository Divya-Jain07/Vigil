import React from 'react';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import { Shield, Brain, Lock, Zap } from 'lucide-react';
import './About.css';

const About = () => {
  return (
    <div className="dashboard-layout">
      <Navbar />
      <main className="dashboard-main container">
        
        <div className="about-hero">
          <h1>Know before you <span>trust.</span></h1>
          <p>
            Vigil is a next-generation threat intelligence platform designed to 
            protect you from phishing, malware, and sophisticated social engineering attacks.
          </p>
        </div>

        <div className="about-grid">
          <div className="about-card">
            <div className="about-icon-wrapper">
              <Brain size={28} />
            </div>
            <h2>AI-Powered Analysis</h2>
            <p>
              We don't just rely on static blacklists. Vigil uses advanced language models 
              and heuristics to understand the context and intent behind URLs and documents.
            </p>
          </div>

          <div className="about-card">
            <div className="about-icon-wrapper">
              <Zap size={28} />
            </div>
            <h2>Instant Verification</h2>
            <p>
              Our multi-layered inspection pipeline correlates threat intelligence from 
              dozens of sources in milliseconds to give you a definitive risk score.
            </p>
          </div>

          <div className="about-card">
            <div className="about-icon-wrapper">
              <Lock size={28} />
            </div>
            <h2>Privacy First</h2>
            <p>
              Your security shouldn't cost your privacy. Scans are processed securely, 
              and we don't sell your data or use your personal files to train public models.
            </p>
          </div>

          <div className="about-card">
            <div className="about-icon-wrapper">
              <Shield size={28} />
            </div>
            <h2>Comprehensive Checks</h2>
            <p>
              From checking SSL/TLS configurations to reverse-engineering malicious PDF payloads, 
              we leave no stone unturned when verifying your digital interactions.
            </p>
          </div>
        </div>

        <div className="about-mission">
          <h2>Our Mission</h2>
          <p>
            The internet operates on a foundation of implicit trust. We click links, open attachments, 
            and share information under the assumption that the infrastructure protects us. But as 
            threats evolve, that implicit trust becomes a vulnerability. 
          </p>
          <p>
            We built Vigil to shift the paradigm from implicit trust to <strong>explicit verification</strong>. 
            By providing a fast, accessible, and highly accurate analysis tool, we empower users 
            to verify before they trust.
          </p>
        </div>

      </main>
      <Footer />
    </div>
  );
};

export default About;
