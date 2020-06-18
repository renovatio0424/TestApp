package com.herry.test.data

import java.io.Serializable

/**
 * Created by herry.park on 2020/06/18.
 **/

data class GifFileInfoData (
    val id: String,
    val path: String,
    val name: String,
    val size: Int = 0,
    val width: Int = 0,
    val height: Int = 0,
    val date: Long = 0
) : Serializable