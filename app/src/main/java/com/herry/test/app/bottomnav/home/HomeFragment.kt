package com.herry.test.app.bottomnav.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.herry.libs.app.nav.NavBundleUtil
import com.herry.libs.helper.ToastHelper
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.extension.navigateTo
import com.herry.test.R
import com.herry.test.app.base.nestednav.BaseNestedNavView
import com.herry.test.app.bottomnav.helper.NavScreenActions
import com.herry.test.app.bottomnav.home.form.HomeBottomNavControlForm
import com.herry.test.app.bottomnav.home.form.HomeBottomNavFragmentForm

class HomeFragment: BaseNestedNavView<HomeContract.View, HomeContract.Presenter>(), HomeContract.View {
    override fun onCreatePresenter(): HomeContract.Presenter = HomePresenter()

    override fun onCreatePresenterView(): HomeContract.View = this

    private var container: View? = null

    private val bottomNavFragmentForm = HomeBottomNavFragmentForm(this,
        onNavNotify = { bundle ->
            onReceivedFromBottomNavFragments(bundle)
        })

    private val bottomNavigatorForm = HomeBottomNavControlForm { selectedItemType ->
        presenter?.setCurrent(selectedItemType)
    }

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
        val context = view?.context ?: return

        bottomNavFragmentForm.bindHolder(context, view.findViewById(R.id.home_fragment_bottom_navigator_fragment_form))
        bottomNavigatorForm.bindFormHolder(context, view.findViewById(R.id.home_fragment_bottom_navigator_form))
    }

    override fun onSelectTab(model: HomeBottomNavControlForm.Model, isStart: Boolean, startArgs: Bundle?) {
        val context = this.context ?: return

        // update bottom navigator
        bottomNavigatorForm.bindFormModel(context, model)
        // update bottom navigated screen
        bottomNavFragmentForm.setNavScreen(model.selected, isStart, startArgs)

//        // update bottom navigator nav graph
//        val destinationNavigationId = when (model.selected) {
//            HomeBottomNavControlForm.ItemType.FEATURE -> R.id.bottom_nav_mix_navigation
//            HomeBottomNavControlForm.ItemType.DISCOVER -> R.id.bottom_nav_search_navigation
//            HomeBottomNavControlForm.ItemType.CREATE -> R.id.bottom_nav_create_navigation
//            HomeBottomNavControlForm.ItemType.ME -> R.id.bottom_nav_me_navigation
//        }
//
//        if (isStart) {
//            val bottomNavigationController = bottomNavFragmentContainer?.findNavController()
//            val bottomNavigatorGraph = bottomNavigationController?.graph
//            if (bottomNavigationController != null && bottomNavigatorGraph != null) {
//                if (bottomNavigatorGraph.startDestinationId != destinationNavigationId) {
//                    bottomNavigatorGraph.setStartDestination(destinationNavigationId)
//                    bottomNavigationController.setGraph(bottomNavigatorGraph, startDestinationArgs = startArgs)
//                }
//            }
//            return
//        }
////        bottomNavFragmentContainer?.findNavController()?.graph?.setStartDestination(destination)
//        navigateTo(
//            navController = bottomNavFragmentContainer?.findNavController(),
//            destinationId = destination,
//            navOptions = NavOptions.Builder()
//                .setPopUpTo(destinationId = destination, inclusive = false, saveState = true)
//                .build()
//        )

//        val destinationNavGraph = when (model.selected) {
//            HomeBottomNavigatorForm.ItemType.FEATURE -> R.navigation.bottom_nav_feature_navigation
//            HomeBottomNavigatorForm.ItemType.DISCOVER -> R.id.discover_fragment
//            HomeBottomNavigatorForm.ItemType.CREATE -> R.navigation.bottom_nav_create_navigation
//            HomeBottomNavigatorForm.ItemType.ME -> R.id.me_fragment
//        }
//
//        val bottomNavigationController = bottomNavFragmentContainer?.findNavController()
//        bottomNavigationController?.setGraph(destinationNavGraph, startDestinationArgs = startArgs)
////        if (bottomNavigationController != null && bottomNavigatorGraph != null) {
////            if (bottomNavigatorGraph.startDestinationId != destination) {
////                bottomNavigatorGraph.setStartDestination(destination)
////                bottomNavigationController.setGraph(bottomNavigatorGraph, startDestinationArgs = startArgs)
////            }
////        }
    }

    private fun onReceivedFromBottomNavFragments(bundle: Bundle) {
        when (NavScreenActions.generate(NavBundleUtil.getNavigationAction(bundle))) {
            NavScreenActions.SHOW_SETTINGS -> {
                navigateTo(destinationId = R.id.setting_fragment)
            }
            else -> {}
        }
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
        } else {
            finishActivity(false)
        }

        return true
    }
}