package com.herry.test.widget

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.util.AppUtil
import com.herry.libs.widget.view.click.OnProtectClickListener
import com.herry.libs.widget.view.titlebar.TitleBar
import com.herry.test.R


/**
 * Created by herry.park on 2020/06/18.
 **/
class TitleBarForm(
    private val activity: () -> Activity,
    private val onClickBack : (() -> Unit)? = null
) : NodeForm<TitleBarForm.Holder, TitleBarForm.Model>(Holder::class, Model::class) {
    inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
        val titleBar: TitleBar? = view.findViewById(R.id.title_bar_view_container)

        init {
            titleBar?.setFirstActionViewOnClickListener(object : OnProtectClickListener() {
                override fun onSingleClick(v: View) {
                    onClickBack?.invoke() ?: AppUtil.pressBackKey(activity = activity(), view.rootView)
                }
            })
        }
    }

    override fun onLayout(): Int = R.layout.title_bar_view

    override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

    override fun onBindModel(context: Context, holder: Holder, model: Model) {
        holder.titleBar?.let { titleBar ->
            titleBar.setTitle(model.title)
            titleBar.setFirstActionViewVisible(model.backEnable)
            if (model.backgroundColor != null) {
                titleBar.setBackgroundColor(model.backgroundColor)
            }
        }
    }

    data class Model (
        val title: String = "",
        val backEnable: Boolean = false,
        @ColorInt val backgroundColor: Int? = null
    )
}