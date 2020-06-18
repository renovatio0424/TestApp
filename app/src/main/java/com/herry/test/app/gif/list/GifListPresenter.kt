package com.herry.test.app.gif.list

import android.content.Context
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodeHelper
import com.herry.libs.nodeview.model.NodeModelGroup
import com.herry.test.data.GifFileInfoData
import com.herry.test.rx.RxCursorIterable
import io.reactivex.Observable


/**
 * Created by herry.park on 2020/06/11.
 **/
class GifListPresenter : GifListContract.Presenter() {

    private val nodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()
    override fun onAttach(view: GifListContract.View) {
        super.onAttach(view)

        view.root.beginTransition()
        NodeHelper.addNode(view.root, nodes)
        view.root.endTransition()
    }

    override fun onLaunched(view: GifListContract.View) {
        // sets list items
        loadGifList()
    }

    private fun loadGifList() {
        subscribeObservable(
            getGifContentsFromMediaStore()
            , {
                launched {
                    updateGifList(it)
                }
            }
        )
    }

    private fun updateGifList(list: MutableList<GifFileInfoData>) {
        view?.getViewContext() ?: return

        this.nodes.beginTransition()

        val nodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()
        NodeHelper.addModels(nodes, *list.toTypedArray())

        NodeHelper.upSert(this.nodes, nodes)
        this.nodes.endTransition()
    }

    private fun getGifContentsFromMediaStore(): Observable<MutableList<GifFileInfoData>> {
        val context: Context? = view?.getViewContext()
        context ?: return Observable.empty()

        return Observable.fromCallable {
            val photos = mutableListOf<GifFileInfoData>()
            val selection = MediaStore.Images.Media.MIME_TYPE + "=?"
            val selectionArgs = arrayOf(MimeTypeMap.getSingleton().getMimeTypeFromExtension("gif"))
            val cursor = context.contentResolver?.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.WIDTH,
                    MediaStore.Video.Media.HEIGHT,
                    MediaStore.Video.Media.DATE_ADDED
                ),
                selection,
                selectionArgs,
                MediaStore.MediaColumns.DATE_ADDED + " DESC"
            )

            cursor?.let {
                RxCursorIterable.from(cursor).forEach { c ->
                    val id = c.getString(c.getColumnIndex(MediaStore.Images.Media._ID))
                    val path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA))
                    val displayName =
                        c.getString(c.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                    val size = c.getInt(c.getColumnIndex(MediaStore.Images.Media.SIZE))
                    val width = c.getInt(c.getColumnIndex(MediaStore.Video.Media.WIDTH))
                    val height = c.getInt(c.getColumnIndex(MediaStore.Video.Media.HEIGHT))
                    val date = c.getLong(c.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))
                    photos.add(
                        GifFileInfoData(
                            id = id,
                            path = path,
                            name = displayName,
                            size = size,
                            width = width,
                            height = height,
                            date = date
                        )
                    )
                }
            }
            photos
        }
    }

    override fun decode(content: GifFileInfoData) {
        view?.onDetail(content)
    }
}