package com.herry.libs.nodeview.viewPager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.herry.libs.nodeview.INodeRoot
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodeNotify
import com.herry.libs.nodeview.model.NodePosition
import com.herry.libs.nodeview.model.NodeRoot

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class NodeViewPagerAdapter(protected val context: () -> Context, log: Boolean = false): PagerAdapter(),
    INodeRoot {

    private val viewTypeToForms = mutableMapOf<Int, NodeForm<out NodeHolder, *>>()
    private val cachedHolder = mutableListOf<NodeHolder>()
    private val recycledHolder = mutableListOf<NodeHolder>()

    override val root = NodeRoot(object : NodeNotify {
        override fun nodeSetChanged() {}

        override fun nodeMoved(from: Int, to: Int) {}

        override fun nodeChanged(position: Int, count: Int) {}

        override fun nodeInserted(position: Int, count: Int) {}

        override fun nodeRemoved(position: Int, count: Int) {}

        override fun nodeEndTransition() {
            notifyDataSetChanged()
        }
    }, log)

    init {
        this.bindForms()
    }

    private fun bindForms() {
        val formsList = mutableListOf<NodeForm<out NodeHolder,*>>()
        onBindForms(formsList)
        for(i in formsList.indices) {
            viewTypeToForms[i + 1] = formsList[i]
        }
    }

    protected abstract fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>)

    override fun getCount(): Int = root.getViewCount()

    @SuppressLint("SetTextI18n")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        getModel(position)?.let { model ->
            var form: NodeForm<out NodeHolder,*>? = null
            for (viewTypeToForm in viewTypeToForms) {
                if (viewTypeToForm.value.mClass.isInstance(model)) {
                    form = viewTypeToForm.value
                }
            }

            form?.let { nodeForm ->
                var holder = findAndRemoveRecycledHolder(nodeForm)
                if(holder == null) {
                    holder = nodeForm.createHolder(context(), container, false)
                    holder?.let {
                        cachedHolder.add(it)
                    }
                }

                holder?.let {
                    it.position = { position }
                    nodeForm.bindModel(context(), it, model)
                    container.addView(it.view)
                    return it.view
                }
            }
        }

        val tv = TextView(context()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.CENTER

            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
            setPadding(20, 20, 20, 20)
            setBackgroundColor(Color.DKGRAY)
            getModel(position)?.let {
                text = "position : ${position}\nmodel : ${it::class.simpleName}"
            } ?: let {
                text = "position : $position"
            }
        }
        container.addView(tv)
        return tv
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (`object` as? View)?.let {
            container.removeView(it)
            findAndRemoveCashedHolder(it)?.let { holder ->
                holder.position = null
                recycledHolder.add(holder)
            }
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    private fun findAndRemoveCashedHolder(view: View): NodeHolder? {
        for(i in cachedHolder.indices) {
            if(cachedHolder[i].view == view) {
                return cachedHolder.removeAt(i)
            }
        }
        return null
    }

    private fun findAndRemoveRecycledHolder(nodeForm: NodeForm<out NodeHolder, *>): NodeHolder? {
        for(i in recycledHolder.indices) {
            if(nodeForm.hClass.isInstance(recycledHolder[i])) {
                return recycledHolder.removeAt(i)
            }
        }
        return null
    }

    fun getNode(position: Int): Node<*>? = getNodePosition(position)?.let {
        root.getNode(it)
    }

    fun getModel(position: Int): Any? = getNodePosition(position)?.let {
        root.getNode(it)?.model
    }

    fun getNodePosition(position: Int): NodePosition? = root.getNodePosition(position)
}