# StackOverflow Search

**A Kotlin Multiplatform project for discovering Stack Overflow questions and exploring their answers.**

![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/UI-Compose%20Multiplatform-4285F4)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Repository-2563EB)
![Platform](https://img.shields.io/badge/Platforms-Android%20%7C%20iOS-64748B)

## Overview

StackOverflow Search is a mobile application built as a coding assessment. Users can search Stack Overflow by keyword or phrase, browse matching questions, and open a question to read its full content and answers. The Android application implements the search-to-details flow using shared Kotlin Multiplatform presentation, domain, and data code.

The repository also contains an iOS application target. **Current status:** the iOS entry point still presents the Compose Multiplatform starter UI; the Stack Overflow search flow is currently wired into the Android application.

## Features

- **Question search:** Search Stack Overflow using a keyword or phrase, with a search action from the keyboard.
- **Search results:** Browse questions with titles, excerpts, answer counts, vote counts, view counts, and author information.
- **Question details:** Open a result to view the question body and its answers.
- **Answer sorting:** Switch between **Active**, **Oldest**, and **Votes** ordering.
- **Rich content:** Convert API-provided HTML content into styled Compose text.
- **Connectivity awareness:** Display offline states and allow retrying requests.
- **UI state management:** Represent idle, loading, success, error, and no-network states explicitly.
- **In-memory question cache:** Retain retrieved questions by ID for navigation to the details screen.
- **Automated tests:** Android host tests for search and details ViewModels and the repository.

## Screenshots

> Add screenshots of the running Android app to `screenshots/` and update the paths below.

| Search | Results | Question details |
|:---:|:---:|:---:|
| _Screenshot coming soon_ | _Screenshot coming soon_ | _Screenshot coming soon_ |

## Tech stack

| Area | Technology |
| --- | --- |
| Language | Kotlin |
| Shared code | Kotlin Multiplatform (Android and iOS targets) |
| UI | Compose Multiplatform, Material 3 |
| Presentation | MVVM, `ViewModel`, Kotlin `StateFlow` |
| Asynchronous work | Kotlin coroutines |
| Dependency injection | Koin |
| Networking | Ktor Client (OkHttp on Android, Darwin on iOS) |
| Serialization | Kotlinx Serialization |
| Navigation | Navigation Compose |
| Image loading | Coil 3 |
| Tests | JUnit 5, MockK, kotlinx-coroutines-test, Kotlin Test |
| API | Stack Exchange API v2.2 |

## Architecture

The code is organized around **MVVM with a repository abstraction and separated presentation, domain, and data responsibilities**.

```text
Compose screens
      |
      v
ViewModels (StateFlow / UI state)
      |
      v
StackOverflowRepository (interface)
      |
      v
StackOverflowRepositoryImpl
      |
      v
QuestionsService -> KtorQuestionsService -> Stack Exchange API
      |
      v
Response DTOs -> domain mappers -> Question / Answer
```

- **Presentation:** `QuestionSearchViewModel`, `QuestionDetailsViewModel`, screen composables, reusable components, and navigation.
- **Domain:** `Question`, `Answer`, `AnswerSort`, repository contract, and DTO-to-domain mappers.
- **Data:** Ktor-backed API service, DTOs, repository implementation, and in-memory question cache.
- **Dependency injection:** Koin modules provide the HTTP client, service, repository, and ViewModels.
- **Platform integration:** Android-specific network connectivity monitoring and URL launching.

The repository stores search results in an in-memory map keyed by question ID. The details ViewModel uses that cache to obtain the selected question before requesting its answers. This is an **in-memory cache**, not offline persistence.

### Project structure

```text
Stackoverflow/
├── androidApp/
│   └── src/main/kotlin/com/apps/stackoverflow/
│       ├── MainActivity.kt
│       ├── StackoverflowApplication.kt
│       ├── connectivity/
│       ├── di/
│       └── platform/
├── shared/
│   └── src/
│       ├── commonMain/kotlin/com/apps/stackoverflow/
│       │   ├── data/
│       │   │   └── remote/
│       │   │       ├── dto/
│       │   │       ├── repository/
│       │   │       └── QuestionsService.kt
│       │   ├── domain/
│       │   ├── di/
│       │   ├── presentation/
│       │   │   ├── components/
│       │   │   ├── navigation/
│       │   │   └── theme/
│       │   └── utils/
│       ├── androidMain/
│       ├── iosMain/
│       ├── androidHostTest/
│       ├── commonTest/
│       └── iosTest/
├── iosApp/
├── gradle/
└── settings.gradle.kts
```

## API integration

The app uses the public [Stack Exchange API](https://api.stackexchange.com/docs) with the base URL:

```text
https://api.stackexchange.com/2.2/
```

**Search questions**

```http
GET /search/advanced?pagesize=20&order=desc&sort=activity&title={query}&site=stackoverflow&filter=withbody
```

**Get answers for a question**

```http
GET /questions/{questionId}/answers?order=desc&sort=activity&site=stackoverflow&filter=withbody
```

The answer endpoint changes its sorting parameters according to the selected option:

| Option | `sort` | `order` |
| --- | --- | --- |
| Active | `activity` | `desc` |
| Oldest | `creation` | `asc` |
| Votes | `votes` | `desc` |

Responses are deserialized into DTOs and mapped to domain models before being exposed to the UI. The API is public; this implementation does not require a user-provided API key.

## Getting started

### Requirements

- Android Studio with support for the project's Kotlin and Android Gradle Plugin versions
- Android SDK **37** (as configured in `gradle/libs.versions.toml`)
- JDK compatible with the configured Android Gradle Plugin
- Android device or emulator running **Android 7.0 (API 24)** or later
- For iOS: macOS, Xcode, and an iOS simulator or supported device

### Clone the repository

```bash
git clone https://github.com/Sbonelo054/StackOv.git
cd StackOv
```

### Run on Android

Open the project in Android Studio, allow Gradle to sync, select the `androidApp` run configuration, and run on an emulator or connected device.

Alternatively, build a debug APK:

**macOS / Linux**

```bash
./gradlew :androidApp:assembleDebug
```

**Windows PowerShell**

```powershell
.\gradlew.bat :androidApp:assembleDebug
```

The generated debug APK is typically located at `androidApp/build/outputs/apk/debug/`.

### Run on iOS

On macOS, open `iosApp/iosApp.xcodeproj` in Xcode and select a simulator or connected device. The current iOS application launches the shared Compose starter screen. Integrating the search/navigation flow into the iOS entry point is a future enhancement.

## Testing

The project includes Android host-side unit tests for:

- `QuestionSearchViewModel`: query updates, search states, network handling, errors, and retry behavior.
- `QuestionDetailsViewModel`: question retrieval, answer loading, sorting, and error states.
- `StackOverflowRepositoryImpl`: service interactions, sorting parameters, failure handling, and cache behavior.

Run the Android host tests:

```bash
./gradlew :shared:testAndroidHostTest
```

On Windows:

```powershell
.\gradlew.bat :shared:testAndroidHostTest
```

Run the iOS simulator test target on a supported macOS environment:

```bash
./gradlew :shared:iosSimulatorArm64Test
```

> Test files are included in the repository; this README does not claim that every test has been executed successfully in CI.

## Design decisions

**Shared application layers.** Core models, repository logic, ViewModels, and Compose UI live in `shared` to reduce duplication between platform targets.

**Explicit UI states.** Sealed UI states make loading, successful results, errors, and no-network scenarios straightforward to render and test.

**Repository contract.** ViewModels depend on `StackOverflowRepository` rather than the HTTP implementation, making the presentation layer easier to unit test.

**Simple caching.** An in-memory question cache allows the details screen to retrieve the question selected from search results without an additional question-details request. The cache is cleared when the process ends.

## Potential improvements

- Wire the existing search and details navigation flow into the iOS application.
- Add pagination to retrieve results beyond the initial page of 20 questions.
- Introduce persistent caching for previously viewed questions and answers.
- Add UI/integration tests and automated CI checks.
- Add screenshots and short demo recordings to this README.
- Improve accessibility and support additional screen sizes.

## Acknowledgements

Question and answer content is provided by the [Stack Exchange API](https://api.stackexchange.com/docs). Stack Overflow and Stack Exchange are trademarks of their respective owners. This is an independent assessment project and is not affiliated with or endorsed by Stack Overflow.
