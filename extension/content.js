// Listen for messages from the background script
chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
  console.log('[Vigil Content Script] Received message:', request);
  if (request.action === 'show_warning') {
    showWarningOverlay(request.data);
  }
});

function showWarningOverlay(data) {
  // Check if warning is already present
  if (document.getElementById('vigil-warning-overlay')) {
    return;
  }

  // Inject dashboard config URL since we can't easily import config.js as a module in a content script without extra setup
  const baseUrl = 'https://vigil-gold-kappa.vercel.app';
  const reportUrl = data.id ? `${baseUrl}/scan/${data.id}` : baseUrl;

  // Create overlay container
  const overlay = document.createElement('div');
  overlay.id = 'vigil-warning-overlay';

  // Build the indicators HTML
  let indicatorsHtml = '';
  if (data.indicators && data.indicators.length > 0) {
    indicatorsHtml = `
      <div class="vigil-indicators">
        <h4>Key Findings</h4>
        <ul>
          ${data.indicators.slice(0, 3).map(ind => `<li>${ind.message || ind.type}</li>`).join('')}
          ${data.indicators.length > 3 ? `<li>...and ${data.indicators.length - 3} more</li>` : ''}
        </ul>
      </div>
    `;
  }

  // Build the overlay HTML
  overlay.innerHTML = `
    <div class="vigil-overlay-backdrop"></div>
    <div class="vigil-overlay-content">
      <div class="vigil-warning-icon">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
          <line x1="12" y1="9" x2="12" y2="13"></line>
          <line x1="12" y1="17" x2="12.01" y2="17"></line>
        </svg>
      </div>
      <h2 class="vigil-warning-title">High Risk Website Detected</h2>
      <p class="vigil-warning-subtitle">Vigil has identified this site as potentially dangerous.</p>
      
      <div class="vigil-risk-score ${data.severity.toLowerCase()}">
        <span class="score">${data.score}/100</span>
        <span class="severity">${data.severity} RISK</span>
      </div>

      ${indicatorsHtml}

      <div class="vigil-actions">
        <button id="vigil-btn-back" class="vigil-btn vigil-btn-primary">Go Back (Safe)</button>
        <button id="vigil-btn-continue" class="vigil-btn vigil-btn-secondary">Continue Anyway</button>
        <a href="${reportUrl}" target="_blank" class="vigil-btn vigil-btn-outline">View Full Report</a>
      </div>
    </div>
  `;

  // Append to body
  document.body.appendChild(overlay);

  // Add event listeners
  document.getElementById('vigil-btn-back').addEventListener('click', () => {
    window.history.back();
  });

  document.getElementById('vigil-btn-continue').addEventListener('click', () => {
    // Add domain to allowlist
    const domain = window.location.hostname;
    chrome.runtime.sendMessage({ action: 'allowlist_domain', domain: domain }, (response) => {
      if (response && response.success) {
        // Remove overlay
        overlay.remove();
      }
    });
  });
}

// Bridge between the React Web App and the Extension Background Script
// The React app sends a postMessage, and this content script forwards it to the extension
window.addEventListener('message', (event) => {
  // Only accept messages from the same window
  if (event.source !== window) return;

  if (event.data && event.data.type === 'VIGIL_AUTH_UPDATE') {
    chrome.runtime.sendMessage({
      action: 'update_auth_token',
      token: event.data.token
    });
  }
});
