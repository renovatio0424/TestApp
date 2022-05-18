package com.herry.libs.nodeview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import kotlin.reflect.KClass
import kotlin.reflect.full.safeCast

@Suppress("unused")
abstract class NodeForm<H : NodeHolder, M : Any>(val hClass: KClass<H>, val mClass: KClass<M>) : NodeView<H>() {

    var model: M? = null
        private set

    open fun createFormHolder(context: Context, parent: ViewGroup?, attach: Boolean = true) {
        super.createHolder(context, parent, attach)
    }

    open fun bindFormHolder(context: Context, parent: ViewGroup?, id: Int) {
        super.bindHolder(context, parent, id)
    }

    open fun bindFormHolder(context: Context, view: View?) {
        super.bindHolder(context, view)
    }

    open fun bindFormModel(context: Context, model: M?) {
        this.model = model
        this.holder?.let { _holder ->
            this.model?.let {
                onBindModel(context, _holder, it)
            } ?: onBindModelEmpty(context, _holder)
        }
    }

    open fun notifyChangedModel(context: Context) {
        bindFormModel(context, this.model)
    }

    fun bindModel(context: Context, holder: NodeHolder, model: Any) {
        val cHolder: H? = hClass.safeCast(holder)
        val cModel: M? = mClass.safeCast(model)

        if (cHolder != null && cModel != null) {
            @Suppress("UNCHECKED_CAST")
            this.model = model as M
            onBindModel(context, cHolder, cModel)
        }
    }

    protected open fun onBindModelEmpty(context: Context, holder: H) {
    }

    abstract fun onBindModel(context: Context, holder: H, model: M)
}