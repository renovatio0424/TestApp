package com.herry.libs.util

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import java.util.*

/**
 * Created by herry.park on 2020/07/06.
 **/
@Suppress("MemberVisibilityCanBePrivate", "unused")
object ListUtil {
    /**
     * Checks position is range or not.
     * @param position checking position
     * @param startPosition start position for checking
     * @param size total size for checking
     * @return result
     */
    fun isPositionInRange(position: Int, startPosition: Int, size: Int): Boolean {
        return position in startPosition until size
    }

    fun isPositionInRange(position: Int, list: MutableList<*>): Boolean {
        return if (isEmpty(list)) {
            false
        } else isPositionInRange(position, 0, list.size)
    }

    /**
     * Compares list
     */
    fun <O1, O2> compare(l1: MutableList<O1>?, l2: MutableList<O2>?, compare: CompareList<O1, O2>): Boolean {
        return if (null == l1 && null == l2) {
            false
        } else if (null == l1) {
            true
        } else if (null == l2) {
            true
        } else { // if (null != l1 && null != l2)
            if (size(l1) != size(l2)) {
                return false
            } else {
                for (i in l1.indices) {
                    val compare1: O1? = l1[i]
                    val compare2: O2? = l2[i]
                    if (null == compare1 && null == compare2) {
                        continue
                    } else if (null == compare1) {
                        return false
                    } else if (null == compare2) {
                        return false
                    }
                    if (!compare.isEqual(compare1, compare2)) {
                        return false
                    }
                }
            }
            true
        }
    }

    fun <O1, O2> compareIgnoreOrder(l1: MutableList<O1>?, l2: MutableList<O2>?, compare: CompareList<O1, O2>): Boolean {
        return if (null == l1 && null == l2) {
            false
        } else if (null == l1) {
            true
        } else if (null == l2) {
            true
        } else { // if (null != l1 && null != l2)
            if (size(l1) != size(l2)) {
                return false
            } else {
                for (compare1 in l1) {
                    if (null == compare1) {
                        continue
                    }
                    var equal = false
                    for (compare2 in l2) {
                        if (null == compare2) {
                            continue
                        }
                        if (compare.isEqual(compare1, compare2)) {
                            equal = true
                            break
                        }
                    }
                    if (!equal) {
                        return false
                    }
                }
            }
            true
        }
    }

    /**
     * Gets list size. This function checks null value.
     * @param list target list object
     * @return size of list
     */
    fun size(list: MutableList<*>?): Int {
        return list?.size ?: 0
    }

    fun isEmpty(list: MutableList<*>?): Boolean {
        return list == null || list.isEmpty()
    }

    operator fun <T> get(list: MutableList<T>?, index: Int): T? {
        return if (list != null && index >= 0 && index < list.size) {
            list[index]
        } else null
    }

    fun <T> remove(list: MutableList<T>?, index: Int): T? {
        return if (list != null && index >= 0 && index < list.size) {
            list.removeAt(index)
        } else null
    }

    /**
     * Adds object to list. If additional object is null, it will be not add to list.
     * @param list target list
     * @param obj additional items
     */
    @SafeVarargs
    fun <T> addListNonNull(list: MutableList<T>, vararg obj: T) {
        for (item in obj) {
            if (item != null) {
                list.add(item)
            }
        }
    }

    fun <T> checkedList(list: MutableList<*>?, type: Class<T>): ArrayList<T> {
        val results = ArrayList<T>()
        if (list != null) {
            for (obj in list) {
                if (type.isInstance(obj)) {
                    type.cast(obj)?.let { results.add(it) }
                }
            }
        }
        return results
    }

    fun <T> getSerializableArrayList(args: Bundle?, key: String?, type: Class<T>): ArrayList<T> {
        var results: ArrayList<T>? = null
        if (args != null) {
            val serializable = args.getSerializable(key)
            if (serializable is MutableList<*>) {
                results = checkedList(serializable as MutableList<*>?, type)
            }
        }
        return results ?: ArrayList()
    }

