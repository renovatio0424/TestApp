package com.herry.libs.app.activity_caller.module

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.herry.libs.app.activity_caller.AC
import com.herry.libs.app.activity_caller.ACModule
import com.herry.libs.app.activity_caller.activity.ACActivity
import com.herry.libs.app.nav.NavMovement

open class ACNavigation(private val caller: Caller, private val listener: ACModule.OnListener<ACNavigation>): ACModule {

    open class Caller(
        internal val useTransition: Boolean = true,
        internal val transitions: Array<Transition>? = null,
        internal val result: ((resultCode: Int, intent: Intent?, bundle: Bundle?) -> Unit)? = null
    )

    class Transition(
        internal val view: View,
        internal val bitmap: Bitmap,
        internal val name: String
    )

    class IntentCaller (
        internal val intent: Intent,
        internal val bundle: Bundle? = null,
        useTransition: Boolean = true,
        transitions: Array<Transition>? = null,
        result: ((resultCode: Int, intent: Intent?, bundle: Bundle?) -> Unit)? = null
    ) : Caller(useTransition, transitions, result)

    class NavCaller (
        internal val cls: Class<out ACActivity>,
        internal val bundle: Bundle? = null,
        internal val startDestination: Int = 0,
        useTransition: Boolean = true,
        transitions: Array<Transition>? = null,
        result: ((resultCode: Int, intent: Intent?, bundle: Bundle?) -> Unit)? = null
    ) : Caller(useTransition, transitions, result)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == AC.REQ_NAVIGATION) {
            done()
            Handler(Looper.getMainLooper()).post {
                caller.result?.let {
                    it(resultCode, data, data?.getBundleExtra(NavMovement.NAV_BUNDLE))
                }
            }
            return true
        }
        return false
    }

    open fun getCallerIntent(activity: Activity): Intent? {
        return when(caller) {
            is NavCaller -> {
                Intent(activity, caller.cls).apply {
                    caller.bundle?.let {
                        putExtra(NavMovement.NAV_BUNDLE, it)
                    }
                    if (caller.startDestination != 0) {
                        putExtra(NavMovement.NAV_START_DESTINATION, caller.startDestination)
                    }
                }
            }
            is IntentCaller -> {
                caller.intent.apply {
                    caller.bundle?.let {
                        putExtra(NavMovement.NAV_BUNDLE, it)
                    }
                }
            }
            else -> null
        }
    }

    final override fun call() {
        val activity = listener.getActivity()

        val intent = getCallerIntent(activity) ?: return

        if(caller.useTransition) {
            var options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle()
            if (caller.transitions?.isNotEmpty() == true) {
                val pairs = mutableListOf<androidx.core.util.Pair<View, String>>()
                val bitmaps = mutableListOf<Pair<String, Bitmap>>()

                for (transition in caller.transitions) {
                    pairs.add(androidx.core.util.Pair.create(transition.view, transition.name))
                    bitmaps.add(Pair(transition.name, transition.bitmap))
                }

                if (pairs.isNotEmpty() && bitmaps.isNotEmpty()) {
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity,
                        *pairs.toTypedArray()
                    ).toBundle()
                    for (bitmap in bitmaps) {
                        ACTransitionUtil.BitmapStorage.put(bitmap.first, bitmap.second)
                    }
                }
            }


            if (caller.result != null) {
                activity.startActivityForResult(intent, AC.REQ_NAVIGATION, options)
            } else {
                activity.startActivity(intent, options)
                done()
            }
        } else {
            if (caller.result != null) {
                activity.startActivityForResult(intent, AC.REQ_NAVIGATION)
            } else {
                activity.startActivity(intent)
                done()
            }
        }
    }

    private fun done() {
        listener.onDone(this)
    }
}