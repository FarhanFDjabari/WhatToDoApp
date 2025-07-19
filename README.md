# WhatToDoApp

WhatToDoApp is a simple to-do list application built with Kotlin Multiplatform, targeting Android and iOS. It allows users to manage their tasks efficiently, with features like setting due dates and times, marking tasks as complete, and receiving notifications for upcoming deadlines.

## Features

*   **Create, Read, Update, and Delete (CRUD) Tasks:** Easily add, view, edit, and delete your tasks.
*   **Due Dates and Times:** Set specific deadlines for your tasks to stay organized.
*   **Task Completion:** Mark tasks as complete to track your progress.
*   **Search:** Quickly find specific tasks using the search functionality.
*   **Notifications:** Get reminders for tasks that are due soon.

## Tech Stack

*   **Kotlin Multiplatform:** Share code between Android and iOS for a consistent experience.
*   **Compose Multiplatform:** Build the user interface for both platforms with a single codebase.
*   **Koin:** A lightweight dependency injection framework for Kotlin.
*   **SQLDelight:** Generate type-safe Kotlin APIs from your SQL statements.

## Running the App

### Android

To run the application on Android, you can use Android Studio.

1.  Open the project in Android Studio.
2.  Select the `composeApp` run configuration.
3.  Choose an Android emulator or a physical device.
4.  Click the "Run" button.

### iOS

To run the application on iOS, you'll need a Mac with Xcode installed.

1.  Open a terminal and navigate to the project's root directory.
2.  Run the following command to build the Xcode project:
    ```bash
    ./gradlew :shared:embedAndSignAppleFrameworkForXcode
    ```
3.  Open the `iosApp/iosApp.xcworkspace` file in Xcode.
4.  Select an iOS simulator or a physical device.
5.  Click the "Run" button.
