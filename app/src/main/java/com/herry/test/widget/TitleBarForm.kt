package com.herry.test.widget

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.test.R


/**
 * Created by herry.park on 2020/06/18.
 **/
class TitleBarForm(
    private val activity: Activity,
    private val onClickBack : (() -> Unit)? = null,
    private val onClickAction: (() -> Unit)? = null,
) : NodeForm<TitleBarForm.Holder, TitleBarForm.Model>(Holder::class, Model::class) {
    inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
        val container: Toolbar? = view.findViewById(R.id.title_bar_view_container)
        val action: TextView? = view.findViewById(R.id.title_bar_view_action)

        init {
            action?.setOnProtectClickListener { onClickAction?.let { it() } }
        }
    }

    override fun onLayout(): Int = R.layout.title_bar_view

    override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

    override fun onBindModel(context: Context, holder: Holder, model: Model) {
        holder.container?.let { toolBar ->
            toolBar.title = model.title
            toolBar.setBackgroundColor(model.backgroundColor ?: kotlin.run {
                val typedValue = TypedValue()
                val a: TypedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(android.R.attr.colorPrimary))
                val color = a.getColor(0, 0)

                a.recycle()
                color
            })
            (activity as AppCompatActivity?)?.let { activity ->
                activity.setSupportActionBar(toolBar)
                activity.supportActionBar?.setDisplayHomeAsUpEnabled(model.backEnable)
                toolBar.setNavigationOnClickListener { onClickBack?.let { it() } }
            }
        }

        holder.action?.let {
            if (model.action.isNotBlank()) {
                it.visibility = View.VISIBLE
                it.text = model.action
            } else {
                it.visibility = View.GONE
            }
        }
    }

    data class Model (
        val title: String = "",
        val backEnable: Boolean = false,
        val action: String = "",
        @ColorInt val backgroundColor: Int? = null
    )
}