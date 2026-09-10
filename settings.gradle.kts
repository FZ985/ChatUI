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

include(":markdown:fluid-markdown")
include(":markdown:markwon-core")
include(":markdown:markwon-ext-latex")
include(":markdown:markwon-ext-strikethrough")
include(":markdown:markwon-ext-tables")
include(":markdown:markwon-ext-tasklist")
include(":markdown:markwon-html")
include(":markdown:markwon-image")
include(":markdown:markwon-inline-parser")
include(":markdown:markwon-syntax-highlight")


include(":blurDemo")
