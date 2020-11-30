package com.herry.test.widget

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.test.R

/**
 * Created by herry.park on 2020/06/18.
 **/
class TitleBarForm(
    private val activity: Activity,
    private val onClickBack : (() -> Unit)? = null
) : NodeForm<TitleBarForm.Holder, TitleBarForm.Model>(Holder::class, Model::class) {
    inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
        val container: Toolbar? = view.findViewById(R.id.title_bar_view_container)
    }

    override fun onLayout(): Int = R.layout.title_bar_view

    override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

    override fun onBindModel(context: Context, holder: Holder, model: Model) {
        holder.container?.let { toolBar ->
            toolBar.title = model.title
            (activity as AppCompatActivity?)?.let { activity ->
                activity.setSupportActionBar(toolBar)
                activity.supportActionBar?.setDisplayHomeAsUpEnabled(model.backEnable)
                toolBar.setNavigationOnClickListener { onClickBack?.let { it() } }
            }
        }
    }

    data class Model (
        val title: String = "",
        val backEnable: Boolean = false
    )
}