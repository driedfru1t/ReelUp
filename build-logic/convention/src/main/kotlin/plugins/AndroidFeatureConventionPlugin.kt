package plugins

import extensions.implementation
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.reelup.android.library.get().pluginId)
                apply(libs.plugins.reelup.android.compose.library.get().pluginId)
                apply(libs.plugins.kotlin.serialization.get().pluginId)
            }

            dependencies {
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
                implementation(libs.kotlinx.serialization.json)

                implementation(platform(libs.ktor.bom))
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.okHttp)
                implementation(libs.ktor.client.resources)

                implementation(platform(libs.arrow.bom))
                implementation(libs.arrow.core)

                implementation(libs.androidx.material.icons.extended)
                implementation(libs.kotlinx.collections.immutable)

                implementation(libs.androidx.navigation3.runtime)
                implementation(libs.androidx.navigation3.ui)
                implementation(libs.androidx.lifecycle.viewmodel.navigation3)
            }
        }
    }
}