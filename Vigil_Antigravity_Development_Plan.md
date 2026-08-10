# Vigil Development Plan

## 1. Project Overview

Vigil is an explainable security analysis platform that helps users
identify potentially malicious or suspicious content.

Vigil will support three analysis inputs:

1.  URLs
2.  Emails
3.  PDF files

It will also include a Chrome Extension using Manifest V3 that can
detect the URL of the current webpage, send it to the Vigil backend, and
display a warning when the result is suspicious.

The primary goal is not to build a system that automatically blocks
websites. The extension should only warn the user and allow the user to
decide whether to continue.

Vigil should use deterministic security checks and external threat
intelligence for the actual analysis. Gemini is used to explain the
findings in human readable language, not as the sole source of the
security decision.

RAG is not required for the current version.

------------------------------------------------------------------------

# 2. Current Project Structure

The repository is organized as:

``` text
Vigil/
├── backend/
├── dashboard/
├── extension/
├── docs/
└── README.md
```

## Backend

The backend is a Spring Boot application.

Current package structure:

``` text
backend/
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── vigil/
        │           ├── BackendApplication.java
        │           └── controller/
        │               └── HealthController.java
        │
        └── resources/
            └── application.properties
```

The backend currently has:

``` text
Spring Boot
Spring Web
Spring Boot Validation
Spring Data MongoDB
Lombok
```

MongoDB will be hosted using MongoDB Atlas.

The health endpoint is:

``` text
GET /api/v1/health
```

and currently returns:

``` text
Vigil backend is running
```

------------------------------------------------------------------------

# 3. Technology Stack

## Frontend

-   React
-   Vite
-   JavaScript or TypeScript
-   React Router
-   Tailwind CSS
-   Axios or Fetch API

## Backend

-   Java 21
-   Spring Boot
-   Spring Web
-   Spring Boot Validation
-   Spring Data MongoDB
-   Spring Security
-   JWT based authentication
-   Maven

## Database

-   MongoDB Atlas

## Security and Threat Intelligence

-   Local deterministic security checks
-   VirusTotal
-   URLScan.io
-   RDAP or WHOIS based domain information

## AI

-   Gemini API

Gemini should explain security findings and generate user friendly
reports.

Gemini must not replace the deterministic security analysis.

## PDF Processing

-   Apache PDFBox

## Browser Extension

-   Chrome Extension
-   Manifest V3
-   JavaScript
-   Chrome Extension APIs

------------------------------------------------------------------------

# 4. High Level Architecture

``` text
                    ┌─────────────────────┐
                    │    React Dashboard  │
                    └──────────┬──────────┘
                               │
                               │ REST API
                               ▼
                    ┌─────────────────────┐
                    │    Spring Boot API  │
                    └──────────┬──────────┘
                               │
             ┌─────────────────┼─────────────────┐
             │                 │                 │
             ▼                 ▼                 ▼
       URL Analyzer      Email Analyzer     PDF Analyzer
             │                 │                 │
             └─────────────────┼─────────────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Threat Intelligence │
                    │     Services        │
                    └──────────┬──────────┘
                               │
                    ┌──────────┼──────────┐
                    │          │          │
                    ▼          ▼          ▼
                VirusTotal  URLScan     RDAP

                               │
                               ▼
                    ┌─────────────────────┐
                    │ Threat Scoring      │
                    │ Engine               │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Gemini Explanation  │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │ Threat Report       │
                    └──────────┬──────────┘
                               │
                    ┌──────────┴──────────┐
                    ▼                     ▼
             React Dashboard       Chrome Extension
```

------------------------------------------------------------------------

# 5. Development Strategy

Do not attempt to implement the complete application at once.

Build the system incrementally.

Each phase must be completed and tested before moving to the next phase.

Do not create unnecessary features before the core workflow works.

The development order is:

``` text
1. Backend foundation
2. MongoDB integration
3. URL analysis engine
4. Threat scoring engine
5. URL scanning API
6. External threat intelligence
7. Gemini explanation
8. Authentication and scan history
9. Email analyzer
10. PDF analyzer
11. React dashboard
12. Chrome extension
13. Testing and hardening
14. Deployment
```

------------------------------------------------------------------------

