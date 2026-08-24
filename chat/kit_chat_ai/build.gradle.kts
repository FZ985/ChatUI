plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val appCompileSdk: Int by rootProject.ext
val appMinSdk: Int by rootProject.ext


android {
    namespace = "io.ai.chat"
    compileSdk = appCompileSdk

    defaultConfig {
        minSdk = appMinSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }

}

dependencies {
    implementation(rootProject.ext["Activity"] as String)
    implementation(rootProject.ext["FragmentKtx"] as String)
    implementation(rootProject.ext["KtxCore"] as String)
    implementation(rootProject.ext["Material"] as String)
    implementation(rootProject.ext["Recyclerview"] as String)

    implementation(rootProject.ext["AndroidViewModel"] as String)
    implementation(rootProject.ext["AndroidLiveData"] as String)
    implementation(rootProject.ext["AndroidLifecycleRuntime"] as String)

    implementation(rootProject.ext["Gson"] as String)
    implementation(rootProject.ext["Okhttp"] as String)
    implementation(rootProject.ext["Okio"] as String)

    implementation(rootProject.ext["blurview"] as String)
    implementation(rootProject.ext["prism4j"] as String) {
        exclude(group = "org.jetbrains", module = "annotations-java5")
    }
    implementation(rootProject.ext["flexmark"] as String)
    implementation(rootProject.ext["ratexAndroid"] as String)

    api(project(":chat:chat_uicommon"))


}