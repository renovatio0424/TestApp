package com.herry.test.data

import java.io.Serializable

/**
 * Created by herry.park on 2020/07/09.
 **/
open class MediaFileInfoData(
    val id: String,
    val mimeType: String,
    val path: String,
    val name: String,
    val size: Int = 0,
    val date: Long = 0
) : Serializable