    fun <T> getSerializableArrayList(intent: Intent?, key: String?, type: Class<T>): ArrayList<T> {
        var results: ArrayList<T>? = null
        if (intent != null) {
            val serializable = intent.getSerializableExtra(key)
            if (serializable is MutableList<*>) {
                results = checkedList(serializable as MutableList<*>, type)
            }
        }
        return results ?: ArrayList()
    }

    fun <T> convertSpareArrayToArrayList(sparseArray: SparseArray<T>?): ArrayList<T> {
        val arrayList = ArrayList<T>()
        if (null == sparseArray) {
            return arrayList
        }
        for (index in 0 until sparseArray.size()) {
            arrayList.add(sparseArray.valueAt(index))
        }
        return arrayList
    }

    fun <T> removeEmpty(list: MutableList<T>, emptyChecker: EmptyChecker<T>?): MutableList<T> {
        val out: MutableList<T> = ArrayList()
        if (!isEmpty(list)) {
            for (item in list) {
                if (null == emptyChecker) {
                    if (null == item) {
                        continue
                    }
                } else {
                    if (emptyChecker.isEmpty(item)) {
                        continue
                    }
                }
                out.add(item)
            }
        }
        return out
    }

    fun <T> removeNull(list: MutableList<T>): MutableList<T> {
        return removeEmpty(list, null)
    }

    fun <T> forEachStep(list: MutableList<T>?, step: Int, listener: OnForEachStepListener<T>) {
        if (list == null) {
            return
        }
        if (step > 0) {
            var i = 0
            while (i < list.size) {
                val args: MutableList<T> = ArrayList()
                var index = i
                while (index < list.size && index < i + step) {
                    args.add(list[index])
                    index++
                }
                listener.onStep(args)
                i += step
            }
        }
    }

    fun <T> forEachOnlyStep(list: MutableList<T>?, step: Int, l: OnForEachStepListener<T>) {
        if (list == null) {
            return
        }
        if (step > 0) {
            var i = 0
            while (i < list.size) {
                if (i + step <= list.size) {
                    val args: MutableList<T> = ArrayList()
                    for (index in i until i + step) {
                        args.add(list[index])
                    }
                    l.onStep(args)
                }
                i += step
            }
        }
    }

    fun <C> asList(sparseArray: SparseArray<C>?): MutableList<C> {
        if (sparseArray == null) return ArrayList()
        val list: MutableList<C> = ArrayList()
        for (i in 0 until sparseArray.size()) list.add(sparseArray.valueAt(i))
        return list
    }

    fun <T> asList(iterator: Iterator<T>?): MutableList<T> {
        val list: MutableList<T> = ArrayList()
        if (iterator != null) {
            while (iterator.hasNext()) {
                list.add(iterator.next())
            }
        }
        return list
    }

    fun <T> subList(list: MutableList<T>?, fromIndex: Int, count: Int): MutableList<T> {
        if (list == null) {
            return ArrayList()
        }
        return if (list.size <= fromIndex) {
            ArrayList()
        } else list.subList(fromIndex, if (fromIndex + count < list.size) fromIndex + count else list.size)
    }

    fun <T> getRandomList(list: MutableList<T>?, count: Int): MutableList<T> {
        val results = ArrayList<T>()
        if (list == null || list.size <= 0) {
            return results
        }
        val temp = ArrayList(list)
        val targetCount = if (temp.size < count) temp.size else count
        for (index in 0 until targetCount) {
            val random = Random().nextInt(temp.size)
            results.add(temp.removeAt(random))
        }
        return results
    }

    interface CompareList<O1, O2> {
        fun isEqual(o1: O1, o2: O2): Boolean
    }

    interface EmptyChecker<T> {
        fun isEmpty(obj: T): Boolean
    }

    interface OnForEachStepListener<T> {
        fun onStep(args: MutableList<T>?)
    }
}