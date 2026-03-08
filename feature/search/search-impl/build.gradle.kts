plugins {
    alias(libs.plugins.reelup.android.feature)
}


android {
    namespace = "com.nikol.search_impl"
}

dependencies {
    implementation(projects.core.domainUtil)
    implementation(projects.core.security)
    implementation(projects.core.network)
    implementation(projects.core.di)
    implementation(projects.core.ui)
    implementation(projects.core.navigation.navApi)
    implementation(projects.core.navigation.navImpl)
    implementation(projects.feature.detail.detailApi)
    implementation(projects.feature.search.searchApi)
    implementation(projects.core.viewModel)

    implementation(libs.pagin.runtime)
    implementation(libs.pagin.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor)
}
