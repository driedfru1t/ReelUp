package com.nikol.home_impl.presentation.mvi.intent

import com.nikol.direct_core.DirectIntent
import com.nikol.home_impl.domain.parameters.Period


sealed interface MovieIntent : DirectIntent {
    data object RefreshData : MovieIntent
    data class NavigateToDetail(val id: String) : MovieIntent
    data object LoadAllData : MovieIntent
    data class ChangePeriodForTrend(val period: Period) : MovieIntent
}