import { config } from './config.js';

chrome.tabs.onUpdated.addListener(async (tabId, changeInfo, tab) => {
  // Trigger on full page load OR on URL change (for Single Page Applications like React)
  if ((changeInfo.status === 'complete' || changeInfo.url) && tab.url && tab.url.startsWith('http')) {
    const url = tab.url;
    
    // Check if the domain is in the allowlist
    const domain = new URL(url).hostname;
    const { allowlist } = await chrome.storage.local.get({ allowlist: [] });
    
    if (allowlist.includes(domain)) {
      console.log(`[Vigil] Domain ${domain} is allowlisted, skipping scan.`);
      return;
    }

    console.log(`[Vigil] Scanning URL: ${url}`);
    
    try {
      const response = await fetch(`${config.API_BASE_URL}/scans/url`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ url: url })
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      
      if (result.success && result.data) {
        // Store the scan result for this tab so the popup can display it
        chrome.storage.local.set({ [`scan_${tabId}`]: result.data });

        const severity = result.data.severity;
        
        // Only warn for HIGH or CRITICAL severity
        if (severity === 'HIGH' || severity === 'CRITICAL') {
          console.log(`[Vigil] High risk detected (${severity}) for ${url}`);
          
          // Send message to content script to display warning
          chrome.tabs.sendMessage(tabId, {
            action: 'show_warning',
            data: result.data
          }).catch(err => {
            // Content script might not be ready yet or page might not support it
            console.error('[Vigil] Error sending message to content script:', err);
          });
        } else {
          console.log(`[Vigil] URL is safe. Severity: ${severity}`);
        }
      }
    } catch (error) {
      console.error('[Vigil] Error scanning URL:', error);
    }
  }
});

// Listen for messages from the content script or popup
chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
  if (request.action === 'allowlist_domain' && request.domain) {
    chrome.storage.local.get({ allowlist: [] }, (result) => {
      const allowlist = result.allowlist;
      if (!allowlist.includes(request.domain)) {
        allowlist.push(request.domain);
        chrome.storage.local.set({ allowlist: allowlist }, () => {
          console.log(`[Vigil] Added ${request.domain} to allowlist.`);
          sendResponse({ success: true });
        });
      } else {
        sendResponse({ success: true });
      }
    });
    return true; // Keep message channel open for async response
  } else if (request.action === 'update_auth_token') {
    if (request.token) {
      chrome.storage.local.set({ vigilToken: request.token }, () => {
        console.log('[Vigil] Auth token synchronized from web app.');
        sendResponse({ success: true });
      });
    } else {
      chrome.storage.local.remove('vigilToken', () => {
        console.log('[Vigil] Auth token cleared from web app.');
        sendResponse({ success: true });
      });
    }
    return true;
  }
});
