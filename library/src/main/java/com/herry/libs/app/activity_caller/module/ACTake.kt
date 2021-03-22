package com.herry.libs.app.activity_caller.module

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import com.herry.libs.app.activity_caller.ACModule
import com.herry.libs.app.activity_caller.activity.ACActivity
import java.io.Serializable

class ACTake(private val caller: Caller, private val listener: ACTakeListener) : ACModule {

    companion object {
        const val USE_ACTIVITY_RESULT_TAKE_CONTRACT = false
    }

    data class Result(
        val callActivity: ComponentActivity,
        val uri: Uri?,
        val success: Boolean
    ) : Serializable

    open class Caller(
        internal val uri: Uri,
        internal val onResult: ((result: Result) -> Unit)? = null
    )

    class TakePicture(
        uri: Uri,
        onResult: ((result: Result) -> Unit)? = null
    ) : Caller(uri, onResult)

    class TakeVideo(
        uri: Uri,
        onResult: ((result: Result) -> Unit)? = null
    ) : Caller(uri, onResult)

    interface ACTakeListener: ACModule.OnListener<ACTake>, ACModule.OnTakeListener

    override fun call() {
        val activity = listener.getActivity() as ACActivity
        when(caller) {
            is TakePicture -> {
                if (USE_ACTIVITY_RESULT_TAKE_CONTRACT) {
                    listener.takePicture(caller.uri, caller.onResult)
                } else {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    // Ensure that there's a camera activity to handle the intent
                    intent.resolveActivity(activity.packageManager ?: return) ?: return

                    // adds permission to other app
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, caller.uri)

                    activity.activityCaller.call(ACNavigation.IntentCaller(intent, onResult = { result ->
                        caller.onResult?.invoke(Result(result.callActivity, caller.uri, result.resultCode == Activity.RESULT_OK))
                    }))
                }
            }

            is TakeVideo -> {
                if (USE_ACTIVITY_RESULT_TAKE_CONTRACT) {
                    listener.takeVideo(caller.uri, caller.onResult)
                } else {
                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    // Ensure that there's a camera activity to handle the intent
                    intent.resolveActivity(activity.packageManager ?: return) ?: return

                    // adds permission to other app
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, caller.uri)

                    activity.activityCaller.call(ACNavigation.IntentCaller(intent, onResult = { result ->
                        caller.onResult?.invoke(Result(result.callActivity, caller.uri, result.resultCode == Activity.RESULT_OK))
                    }))
                }
            }
        }
    }
}