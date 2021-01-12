package com.herry.libs.mvp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

class MVPPresenterViewModelFactory<V: MVPView<P>, P: MVPPresenter<V>>(private val view: MVPViewCreation<V, P>) : ViewModelProvider.Factory {

    private var created = false

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        created = true

        @Suppress("UNCHECKED_CAST")
        return view.onCreatePresenter() as T
    }

    companion object {
        fun <V: MVPView<P>, P: MVPPresenter<V>> create(owner: ViewModelStoreOwner, viewModelView: MVPViewCreation<V, P>): MVPPresenterViewModel<V, P>? {
            val factory = MVPPresenterViewModelFactory(viewModelView)
            @Suppress("UNCHECKED_CAST")
            val viewModel = ViewModelProvider(owner, factory).get(MVPPresenter::class.java) as P?
            return MVPPresenterViewModel(viewModel, !factory.created)
        }
    }
}

data class MVPPresenterViewModel<V: MVPView<P>, P: MVPPresenter<V>>(val presenter: P?, val reloaded: Boolean)
