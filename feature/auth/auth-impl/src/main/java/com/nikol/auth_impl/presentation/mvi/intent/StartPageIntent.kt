package com.nikol.auth_impl.presentation.mvi.intent

import com.nikol.direct_core.DirectIntent

sealed interface StartPageIntent : DirectIntent {
    data object CreateAccount : StartPageIntent
    data object LogIn : StartPageIntent
    data object ContinueWithGuestAccount : StartPageIntent
    data class ChangePassword(val password: String) : StartPageIntent
    data class ChangeLogin(val login: String) : StartPageIntent
    data object SwitchPasswordVisibility : StartPageIntent
}