# 6. Phase 1: Backend Foundation

## Objective

Establish a clean Spring Boot backend structure.

## Tasks

1.  Keep the existing Spring Boot project.
2.  Keep Java 21.
3.  Keep Maven.
4.  Use `com.vigil` as the root package.
5.  Create clear packages as they become necessary.

Recommended package structure:

``` text
com.vigil
├── controller
├── service
├── repository
├── model
├── dto
├── exception
├── config
├── security
├── analyzer
│   ├── url
│   ├── email
│   └── pdf
├── client
│   ├── virustotal
│   ├── urlscan
│   ├── rdap
│   └── gemini
└── util
```

Do not create empty packages just for the sake of having them. Create
them when functionality requires them.

## Requirements

Use constructor based dependency injection.

Avoid field injection.

Use DTOs for API requests and responses.

Do not expose MongoDB entities directly from public APIs unless there is
a clear reason.

Use request validation.

Use centralized exception handling.

Use API versioning:

``` text
/api/v1/...
```

------------------------------------------------------------------------

# 7. Phase 2: MongoDB Atlas Integration

## Objective

Connect Spring Boot to MongoDB Atlas securely.

## Tasks

1.  Configure MongoDB Atlas.
2.  Use an environment variable for the connection URI.
3.  Never hardcode MongoDB credentials.
4.  Configure:

``` properties
spring.data.mongodb.uri=${MONGODB_URI}
```

5.  Verify that Spring Boot can save and retrieve a test document.
6.  Remove temporary database test code after verification.

## Initial domain models

Eventually create:

``` text
User
Scan
ThreatIndicator
ThreatReport
```

Do not build every model immediately.

Create models when their related feature is implemented.

------------------------------------------------------------------------

# 8. Phase 3: URL Analysis Engine

This is the core security component of Vigil.

The URL analyzer must be independent of the REST controller.

The controller should not contain security logic.

The architecture should be:

``` text
URL Request
    ↓
UrlAnalysisService
    ↓
Individual Security Checks
    ↓
Threat Indicators
```

## Local URL Checks

Implement these as independent, testable components:

``` text
HTTPS check
URL length check
Suspicious TLD detection
IP address in URL detection
Excessive hyphen detection
URL shortener detection
Typosquatting detection
Suspicious keyword analysis
```

Each check should return structured evidence.

Conceptually:

``` text
ThreatIndicator
├── type
├── severity
├── score
├── message
└── source
```

Do not simply return true or false.

The system needs to explain why a URL was considered suspicious.

------------------------------------------------------------------------

# 9. Phase 4: Threat Scoring Engine

Create a dedicated threat scoring service.

Input:

``` text
List of ThreatIndicator
```

Output:

``` text
ThreatScore
Severity
```

Example severity ranges:

``` text
0 to 29      LOW
30 to 59     MEDIUM
60 to 79     HIGH
80 to 100    CRITICAL
```

These values are initial defaults and can be adjusted after testing.

Keep scoring logic separate from individual checks.

The analyzer should identify evidence.

The scoring engine should interpret the combined evidence.

------------------------------------------------------------------------

# 10. Phase 5: URL Scanning API

Create:

``` text
POST /api/v1/scans/url
```

Request:

``` json
{
  "url": "https://example.com"
}
```

Response should contain:

``` text
scan identifier
input URL
risk score
severity
security indicators
analysis status
```

A conceptual response:

``` json
{
  "success": true,
  "data": {
    "score": 15,
    "severity": "LOW",
    "indicators": []
  }
}
```

The exact response structure can be improved as implementation
progresses.

## Validation

Reject:

-   Empty URLs
-   Null URLs
-   Malformed URLs
-   Unsupported input

Return proper HTTP status codes.

------------------------------------------------------------------------

# 11. Phase 6: External Threat Intelligence

Add external services only after the local URL analyzer works.

Create dedicated clients:

``` text
VirusTotalClient
UrlScanClient
RdapClient
```

Do not put external API calls directly into controllers.

Architecture:

``` text
UrlAnalysisService
       │
       ├── LocalSecurityChecks
       ├── VirusTotalClient
       ├── UrlScanClient
       └── RdapClient
```

