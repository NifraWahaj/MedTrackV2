# MedTrack Android Application

MedTrack is a mobile application designed to help users manage their medication schedules and stay updated with health-related content. The app provides features such as adding and managing medication reminders, reading and writing blogs, and interacting with other users through forums.

## Functionalities

### 1. **User Authentication**

* **Firebase Authentication**:
  * User registration and login.
  * Password recovery.
  * Account deletion.
  * Password updates.

### 2. **Medication Management**

* **Add Medication**:
  * Medications can be added with the following frequency options:
    * **Once Daily**.
    * **Twice Daily**.
    * **Interval** (e.g., every 4 hours starting from X and ending at Y).
    * **Specific Days** (e.g., once on selected days within a date range).
  * Set the medication schedule using a date/time picker.
  * Specify refill amount and refill threshold to receive notifications when refills are low.
* **Manage Medications**:
  * Medications are categorized into:
    * **Active Meds**: Medications with a due date on or after today.
    * **Inactive Meds**: Medications whose schedule has ended.
  * Update refill amount and threshold at any time.
  * Skip or mark a medication as taken.
  * Delete a medication.
* **Notifications**:
  * Receive alerts when medication refills fall below the threshold.

### 3. **Home Tab**

* **Horizontal Calendar**:
  * Select today’s date or navigate up to 5 days ahead/behind.
  * View medications scheduled for the selected day.
* **Quick Actions**:
  * Update medication details (e.g., refill amount, refill threshold).
  * Skip or mark a medication as taken.

### 4. **Alerts Tab**

* Notifications for:
  * Medication refill reminders.
  * Community updates (e.g., blog reviews, ratings).

### 5. **Community (Blog) Tab**

* **Viewing Blogs**:
  * Users can browse and read various health-related blogs posted by other users.
* **Adding Blogs**:
  * Users can write and post their own blogs.
* **Editing Blogs**:
  * Users can edit their previously posted blogs.
* **Deleting Blogs**:
  * Users can delete their blogs if necessary.
* **Blog Content Navigation**:
  * When a user clicks on a blog card, they are directed to the detailed blog view in the `<span>BlogContentFragment</span>`.
* **Write Blogs**:
  * Rich-text editor with features like:
    * Bold, italics, headings.
    * Lists, color text.
    * Image insertion.
    * Undo/redo options.
  * Submit blogs for admin approval.
* **View Blogs**:
  * Approved blogs appear in the community feed.
  * Search functionality to find specific blogs.
* **Engage with Blogs**:
  * Rate blogs (1-5 stars).
  * Write reviews.
  * Notify authors about reviews and ratings.

### 6. **Profile Management**

* Upload and update profile photos.
* Edit personal information like name.
* View analytics and reports about medication adherence and patterns.
* Delete account.

## Tabs Overview

1. **Alerts**: View notifications for medications and community updates.
2. **Meds**: Manage active and inactive medications.
3. **Home**: View daily medication schedules and perform quick actions.
4. **Community**: Interact with blogs, submit your own, and provide feedback.
5. **Profile**: Manage personal information and access analytics.

## Technologies Used

* **Frontend**: Java (Mobile App)
* **Backend**: Firebase
  * Firebase Authentication
  * Firebase Firestore (Database)
  * Firebase Storage
  * Firebase Cloud Messaging (Push Notifications)

## Setup and Installation

To set up the project locally, follow these steps:

### Prerequisites

- Android Studio
- Firebase account (for Firebase Authentication, Firestore Database, and Notifications)

### Installation Steps

1. **Clone the Repository**Clone the repository using the following command:

   ```
   git clone https://github.com/NifraWahaj/MedTrackV2.git
   ```
2. **Open the Project in Android Studio**Open the cloned project in Android Studio.
3. **Configure Firebase**

   - Set up Firebase by creating a new project in the [Firebase Console](https://console.firebase.google.com/).
   - Follow the instructions to add Firebase to your Android app, including downloading the `google-services.json` file and adding it to the `app/` directory.
   - Enable Firebase Authentication (Email/Password) and Firestore Database.
4. **Dependencies**Ensure all dependencies are synced by opening the `build.gradle` files (both project and app-level) and clicking "Sync Now."
5. **Running the Application**
   Connect an Android device or use an emulator and click the "Run" button in Android Studio.

## Directory Structure

```
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   ├── com/
│   │   │   │   │   ├── medtrack/
│   │   │   │   │   │   ├── activities/          # Contains activities like MainActivity
│   │   │   │   │   │   ├── fragments/           # Contains fragments like BlogContentFragment
│   │   │   │   │   │   ├── adapters/            # Contains adapters like BlogAdapter
│   │   │   │   │   │   ├── models/              # Data models for Blog, MedReminder, etc.
│   │   │   │   │   │   └── utils/               # Helper classes
│   │   │   │   ├── res/
│   │   │   │   │   ├── layout/                  # XML files for layouts
│   │   │   │   │   ├── drawable/                # Image resources
│   │   │   │   │   ├── values/                  # Colors, strings, styles
│   │   │   │   └── AndroidManifest.xml          # Android app configuration file
├── build.gradle
└── settings.gradle
```

## Firebase Setup

1. **Firebase Authentication**:Ensure that Firebase Authentication is enabled in the Firebase Console. Users can log in with email and password.
2. **Firestore Database**:Configure Firestore to store and retrieve blog posts, medication reminders, and user profiles.
3. **Push Notifications**:
   For reminders, you can integrate Firebase Cloud Messaging (FCM) to push notifications when it's time for medication.

## Admin Panel

The admin panel for approving blogs and viewing analytical reports is built using ReactJS with Firebase. You can find the repository for the admin panel [here.](https://github.com/NifraWahaj/MedTrack-Admin)

## Screenshots

## Screenshots

<img src="/medtrack%20screenshots/medtrack15.jpeg" alt="Screenshot 1" width="300">
<img src="/medtrack%20screenshots/medtrack8.jpeg" alt="Screenshot 2" width="300">
<img src="/medtrack%20screenshots/medtrack3.jpeg" alt="Screenshot 3" width="300">
<img src="/medtrack%20screenshots/medtrack2.jpeg" alt="Screenshot 4" width="300">
<img src="/medtrack%20screenshots/medtrack.jpeg" alt="Screenshot 5" width="300">
<img src="/medtrack%20screenshots/medtrack13.jpeg" alt="Screenshot 6" width="300">
<img src="/medtrack%20screenshots/medtrack14.jpeg" alt="Screenshot 7" width="300">
<img src="/medtrack%20screenshots/medtrack11.jpeg" alt="Screenshot 8" width="300">
<img src="/medtrack%20screenshots/medtrack10.jpeg" alt="Screenshot 9" width="300">
<img src="/medtrack%20screenshots/medtrack9.jpeg" alt="Screenshot 10" width="300">
<img src="/medtrack%20screenshots/medtrack8.jpeg" alt="Screenshot 11" width="300">
<img src="/medtrack%20screenshots/medtrack5.jpeg" alt="Screenshot 12" width="300">
<img src="/medtrack%20screenshots/medtrack4.jpeg" alt="Screenshot 13" width="300">





