package com.herry.test.app.sample.feeds.search

import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodeHelper
import com.herry.libs.nodeview.model.NodeModelGroup
import com.herry.test.app.sample.repository.database.feed.FeedDB
import com.herry.test.app.sample.repository.database.feed.FeedDBRepository
import com.herry.test.app.sample.repository.database.searchlog.RecentlySearchKeyword
import com.herry.test.app.sample.repository.database.searchlog.RecentlySearchKeywordDB
import com.herry.test.app.sample.repository.database.searchlog.RecentlySearchKeywordDBRepository
import com.herry.test.rx.RxSchedulerProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class SearchFeedsPresenter : SearchFeedsContract.Presenter() {

    private var feedDatabase: FeedDB? = null
    private var feedRepository: FeedDBRepository? = null

    private var recentlyDatabase: RecentlySearchKeywordDB? = null
    private var recentlyRepository: RecentlySearchKeywordDBRepository? = null


    private val recentlySearchWordNodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()
    private val searchAutoCompletesNodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()
    private val searchAutoCompleteHotelsNodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()

    private val autoCompleteCompositeDisposable = CompositeDisposable()
    private val autoCompleteBehaviorSubject = BehaviorSubject.create<String>()

//    private val autoCompleteHotelsDisposable = LastOneObservable<Pair<AutoCompleteData, MutableList<NewHotelListData>>>(
//        {
//            launched {
//                displayAutoCompleteHotels(it.first, it.second)
//            }
//        },
//        onlyLastOne = false
//    )

    override fun onAttach(view: SearchFeedsContract.View) {
        super.onAttach(view)

        val context = view.getViewContext() ?: return
        feedDatabase = FeedDB.getInstance(context)?.also { db ->
            feedRepository = FeedDBRepository(db.dao())
        }

        recentlyDatabase = RecentlySearchKeywordDB.getInstance(context)?.also { db ->
            recentlyRepository = RecentlySearchKeywordDBRepository(db.dao())
        }

        view.recentlyRoot.beginTransition()
        view.recentlyRoot.clearChild()
        NodeHelper.addNode(view.recentlyRoot, recentlySearchWordNodes)
        view.recentlyRoot.endTransition()

        view.searchRoot.beginTransition()
        view.searchRoot.clearChild()
        NodeHelper.addNode(view.searchRoot, searchAutoCompletesNodes)
        NodeHelper.addNode(view.searchRoot, searchAutoCompleteHotelsNodes)
        view.searchRoot.endTransition()

        setSearchFeedComposite()
    }

    override fun onDetach() {
        autoCompleteCompositeDisposable.dispose()

        recentlyRepository = null
        recentlyDatabase = null

        feedRepository = null
        feedDatabase = null

//        autoCompleteHotelsDisposable.dispose()

        super.onDetach()
    }

    override fun onLaunch(view: SearchFeedsContract.View, recreated: Boolean) {
        launch {
            loadRecentlySearchWords()

            searchFeed("")
        }
    }

    override fun searchFeed(keyword: String) {
        autoCompleteBehaviorSubject.onNext(keyword)
    }

    private fun getAutoCompleteKeywords(keyword: String): Observable<FeedDBRepository.AutoCompleteKeywords> {
        return Observable.create { emitter ->

            emitter.onNext(feedRepository?.getAutoCompleteKeywords(keyword) ?: FeedDBRepository.AutoCompleteKeywords(keyword))
            emitter.onComplete()
        }
    }

    private fun setSearchFeedComposite() {
        view ?: return

        autoCompleteCompositeDisposable.add(
            presenterObservable(
                autoCompleteBehaviorSubject.subscribeOn(RxSchedulerProvider.io())
                    .debounce(400, TimeUnit.MILLISECONDS)
                    .observeOn(RxSchedulerProvider.ui())
                    .concatMap { searchText ->
                        Observable.create<String> {
                            it.onNext(searchText)
                            it.onComplete()
                        }
                    },
                loadView = false
            )
                .subscribe { searchText ->
                    if (searchText.isNotEmpty()) {
//                        Trace.d("Herry", "autoComplete search keyword load : $searchText")
//                        view.onLoadViewVisible(true)
                        subscribeObservable(
                            getAutoCompleteKeywords(searchText),
                            {
                                setAutoCompleteResults(it)
//                                view.onLoadViewVisible(false)
                            },
                            loadView = false
                        )
                    } else {
                        view?.onChangedViewMode(SearchFeedsContract.ViewMode.RECENTLY)
//                        view.onLoadViewVisible(false)
                    }
                }
        )
    }

    private fun setAutoCompleteResults(dataSet: FeedDBRepository.AutoCompleteKeywords) {
        displaySearchAutoCompletes(dataSet.keywords)
//        Trace.d("Herry", "displaySearchAutoCompletes search keyword : ${dataSet.keyword}")
//        loadAutoCompleteHotels(dataSet.feeds.firstOrNull())
    }

    private fun displaySearchAutoCompletes(list: MutableList<String>) {
        this.searchAutoCompletesNodes.beginTransition()

        val nodes = NodeHelper.createNodeGroup()
        if (list.isNotEmpty()) {
            NodeHelper.addModels(nodes, *list.toTypedArray())
        } else {
            NodeHelper.addModel(nodes, SearchFeedsContract.EmptyModel())
        }
        NodeHelper.upSert(this.searchAutoCompletesNodes, nodes)

        this.searchAutoCompletesNodes.endTransition()

        view?.onChangedViewMode(SearchFeedsContract.ViewMode.SEARCH)
    }

//    private fun loadAutoCompleteHotels(autoCompleteData: Feed?) {
//        launch(LaunchWhen.RESUMED) {
//            autoCompleteHotelsDisposable.dispose()
//
//            if (null != autoCompleteData) {
//                autoCompleteHotelsDisposable.subscribe(
//                    getPresenterObservable(
//                        RestHotelRequest().getAutoCompleteHotels(
//                            autoCompleteData
//                        ).map {
//                            Pair(autoCompleteData, it)
//                        },
//                        loadView = false
//                    )
//                        .observeOn(SchedulerProvider.ui())
//                )
//            } else {
//                displayAutoCompleteHotels(null)
//            }
//        }
//    }
//
//    private fun displayAutoCompleteHotels(autoCompleteData: Feed?, list: MutableList<Feed> = mutableListOf()) {
//        this.searchAutoCompleteHotelsNodes.beginTransition()
//
//        val nodes = NodeHelper.createNodeGroup()
//        if (null != autoCompleteData && list.isNotEmpty()) {
//            NodeHelper.addModels(nodes, *list.toTypedArray())
//        }
//        NodeHelper.upSert(this.searchAutoCompleteHotelsNodes, nodes)
//
//        this.searchAutoCompleteHotelsNodes.endTransition()
//    }

    private fun getRecentlySearchKeywords(): Observable<MutableList<RecentlySearchKeyword>> {
        return Observable.create { emitter ->
            emitter.onNext(recentlyRepository?.getList()?.toMutableList() ?: mutableListOf())
            emitter.onComplete()
        }
    }

    private fun loadRecentlySearchWords() {
        subscribeObservable(
            getRecentlySearchKeywords(),
            onNext = {
                displayRecentlySearchWords(it)
            },
            loadView = false
        )
    }

    private fun displayRecentlySearchWords(list: MutableList<RecentlySearchKeyword>) {
        this.recentlySearchWordNodes.beginTransition()

        val recentlySearchWordNodes = NodeHelper.createNodeGroup()

        if(list.isNotEmpty()) {
            NodeHelper.addModels(recentlySearchWordNodes, *list.toTypedArray())
        }
        NodeHelper.upSert(this.recentlySearchWordNodes, recentlySearchWordNodes)

        this.recentlySearchWordNodes.endTransition()
    }
}