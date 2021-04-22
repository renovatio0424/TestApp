package com.herry.test.app.base.nestednav

import com.herry.libs.mvp.MVPPresenter
import com.herry.libs.mvp.MVPView
import com.herry.test.app.base.nav.BaseNavView

@Suppress("unused")
abstract class BaseNestedNavView<V: MVPView<P>, P: MVPPresenter<V>>: BaseNavView<V, P>(), NestedNavMovement