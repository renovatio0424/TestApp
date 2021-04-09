package com.herry.test.app.checker.password_setting

import com.herry.libs.data_checker.DataCheckerChangeData
import com.herry.libs.util.preferences.PreferenceUtil
import com.herry.test.sharedpref.SharedPrefKeys

/**
 * Created by herry.park on 2020/7/7
 **/
class PasswordSettingPresenter : PasswordSettingContract.Presenter() {

    private val passwordChecker: DataCheckerChangeData<String> = DataCheckerChangeData()

    override fun onLaunch(view: PasswordSettingContract.View, recreated: Boolean) {
        if (!recreated) {
            passwordChecker.setBase(PreferenceUtil.get(SharedPrefKeys.PASSWORD, ""))
        }
        // sets list items
        display()
    }

    private fun display() {
        view?.getContext() ?: return

        view?.onDisplayPassword(passwordChecker.data ?: "")
    }

    override fun setPassword(password: String?) {
        passwordChecker.setData(password ?: "")
    }

    override fun isChangedPassword(): Boolean {
        val result = passwordChecker.isChanged
        if (result) {
            PreferenceUtil.set(SharedPrefKeys.PASSWORD, passwordChecker.data)
        }
        return result
    }
}