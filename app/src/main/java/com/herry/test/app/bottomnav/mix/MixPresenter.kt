package com.herry.test.app.bottomnav.mix

import com.google.android.exoplayer2.ExoPlayer
import com.herry.libs.media.exoplayer.ExoPlayerManager
import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodeHelper
import com.herry.libs.nodeview.model.NodeModelGroup
import com.herry.test.app.bottomnav.mix.forms.FeedDetailForm
import io.reactivex.Observable


class MixPresenter : MixContract.Presenter() {

    private val feedNodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()
    private var currentPosition: Int = 0

    private val exoPlayerManger: ExoPlayerManager = ExoPlayerManager(
        context = {
            view?.getContext()
        },
        isSingleInstance = false
    )

    override fun onAttach(view: MixContract.View) {
        super.onAttach(view)

        view.root.beginTransition()
        NodeHelper.addNode(view.root, feedNodes)
        view.root.endTransition()
    }

    override fun onLaunch(view: MixContract.View, recreated: Boolean) {
        launch {
            load(!recreated)
        }
    }

    override fun onResume(view: MixContract.View) {
        launch {
            load(false)
        }
    }

    override fun onPause(view: MixContract.View) {
        launch {
            stopPlayAll()
        }
    }

    private fun load(init: Boolean) {
        if (init) {
            loadFeeds()
        } else {
            reloadFeeds()
        }
    }

    private fun loadFeeds(lastId: String = "-1") {
        subscribeObservable(Observable.create<MutableList<FeedDetailForm.Model>> { emitter ->
            val list: MutableList<FeedDetailForm.Model> = mutableListOf()
            val feeds: LinkedHashMap<String, String> = getFeeds(lastId, 10)
            feeds.keys.forEach { id ->
                val url = feeds[id]
                if (url != null) {
                    list.add(FeedDetailForm.Model(id, url))
                }
            }
            emitter.onNext(list)
            emitter.onComplete()
        }, { videos ->
            display(lastId == "-1", videos)
        })
    }

    @Suppress("SameParameterValue")
    private fun getFeeds(lastId: String, pageCounts: Int = 10): LinkedHashMap<String, String> {
        val videos = videos()

        val result = LinkedHashMap<String, String>()
        var includeKeyNext = lastId == "-1"
        videos.keys.forEach { id ->
            if (includeKeyNext) {
                val value = videos[id] ?: return@forEach
                result[id] = value
                if (result.size >= pageCounts) {
                    return result
                }
            } else if (!includeKeyNext && id == lastId) {
                includeKeyNext = true
            }
        }

        return result
    }

