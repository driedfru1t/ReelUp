package com.nikol.reelup.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nikol.auth_api.Auth
import com.nikol.auth_impl.presentation.nav.auth
import com.nikol.nav_impl.commonDestination.MainGraph
import com.nikol.reelup.navigation.mainGraph
import com.nikol.ui.theme.ReelUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReelUpTheme {
                val backStack = rememberNavBackStack(Auth)
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    NavDisplay(
                        backStack = backStack,
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator(),
                        ),
                        transitionSpec = {
                            // Новый экран заезжает справа (от 'width' до 0)
                            // Старый экран уезжает влево на 1/4 своей ширины (эффект параллакса)
                            slideInHorizontally(animationSpec = tween(400)) { width -> width } togetherWith
                                    slideOutHorizontally(animationSpec = tween(400)) { width -> -width / 4 }
                        },
                        // Анимация при движении НАЗАД (Pop)
                        popTransitionSpec = {
                            // Предыдущий экран возвращается из параллакса (от -1/4 ширины до 0)
                            // Текущий экран уезжает вправо (от 0 до 'width')
                            slideInHorizontally(animationSpec = tween(400)) { width -> -width / 4 } togetherWith
                                    slideOutHorizontally(animationSpec = tween(400)) { width -> width }
                        },
                        // Анимация для Predictive Back (жест назад пальцем)
                        predictivePopTransitionSpec = {
                            slideInHorizontally(animationSpec = tween(400)) { width -> -width / 4 } togetherWith
                                    slideOutHorizontally(animationSpec = tween(400)) { width -> width }
                        },
                        entryProvider = entryProvider {
                            auth { backStack.apply { clear(); add(MainGraph) } }
                            mainGraph { backStack.apply { clear(); add(Auth) } }
                        }
                    )
                }
            }
        }
    }
}
