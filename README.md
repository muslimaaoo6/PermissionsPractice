# Permissions Practice Lab

This is my Android lab project built with Jetpack Compose. The app handles runtime permissions for modern Android versions (API 34+).

## Features
- **Camera Access**: Requests camera permission. Tracks how many retry attempts are left (max 3 tries).
- **Geolocation**: Handles location permissions with a try-counter logic.
- **Photos & Videos**: Requests media access to pick files from the gallery.
- **Blocked State & Settings**: When all tries are exhausted, it locks the button and shows a "Go to Settings" option so the user can enable it manually.

## Tech Stack
- Kotlin
- Jetpack Compose
- Target SDK 34 (Android 14)

## How to run
1. Clone or download the project.
2. Open it in **Android Studio**.
3. Let Gradle sync, then run it on an emulator or real device.