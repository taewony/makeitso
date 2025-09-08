# Plan: Porting V2 TODO Application to a Default Android Studio Project Structure (V3)

This plan outlines the steps to migrate the existing V2 application to a new V3 module with a standard Android Studio project structure and updated package names.

## 1. Project Structure Setup (V3)

*   Create a new top-level directory named `v3`.
*   Mimic the standard Android project structure within `v3/app/src/main`. This includes:
    *   `java/com/example/makeitso` for Kotlin source files.
    *   `res/` for all application resources (drawable, layout, values, etc.).
*   Create corresponding directories for `androidTest` and `test`.

## 2. File Migration

*   **Copy, don't move:** To ensure a safe fallback, copy the entire `v2` directory contents to the new `v3` directory. This includes:
    *   Gradle build files (`build.gradle.kts`, `settings.gradle.kts`, etc.).
    *   Source code from `v2/app/src/main/java/com/google/firebase/example/makeitso` to `v3/app/src/main/java/com/example/makeitso`.
    *   Resources from `v2/app/src/main/res` to `v3/app/src/main/res`.
    *   Test files from `v2/app/src/androidTest` and `v2/app/src/test` to their corresponding locations in `v3`.

## 3. Package Name Refactoring

*   Perform a project-wide search for the old package name `com.google.firebase.example.makeitso`.
*   Replace all occurrences with the new package name `com.example.makeitso`.
*   **Key files to update:**
    *   All Kotlin files (`.kt`) in `v3/app/src/main/java/com/example/makeitso`.
    *   `v3/app/src/main/AndroidManifest.xml`.
    *   `v3/app/build.gradle.kts` (specifically the `applicationId`).

## 4. Build Configuration Updates

*   **`settings.gradle.kts`:** Update the root `settings.gradle.kts` to include the new `:v3:app` module.
*   **`v3/app/build.gradle.kts`:**
    *   Verify the `applicationId` is set to `com.example.makeitso`.
    *   Ensure all dependencies and plugins are correctly configured.
*   **`v3/build.gradle.kts`:** Review the project-level build file for any necessary adjustments.

## 5. Verification

*   **Sync Gradle:** Perform a Gradle sync to ensure all dependencies are resolved and the project is correctly configured.
*   **Build the project:** Run a full build to check for any compilation errors.
*   **Run tests:** Execute all unit and instrumentation tests to verify the application logic remains correct.
*   **Run the application:** Deploy the application to an emulator or device to ensure it runs as expected.
