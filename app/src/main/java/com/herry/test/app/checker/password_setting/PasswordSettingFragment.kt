package com.herry.test.app.checker.password_setting

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.model.NodeRoot
import com.herry.libs.nodeview.recycler.NodeRecyclerAdapter
import com.herry.libs.nodeview.recycler.NodeRecyclerForm
import com.herry.libs.util.AppUtil
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.test.R
import com.herry.test.app.base.BaseView
import com.herry.test.widget.TitleBarForm
import kotlinx.android.synthetic.main.data_checker_main_fragment.view.*
import kotlinx.android.synthetic.main.data_checker_main_password.view.*

/**
 * Created by herry.park on 2020/7/7
 **/
class PasswordSettingFragment : BaseView<PasswordSettingContract.View, PasswordSettingContract.Presenter>(), PasswordSettingContract.View {

    override fun onCreatePresenter(): PasswordSettingContract.Presenter? =
        PasswordSettingPresenter()

    override fun onCreatePresenterView(): PasswordSettingContract.View = this

    override val root: NodeRoot
        get() = adapter.root

    private val adapter: Adapter = Adapter()

    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == this.container) {
            this.container = inflater.inflate(R.layout.data_checker_main_fragment, container, false)
            init(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return

        TitleBarForm(
            activity = requireActivity(),
            onClickBack = { AppUtil.pressBackKey(requireActivity(), view) }
        ).apply {
            bindFormHolder(view.context, view.data_checker_main_fragment_title)
            bindFormModel(view.context, TitleBarForm.Model(title = "Data Checker Main", backEnable = true))
        }

        view.data_checker_main_fragment_container.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            if (itemAnimator is SimpleItemAnimator) {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            adapter = this@PasswordSettingFragment.adapter
        }
    }

    inner class Adapter: NodeRecyclerAdapter(::requireContext) {
        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
            list.add(DataCheckerPasswordItemForm())
        }
    }

    private inner class DataCheckerPasswordItemForm : NodeForm<DataCheckerPasswordItemForm.Holder, PasswordSettingContract.PasswordModel>(
        Holder::class, PasswordSettingContract.PasswordModel::class) {
        inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
            init {
                view.setOnProtectClickListener {
                    NodeRecyclerForm.getBindModel(this@DataCheckerPasswordItemForm, this@Holder)?.let {
//                        presenter?.showPasswordSetting(it)
                    }
                }
            }
        }

        override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

        override fun onLayout(): Int = R.layout.data_checker_main_password

        override fun onBindModel(context: Context, holder: Holder, model: PasswordSettingContract.PasswordModel) {
            holder.view.data_checker_main_password_status.text = if (model.password.isBlank()) "Not exit password" else "Exist password"
        }
    }
}