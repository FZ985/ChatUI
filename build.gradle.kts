// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
//    id("com.android.application") version "8.2.1" apply false
//    id("com.android.library") version "8.2.1" apply false
//    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.android.application") version "8.12.2" apply false
    id("com.android.library") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false
}
apply(from = "gradle/app.gradle")
apply(from = "gradle/config.gradle")