    private fun videos(): LinkedHashMap<String, String> {
        val videos: LinkedHashMap<String, String> = linkedMapOf()

        mutableListOf(
            "https://cdn-project-feed.kinemasters.com/projects/625cb8cadf55aa02f2169afb/27wv6KppHJf38J3HglLcYnRSIzk.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/62660598df55aa02f2169b4d/28GqSpRbhNkVhOUz11qSVKqLncn.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/62660138df55aa02f2169b3e/28GoCEGPDIm5tuIhFFDsnM6NPLO.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/6268b427e42f4202ffa2bec4/28Mz4LnedIrviCPllbOdp8Kev9i.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/62660563df55aa02f2169b4c/28GqMElAieAQiuhFdUFnaPNRUM6.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/626604f0df55aa02f2169b4a/28Gq7vWRkacV7A42moRNzXxBm9v.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/626606c1df55aa02f2169b53/28Gr48O3GY9hFvY5UNgg45kwVc2.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb8ecdf55aa02f2169afc/27wvAndCkidULEXE9lxwdF0JQna.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/626604c0df55aa02f2169b49/28Gq1pT82ccOObHezZrSWhHRfl8.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/6266019ddf55aa02f2169b40/28GoOn48q6DzlMQ2eXJWRTvvK1D.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/626602a9df55aa02f2169b42/28N0gudl52rINEv5E7ot2ym2Ic4.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/626600bcdf55aa02f2169b3b/28GnwfwDf33IpVApX46bdocttQn.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/6268b40be42f4202ffa2bec3/28MzAEny4EQhhhAXmQ3hBfPoC7X.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/6266016fdf55aa02f2169b3f/28GoJ5AiTdxCa0JX0aO89CqT3GF.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/626606e1df55aa02f2169b54/28Gr8EICGXeyBEgDFsI7u6SIcxE.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/62660095df55aa02f2169b3a/28GnrfFRqhlzVUUoLtm3867F2Os.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb961df55aa02f2169aff/27wvPXTRBHmGhA4biq0bFuespl6.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/626602efdf55aa02f2169b43/28Gp5HIrRcFgnEyvyOc3EV1wpBk.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/6266061edf55aa02f2169b50/28GqjoCbxshSN0ysl6iV2HyADCD.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/62660751df55aa02f2169b55/28GrMLT3h8gbxuIQDWr6VJXWwIo.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/626606a0df55aa02f2169b52/28Gr09L1GxeCQwtZq9bahU0mfn4.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/6268b446e42f4202ffa2bec5/28Mz6Wfnqoe5WEEhR0XWy42kv9I.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/6265fa77df55aa02f2169b38/28GkgwG7VK0Y1dJ8qXSrp85r3Rz.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/626605badf55aa02f2169b4e/28GqXFfVLQ2nz3ge5quy8sHP8Ev.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/6265fd52df55aa02f2169b39/28GmAmo3XT2IoIG9sEWcpKYel5J.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/62660401df55aa02f2169b48/28GpdqwGdvukLFjUnSnJYTjFflR.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb27bdf55aa02f2169adf/27wrpWoasM9segHMAWSKafE9ivm.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbb34df55aa02f2169b07/27wwM2ilJ3363jA7HT3SE919hzY.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbd19df55aa02f2169b10/27wxKy3VeBn7Uxsti2LUfP3j2iV.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb45adf55aa02f2169ae8/27wsnhN7zmRg4ah22DICRiEpjmR.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbaa4df55aa02f2169b04/27ww3wmMoctWDJmcD6cylR24TR7.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbc06df55aa02f2169b0b/27wwmQLwcVXcvvLDeSMbziaOYa9.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbd33df55aa02f2169b11/27wxOE4NPKzpX7nY1LjSdX0FQRS.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc19ddf55aa02f2169b26/27wzgFszcmDZIKmPhZYA9B9Mnka.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc379df55aa02f2169b2c/27x0e5ArRH3tWKspOtnBUcHUyif.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbb9bdf55aa02f2169b0a/27wwZ4hPd6bFn1keKalu7DHXx3q.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbaebdf55aa02f2169b05/27wwD0ACQSUNrkJi1SIZHSNJ1xK.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb435df55aa02f2169ae7/27wsj1K2JBFHhFkTwliUudoVuny.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb1d3df55aa02f2169adb/27wrUGt0FrB6zKJ1x0ckZUIcmwG.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc39ddf55aa02f2169b2d/27x0illpNVN6i1xEFsw00sq4ACm.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbd76df55aa02f2169b13/27wxWsbMMcY7U0yiPZIam9npsQj.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc085df55aa02f2169b20/27wz7AqZUHcNvsqawfVdLjmCdSI.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbd91df55aa02f2169b14/27wxa9muWs64hlczb1BNf9gz3jl.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/624664ed305354033cd30ef6/27jFwqvELtZi8QAl3ryaF6EcDBZ.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/62466501305354033cd30ef7/27jGEfZBorHYM304chh9nj7kc9g.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc0a2df55aa02f2169b21/27wzAsV5ddhZFCh84O0X1Wv49H8.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbd54df55aa02f2169b12/27wxSYyjIiwaq5lHzMfQSD8XiVy.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/6246651f305354033cd30ef8/27jGJpGsnqY6uYN8nJuvoJnE4eB.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/604df45501071402c972bb40/1qp2PmqmtZLMfBpaPen6MCyiTfk.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbb56df55aa02f2169b08/27wwQN50Q7s2uZvcfhswTU0SDwu.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb793df55aa02f2169af5/27wuTQPpisPQEqEzgv6APAhdsz4.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbb73df55aa02f2169b09/27wwU9gljDrRugai6eUzExZA27j.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbea2df55aa02f2169b17/27wy8SyqBuDSTzaAd5NY35d4lgN.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbfbddf55aa02f2169b1c/286LZSU2IeJPhZN0a1vhDIGcBia.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc066df55aa02f2169b1f/27wz3FGIPkoVwjK51uVbOOmaWuE.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbf0fdf55aa02f2169b19/27wyMAHckAjD0VHgpzNcacoF4Ut.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc02cdf55aa02f2169b1d/27wyvqT8vOcNxeKV5hFdAdxaNfi.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc1bcdf55aa02f2169b27/286DTWF4rD9BhlXUyhUwtWVuCvh.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb588df55aa02f2169aed/286JEwXTIIS75JAhvnwhLmNW9wr.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb768df55aa02f2169af4/27wuNsfXCW5tDZAQY6gczsAPMgW.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc2eedf55aa02f2169b2a/27x0MiahPDD1NylsJa9pU3lYBLA.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbde9df55aa02f2169b15/27wxl8m455y4THdF6qYqKGVRLrB.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc2c9df55aa02f2169b29/27x0I70i7tHhHFxhQ918Udd78cc.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb564df55aa02f2169aec/27wtLESRfuLg2q41vhAzKjsGIAi.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc049df55aa02f2169b1e/27wyzUWL98mRfG05dVLZCBfHpud.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbef0df55aa02f2169b18/27wyIJSazIpauIOrPZ36iHxtUWs.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb7b7df55aa02f2169af6/27wuXyBgWldC92hmLWdEC8NLrEx.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbfa2df55aa02f2169b1b/27wyed2dGQz0lHYxBlSrBlmIsFa.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc3ebdf55aa02f2169b2f/27x0sUDZ0fPqDDCExMviyGArRak.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb80adf55aa02f2169af8/27wuiK3gdGomX8GOsqaZ6NziRNF.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb3e3df55aa02f2169ae5/27wsYmIoHZYYq9IGKUDMjOL23ak.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb35fdf55aa02f2169ae3/27wsID31N6iPqf0EZNTGEE31VNd.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc14fdf55aa02f2169b25/27wzWaGNHp5uT6GHVVOFGcSmnsJ.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbca5df55aa02f2169b0e/27wx6bMVMbJIdQDfCcCQiengG2S.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb483df55aa02f2169ae9/27wssrOw9KexHisYIVTpgrBf0n9.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb2cddf55aa02f2169ae0/27wrzlUwIb1sDOwYYtwQnTeWxvR.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb86bdf55aa02f2169afa/27wuuQR8JGjl9r7rFe05RE2Hogr.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb4f6df55aa02f2169aea/27wt7BXz8dRWu1dBrGX1iL4T87b.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbc84df55aa02f2169b0d/27wx2NFpYn1kwURa2fmdqcr6H9h.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbe09df55aa02f2169b16/27wxpIDQ2UqtgZPUPd4pHQ6aTDO.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb90adf55aa02f2169afd/27wvEWlrkL36zrsaPSWIDmlhaMp.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc133df55aa02f2169b24/27wzT2gJncD4Il8ZJAhGBUHWmSV.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb621df55aa02f2169aef/27wtixeLRhKkMskE9YVGMgdSoGR.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cba77df55aa02f2169b03/27wvyFmCeP3cOhvKBLHhjhB9JSr.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb51bdf55aa02f2169aeb/27wtBzi4FqARU6ZfWYXJClxzBiP.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb938df55aa02f2169afe/27wvKEawUcJUhy8E5w8gmaFVbOB.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb394df55aa02f2169ae4/27wsOjTM5MzW6QtKJjBqNStE8Ir.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb335df55aa02f2169ae2/27wsCtrW20PmkNLE1kNJBTWoENO.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb82edf55aa02f2169af9/27wump540lFN3up4E5J2wngx8Wa.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbf7edf55aa02f2169b1a/27wya15OzgYz7Rc3CatHkfWPl3Q.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc117df55aa02f2169b23/27wzPZNJfdqXL8tymtPbSZxklQG.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb7ebdf55aa02f2169af7/27wueP0suCXFMtBlkAYK0fgytPk.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb2f2df55aa02f2169ae1/27ws4UtnI5Evd9iIhllT9TUYnUf.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb40adf55aa02f2169ae6/27wsdj0mrU0KYKXz0YPLZzFVv0t.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbb13df55aa02f2169b06/27wwI47vpOdQIZf1ktS0hiWrPMw.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cbc60df55aa02f2169b0c/27wwxsL24a706wOGHllazY4EuTU.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cc336df55aa02f2169b2b/27x0VpcJfijRnB4OGsDtSwE5l4z.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/624e8c98305354033cd30f11/27SYUVx43Cgz36LTf8BesmGvszC.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb6d0df55aa02f2169af2/27wu4p7Sj9BmByeR1pNjWpaO2eW.mp4",
            "https://cdn-project-feed.kinemasters.com/projects/625cb0dcdf55aa02f2169ad7/27wqzGQYi4WgRp3L0NI7tIq0Kzw.mp4"
        ).forEachIndexed { index, url ->
            videos[(index+1).toString()] = url
        }

        return videos
    }

