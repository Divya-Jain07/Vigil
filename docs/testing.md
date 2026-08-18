# Testing Guide

This document outlines how to test the various components of the Vigil ecosystem.

## Backend Testing (Spring Boot)

The backend utilizes JUnit 5 and Mockito for unit testing. 

### Running Tests
To run the entire test suite, navigate to the `backend` directory and use Maven:

```bash
cd backend
./mvnw test
```

### Test Configuration
The backend contains a specific `application.properties` file located in `src/test/resources/` that configures tests to use a local or in-memory MongoDB database to avoid polluting the production/development database.

### Key Areas to Test
1. **ThreatScoringService**: This is the most critical component. Write unit tests to ensure that different combinations of VirusTotal hits and Suspicious Keywords result in the correct severity (LOW, MEDIUM, HIGH, CRITICAL).
2. **Controllers**: Ensure endpoints return correct HTTP status codes (200, 400, 401, 403) based on authentication states and input validation.

## Frontend Testing (React)

Currently, manual testing is required for the frontend.

### Component Verification Checklist
- **Authentication**: Verify Login, Registration, and Logout flows. Ensure JWT is correctly stored in `localStorage` and removed on logout.
- **Protected Routes**: Attempt to navigate to `/profile` or `/history` without being logged in. You should be redirected to `/login`.
- **Dark/Light Mode**: Toggle the theme using the Navbar button and ensure all components (Navbar, Footer, VerificationPanel, ScanResults) adapt properly.
- **Scan Flow**: Submit a URL, Email, and PDF. Verify that the UI enters a loading state, and successfully transitions to the `/scan/:id` route upon completion.

## Extension Testing

The Chrome Extension requires manual browser testing.

### Loading the Extension
1. Go to `chrome://extensions/`
2. Enable **Developer mode**
3. Click **Load unpacked** and select the `Vigil/extension` directory.

### Extension Verification Checklist
- **Authentication Sync**: Log into the React Dashboard. Verify that the extension automatically picks up the JWT token (you should be able to scan authenticated endpoints without error).
- **Background Scanning**: Visit a known safe site (e.g., `google.com`) and a known test malicious site (e.g., `eicar.org`). Verify the extension icon changes color and the popup displays the correct status.
- **Content Script Blocking**: When navigating to a dangerous site, verify that the red overlay warning is injected into the page, preventing interaction with the dangerous content.
- **Popup Forms**: Test the manual URL, Email, and PDF forms in the extension popup. Verify they communicate correctly with the backend.
