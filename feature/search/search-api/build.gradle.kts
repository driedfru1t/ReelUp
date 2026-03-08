plugins {
    alias(libs.plugins.reelup.android.library)
    alias(libs.plugins.kotlin.serialization)
}


android {
    namespace = "com.nikol.search_api"
}

dependencies {
    implementation(projects.core.navigation.navApi)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation3.runtime)
}