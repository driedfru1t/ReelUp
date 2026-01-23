pluginManagement {
    includeBuild("build-logic")
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
        mavenLocal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "ReelUp"
include(":app")
include(":core:di")
include(":core:viewModel")
include(":feature:auth:auth-api")
include(":feature:auth:auth-impl")
include(":core:navigation:nav-api")
include(":core:navigation:nav-impl")
include(":core:network")
include(":core:domainUtil")
include(":core:security")
include(":feature:home:home-api")
include(":feature:home:home-impl")
include(":core:ui")
include(":feature:detail:detail-api")
include(":feature:detail:detail-impl")
include(":feature:search:search-api")
include(":feature:search:search-impl")
