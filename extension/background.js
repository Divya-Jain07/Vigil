import { config } from './config.js';


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