External API failures should not crash the complete analysis.

For example:

``` text
VirusTotal unavailable
       ↓
Continue analysis
       ↓
Mark VirusTotal result as unavailable
       ↓
Return remaining findings
```

Handle:

-   API errors
-   Timeouts
-   Rate limits
-   Invalid API responses
-   Missing data

Keep API keys in environment variables.

------------------------------------------------------------------------

# 12. Phase 7: Gemini Explanation

Add a dedicated Gemini client and explanation service.

Architecture:

``` text
Security Evidence
       ↓
Threat Score
       ↓
Gemini Explanation Service
       ↓
Human Readable Explanation
```

Gemini should receive structured evidence.

It should generate:

``` text
Summary
Why the input is suspicious
Important indicators
Recommended action
```

Gemini must not be the only component deciding whether a URL is
malicious.

The deterministic engine and threat intelligence results remain the
source of security evidence.

Do not implement RAG for the current version.

------------------------------------------------------------------------

# 13. Phase 8: Authentication and Scan History

Implement authentication after the core URL analysis pipeline works.

Features:

``` text
Register
Login
JWT authentication
Protected endpoints
Current user
Logout handling
```

Use Spring Security.

Store passwords using a strong password hashing algorithm.

Never store plain text passwords.

## Scan History

Authenticated users should be able to view previous scans.

Example:

``` text
GET /api/v1/scans
GET /api/v1/scans/{id}
```

Users must only be able to access their own scans.

------------------------------------------------------------------------

# 14. Phase 9: Email Analyzer

Create a dedicated email analysis pipeline.

Architecture:

``` text
Email
  ↓
Email Parser
  ↓
Sender Analysis
  ↓
Subject Analysis
  ↓
Body Analysis
  ↓
Extract URLs
  ↓
Existing URL Analyzer
  ↓
Threat Scoring
  ↓
Gemini Explanation
```

## Email Checks

Analyze:

``` text
Sender address
Sender domain
Reply-To mismatch
Subject patterns
Suspicious language
Urgency or manipulation indicators
Links
Link text versus actual URL
Attachments metadata when available
```

Do not duplicate URL security checks.

Extract URLs from the email and send them through the existing URL
analysis engine.

------------------------------------------------------------------------

# 15. Phase 10: PDF Analyzer

Use Apache PDFBox.

Architecture:

``` text
PDF
 ↓
PDF Parser
 ↓
Extract text
Extract URLs
Extract metadata
 ↓
Analyze suspicious content
 ↓
Send extracted URLs to URL Analyzer
 ↓
Threat Scoring
 ↓
Gemini Explanation
```

## PDF checks

Analyze:

``` text
Suspicious URLs
Embedded links
Suspicious keywords
Potential phishing language
Unexpected metadata
Potentially suspicious file characteristics
```

The PDF analyzer should not execute files or embedded content.

Treat uploaded files as untrusted input.

Add appropriate:

``` text
File size limits
File type validation
Safe parsing
Error handling
```

------------------------------------------------------------------------

# 16. Phase 11: React Dashboard

Only begin the dashboard after the backend URL, email and PDF APIs are
reasonably stable.

Use:

``` text
React
Vite
Tailwind CSS
React Router
```

Initial pages:

``` text
Login
Register
Dashboard
URL Scanner
Email Scanner
PDF Scanner
Scan Report
Scan History
```

## Main workflow

``` text
User enters URL
       ↓
React
       ↓
Spring Boot API
       ↓
Threat Analysis
       ↓
Response
       ↓
Threat Report UI
```

The dashboard should clearly communicate:

``` text
Risk score
Severity
Why the input is suspicious
Security indicators
External intelligence findings
AI explanation
Recommended action
```

Do not expose API keys to the React application.

All external API calls should go through Spring Boot.

------------------------------------------------------------------------

# 17. Phase 12: Chrome Extension

Use Manifest V3.

The extension should not duplicate the backend security engine.

Its responsibility is:

``` text
Detect current URL
       ↓
Send URL to Vigil backend
       ↓
Receive analysis result
       ↓
Show warning when risk is high
```

## Warning behavior

The extension should display a warning such as:

