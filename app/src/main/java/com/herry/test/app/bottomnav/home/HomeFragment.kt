package com.herry.test.app.bottomnav.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.herry.libs.helper.ToastHelper
import com.herry.libs.util.AppUtil
import com.herry.libs.util.FragmentAddingOption
import com.herry.libs.util.ViewUtil
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavView
import com.herry.test.app.bottomnav.create.CreateFragment
import com.herry.test.app.bottomnav.discover.DiscoverFragment
import com.herry.test.app.bottomnav.feature.FeatureFragment
import com.herry.test.app.bottomnav.form.HomeBottomNavigatorForm
import com.herry.test.app.bottomnav.me.MeFragment

class HomeFragment: BaseNavView<HomeContract.View, HomeContract.Presenter>(), HomeContract.View {
    override fun onCreatePresenter(): HomeContract.Presenter = HomePresenter()

    override fun onCreatePresenterView(): HomeContract.View = this

    private var container: View? = null

    private var fragmentContainer: View? = null

    private val fragments = LinkedHashMap<HomeBottomNavigatorForm.ItemType, Fragment>()

    private val bottomNavigatorForm = HomeBottomNavigatorForm(
        onClickItem = { type ->
            lifecycleScope.launchWhenResumed {
                setFragment(type)
            }
        }
    )

    private var pressedBackKey = false

    private val pressedBackKeyHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.home_fragment, container, false)
            init(this.container)
        } else {
            ViewUtil.removeViewFormParent(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return

        fragmentContainer = view.findViewById(R.id.home_fragment_container)
        bottomNavigatorForm.bindFormHolder(view.context, view.findViewById(R.id.home_fragment_bottom_navigator))
    }

    override fun onNavigator(model: HomeBottomNavigatorForm.Model) {
        val context = this.context ?: return

        if (bottomNavigatorForm.model?.selected == model.selected) return

        bottomNavigatorForm.bindFormModel(context, model)
    }

    private fun setFragment(itemType: HomeBottomNavigatorForm.ItemType) {
        resetPressedBackKey()

        // protect double touch
        if (presenter?.getCurrent() == itemType) return

        var fragment: Fragment? = fragments[itemType]

        if (null == fragment) {
            fragment = when (itemType) {
                HomeBottomNavigatorForm.ItemType.FEATURE -> FeatureFragment.newInstance()
                HomeBottomNavigatorForm.ItemType.DISCOVER -> DiscoverFragment.newInstance()
                HomeBottomNavigatorForm.ItemType.CREATE -> CreateFragment.newInstance()
                HomeBottomNavigatorForm.ItemType.ME -> MeFragment.newInstance(
                    onShowCreate = {
                        bottomNavigatorForm.performClick(HomeBottomNavigatorForm.ItemType.CREATE)
                    }
                )
            }
        }

        val tag = when (itemType) {
            HomeBottomNavigatorForm.ItemType.FEATURE -> HomeFragment::class.simpleName
            HomeBottomNavigatorForm.ItemType.DISCOVER -> DiscoverFragment::class.simpleName
            HomeBottomNavigatorForm.ItemType.CREATE -> CreateFragment::class.simpleName
            HomeBottomNavigatorForm.ItemType.ME -> MeFragment::class.simpleName
        }

        fragments[itemType] = fragment

        AppUtil.setChildFragment(
            this,
            this.fragmentContainer?.id ?: 0,
            fragment,
            FragmentAddingOption(
                tag = tag,
                isAddToBackStack = true
            ))

        presenter?.setCurrent(itemType)
    }

    override fun onResume() {
        super.onResume()

        resetPressedBackKey()
    }

    private fun resetPressedBackKey() {
        pressedBackKeyHandler.removeCallbacksAndMessages(null)
        pressedBackKey = false
    }

    override fun onNavigateUp(): Boolean {
        if (!pressedBackKey) {
            ToastHelper.showToast(activity, "뒤로가기 버튼을 한번\n더 누르면 앱이 종료됩니다.")
            pressedBackKey = true
            pressedBackKeyHandler.postDelayed({ resetPressedBackKey() }, 2000L)

            return true
        }

        return super.onNavigateUp()
    }
}