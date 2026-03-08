plugins {
    alias(libs.plugins.reelup.android.library)
    alias(libs.plugins.reelup.android.compose.library)
    alias(libs.plugins.reelup.koin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.nikol.nav_impl"
}

dependencies {
    api(projects.core.navigation.navApi)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}