``` text
High Risk Website

Risk Score: 87 / 100

Vigil detected multiple suspicious indicators.

[Go Back]

[Continue Anyway]

[View Full Report]
```

The extension should warn the user.

It should not attempt to automatically block the website.

## Important

The extension must handle:

``` text
Backend unavailable
Network timeout
API failure
Safe URL
Suspicious URL
High risk URL
```

------------------------------------------------------------------------

# 18. Phase 13: Testing

Testing must be implemented alongside development rather than only at
the very end.

## Unit tests

Test:

``` text
URL security checks
Threat scoring
Email analysis
PDF analysis
DTO validation
Utility functions
```

## Integration tests

Test:

``` text
Controller
Service
MongoDB
External API clients
```

## API testing

Test:

``` text
Valid requests
Invalid requests
Authentication
Unauthorized access
Malformed URLs
Large files
Unsupported files
External API failures
```

## Security testing

Check:

``` text
Input validation
Authentication
Authorization
Sensitive data exposure
API key exposure
File upload restrictions
Injection risks
Rate limiting
```

------------------------------------------------------------------------

# 19. Phase 14: Deployment

Deployment should happen after the core application is stable.

Components:

``` text
React Dashboard
       ↓
Frontend hosting

Spring Boot
       ↓
Backend hosting

MongoDB
       ↓
MongoDB Atlas

Chrome Extension
       ↓
Chrome
```

All secrets must be configured through deployment environment variables.

Never commit:

``` text
MongoDB password
Gemini API key
VirusTotal API key
URLScan API key
JWT secret
```

------------------------------------------------------------------------

# 20. Backend API Structure

Use a consistent REST structure.

Initial API structure:

``` text
/api/v1
│
├── /health
│
├── /auth
│   ├── POST /register
│   └── POST /login
│
├── /scans
│   ├── POST /url
│   ├── POST /email
│   ├── POST /pdf
│   ├── GET /
│   └── GET /{id}
│
└── /users
    └── GET /me
```

The exact endpoints can be adjusted when implementation requires it.

------------------------------------------------------------------------

# 21. Recommended Backend Structure After Major Features

The final backend should evolve toward:

``` text
backend/
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── vigil/
        │           ├── BackendApplication.java
        │           │
        │           ├── controller/
        │           │   ├── AuthController.java
        │           │   ├── UrlScanController.java
        │           │   ├── EmailScanController.java
        │           │   ├── PdfScanController.java
        │           │   └── UserController.java
        │           │
        │           ├── service/
        │           │   ├── UrlAnalysisService.java
        │           │   ├── EmailAnalysisService.java
        │           │   ├── PdfAnalysisService.java
        │           │   ├── ThreatScoringService.java
        │           │   └── AiExplanationService.java
        │           │
        │           ├── analyzer/
        │           │   ├── url/
        │           │   ├── email/
        │           │   └── pdf/
        │           │
        │           ├── client/
        │           │   ├── virustotal/
        │           │   ├── urlscan/
        │           │   ├── rdap/
        │           │   └── gemini/
        │           │
        │           ├── repository/
        │           │
        │           ├── model/
        │           │
        │           ├── dto/
        │           │
        │           ├── security/
        │           │
        │           ├── config/
        │           │
        │           ├── exception/
        │           │
        │           └── util/
        │
        └── resources/
            └── application.properties
```

This is a target structure, not something that should all be created
immediately.

------------------------------------------------------------------------

# 22. Engineering Standards

Follow these principles throughout development.

## Separation of concerns

Controllers handle HTTP.

Services handle application logic.

Analyzers handle security checks.

Clients handle external APIs.

Repositories handle persistence.

DTOs handle API data transfer.

Models represent persisted domain data.

## Dependency injection

Use constructor injection.

Avoid field injection.

## Configuration

Use configuration properties and environment variables for external
configuration.

Never hardcode secrets.

## Error handling

Use centralized exception handling.

Return consistent API error responses.

## Validation

Validate all user controlled input.

Treat URLs, emails and uploaded PDFs as untrusted input.

## Logging

Use structured and meaningful logs.

Never log:

``` text
Passwords
JWTs
API keys
MongoDB credentials
Sensitive uploaded content
```

## API versioning

