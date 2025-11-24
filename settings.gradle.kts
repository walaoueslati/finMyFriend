pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Repositories principaux pour télécharger toutes les dépendances
        google()
        mavenCentral()
        // Optionnel : JitPack si tu utilises des libs GitHub
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "wala"
include(":app")
