package com.herry.libs.app.nav

import android.os.Bundle
import com.herry.libs.util.BundleUtil
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.full.cast

object NavActionUtil {

    inline fun <reified T: Any> createActionDataBundle(action: String, data: T?): Bundle {
        return NavBundleUtil.createNavigationAction(action).apply {
            data ?: return@apply

            if (data is java.io.Serializable) {
                putSerializable("action_data", data)
            } else {
                putString("action_data", Json.encodeToString(data))
            }
        }
    }

    inline fun <reified T: Any> getActionDataFromBundle(bundle: Bundle): T? {
        val action = NavBundleUtil.getNavigationAction(bundle)
        if (action.isEmpty()) {
            return null
        }

        if (T::class.isInstance(java.io.Serializable::class)) {
            return BundleUtil.getSerializableData(bundle, "action_data", T::class)
        } else {
            val actionData = BundleUtil[bundle, "action_data", ""]
            if (actionData.isNotBlank()) {
                val output = Json.decodeFromString<T>(actionData)
                if (T::class.isInstance(output)) {
                    return T::class.cast(output)
                }
            }
        }
        return null
    }
}