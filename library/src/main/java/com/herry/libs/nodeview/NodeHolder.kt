package com.herry.libs.nodeview

import android.content.Context
import android.view.View

open class NodeHolder(val context: Context, val view: View) {
    var position: (() -> Int)? = null
}