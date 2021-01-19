package com.herry.test.widget

import android.content.Context
import androidx.annotation.StyleRes
import com.herry.libs.widget.view.AppDialog
import com.herry.test.R

class Popup(
    context: Context?,
    @StyleRes style: Int = R.style.PopupTheme,
    @StyleRes dialogThemeResId: Int = 0
) : AppDialog(context, style, dialogThemeResId)