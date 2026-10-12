# Agro - Intelligent Farming Assistant

**Agro** is a comprehensive Android application designed to empower farmers with data-driven insights and modern agricultural tools. From disease detection to real-time weather updates, Agro serves as a digital companion for the modern agriculturist.

## Start here

Agro brings crop-image classification, local weather, and links to farming resources into one Android app.

1. Sign in and open the farmer dashboard.
2. Take or select a plant image in the disease detector to view the model's predicted class, confidence, and associated remedy information.
3. Allow location access to view current OpenWeatherMap conditions and the app's rule-based farming suggestions.
4. Explore crop-management and government-scheme screens, or open linked agricultural portals from the marketplace screen.

**Scope:** the marketplace screen opens external portals such as e-NAM and Agmarknet; it is not an in-app buying/selling transaction system. The plant detector is a prototype classifier, and its confidence display is not a measured field-accuracy guarantee.

### Where to look in the code

- [DiseaseDetectorActivity.java](app/src/main/java/com/example/agro/DiseaseDetectorActivity.java): image capture/selection and on-device TensorFlow Lite inference.
- [WeatherActivity.java](app/src/main/java/com/example/agro/WeatherActivity.java): location and current-weather lookup.
- [MarketplaceActivity.java](app/src/main/java/com/example/agro/MarketplaceActivity.java): external agricultural-resource links.
- [app/build.gradle.kts](app/build.gradle.kts): Android SDK 36, minimum API 24, Java 11, and weather-key configuration.

## 🚀 Features

*   **Plant Disease Detection**: Utilize the power of AI to identify crop diseases instantly. Simply take a photo or upload from the gallery to get a predicted disease class and associated remedies (Cultural & Chemical).
*   **Real-time Weather Updates**: Stay ahead of the weather with current weather conditions to better plan irrigation, sowing, and harvesting.
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
    git clone https://github.com/parzival787/agro.git
    ```
2.  **Open in Android Studio**:
    Import the project and let Gradle sync.
3.  **Firebase Setup**:
    *   Add your `google-services.json` to the `app/` directory.
    *   Enable Email/Password authentication in the Firebase Console.
    *   Setup Firestore and Storage buckets.
4.  **API Keys**:
    *   Create a `local.properties` file in the root directory.
    *   Add your OpenWeatherMap key as a quoted Java string literal: `WEATHER_API_KEY="your_api_key_here"`. The build script passes this value directly to `buildConfigField`.
5.  **Build and Run**:
    Click the "Run" button in Android Studio to deploy the app to your device or emulator.

---
*Developed to bridge the gap between technology and traditional farming.*
