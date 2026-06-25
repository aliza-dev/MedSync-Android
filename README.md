<div align="center">

# 🏥 MedSync - Medical Management System

![Android](https://img.shields.io/badge/Android-API%2024+-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Realtime%20DB-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![License](https://img.shields.io/badge/License-Academic-blue?style=for-the-badge)

**A dual-role healthcare platform connecting patients and doctors through secure, real-time mobile workflows.**

<br/>

[Features](#-features) ·
[Navigation Flow](#-app-navigation-flow) ·
[Screenshots](#-visual-showcase) ·
[Project Structure](#-project-structure) ·
[Quick Start](#-quick-start-installation) ·
[Learning Outcomes](#-learning-outcomes)

</div>

---

## 📖 Overview

**MedSync** is a native Android healthcare management application engineered around a **dual-role architecture** — delivering purpose-built experiences for **Patients** and **Doctors** within a single, unified mobile app. Patients can securely register, book appointments with date and time selection, and review their complete treatment and billing history. Doctors can manage incoming appointment requests, accept or reject scheduled visits, record clinical findings, and generate itemized medical bills — all synchronized in real time through **Firebase**.

This project was developed and submitted as an academic assignment for **CS-512 Mobile App Development** at the **University of Agriculture, Faisalabad**. MedSync demonstrates industry-aligned mobile development practices including cloud-backed authentication, role-based navigation, Material Design UI, RecyclerView-driven list screens, and real-time data persistence using Firebase Realtime Database — bridging classroom theory with a production-style healthcare workflow.

---

## ✨ Features

### 🔐 Authentication

- ✅ **Firebase Authentication** — Secure email/password registration and login for both roles
- ✅ **Session Management** — Persistent auth state with automatic re-authentication on app launch
- ✅ **Role-Based Routing** — Post-login navigation directed to Patient or Doctor portals based on stored user role
- ✅ **Dual Registration Flows** — Separate, role-specific sign-up screens with tailored profile fields
- ✅ **Protected Screens** — All dashboard and clinical screens accessible only after successful authentication

### 👤 Patient Portal

- ✅ **Patient Registration** — Create an account with name, email, age, and password
- ✅ **Patient Dashboard** — Central home screen displaying profile summary and quick-action navigation
- ✅ **Book Appointments** — Schedule visits with doctor name, **Date/Time picker**, and problem description
- ✅ **View Bills History** — Browse itemized medical bills generated from completed appointments
- ✅ **View Treatment History** — Access diagnosis, prescription, and progress records in one consolidated view
- ✅ **Real-Time Updates** — Appointment and history data refreshed live from Firebase

### 🩺 Doctor Portal

- ✅ **Doctor Dashboard** — Profile hub showing name, specialization, email, and navigation shortcuts
- ✅ **Manage Pending Appointments** — Review incoming patient booking requests in a card-based list
- ✅ **Accept / Reject Today's Appointments** — One-tap approval or decline for scheduled visits
- ✅ **Add Diagnosis, Prescription & Progress** — Record clinical findings and treatment updates per appointment
- ✅ **Generate Itemized Bills** — Create bills with consultation fees and medicine charges
- ✅ **Browse Patient History** — View completed treatment records across all patients
- ✅ **Appointment Detail View** — Full appointment context before taking clinical action

---

## 🧭 App Navigation Flow

```
MedSync Launch
│
└── LoginRegisterActivity (Role Selection)
    │
    ├── 👤 PATIENT PATH
    │   ├── PatientRegisterActivity ──► Register new account
    │   ├── LoginActivity ────────────► Sign in existing account
    │   │
    │   └── PatientHomeActivity (Dashboard)
    │       ├── BookAppointmentActivity ──────────► Schedule new visit
    │       ├── PatientTreatmentHistoryActivity ──► View diagnosis & prescriptions
    │       └── PatientBillsHistoryActivity ──────► View medical bills
    │
    └── 🩺 DOCTOR PATH
        ├── DoctorRegisterActivity ───► Register new account
        ├── LoginActivity ────────────► Sign in existing account
        │
        └── DoctorProfileActivity (Dashboard)
            ├── PendingAppointmentsActivity ──────► Manage incoming requests
            ├── TodaysAppointmentsActivity ───────► Accept / Reject visits
            │   └── AppointmentActionActivity ────► Take action on appointment
            ├── AppointmentDetailActivity ────────► Treatment & billing entry
            └── DoctorPatientHistoryActivity ─────► Browse patient records
```

**Condensed user journey:**

```
Launch → Role Selection
         │
         ├── Patient → Login/Register → Dashboard → Book Appointments / Treatment History / Bills History
         │
         └── Doctor  → Login/Register → Dashboard → Pending Requests / Today's Appointments → Treat & Bill / Patient History
```

---

## 📸 Visual Showcase

### 👤 Patient View

| Role Selection | Patient Registration | Patient Dashboard |
|:---:|:---:|:---:|
| <img src="assets/role-selection.jpeg" width="250"> | <img src="assets/patient-register.jpeg" width="250"> | <img src="assets/patient-dashboard.jpeg" width="250"> |

### 🩺 Doctor View

| Doctor Registration | Doctor Dashboard | Appointment Action |
|:---:|:---:|:---:|
| <img src="assets/doctor-register.jpeg" width="250"> | <img src="assets/doctor-dashboard.jpeg" width="250"> | <img src="assets/appointment-action.jpeg" width="250"> |

### 💊 Treatment & History

| Today's Appointments | Treatment & Billing |
|:---:|:---:|
| <img src="assets/todays-appointments.jpeg" width="250"> | <img src="assets/treatment-billing-detail.jpeg" width="250"> |

---

## 📂 Project Structure

```
MedicalManagementSystem/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/medical/app/
│   │   │   │   ├── LoginRegisterActivity.java           # Launcher — role selection
│   │   │   │   ├── LoginActivity.java                   # Shared login screen
│   │   │   │   ├── PatientRegisterActivity.java         # Patient sign-up
│   │   │   │   ├── PatientHomeActivity.java             # Patient dashboard
│   │   │   │   ├── BookAppointmentActivity.java         # Appointment booking
│   │   │   │   ├── PatientTreatmentHistoryActivity.java
│   │   │   │   ├── PatientBillsHistoryActivity.java
│   │   │   │   ├── DoctorRegisterActivity.java          # Doctor sign-up
│   │   │   │   ├── DoctorProfileActivity.java           # Doctor dashboard
│   │   │   │   ├── PendingAppointmentsActivity.java
│   │   │   │   ├── TodaysAppointmentsActivity.java
│   │   │   │   ├── AppointmentActionActivity.java
│   │   │   │   ├── AppointmentDetailActivity.java
│   │   │   │   ├── DoctorPatientHistoryActivity.java
│   │   │   │   │
│   │   │   │   ├── models/
│   │   │   │   │   ├── Patient.java                     # Patient data model
│   │   │   │   │   ├── Doctor.java                      # Doctor data model
│   │   │   │   │   └── Appointment.java                 # Appointment data model
│   │   │   │   │
│   │   │   │   └── utils/
│   │   │   │       └── AppointmentHelper.java           # Shared appointment utilities
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/                              # XML activity & list item layouts
│   │   │   │   ├── drawable/                            # Backgrounds, icons, buttons
│   │   │   │   ├── values/                              # Colors, strings, themes, styles
│   │   │   │   └── mipmap/                              # App launcher icons
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   ├── test/                                        # Unit tests
│   │   └── androidTest/                                 # Instrumented tests
│   │
│   ├── build.gradle                                     # App-level dependencies
│   └── google-services.json                             # Firebase config (not in repo)
│
├── assets/                                              # README screenshots
├── gradle/                                              # Version catalog & wrapper
├── build.gradle                                         # Project-level config
├── settings.gradle
└── README.md
```

---

## 👥 User Roles

| Role | Access | Key Features |
|------|--------|--------------|
| 👤 **Patient** | Patient Home, Book Appointment, Treatment History, Bills History | Register & login · View profile dashboard · Book appointments with date/time picker · Review diagnosis & prescriptions · View itemized medical bills |
| 🩺 **Doctor** | Doctor Dashboard, Pending Appointments, Today's Appointments, Treatment & Billing, Patient History | Register & login · Manage pending booking requests · Accept/reject scheduled visits · Record diagnosis, prescription & progress · Generate itemized bills · Browse patient medical records |

---

## 🛠️ Technology Stack

### 🎨 Frontend

| Technology | Purpose |
|------------|---------|
| **XML Layouts** | Declarative UI for all activity screens and list item cards |
| **Material Design Components** | Themed buttons, text fields, cards, and chips |
| **CardView** | Elevated card containers for appointments and history items |
| **RecyclerView** | Dynamic, scrollable lists for appointments, bills, and treatment records |
| **ConstraintLayout** | Flexible, responsive screen composition |
| **Date/Time Pickers** | Native Android pickers for appointment scheduling |

### ☁️ Backend & Cloud

| Technology | Purpose |
|------------|---------|
| **Firebase Authentication** | Email/password user registration, login, and session management |
| **Firebase Realtime Database** | Cloud persistence with live listeners for instant cross-role sync |
| **Firebase BOM** | Centralized dependency version management for Firebase SDKs |

### 🔧 Tools & Build

| Tool | Purpose |
|------|---------|
| **Android Studio** | IDE for development, debugging, and emulator testing |
| **Gradle** | Build automation and dependency resolution |
| **Google Services Plugin** | Firebase project integration via `google-services.json` |
| **Version Catalog** (`libs.versions.toml`) | Centralized library version management |
| **JUnit & Espresso** | Unit and UI testing frameworks |

### 💻 Language & Platform

| Detail | Value |
|--------|-------|
| **Language** | Java 11 |
| **Min SDK** | API 24 (Android 7.0) |
| **Target SDK** | API 36 |
| **Package Name** | `com.medical.app` |

---

## 🚀 Quick Start (Installation)

### 📋 Prerequisites

Before you begin, ensure the following are installed and configured:

| Requirement | Details |
|-------------|---------|
| ☕ **JDK** | Version 11 or higher |
| 🤖 **Android Studio** | Latest stable version (Hedgehog or newer recommended) |
| 🔥 **Firebase Account** | With Authentication and Realtime Database enabled |
| 📱 **Device / Emulator** | API Level 24 (Android 7.0) or above |
| 🌐 **Internet Connection** | Required for Firebase services |

---

### 📥 Step 1 — Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/MedicalManagementSystem.git
cd MedicalManagementSystem
```

---

### 🔥 Step 2 — Configure Firebase

1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project (or use an existing one).
2. Navigate to **Authentication → Sign-in method** and enable **Email/Password**.
3. Navigate to **Realtime Database** and create a new database instance.
4. Apply appropriate **security rules** to restrict access to authenticated users.
5. Click **Add app → Android** and register with package name:

   ```
   com.medical.app
   ```

6. Download the generated **`google-services.json`** file.
7. Place it inside the **`app/`** directory:

   ```
   MedicalManagementSystem/
   └── app/
       └── google-services.json   ← Place file here
   ```

> ⚠️ **Important:** The project will **not build** without a valid `google-services.json` in the `app/` directory. This file is excluded from version control for security.

---

### ▶️ Step 3 — Build & Run

**Option A — Android Studio (Recommended)**

1. Open Android Studio → **File → Open** → select the `MedicalManagementSystem` folder.
2. Wait for Gradle sync to complete.
3. Connect a physical device or launch an emulator.
4. Click **Run ▶** (or press `Shift + F10`).

**Option B — Command Line**

```bash
# Windows
gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

---

### 🧪 Step 4 — Verify the App

- [ ] Launch app and confirm the **Role Selection** screen appears
- [ ] Register a **Patient** account and reach the Patient Dashboard
- [ ] Register a **Doctor** account and reach the Doctor Dashboard
- [ ] Book an appointment as Patient and verify it appears in Doctor's pending list
- [ ] Accept the appointment, add treatment details, and generate a bill

---

## ⚠️ Production Checklist & Security

Use this checklist before deploying or submitting the project:

### 🔐 Security

- [x] Firebase Authentication enabled with Email/Password sign-in
- [x] Role-based routing — patients and doctors directed to separate portals post-login
- [x] User role stored and validated in Firebase Realtime Database
- [x] `google-services.json` excluded from version control (`.gitignore`)
- [ ] Firebase Realtime Database rules configured for authenticated-only access
- [ ] Firebase Realtime Database rules enforce role-aware read/write permissions
- [ ] No hardcoded credentials or API keys in source code

### 🧪 Testing & Quality

- [x] App launches successfully on API 24+ devices and emulators
- [x] Patient registration, login, and dashboard flow verified
- [x] Doctor registration, login, and dashboard flow verified
- [x] Appointment booking and doctor accept/reject workflow tested
- [x] Treatment entry and bill generation end-to-end tested
- [ ] Unit tests (`ExampleUnitTest`) expanded for core business logic
- [ ] Instrumented UI tests (`ExampleInstrumentedTest`) expanded for critical flows

### 📦 Build & Release

- [x] Debug build compiles without errors via Gradle
- [ ] Release build tested with ProGuard rules reviewed
- [ ] App icon and branding assets finalized
- [ ] README screenshots up to date with current UI

---

## 🎯 Learning Outcomes

Through the design and implementation of **MedSync**, the following academic and technical competencies were demonstrated:

| # | Learning Outcome | Description |
|---|------------------|-------------|
| 1 | 🔥 **Firebase Integration** | End-to-end setup of Authentication and Realtime Database with live `ValueEventListener` data binding |
| 2 | 📋 **RecyclerView & CardView** | Dynamic list rendering with custom card layouts for appointments, history, and billing screens |
| 3 | 🎭 **Role-Based Authentication** | Dual-portal architecture with cloud-stored role flags driving conditional navigation |
| 4 | 🎨 **Material Design UI** | Consistent XML theming — input fields, primary/secondary buttons, cards, and screen backgrounds |
| 5 | ☁️ **Real-Time Data Sync** | CRUD operations on Firebase with instant UI updates visible across Patient and Doctor roles |
| 6 | 📱 **Android Activity Lifecycle** | Multi-screen navigation with intent-based routing across 14 activity classes |

---

## 🔮 Future Enhancements

The following features are planned for future iterations of MedSync:

| Enhancement | Description |
|-------------|-------------|
| 📄 **PDF Bill Generation** | Export and share itemized medical bills as downloadable PDF documents |
| 🔔 **Push Notifications** | Appointment reminders, accept/reject alerts, and bill confirmations via Firebase Cloud Messaging (FCM) |
| 📹 **Video Consultations** | In-app telemedicine sessions between patients and doctors |
| 💬 **In-App Messaging** | Secure chat channel for pre- and post-appointment doctor-patient communication |
| 🌙 **Dark Mode Toggle** | User-selectable theme with full night-mode resource support |

---

## 👨‍💻 Author

<div align="center">

**Aliza Tariq**

University of Agriculture, Faisalabad

*CS-512 — Mobile App Development*

<br/>

`com.medical.app` · Android · Java · Firebase

<br/>

⭐ *If you found this project useful, consider giving it a star!*

</div>
