package com.herry.libs.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.text.format.Formatter

@Suppress("unused")
object MemoryUtil {

    private fun getCurrentMemoryInfo(activity: Activity?) : ActivityManager.MemoryInfo? {
        activity ?: return null
        val memoryInfo = ActivityManager.MemoryInfo()
        (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(memoryInfo)

        return memoryInfo
    }

    fun getCurrentMemoryInformation(activity: Activity?): String {
        activity ?: return ""
        val memoryInfo = getCurrentMemoryInfo(activity) ?: return ""

        val nativeHeapSize = memoryInfo.totalMem
        val nativeHeapFreeSize = memoryInfo.availMem
        val usedMemInBytes = nativeHeapSize - nativeHeapFreeSize
        val usedMemInPercentage = usedMemInBytes * 100 / nativeHeapSize
        return "total:${Formatter.formatFileSize(activity, nativeHeapSize)} " +
                "free:${Formatter.formatFileSize(activity, nativeHeapFreeSize)} " +
                "used:${Formatter.formatFileSize(activity, usedMemInBytes)} ($usedMemInPercentage%)"
    }

    fun getCurrentUsedMemoryInBytes(activity: Activity?): Long {
        activity ?: return -1L
        val memoryInfo = ActivityManager.MemoryInfo()
        (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(memoryInfo)
        val nativeHeapSize = memoryInfo.totalMem
        val nativeHeapFreeSize = memoryInfo.availMem
        return nativeHeapSize - nativeHeapFreeSize
    }
}