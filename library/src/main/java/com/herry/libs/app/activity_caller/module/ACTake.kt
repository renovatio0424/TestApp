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

    data class Result(
        val callActivity: ComponentActivity,
        val uris: MutableList<Uri> = mutableListOf(),
        val success: Boolean
    ) : Serializable

    open class Caller(
        internal val isMultiple: Boolean = false,
        internal val uri: Uri?,
        internal val onResult: ((result: Result) -> Unit)? = null
    )

    class PickPicture(
        isMultiple: Boolean = false,
        onResult: ((result: Result) -> Unit)? = null
    ) : Caller(isMultiple, null, onResult)

    class PickVideo(
        isMultiple: Boolean = false,
        onResult: ((result: Result) -> Unit)? = null
    ) : Caller(isMultiple, null, onResult)

    class TakePicture(
        uri: Uri,
        onResult: ((result: Result) -> Unit)? = null
    ) : Caller(false, uri, onResult)

    class TakeVideo(
        uri: Uri,
        onResult: ((result: Result) -> Unit)? = null
    ) : Caller(false, uri, onResult)

    interface ACTakeListener: ACModule.OnListener<ACTake>

    override fun call() {
        val activity = listener.getActivity() as ACActivity
        when(caller) {
            is PickPicture -> {
                val intent = Intent(Intent.ACTION_PICK).apply {
                    type = MediaStore.Images.Media.CONTENT_TYPE
                    data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, caller.isMultiple)
                }
                intent.resolveActivity(activity.packageManager ?: return) ?: return

                activity.activityCaller.call(ACNavigation.IntentCaller(intent, onResult = { result ->
                    val uris = mutableListOf<Uri>()

                    val picked: Uri? = result.intent?.data
                    if (picked != null) {
                        uris.add(picked)
                    }

                    caller.onResult?.invoke(Result(result.callActivity, uris, result.resultCode == Activity.RESULT_OK))
                }))
            }

            is PickVideo -> {
                val intent = Intent(Intent.ACTION_PICK).apply {
                    type = MediaStore.Video.Media.CONTENT_TYPE
                    data = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, caller.isMultiple)
                }
                intent.resolveActivity(activity.packageManager ?: return) ?: return

                activity.activityCaller.call(ACNavigation.IntentCaller(intent, onResult = { result ->
                    val uris = mutableListOf<Uri>()

                    val picked: Uri? = result.intent?.data
                    if (picked != null) {
                        uris.add(picked)
                    }

                    caller.onResult?.invoke(Result(result.callActivity, uris, result.resultCode == Activity.RESULT_OK))
                }))
            }

            is TakePicture -> {
                val uri = caller.uri
                if (uri == null) {
                    caller.onResult?.invoke(Result(activity, mutableListOf(), false))
                    return
                }

                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                // Ensure that there's a camera activity to handle the intent
                intent.resolveActivity(activity.packageManager ?: return) ?: return

                // adds permission to other app
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

                activity.activityCaller.call(ACNavigation.IntentCaller(intent, onResult = { result ->
                    caller.onResult?.invoke(Result(result.callActivity, mutableListOf(uri), result.resultCode == Activity.RESULT_OK))
                }))
            }

            is TakeVideo -> {
                val uri = caller.uri
                if (uri == null) {
                    caller.onResult?.invoke(Result(activity, mutableListOf(), false))
                    return
                }

                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                // Ensure that there's a camera activity to handle the intent
                intent.resolveActivity(activity.packageManager ?: return) ?: return

                // adds permission to other app
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

                activity.activityCaller.call(ACNavigation.IntentCaller(intent, onResult = { result ->
                    caller.onResult?.invoke(Result(result.callActivity, mutableListOf(uri), result.resultCode == Activity.RESULT_OK))
                }))
            }
        }
    }
}