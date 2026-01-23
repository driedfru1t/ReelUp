package com.nikol.viewmodel

import com.nikol.direct_android.DirectViewModel
import com.nikol.direct_core.DirectEffect
import com.nikol.direct_core.DirectIntent
import com.nikol.direct_core.DirectState

interface DirectRouter

abstract class DirectRouterViewModel<INTENT : DirectIntent, STATE : DirectState, EFFECT : DirectEffect, ROUTER : DirectRouter> :
    DirectViewModel<INTENT, STATE, EFFECT>() {

    var router: ROUTER? = null
        private set

    fun attachRouter(r: ROUTER) {
        this.router = r
    }

    fun detachRouter() {
        this.router = null
    }

    protected inline fun navigate(block: ROUTER.() -> Unit) {
        router?.run(block)
    }

    override fun onCleared() {
        detachRouter()
        super.onCleared()
    }
}