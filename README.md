ShopNow User App

Welcome to ShopNow User App! This app connects you with vendors seamlessly, providing a convenient shopping experience.

Dependencies

To ensure the app functions correctly, make sure you have the following dependencies and plugins added to your build.gradle.kts file:

kotlin
Copy code
plugins {
    id("kotlin-android")
    id("kotlin-kapt") // Kotlin Annotation Processing Plugin
}

dependencies {
    implementation("com.google.firebase:firebase-messaging:22.0.0") // Replace with the latest version
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.google.firebase:firebase-storage:20.0.0")
}

Usage
Firebase Messaging: This dependency is crucial for receiving real-time notifications from vendors about your orders and updates.

Glide: Glide is used for efficient image loading and caching within the app.

Glide Compiler: The Glide annotation processor is used to generate Glide's API.

Firebase Storage: Firebase Storage is utilized for storing and retrieving user data securely.

Kotlin Kapt Plugin
Make sure you have the Kotlin Annotation Processing Plugin (kotlin-kapt) applied in your build.gradle.kts file to enable annotation processing for libraries like Glide.

Becoming a Vendor
If you're interested in becoming a vendor on ShopNow, you can find the vendor app code  here https://github.com/kanavnayyer/shopnowVendor. Feel free to explore and contribute to the vendor side of our platform!

