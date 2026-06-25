<div align="center">
  <h1>🏥 MedSync - Medical Management System</h1>

  <p align="center">
    <img src="https://img.shields.io/badge/PLATFORM-ANDROID-3DDC84?style=for-the-badge" alt="Platform">
    <img src="https://img.shields.io/badge/LANGUAGE-JAVA-ED8B00?style=for-the-badge" alt="Language">
    <img src="https://img.shields.io/badge/BACKEND-FIREBASE-FFCA28?style=for-the-badge" alt="Backend">
    <img src="https://img.shields.io/badge/MIN_SDK-23-007ACC?style=for-the-badge" alt="Min SDK">
    <img src="https://img.shields.io/badge/TARGET_SDK-36-4CAF50?style=for-the-badge" alt="Target SDK">
  </p>

  <h3>A role-based Android application for managing clinic appointments, patient histories, and medical billing — powered by Firebase.</h3>
  
  <i>Submitted for CS-512 Mobile App Development<br>
  University of Agriculture, Faisalabad<br>
  Instructor: Prof. Waseem</i>
</div>

<br>
<hr>

## ✨ Core Features

### 👤 Patient Portal

- 🔐 **Registration** — Secure email/password sign-up via Firebase Authentication
- 🏠 **Dashboard** — Profile overview with quick navigation to all patient features
- 📅 **Book Appointments** — Schedule visits with doctor name, date/time picker, and problem description
- 🧾 **Bills History** — Review itemized medical bills from completed appointments
- 💊 **Treatment History** — Access diagnosis, prescription, and progress records in one place

### 🩺 Doctor Portal

- 🏠 **Dashboard** — Central hub with profile, specialization, and navigation shortcuts
- ⏳ **Manage Pending Appointments** — Review incoming patient booking requests in real time
- ✅ **Accept / Reject Appointments** — Approve or decline scheduled visits with one tap
- 🩺 **Clinical Records** — Record diagnosis, prescription, and treatment progress per visit
- 💰 **Automated Billing** — Generate itemized bills with consultation and medicine fees

### 🔐 Security Features

- 🔑 **Firebase Authentication** — Industry-standard email/password auth with secure session management
- 🎭 **Role-Based Routing** — Post-login navigation driven by user role (`patient` or `doctor`)

---

## 🔄 App Flow (High-Level Navigation)

```mermaid
graph TD
    A[🚀 LoginRegisterActivity] --> B{Select Role}
    
    B -->|Patient| C[📄 PatientRegisterActivity]
    B -->|Doctor| D[📄 DoctorRegisterActivity]
    B -->|Login| E[🔐 LoginActivity]
    
    E --> F{Read role from Firebase}
    
    F -->|patient| G[🏠 PatientHomeActivity]
    G --> H[📅 Book Appointment]
    G --> I[💊 Treatment History]
    G --> J[🧾 Bills History]
    
    F -->|doctor| K[🏠 DoctorProfileActivity]
    K --> L[⏳ Pending Appointments]
    K --> M[📅 Today's Appointments]
    K --> N[📚 Patient History]
    
    L --> O[✅ AppointmentActionActivity]
    M --> O
    
    O -->|Accept| P[📝 AppointmentDetailActivity]
    P --> Q[💰 Complete & Generate Bill]
```

---

## 📸 Visual Showcase

### 👤 Patient Experience

| Role Selection | Patient Registration | Patient Dashboard |
|:---:|:---:|:---:|
| <img src="assets/role-selection.jpeg" width="250"> | <img src="assets/patient-register.jpeg" width="250"> | <img src="assets/patient-dashboard.jpeg" width="250"> |

### 🩺 Doctor Workflow

| Doctor Registration | Doctor Dashboard | Appointment Action |
|:---:|:---:|:---:|
| <img src="assets/doctor-register.jpeg" width="250"> | <img src="assets/doctor-dashboard.jpeg" width="250"> | <img src="assets/appointment-action.jpeg" width="250"> |

### 🏥 Treatment & Records

| Today's Appointments | Treatment & Billing | |
|:---:|:---:|:---:|
| <img src="assets/todays-appointments.jpeg" width="250"> | <img src="assets/treatment-billing-detail.jpeg" width="250"> | |

---

## 👥 User Roles

| Role | Access | Key Features |
|------|--------|--------------|
| 👤 **Patient** | Patient Home, Booking, History screens | Register & login · View profile · Book appointments · Treatment history · Bills history |
| 🩺 **Doctor** | Doctor Dashboard, Appointment management, Clinical tools | Register & login · Pending appointments · Accept/reject visits · Update treatment · Generate bills · Patient history |

---

## 🛠️ Technology Stack

**Frontend**
- XML layouts · Material Design Components · CardView · RecyclerView · ConstraintLayout

**Backend**
- Firebase Authentication (Email/Password) · Firebase Realtime Database

**Language**
- Java 11

**Tools**
- Android Studio · Gradle · Google Services Plugin

---

## 🚀 Quick Start

**1. Clone the repository**

```bash
git clone https://github.com/YOUR_USERNAME/MedicalManagementSystem.git
cd MedicalManagementSystem
```

**2. Configure Firebase**

1. Create a project at the [Firebase Console](https://console.firebase.google.com/)
2. Enable **Email/Password** sign-in under Authentication
3. Create a **Realtime Database** and apply security rules
4. Register an Android app with package name `com.medical.app`
5. Download `google-services.json` and place it in the **`app/`** directory

**3. Build & Run**

```bash
./gradlew assembleDebug
```

Open the project in **Android Studio**, connect a device or launch an emulator, and click **Run ▶**.

> ⚠️ **Critical:** Ensure you place your `google-services.json` file inside the `app/` directory before building.

---

## ⚠️ Production Checklist

- [ ] **Secure Setup Directory** — Confirm `google-services.json` is in `app/` and excluded from version control
- [ ] **Update Firebase Rules** — Apply authenticated, role-aware security rules in the Realtime Database
- [ ] **Test All User Workflows** — Verify Patient and Doctor registration, booking, treatment, and billing flows end-to-end

---

## 🎯 Learning Outcomes

Through building **MedSync**, the following academic competencies were demonstrated:

- 🔥 **Firebase Integration** — End-to-end setup of Authentication and Realtime Database with live data listeners
- 📋 **RecyclerView & CardView** — Dynamic list rendering for appointments, history, and billing screens
- 🎭 **Role-Based Authentication** — Dual-portal routing based on user role stored in the cloud
- 🎨 **Material Design UI** — Consistent theming, input fields, buttons, and card-based layouts in XML

---

## 🔮 Future Enhancements

- 📄 **PDF Bill Generation** — Export and share itemized medical bills as downloadable PDF documents
- 🔔 **Push Notifications** — Appointment reminders, accept/reject alerts, and bill confirmations via FCM
- 📹 **Video Consultations** — In-app telemedicine sessions between patients and doctors
- 💬 **In-App Messaging** — Secure chat channel for pre- and post-appointment communication

---

## 👨‍💻 Author

> **Aliza Tariq**<br>
> University of Agriculture, Faisalabad<br>
> Department of Computer Science<br>
> Course: CS-512 — Mobile App Development<br>
> Instructor: Prof. Waseem

<br>
<hr>
<br>

<p align="center">Made with ❤️ using Android Studio & Firebase</p>
