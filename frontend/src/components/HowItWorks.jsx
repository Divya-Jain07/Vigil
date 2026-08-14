import React from 'react';
import { Search, Share2, BarChart2, MessageSquare, ShieldCheck } from 'lucide-react';
import './HowItWorks.css';

const HowItWorks = () => {
  return (
    <div className="how-it-works">
      <div className="how-header">
        <h2>How Vigil works</h2>
        <p>Our multi-layered verification process.</p>
      </div>

      <div className="pipeline">
        {/* Step 1 */}
        <div className="pipeline-step">
          <div className="step-indicator">
            <div className="step-icon">
              <Search size={20} />
            </div>
            <div className="step-line"></div>
          </div>
          <div className="step-content">
            <span className="step-number">01</span>
            <div className="step-text">
              <h3>Inspect</h3>
              <p>We perform local checks and extract important signals.</p>
            </div>
          </div>
        </div>

        {/* Step 2 */}
        <div className="pipeline-step">
          <div className="step-indicator">
            <div className="step-icon">
              <Share2 size={20} />
            </div>
            <div className="step-line"></div>
          </div>
          <div className="step-content">
            <span className="step-number">02</span>
            <div className="step-text">
              <h3>Correlate</h3>
              <p>We correlate with threat intelligence sources.</p>
            </div>
          </div>
        </div>

        {/* Step 3 */}
        <div className="pipeline-step">
          <div className="step-indicator">
            <div className="step-icon">
              <BarChart2 size={20} />
            </div>
            <div className="step-line"></div>
          </div>
          <div className="step-content">
            <span className="step-number">03</span>
            <div className="step-text">
              <h3>Assess</h3>
              <p>We calculate risk and confidence scores.</p>
            </div>
          </div>
        </div>

        {/* Step 4 */}
        <div className="pipeline-step">
          <div className="step-indicator">
            <div className="step-icon">
              <MessageSquare size={20} />
            </div>
          </div>
          <div className="step-content">
            <span className="step-number">04</span>
            <div className="step-text">
              <h3>Explain</h3>
              <p>We present clear insights and actionable recommendations.</p>
            </div>
          </div>
        </div>
      </div>

      <div className="trust-badge">
        <ShieldCheck size={24} className="trust-icon" />
        <span>Built to help you<br/>verify before you trust.</span>
      </div>
    </div>
  );
};

export default HowItWorks;
