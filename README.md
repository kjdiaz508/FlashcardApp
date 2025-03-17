# Flashcard App

## Description
This Flashcard App allows users to create, review, and quiz themselves using flashcards that include both text and images. Users can take pictures with their phone camera or select images from their gallery to attach to flashcards. The app features a study mode, a create mode, and settings for customization. It is built with Jetpack Compose for UI and leverages Android's media storage and database capabilities.

## Figma Design
![Design](https://github.com/user-attachments/assets/e95d09fd-c1e8-410f-954e-a9fabd5c4ed9)

## Features
- **Room Database** for storing flashcards and decks persistently.
- **ViewModel & State Management** for handling UI state efficiently.
- **Camera Integration** for capturing images and attaching them to flashcards.
- **Media Storage Access** to allow users to select images from their gallery.
- **DataStore Preferences** for storing settings such as dark mode.
- **Creating a Set** a screen for creating the flashcard set
- **Viewing all sets in Home Screen** All sets displayed in a grid
- **Review a flashcard set** Study an individual set by flipping each flashcard

## Third-Party Libraries
- **Coil** for image loading (`coil3.compose.AsyncImage`).
- **Jetpack Room** for database management.
- **Jetpack Navigation** for navigation between screens.
- **DataStore Preferences** for persistent app settings.

## Dependencies & Requirements
- **Minimum SDK Version**: 24
- **Target SDK Version**: 34
- **Device Features Required**:
  - Camera (`android.hardware.camera.any`)
  - Storage access for saving and retrieving images.

## Additional comments
- Room databse took a long time to figure out, the book was not extremely helpful because they left out a lot of information about how to add the proper version of the dependency and some information was outdated.
- I may have missed it, but the book seems to skip over how to request permissions during runtime, so I had to figure out how to do that.
- I followed the code style used in the book as closely as I could, but I went simpler if something didn't make sense to me.
- Implemented dark mode using DataStore Preferences.

## How to Run the App
1. Clone the repository.
2. Open the project in Android Studio .
3. Ensure you have an Android device or emulator with Camera & Storage permissions enabled.
4. Build and run the app using the `Run` option in Android Studio.

## Future Enhancements
- Add a quiz feature with multiple-choice questions.
- Implement cloud sync for flashcard storage.
- Improve UI/UX with animations and better accessibility features.

