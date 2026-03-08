package com.nikol.viewmodel


import direct.direct_core.DirectEffect
import direct.direct_core.DirectIntent
import direct.direct_core.DirectState
import direct.direct_core.IntentBuilder
import direct.direct_core.onSingle
import direct.direct_viewmodel.DirectViewModel

@DslMarker
annotation class NavDsl

@NavDsl
interface DirectRouter

@NavDsl
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

    protected inline fun <reified I : INTENT> IntentBuilder<INTENT, STATE, EFFECT>.onNavigate(
        isFinal: Boolean = false,
        crossinline block: ROUTER.(I) -> Unit
    ) = onSingle<I> { intent ->
        navigate {
            block(intent)
            if (isFinal) detachRouter()
        }
    }
    override fun onCleared() {
        detachRouter()
        super.onCleared()
    }
}