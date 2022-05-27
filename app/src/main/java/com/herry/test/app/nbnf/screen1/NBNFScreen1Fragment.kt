package com.herry.test.app.nbnf.screen1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.herry.libs.app.nav.BottomNavHostFragment
import com.herry.libs.app.nav.NavBundleUtil
import com.herry.libs.helper.ToastHelper
import com.herry.libs.util.BundleUtil
import com.herry.libs.util.ViewUtil
import com.herry.libs.widget.extension.findNestedNavHostFragment
import com.herry.libs.widget.extension.setFragmentNotifyListener
import com.herry.libs.widget.extension.setNavigate
import com.herry.test.R
import com.herry.test.app.base.nestednav.BaseNestedNavFragment
import com.herry.test.app.nbnf.screen2.NBNFScreen2Fragment

class NBNFScreen1Fragment : BaseNestedNavFragment() {

    companion object {
        private const val TAB_CHILD_1_ID = R.id.nbnf_screen_1_child_1_navigation
        private const val TAB_CHILD_2_ID = R.id.nbnf_screen_1_child_2_navigation
    }

    private val viewModel: NBNFScreen1ViewModel by viewModels()

    private var container: View? = null

    private var bottomNavHostFragment: BottomNavHostFragment? = null
    private var bottomTabs = mutableListOf<View>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (this.container == null) {
            this.container = inflater.inflate(R.layout.nbnf_screen_1_fragment, container, false)
            init(this.container)
        } else {
            ViewUtil.removeViewFormParent(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return
//        val context = view?.context ?: return

        bottomNavHostFragment = findNestedNavHostFragment(R.id.nbnf_screen_1_child_fragment_container) as? BottomNavHostFragment
        bottomNavHostFragment?.let { navHostFragment ->
            navHostFragment.setFragmentNotifyListener { _, bundle ->
            }
            addNestedNavHostFragment(navHostFragment)
        }

        view.findViewById<View>(R.id.nbnf_screen_1_tab_child_1)?.apply {
            setOnClickListener { clickBottomTab(TAB_CHILD_1_ID) }
        }?.also {
            bottomTabs.add(it)
        }

        view.findViewById<View>(R.id.nbnf_screen_1_tab_child_2)?.apply {
            setOnClickListener { clickBottomTab(TAB_CHILD_2_ID) }
        }?.also {
            bottomTabs.add(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickBottomTab(viewModel.getCurrentTabID() ?: TAB_CHILD_1_ID)
    }

    private fun clickBottomTab(childId: Int) {
        bottomTabs.forEachIndexed { index, view ->
            view.isSelected = (index == 0 && childId == TAB_CHILD_1_ID)
                    || (index == 1 && childId == TAB_CHILD_2_ID)
        }
        bottomNavHostFragment?.setNavigate(childId)

        viewModel.setCurrentTabID(childId)
    }

    private var pressedBackKey = false
    private val pressedBackKeyHandler = Handler(Looper.getMainLooper())

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

    override fun onNavigateUpResult(fromNavigationId: Int, result: Bundle) {
        when (fromNavigationId) {
            R.id.nbnf_screen_2_fragment -> {
                if (NavBundleUtil.isNavigationResultOk(result)) {
                    val resultMessage = BundleUtil[result, NBNFScreen2Fragment.RESULT_MESSAGE, ""]
                    if (resultMessage.isNotEmpty()) {
                        ToastHelper.showToast(activity, "the screen2 result message is $resultMessage")
                    }
                }
            }
        }
    }
}

internal class NBNFScreen1ViewModel : ViewModel() {
    private val currentTabID = MutableLiveData<Int>()

    fun getCurrentTabID(): Int? = currentTabID.value

    fun setCurrentTabID(id: Int) {
        currentTabID.value = id
    }
}
