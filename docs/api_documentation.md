# API Documentation

The Vigil Backend exposes a RESTful API. All endpoints (except public authentication and specific scan endpoints) require a valid JWT token passed in the `Authorization` header.

`Authorization: Bearer <your_jwt_token>`

---

## Authentication Endpoints

### 1. Register
- **Endpoint**: `POST /api/v1/auth/register`
- **Description**: Creates a new user account.
- **Body (JSON)**:
  ```json
  {
    "email": "divya@example.com",
    "password": "securepassword123"
  }
  ```
- **Response**: `200 OK` (Returns success status, JWT token, and message).

### 2. Login
- **Endpoint**: `POST /api/v1/auth/login`
- **Description**: Authenticates a user and returns a token.
- **Body (JSON)**:
  ```json
  {
    "email": "divya@example.com",
    "password": "securepassword123"
  }
  ```

### 3. Change Password
- **Endpoint**: `PUT /api/v1/auth/change-password`
- **Description**: Updates the authenticated user's password. Requires Authentication.
- **Body (JSON)**:
  ```json
  {
    "currentPassword": "securepassword123",
    "newPassword": "newsecurepassword456"
  }
  ```

---

## Scan Endpoints

### 1. Scan URL
- **Endpoint**: `POST /api/v1/scans/url`
- **Description**: Analyzes a URL for threats using VirusTotal and urlscan.io.
- **Body (JSON)**:
  ```json
  {
    "url": "https://suspicious-site.com"
  }
  ```
- **Response**: `200 OK` (Returns `ScanResult` object with score, severity, and AI explanation).

### 2. Scan Email
- **Endpoint**: `POST /api/v1/scans/email`
- **Description**: Analyzes raw email text/headers for phishing indicators.
- **Body (JSON)**:
  ```json
  {
    "sender": "admin@paypal-security-update.com",
    "replyTo": "scammer@random-domain.com",
    "subject": "Urgent: Account Suspended",
    "body": "Click here to verify your account...",
    "rawHeaders": "Received: from ...\nDKIM-Signature: ..."
  }
  ```
  *(Note: `replyTo` and `rawHeaders` are optional but heavily utilized by internal checks like SPF/DKIM verification and mismatch analysis).*

### 3. Scan PDF
- **Endpoint**: `POST /api/v1/scans/pdf`
- **Description**: Analyzes a PDF document for embedded links and malicious metadata.
- **Content-Type**: `multipart/form-data`
- **Body**:
  - `file`: The PDF file to be scanned.

### 4. Get Scan by ID
- **Endpoint**: `GET /api/v1/scans/{id}`
- **Description**: Retrieves a specific scan result by its MongoDB ID. Publicly accessible only for anonymous scans (no `userId`) to allow sharing reports; returns `403 Forbidden` if the scan belongs to a different logged-in user.

### 5. Get User History
- **Endpoint**: `GET /api/v1/scans`
- **Description**: Retrieves a list of all scans performed by the authenticated user, ordered by most recent first. Requires Authentication.
- **Response**: `200 OK` (Returns a JSON array of `ScanResult` objects).

### 6. Claim Anonymous Scan
- **Endpoint**: `PUT /api/v1/scans/{id}/claim`
- **Description**: Associates an anonymous scan (performed via the extension before logging in) with the currently authenticated user's account. Requires Authentication.
- **Response**: `200 OK` (Returns the updated `ScanResult`).
