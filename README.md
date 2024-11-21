 

# MedTrack Android Application

MedTrack is a mobile application designed to help users manage their medication schedules and stay updated with health-related content. The app provides features such as adding and managing medication reminders, reading and writing blogs, and interacting with other users through forums.

## Features

### 1. **User Authentication**
   - Users can sign up and log in using their credentials.
   - Firebase is used for user authentication.

### 2. **Managing Medication Reminders**
   - Users can add medication reminders with specific time intervals.
   - Reminders are triggered via notifications.
   - Users can edit or delete existing reminders.

### 3. **Blog Management**
   - **Viewing Blogs:** Users can browse and read various health-related blogs posted by other users.
   - **Adding Blogs:** Users can write and post their own blogs.
   - **Editing Blogs:** Users can edit their previously posted blogs.
   - **Deleting Blogs:** Users can delete their blogs if necessary.
   - **Blog Content Navigation:** When a user clicks on a blog card, they are directed to the detailed blog view in the `BlogContentFragment`.

### 4. **Forum Interaction**
   - Users can participate in health-related discussions through the forum section.

### 5. **User Profile**
   - Users can view and edit their profile information, including their name, email, and other personal details.

## Setup and Installation

To set up the project locally, follow these steps:

### Prerequisites

- Android Studio
- Firebase account (for Firebase Authentication, Firestore Database, and Notifications)

### Installation Steps

1. **Clone the Repository**  
   Clone the repository using the following command:
   ```
   git clone https://github.com/NifraWahaj/MedTrackV2.git
   ```

2. **Open the Project in Android Studio**  
   Open the cloned project in Android Studio.

3. **Configure Firebase**  
   - Set up Firebase by creating a new project in the [Firebase Console](https://console.firebase.google.com/).
   - Follow the instructions to add Firebase to your Android app, including downloading the `google-services.json` file and adding it to the `app/` directory.
   - Enable Firebase Authentication (Email/Password) and Firestore Database.

4. **Dependencies**  
   Ensure all dependencies are synced by opening the `build.gradle` files (both project and app-level) and clicking "Sync Now."

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

1. **Firebase Authentication**:  
   Ensure that Firebase Authentication is enabled in the Firebase Console. Users can log in with email and password.

2. **Firestore Database**:  
   Configure Firestore to store and retrieve blog posts, medication reminders, and user profiles.

3. **Push Notifications (Optional)**:  
   For reminders, you can integrate Firebase Cloud Messaging (FCM) to push notifications when it's time for medication.

 

 
