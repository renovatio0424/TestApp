package com.herry.test.app.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.herry.libs.app.activity_caller.module.ACNavigation
import com.herry.libs.app.activity_caller.module.ACPermission
import com.herry.libs.helper.ToastHelper
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.model.NodeRoot
import com.herry.libs.nodeview.recycler.NodeRecyclerAdapter
import com.herry.libs.nodeview.recycler.NodeRecyclerForm
import com.herry.libs.widget.extension.setOnProtectClickListener
import com.herry.test.R
import com.herry.test.app.base.BaseView
import com.herry.test.app.base.ac.AppACNavigation
import com.herry.test.app.checker.main.DataCheckerMainFragment
import com.herry.test.app.gif.list.GifListFragment
import com.herry.test.app.intent.list.IntentListFragment
import com.herry.test.app.layout.LayoutSampleFragment
import com.herry.test.widget.TitleBarForm
import kotlinx.android.synthetic.main.main_fragment.view.*
import kotlinx.android.synthetic.main.main_test_item.view.*


/**
 * Created by herry.park on 2020/06/11.
 **/
class MainFragment : BaseView<MainContract.View, MainContract.Presenter>(), MainContract.View {

    override fun onCreatePresenter(): MainContract.Presenter? = MainPresenter()

    override fun onCreatePresenterView(): MainContract.View = this

    override val root: NodeRoot
        get() = adapter.root

    private val adapter: Adapter = Adapter()

    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == this.container) {
            this.container = inflater.inflate(R.layout.main_fragment, container, false)
            init(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return

        TitleBarForm(
            activity = requireActivity()
        ).apply {
            bindFormHolder(view.context, view.main_fragment_title)
            bindFormModel(view.context, TitleBarForm.Model(title = "Test List"))
        }

        view.main_fragment_list.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            if (itemAnimator is SimpleItemAnimator) {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            adapter = this@MainFragment.adapter
        }
    }

    inner class Adapter: NodeRecyclerAdapter(::requireContext) {
        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
            list.add(TestItemForm())
        }
    }

    override fun onScreen(type: MainContract.TestItemType) {
        when (type) {
            MainContract.TestItemType.SCHEME_TEST -> activityCaller?.call(AppACNavigation.SingleCaller(IntentListFragment::class))
            MainContract.TestItemType.GIF_DECODER -> {
                activityCaller?.call(
                    ACPermission.Caller(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        onGranted = {
                            Handler(Looper.getMainLooper()).post {
                                activityCaller?.call(
                                    AppACNavigation.SingleCaller(
                                        GifListFragment::class
                                    )
                                )
                            }
                        }
                    ))
            }
            MainContract.TestItemType.CHECKER_LIST -> activityCaller?.call(
                AppACNavigation.SingleCaller(
                    DataCheckerMainFragment::class
                )
            )
            MainContract.TestItemType.LAYOUT_SAMPLE -> activityCaller?.call(
                AppACNavigation.SingleCaller(
                    LayoutSampleFragment::class
                )
            )
            MainContract.TestItemType.PICK_PHOTO -> {
                val intent = Intent(Intent.ACTION_PICK).apply {
                    setType(MediaStore.Images.Media.CONTENT_TYPE)
                    data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                intent.resolveActivity(activity?.packageManager ?: return) ?: return

                activityCaller?.call(
                    ACPermission.Caller(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        onGranted = {
                            activityCaller?.call(ACNavigation.IntentCaller(intent) { resultCode, intent, _ ->
                                if (Activity.RESULT_OK == resultCode) {
                                    val selectedImage: Uri? = intent?.data

                                    ToastHelper.showToast(activity, "selected photo: ")
                                } else {
                                    ToastHelper.showToast(activity, "cancel photo selection")
                                }
                            })
                        }
                    ))
            }
        }
    }

    private inner class TestItemForm : NodeForm<TestItemForm.Holder, MainContract.TestItemType>(Holder::class, MainContract.TestItemType::class) {
        inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
            init {
                view.setOnProtectClickListener {
                    NodeRecyclerForm.getBindModel(this@TestItemForm, this@Holder)?.let {
                        presenter?.moveToScreen(it)
                    }
                }
            }
        }

        override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

        override fun onLayout(): Int = R.layout.main_test_item

        override fun onBindModel(context: Context, holder: TestItemForm.Holder, model: MainContract.TestItemType) {
            holder.view.main_test_item_title.text = when (model) {
                MainContract.TestItemType.SCHEME_TEST -> "Intent"
                MainContract.TestItemType.GIF_DECODER -> "GIF Decoder"
                MainContract.TestItemType.CHECKER_LIST -> "Data Checker"
                MainContract.TestItemType.LAYOUT_SAMPLE -> "Layout Sample"
                MainContract.TestItemType.PICK_PHOTO -> "Take photo"
            }
        }
    }
}