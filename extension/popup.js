import { config } from './config.js';

document.addEventListener('DOMContentLoaded', () => {
  const statusDot = document.getElementById('status-dot');
  const statusTitle = document.getElementById('status-title');
  const statusDesc = document.getElementById('status-desc');
  const resultContainer = document.getElementById('result-container');
  const resultScore = document.getElementById('result-score');
  const resultSeverity = document.getElementById('result-severity');
  const viewReportBtn = document.getElementById('view-report-btn');
  const loadingContainer = document.getElementById('loading-container');

  // Tab switching logic
  const tabBtns = document.querySelectorAll('.tab-btn');
  const tabContents = document.querySelectorAll('.tab-content');

  tabBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      // Remove active class from all
      tabBtns.forEach(b => b.classList.remove('active'));
      tabContents.forEach(c => c.classList.remove('active'));
      
      // Add active class to clicked tab
      btn.classList.add('active');
      const targetId = btn.getAttribute('data-target');
      document.getElementById(targetId).classList.add('active');

      // Hide results when switching tabs, except if we are on URL tab and have data
      resultContainer.style.display = 'none';
      if (targetId === 'tab-url') {
        checkUrlScanStatus();
      }
    });
  });

  const displayResult = (scanData) => {
    loadingContainer.style.display = 'none';
    resultContainer.style.display = 'block';
    resultScore.textContent = `${scanData.score}/100`;
    resultSeverity.textContent = `${scanData.severity} RISK`;
    
    // Color coding
    if (scanData.severity === 'CRITICAL' || scanData.severity === 'HIGH') {
      statusDot.style.backgroundColor = 'var(--color-risk)';
      statusDot.style.boxShadow = '0 0 10px var(--color-risk)';
      resultScore.style.color = 'var(--color-risk)';
      resultSeverity.style.color = 'var(--color-risk)';
    } else if (scanData.severity === 'MEDIUM') {
      statusDot.style.backgroundColor = 'var(--color-warning)';
      statusDot.style.boxShadow = '0 0 10px var(--color-warning)';
      resultScore.style.color = 'var(--color-warning)';
      resultSeverity.style.color = 'var(--color-warning)';
    } else {
      statusDot.style.backgroundColor = 'var(--color-safe)';
      statusDot.style.boxShadow = '0 0 10px var(--color-safe)';
      resultScore.style.color = 'var(--color-safe)';
      resultSeverity.style.color = 'var(--color-safe)';
    }

    // Setup View Report button
    if (scanData.id) {
      viewReportBtn.style.display = 'inline-block';
      viewReportBtn.href = `http://localhost:5173/scan/${scanData.id}`;
      viewReportBtn.target = '_blank';
    } else {
      viewReportBtn.style.display = 'none';
    }
  };

  const checkUrlScanStatus = () => {
    chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
      if (!tabs || tabs.length === 0) return;
      const activeTab = tabs[0];
      const tabId = activeTab.id;
  
      chrome.storage.local.get([`scan_${tabId}`], (result) => {
        const scanData = result[`scan_${tabId}`];
        if (scanData) {
          statusTitle.textContent = 'Scan Complete';
          statusDesc.textContent = 'This page has been analyzed by Vigil.';
          displayResult(scanData);
        } else {
          statusTitle.textContent = 'Active & Monitoring';
          statusDesc.textContent = 'Vigil is active but no recent scan data is available for this specific page.';
          statusDot.style.backgroundColor = 'var(--color-primary)';
          statusDot.style.boxShadow = 'none';
          resultContainer.style.display = 'none';
        }
      });
    });
  };

  // Initial check for URL tab
  checkUrlScanStatus();

  // Email form submission
  document.getElementById('email-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const body = document.getElementById('email-body').value;
    const sender = 'Unknown (Pasted)';
    const subject = 'Pasted Email Content';

    resultContainer.style.display = 'none';
    loadingContainer.style.display = 'block';

    try {
      const { vigilToken } = await chrome.storage.local.get('vigilToken');
      const headers = {
        'Content-Type': 'application/json'
      };
      if (vigilToken) {
        headers['Authorization'] = `Bearer ${vigilToken}`;
      }

      const response = await fetch(`${config.API_BASE_URL}/scans/email`, {
        method: 'POST',
        headers: headers,
        body: JSON.stringify({ sender, subject, body })
      });

      const result = await response.json();
      if (result.success && result.data) {
        displayResult(result.data);
      } else {
        alert('Failed to scan email.');
        loadingContainer.style.display = 'none';
      }
    } catch (error) {
      console.error('Email scan error:', error);
      alert('Error scanning email.');
      loadingContainer.style.display = 'none';
    }
  });

  // PDF form submission
  document.getElementById('pdf-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const fileInput = document.getElementById('pdf-file');
    if (fileInput.files.length === 0) return;
    const file = fileInput.files[0];

    resultContainer.style.display = 'none';
    loadingContainer.style.display = 'block';

    const formData = new FormData();
    formData.append('file', file);

    try {
      const { vigilToken } = await chrome.storage.local.get('vigilToken');
      const headers = {};
      if (vigilToken) {
        headers['Authorization'] = `Bearer ${vigilToken}`;
      }

      const response = await fetch(`${config.API_BASE_URL}/scans/pdf`, {
        method: 'POST',
        headers: headers,
        body: formData
      });

      const result = await response.json();
      if (result.success && result.data) {
        displayResult(result.data);
      } else {
        alert('Failed to scan PDF. ' + (result.data ? result.data.explanation : ''));
        loadingContainer.style.display = 'none';
      }
    } catch (error) {
      console.error('PDF scan error:', error);
      alert('Error scanning PDF.');
      loadingContainer.style.display = 'none';
    }
  });
});