    private fun reloadFeeds() {
        val videos = NodeHelper.getChildrenModels<FeedDetailForm.Model>(feedNodes)
        if (videos.size <= 0) {
            loadFeeds("-1")
        } else {
            feedNodes.beginTransition()
            feedNodes.clearChild()
            feedNodes.endTransition()
            display(false, videos)
            view?.onScrollTo(currentPosition)
        }
    }

    private fun display(reset: Boolean, list: MutableList<FeedDetailForm.Model>) {
        this.feedNodes.beginTransition()
        if (reset) {
            val nodes = NodeHelper.createNodeGroup()
            NodeHelper.addModels(nodes, *list.toTypedArray())
            NodeHelper.upSert(this.feedNodes, nodes)
        } else {
            NodeHelper.addModels(this.feedNodes, *list.toTypedArray())
        }
        this.feedNodes.endTransition()

        if (reset) {
            view?.onLaunched(this.feedNodes.getChildCount())
        }
    }

    override fun setCurrentPosition(position: Int) {
        this.currentPosition = position
    }

    override fun preparePlayer(model: FeedDetailForm.Model?): ExoPlayer? {
        model ?: return null

        return exoPlayerManger.prepare(model.id, model.url)
    }

    private fun getFeedModelFromFeeds(position: Int): FeedDetailForm.Model?{
        val nodePosition = feedNodes.getNodePosition(position) ?: return null
        val node = feedNodes.getNode(nodePosition) ?: return null
        return node.model as? FeedDetailForm.Model
    }

    override fun play(position: Int) {
        val model = getFeedModelFromFeeds(position) ?: return

        exoPlayerManger.play(model.id, model.url, true)
    }

    override fun stop(position: Int) {
        val model = getFeedModelFromFeeds(position) ?: return

        exoPlayerManger.stop(model.id)
    }

    override fun stop(model: FeedDetailForm.Model?) {
        model ?: return
        exoPlayerManger.stop(model.id)
    }

    private fun stopPlayAll() {
        exoPlayerManger.stopAll()
    }

    override fun loadMore() {
        val feeds = NodeHelper.getChildrenModels<FeedDetailForm.Model>(feedNodes)
        val lastId = if (feeds.isNotEmpty()) feeds.last().id else return
        loadFeeds(lastId)
    }
}