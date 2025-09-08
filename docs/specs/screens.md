# Application Screens

This document provides an overview of all the screens available in the application's navigation graph.

---

## 1. Home Screen

-   **Route:** `HomeRoute`
-   **Composable:** `HomeScreen()`
-   **ViewModel:** `HomeViewModel`
-   **Description:** Displays a list of "To-do" lists. This screen was intended as a main dashboard but is not the default start screen. It contains FABs for creating a new list and the "AI Nudge" feature.

---

## 2. To-do List Screen

-   **Route:** `TodoListRoute`
-   **Composable:** `TodoListScreen()`
-   **ViewModel:** `TodoListViewModel`
-   **Description:** The default starting screen of the application. It displays the individual items of a specific to-do list. It performs an authentication check on launch and redirects to the Sign In screen if the user is not logged in.

---

## 3. Settings Screen

-   **Route:** `SettingsRoute`
-   **Composable:** `SettingsScreen()`
-   **ViewModel:** `SettingsViewModel`
-   **Description:** Allows the user to manage application settings. Provides options to navigate back to the main screen or to sign in/out.

---

## 4. Sign In Screen

-   **Route:** `SignInRoute`
-   **Composable:** `SignInScreen()`
-   **ViewModel:** `SignInViewModel`
-   **Description:** Provides UI for users to sign in using their credentials (Email/Password). Also links to the Sign Up screen.

---

## 5. Sign Up Screen

-   **Route:** `SignUpRoute`
-   **Composable:** `SignUpScreen()`
-   **ViewModel:** `SignUpViewModel`
-   **Description:** Allows new users to create an account.

---

## 6. To-do Item Screen

-   **Route:** `TodoItemRoute`
-   **Composable:** `TodoItemScreen()`
-   **ViewModel:** `TodoItemViewModel`
-   **Description:** A screen for adding a new to-do item or editing an existing one.
