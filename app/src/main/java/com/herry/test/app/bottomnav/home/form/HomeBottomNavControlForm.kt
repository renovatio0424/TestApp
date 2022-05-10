package com.herry.test.app.bottomnav.home.form

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.util.ViewUtil
import com.herry.test.R
import java.io.Serializable

class HomeBottomNavControlForm(
    private val onSelectedItem: (itemType: HomeBottomNavScreenId) -> Unit
) : NodeForm<HomeBottomNavControlForm.Holder, HomeBottomNavControlForm.Model>(Holder::class, Model::class) {

    @Suppress("unused")
    class Model(
        val selected: HomeBottomNavScreenId = HomeBottomNavScreenId.FEEDS,
        val items: ArrayList<HomeBottomNavControlItemForm.Model> = arrayListOf()
    ) : Serializable

    inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
        val items: LinearLayout = view.findViewById(R.id.home_bottom_nav_control_form_items)

        init {
            ViewUtil.setProtectTouchLowLayer(view, true)
        }
    }

    override fun onLayout(): Int = R.layout.home_bottom_nav_control_form

    override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

    override fun onBindModel(context: Context, holder: Holder, model: Model) {
        holder.items.let { container ->
            container.removeAllViews()

            model.items.forEach { item ->
                // create view
                val itemForm = HomeBottomNavControlItemForm(onClick = { id ->
                    val selectedScreenId = HomeBottomNavScreenId.generate(id) ?: return@HomeBottomNavControlItemForm
                    onSelectedItem.invoke(selectedScreenId)
                }).apply {
                    createFormHolder(context, container)
                }
                val childView = itemForm.getView()
                if (childView != null) {
                    ViewUtil.removeViewFormParent(childView)
                    childView.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                        this.weight = 1f
                    }

                    childView.isSelected = model.selected.id == item.id
                    // add view to container
                    container.addView(childView)

                    // bind data
                    itemForm.bindFormModel(context, item)
                }
            }
        }

        onSelectedItem.invoke(model.selected)
    }
}