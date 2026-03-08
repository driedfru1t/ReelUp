package com.nikol.detail_impl.presentation.mvi.intent

import com.nikol.detail_api.ContentType
import com.nikol.detail_api.DetailScreen
import direct.direct_core.DirectIntent

sealed interface DetailIntent : DirectIntent {
    data class LoadAllData(val contentType: ContentType) : DetailIntent
    data object Update : DetailIntent
    data object NavigateBack : DetailIntent
    data class NavigateOtherDetail(val contentType: DetailScreen) : DetailIntent

    data object NavigateToAllActors : DetailIntent
    data object ShowAllPoster : DetailIntent
    data object SeeTriller : DetailIntent
    data object ToggleDescription : DetailIntent
    data object ToggleAction : DetailIntent

}