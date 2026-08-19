# Agro - Intelligent Farming Assistant

**Agro** is a comprehensive Android application designed to empower farmers with data-driven insights and modern agricultural tools. From disease detection to real-time weather updates, Agro serves as a digital companion for the modern agriculturist.

## 🚀 Features

*   **Plant Disease Detection**: Utilize the power of AI to identify crop diseases instantly. Simply take a photo or upload from the gallery to get a diagnosis and recommended remedies (Cultural & Chemical).
*   **Real-time Weather Updates**: Stay ahead of the weather with accurate forecasts to better plan irrigation, sowing, and harvesting.
*   **Farmer Dashboard**: A centralized hub for quick access to all vital farming information and modules.
*   **Marketplace**: A dedicated space for farmers to explore market trends and potentially connect with agricultural commerce.
*   **Government Schemes**: Stay informed about the latest agricultural subsidies, grants, and schemes provided by the government.
*   **Crop Management**: Tools to help track and manage different crop cycles effectively.
*   **Secure Authentication**: Integrated with Firebase for secure user registration and login.
*   **Profile Management**: Personalized user profiles with data stored securely in Firestore.

## 🛠 Tech Stack

*   **Language**: Java / Kotlin (DSL for Gradle)
*   **Backend**: Firebase (Authentication, Firestore, Storage)
*   **Machine Learning**: TensorFlow Lite (Plant Disease Model)
*   **Networking**: Volley, OkHttp
*   **UI Components**: Material Design, Android X, SwipeRefreshLayout, BottomSheetDialog
*   **Image Processing**: Glide, ThumbnailUtils

## 📦 Installation & Setup

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/YOUR_USERNAME/agro.git
    ```
2.  **Open in Android Studio**:
    Import the project and let Gradle sync.
3.  **Firebase Setup**:
    *   Add your `google-services.json` to the `app/` directory.
    *   Enable Email/Password authentication in the Firebase Console.
    *   Setup Firestore and Storage buckets.
4.  **API Keys**:
    *   Create a `local.properties` file in the root directory.
    *   Add your Weather API key: `WEATHER_API_KEY=your_api_key_here`.
5.  **Build and Run**:
    Click the "Run" button in Android Studio to deploy the app to your device or emulator.

---
*Developed to bridge the gap between technology and traditional farming.*