Use:

``` text
/api/v1
```

from the beginning.

## Documentation

Document important APIs and architectural decisions.

Use OpenAPI or Swagger when the backend API becomes stable enough to
document.

------------------------------------------------------------------------

# 23. Important Security Principles

Vigil itself is a security application, so the implementation must also
follow secure development practices.

Never trust user input.

Never execute uploaded files.

Never execute JavaScript from analyzed pages.

Never expose external API keys to the frontend or extension.

Never store plain text passwords.

Never commit secrets.

Limit uploaded PDF size.

Validate file types.

Sanitize extracted content before displaying it.

Handle external API failures safely.

Use timeouts for external requests.

Do not allow the extension to automatically block or manipulate
websites.

------------------------------------------------------------------------

# 24. Implementation Rules

## Build incrementally

Complete one feature before beginning the next major feature.

## Do not overengineer

Do not introduce Redis, Kafka, microservices or other infrastructure
unless a real requirement appears.

The initial architecture should be a modular Spring Boot application.

## Reuse existing analysis components

For example:

``` text
Email
 ↓
Extract URL
 ↓
Existing UrlAnalyzer
```

and:

``` text
PDF
 ↓
Extract URL
 ↓
Existing UrlAnalyzer
```

Do not implement duplicate URL analysis logic.

## Keep AI secondary

The security engine produces evidence.

External services provide additional intelligence.

Gemini explains the evidence.

## Keep the extension thin

The extension should communicate with the backend rather than
reimplementing the entire security engine.

------------------------------------------------------------------------

# 25. Build Completion Criteria

Vigil should eventually support this complete workflow:

``` text
User
 ↓
React Dashboard
 ↓
Submit URL
 ↓
Spring Boot
 ↓
Local Security Checks
 ↓
External Threat Intelligence
 ↓
Threat Scoring
 ↓
Gemini Explanation
 ↓
Threat Report
 ↓
MongoDB Atlas
 ↓
User Scan History
```

For the extension:

``` text
User visits website
 ↓
Manifest V3 extension detects URL
 ↓
Vigil backend analyzes URL
 ↓
Risk result returned
 ↓
Extension displays warning if necessary
```

For email:

``` text
Email
 ↓
Email analysis
 ↓
Extract URLs
 ↓
URL analysis
 ↓
Threat scoring
 ↓
AI explanation
 ↓
Report
```

For PDF:

``` text
PDF
 ↓
PDF parsing
 ↓
Extract text and URLs
 ↓
Analyze content
 ↓
URL analysis
 ↓
Threat scoring
 ↓
AI explanation
 ↓
Report
```

------------------------------------------------------------------------

# 26. Immediate Development Order

Start with exactly this sequence:

``` text
STEP 1
Verify Spring Boot backend
        ↓
STEP 2
Verify MongoDB Atlas connection
        ↓
STEP 3
Remove temporary MongoDB test code
        ↓
STEP 4
Design actual Scan and ThreatIndicator models
        ↓
STEP 5
Build URL analyzer
        ↓
STEP 6
Build individual URL security checks
        ↓
STEP 7
Build threat scoring
        ↓
STEP 8
Create URL scanning API
        ↓
STEP 9
Add unit tests
        ↓
STEP 10
Add VirusTotal
        ↓
STEP 11
Add URLScan.io
        ↓
STEP 12
Add RDAP
        ↓
STEP 13
Combine local and external evidence
        ↓
STEP 14
Add Gemini explanation
        ↓
STEP 15
Add authentication
        ↓
STEP 16
Add scan history
        ↓
STEP 17
Build email analyzer
        ↓
STEP 18
Build PDF analyzer
        ↓
STEP 19
Build React dashboard
        ↓
STEP 20
Build Manifest V3 extension
        ↓
STEP 21
Testing and security hardening
        ↓
STEP 22
Deployment
```

Do not skip ahead to the React dashboard or extension before the backend
analysis pipeline is working.

The first major technical milestone is:

``` text
POST /api/v1/scans/url
        ↓
URL Analyzer
        ↓
Threat Indicators
        ↓
Threat Score
        ↓
JSON response
```

Once this works reliably, the rest of Vigil can be built around this
core.
