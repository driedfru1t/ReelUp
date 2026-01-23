package com.nikol.auth_impl.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nikol.auth_impl.presentation.viewModel.CheckLogInPageViewModel
import com.nikol.auth_impl.presentation.viewModel.CheckRouter
import com.nikol.di.scope.directViewModel

@Composable
fun CheckScreen(
    navToStart: () -> Unit,
    navToHome: () -> Unit
) {
    val viewModel = directViewModel<CheckLogInPageViewModel, CheckRouter> {
        object : CheckRouter {
            override fun toStart() = navToStart()
            override fun toHome() = navToHome()
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}