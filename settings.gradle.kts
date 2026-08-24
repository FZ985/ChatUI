pluginManagement {
    repositories {
        maven("https://jitpack.io")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://jitpack.io")
        google()
        mavenCentral()
    }
}

rootProject.name = "ChatUI"
include(":app")
include(":chat:chat_core")
include(":chat:chat_uicommon")
include(":chat:kit_chat")
include(":chat:kit_conversation")
include(":chat:kit_chat_ai")
