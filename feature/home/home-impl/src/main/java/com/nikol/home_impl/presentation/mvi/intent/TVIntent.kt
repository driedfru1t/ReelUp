package com.nikol.home_impl.presentation.mvi.intent

import com.nikol.direct_core.DirectIntent
import com.nikol.home_impl.domain.parameters.Period

sealed interface TVIntent : DirectIntent {
    data object RefreshData : TVIntent
    data class NavigateToDetail(val id: String) : TVIntent
    data object LoadAllData : TVIntent
    data class ChangePeriodForTrend(val period: Period) : TVIntent
}