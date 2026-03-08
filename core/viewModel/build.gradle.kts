plugins {
    alias(libs.plugins.reelup.android.library)
    alias(libs.plugins.reelup.android.compose.library)
}

android {
    namespace = "com.nikol.viewmodel"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(projects.core.navigation.navApi)
    implementation(libs.androidx.appcompat)

    val directVersion = "0.3.0"

    //api("com.github.driedfru1t.Direct:direct-android:$directVersion")
    api("direct:direct-viewModel:0.3.0")
}

