import { config } from './config.js';

document.addEventListener('DOMContentLoaded', () => {
  const statusDot = document.getElementById('status-dot');
  const statusTitle = document.getElementById('status-title');
  const statusDesc = document.getElementById('status-desc');
  const resultContainer = document.getElementById('result-container');
  const resultScore = document.getElementById('result-score');
  const resultSeverity = document.getElementById('result-severity');
  const viewReportBtn = document.getElementById('view-report-btn');

  // Get current active tab
  chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
    if (!tabs || tabs.length === 0) return;
    
    const activeTab = tabs[0];
    const tabId = activeTab.id;

    // Check if we have a scan result for this tab
    chrome.storage.local.get([`scan_${tabId}`], (result) => {
      const scanData = result[`scan_${tabId}`];
      
      if (scanData) {
        // Update UI with scan results
        statusTitle.textContent = 'Scan Complete';
        statusDesc.textContent = 'This page has been analyzed by Vigil.';
        
        resultContainer.style.display = 'block';
        resultScore.textContent = `${scanData.score}/100`;
        resultSeverity.textContent = `${scanData.severity} RISK`;
        
        // Color coding
        if (scanData.severity === 'CRITICAL' || scanData.severity === 'HIGH') {
          statusDot.style.backgroundColor = '#ef4444'; // Red
          statusDot.style.boxShadow = '0 0 10px rgba(239, 68, 68, 0.4)';
          resultScore.style.color = '#ef4444';
          resultSeverity.style.color = '#ef4444';
        } else if (scanData.severity === 'MEDIUM') {
          statusDot.style.backgroundColor = '#f5a623'; // Orange
          statusDot.style.boxShadow = '0 0 10px rgba(245, 166, 35, 0.4)';
          resultScore.style.color = '#f5a623';
          resultSeverity.style.color = '#f5a623';
        } else {
          statusDot.style.backgroundColor = '#10b981'; // Green
          statusDot.style.boxShadow = '0 0 10px rgba(16, 185, 129, 0.4)';
          resultScore.style.color = '#10b981';
          resultSeverity.style.color = '#10b981';
        }

        // Setup View Report button
        if (scanData.id) {
          viewReportBtn.style.display = 'inline-block';
          viewReportBtn.href = `http://localhost:5173/scan/${scanData.id}`;
          viewReportBtn.target = '_blank';
        }
      } else {
        // No scan data for this tab (maybe it's a safe internal page or wasn't scanned)
        statusTitle.textContent = 'Active & Monitoring';
        statusDesc.textContent = 'Vigil is active but no recent scan data is available for this specific page.';
        statusDot.style.backgroundColor = '#06b6d4'; // Cyan
        statusDot.style.boxShadow = '0 0 10px rgba(6, 182, 212, 0.4)';
      }
    });
  });
});
