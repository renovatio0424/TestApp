package com.herry.test.app.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.herry.libs.helper.ApiHelper
import com.herry.test.app.base.activity_caller.AC

open class BaseFragment: Fragment() {
    internal open var aC: AC? = null
    protected open var screenTag = ""

    protected fun getDefaultArguments(): Bundle {
        return arguments ?: Bundle()
    }

    fun setDefaultArguments(bundle: Bundle) {
        setArguments(bundle)
    }

    @Suppress("DEPRECATION")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        if (ApiHelper.hasMarshmallow()) {
            return
        }

        onAttachToContext(activity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (ApiHelper.hasMarshmallow()) {
            onAttachToContext(context)
        }
    }

    protected open fun onAttachToContext(context: Context?) {
        if(context is AC) {
            aC = context
        } else {
            aC = null
        }
    }

    override fun onDetach() {
        super.onDetach()
        aC = null
    }

    open fun onBackPressed(): Boolean = false
}