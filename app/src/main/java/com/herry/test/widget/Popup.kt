package com.herry.test.widget

import android.app.Activity
import androidx.annotation.StyleRes
import com.herry.libs.widget.view.AppDialog
import com.herry.test.R

class Popup(activity: Activity?, @StyleRes style: Int = R.style.PopupTheme, @StyleRes dialogThemeResId: Int = 0
) : AppDialog(activity, style, dialogThemeResId)
