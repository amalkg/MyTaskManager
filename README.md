# TaskManager

## Description
Task Manager App is a simple Android application that allows users to create, manage, and organize their tasks efficiently.

![Screenshot 1](https://github.com/amalkg/TaskManager/assets/63140350/ddf8add1-e996-43c9-bc63-2952ac55fbbd)

![Screenshot 2](https://github.com/amalkg/TaskManager/assets/63140350/8124596d-1955-41d2-8f3f-e5f2cbf1178d)

![Screenshot 3](https://github.com/amalkg/TaskManager/assets/63140350/59b03b39-808c-4130-bc3f-903fbdf96fc8)

## Installation
1. Clone the repository.
2. Open the project in Android Studio.
3. Checkout to the master branch
4. Build and run the project on an Android device or emulator.

## Features
- Create tasks with detailed descriptions, category, priority and deadlines.
- Reminders for each tasks.
- Mark tasks as complete when finished.
- Delete and update tasks.

## Technologies Used
- Kotlin - https://kotlinlang.org/docs/android-overview.html
- MVVM - software architectural pattern that separates the development of user interface (UI) from business logic and data.
  - Model - represents the data and business logic of the application. It can include data sources, such as a database, network calls, or repositories.
  - View -  represents the UI components of the application. It includes activities, fragments, or views responsible for displaying data and capturing user input.
  - View model - serves as an intermediary between the Model and the View. It holds the UI-related data needed for the View, but it's independent of the View itself.

![mvvm-arch](https://github.com/amalkg/TaskManager/assets/63140350/11b43de2-26c8-437c-992c-664faa9cbcb8)

- Hilt - https://developer.android.com/training/dependency-injection/hilt-android
- Data binding - https://developer.android.com/topic/libraries/data-binding
- Datastore - https://developer.android.com/reference/kotlin/androidx/datastore/core/DataStore
- Work manager - https://developer.android.com/reference/androidx/work/WorkManager
- Unit test - https://developer.android.com/training/testing/local-tests

## Author
- Amal K G
