package com.herry.test.app.bottomnav.form

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.test.R
import java.io.Serializable

class HomeBottomNavigatorForm(
    private val onClickItem: (itemType: ItemType) -> Unit
) : NodeForm<HomeBottomNavigatorForm.Holder, HomeBottomNavigatorForm.Model>(Holder::class, Model::class) {

    enum class ItemType {
        FEATURE,
        DISCOVER,
        CREATE,
        ME
    }

    @Suppress("unused")
    class Model(
        val hasNew: Boolean = false,
        val selected: ItemType = ItemType.FEATURE
    ) : Serializable

    inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
        val feature: FrameLayout = view.findViewById(R.id.home_bottom_navigator_form_feature)
        val discover: FrameLayout = view.findViewById(R.id.home_bottom_navigator_form_discover)
        val create: FrameLayout = view.findViewById(R.id.home_bottom_navigator_form_create)
        val me: FrameLayout = view.findViewById(R.id.home_bottom_navigator_form_me)

        init {
            ViewUtil.setProtectTouchLowLayer(view, true)

            feature.setOnProtectClickListener {
                performClick(ItemType.FEATURE)
            }

            discover.setOnProtectClickListener {
                performClick(ItemType.DISCOVER)
            }

            create.setOnProtectClickListener {
                performClick(ItemType.CREATE)
            }

            me.setOnProtectClickListener {
                performClick(ItemType.ME)
            }
        }
    }

    override fun onLayout(): Int = R.layout.home_bottom_navigator_form

    override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

    override fun onBindModel(context: Context, holder: Holder, model: Model) {
        setSelected(model.selected)
        onClickItem(model.selected)
    }

    private fun setSelected(item: ItemType) {
        val holder = this.holder ?: return

        val unSelectViews: MutableList<View> = mutableListOf()
        val selectView: View = when (item) {
            ItemType.FEATURE -> holder.feature
            ItemType.DISCOVER -> holder.discover
            ItemType.CREATE -> holder.create
            ItemType.ME -> holder.me
        }

        if (selectView != holder.feature) unSelectViews.add(holder.feature)
        if (selectView != holder.discover) unSelectViews.add(holder.discover)
        if (selectView != holder.create) unSelectViews.add(holder.create)
        if (selectView != holder.me) unSelectViews.add(holder.me)

        selectView.isSelected = true
        unSelectViews.forEach { view -> view.isSelected = false }
    }

    fun performClick(item: ItemType) {
        setSelected(item)
        onClickItem(item)
    }
}