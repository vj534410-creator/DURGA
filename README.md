# DURGA — Safety & Support Platform

The **DURGA Safety & Support Platform** is an enterprise-grade, privacy-first safety ecosystem designed for citizen protection, emergency response, and statutory incident reporting in India.

---

## 🏗️ System Architecture

```
                  ┌─────────────────────────────────────────┐
                  │              DURGA BACKEND              │
                  │        Node.js / Express REST API       │
                  └────────────────────┬────────────────────┘
                                       │
             ┌─────────────────────────┼─────────────────────────┐
             │                         │                         │
             ▼                         ▼                         ▼
      ┌─────────────┐           ┌─────────────┐           ┌──────────────┐
      │ Android     │           │ Admin Web   │           │ Officer Web  │
      │ User App    │           │ Panel       │           │ Portal       │
      └─────────────┘           └─────────────┘           └──────────────┘
             │                         │                         │
             └─────────────────────────┼─────────────────────────┘
                                       ▼
                              ┌──────────────────┐
                              │    PostgreSQL    │
                              │ Central Database │
                              └──────────────────┘
                                       │
                                       ▼
                              Secure File Storage
```

---

## 📱 Component Overview

### 1. Application 1: DURGA User Android App (`/app`)
- **Technology**: 100% Kotlin, Jetpack Compose, Material 3, Clean Architecture, MVVM.
- **Data Persistence**: Room Database (`DurgaDatabase.kt`), AES-256 encrypted local vault (`SecurityVault.kt`).
- **Offline First**: Complete offline incident drafting, emergency directory access, and background sync.
- **Multilingual**: Native first-class Gujarati (ગુજરાતી), Hindi (हिन्दी), and English support.
- **Discreet Safety**: Neutral Quick Exit feature, pin protection, fake calculator/notes disguise capability.
- **Themes**: 6 distinct M3 palettes including Durga Pink, Rose Pink, Gujarat Saffron, Navy Blue, Forest Green, and Deep Crimson.

### 2. Application 2: Central Backend REST API (`/backend`)
- **Technology**: Node.js, TypeScript, Express, JWT Authentication, SHA-256 Evidence Hashing.
- **Role-Based Access Control (RBAC)**: Strict segregation between `ROLE_USER`, `ROLE_POLICE_OFFICER`, `ROLE_SENIOR_OFFICER`, `ROLE_AUTHORITY_REVIEWER`, and `ROLE_ADMIN`.
- **Statutory Guardrails**: Realized through `GovernmentIntegrationService` which marks outgoing cases as `READY_FOR_OFFICIAL_INTEGRATION` without fabricating government submission claims until real credentials are provided.
- **Health Check**: `GET /health` returns `200 OK`.
- **API Documentation**: OpenAPI 3.0 specification available at `backend/docs/openapi.yaml`.

### 3. Application 3: DURGA Admin Web Panel (`/admin-panel`)
- **Port**: `3001`
- **Features**: Live KPI cards, complaints triage, case reassignment to investigating officers, roster of registered law enforcement officers, tamper-evident audit logs inspection.

### 4. Application 4: DURGA Authorized Officer / Police Web Portal (`/officer-portal`)
- **Port**: `3002`
- **Features**: Police badge login (`POL-GJ-XXXX`), strict assigned cases docket, victim statement review, SHA-256 evidence chain-of-custody verification, investigation status updates, and official dispatch notes.

### 5. Central Database: PostgreSQL (`/database`)
- **Schema**: `database/schema.sql` (25+ tables covering users, sessions, complaints, evidence, case notes, audit logs, and statutory submissions).
- **Seed Data**: `database/seed.sql` (Standard roles, categories, and verified Indian emergency services: 112, 1091, 181, 1098, 1930, 108).

---

## 🚀 Quick Start Guide

### Running All Services via Docker
```bash
cd docker
docker-compose up --build
```

### Running Locally without Docker
```bash
# 1. Run full test verification suite
./scripts/test_all.sh

# 2. Launch Backend, Admin Panel, and Officer Portal concurrently
./scripts/run_all.sh
```

- **Backend REST API**: http://localhost:3000
- **Admin Web Panel**: http://localhost:3001
- **Police Officer Portal**: http://localhost:3002

---

## 🔒 Security & Statutory Compliance
1. **Zero Secret Leakage**: No hardcoded API keys in Android or web code. Keys are injected via environment variables.
2. **Immutable Audit Trail**: All officer and admin actions create a tamper-evident audit log with timestamps, badge numbers, and IP addresses.
3. **Evidence Integrity**: All uploaded evidence files generate cryptographic SHA-256 hashes for chain-of-custody verification.
