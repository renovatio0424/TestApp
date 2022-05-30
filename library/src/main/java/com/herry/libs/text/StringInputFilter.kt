package com.herry.libs.text

import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.Spanned
import android.text.TextUtils
import android.widget.EditText
import java.io.UnsupportedEncodingException
import java.util.regex.Pattern

/**
 * Created by herry.park
 */
@Suppress("unused")
object StringInputFilter {
    fun getValidNickName(listener: OnValidListener?): InputFilter {
        return InputFilter { source, start, end, _, _, _ ->
            for (ch in start until end) {
                if (!Character.isLetter(source[ch].code)
                    && !Character.isDigit(source[ch])
                ) {
                    listener?.onInvalidInput()
                    return@InputFilter ""
                }
            }
            null
        }
    }

    fun getValidPassword(listener: OnValidListener?): InputFilter {
        return InputFilter { source, start, end, _, _, _ ->
            for (ch in start until end) {
                if (!ValidChecker.isAlphabet(source[ch].code)
                    && !Character.isDigit(source[ch].code)
                    && (ValidChecker.isSpaceCharacter(source[ch].code) || !ValidChecker.isSpecialCharacter(source[ch].code))
                ) {
                    listener?.onInvalidInput()
                    return@InputFilter ""
                }
            }
            null
        }
    }

    fun getByteLimit(max: Int, charset: String?): InputFilter {
        return ByteLengthFilter(max, charset)
    }

    fun getValidID(listener: OnValidListener?): InputFilter {
        return InputFilter { source, start, end, _, _, _ ->
            for (ch in start until end) {
                if (!ValidChecker.isAlphabet(source[ch].code)
                    && !Character.isDigit(source[ch])
                    && source[ch].toString() != "@"
                    && source[ch].toString() != "."
                ) {
                    listener?.onInvalidInput()
                    return@InputFilter ""
                }
            }
            null
        }
    }

    /* To restrict Space Bar in Keyboard */
    fun restrictSpaceCharacter(listener: OnValidListener?): InputFilter {
        return InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (Character.isWhitespace(source[i])) {
                    listener?.onInvalidInput()
                    return@InputFilter ""
                }
            }
            null
        }
    }

    /* To restrict special character in Keyboard */
    fun restrictCharacters(listener: OnValidListener?, vararg restrictChars: Char): InputFilter? {
        return if (restrictChars.isEmpty()) {
            null
        } else InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                val sourceChar = source[i]
                for (restrictChar in restrictChars) {
                    if (restrictChar == sourceChar) {
                        listener?.onInvalidInput()
                        return@InputFilter ""
                    }
                }
            }
            null
        }
    }

    /* To restrict special character in Keyboard */
    fun restrictCharacterTypes(listener: OnValidListener?, vararg restrictCharTypes: Int): InputFilter? {
        return if (restrictCharTypes.isEmpty()) {
            null
        } else InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                val type = Character.getType(source[i])
                for (restrictCharType in restrictCharTypes) {
                    if (restrictCharType == type) {
                        listener?.onInvalidInput()
                        return@InputFilter ""
                    }
                }
            }
            null
        }
    }

    fun trimFirstCharacter(editText: EditText): InputFilter {
        return InputFilter { source, _, _, _, _, _ ->
            if (TextUtils.isEmpty(source)) {
                return@InputFilter ""
            }
            val previousText = editText.text.toString()
            if (TextUtils.isEmpty(previousText) && TextUtils.isEmpty(source.toString().trim { it <= ' ' })) {
                ""
            } else null
        }
    }

    fun numericAndEnglish(): InputFilter {
        return InputFilter { source, _, _, _, _, _ -> // allows english and number
            val pattern = Pattern.compile("^[a-zA-Z0-9]+$")
            if (!pattern.matcher(source).matches()) {
                ""
            } else null
        }
    }

    private open class ByteLengthFilter(protected val maxByte: Int, private val charset: String?) : InputFilter {
        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
            var expected = ""
            expected += dest.subSequence(0, dstart)
            expected += source.subSequence(start, end)
            expected += dest.subSequence(dend, dest.length)
            var keep = calculateMaxLength(expected) - (dest.length - (dend - dstart))
            if (keep < 0) {
                keep = 0
            }
            val rekeep = plusMaxLength(dest.toString(), source.toString(), start)
            return if (keep <= 0 && rekeep <= 0) {
                ""
            } else if (keep >= end - start) {
                null
            } else {
                if (dest.isEmpty() && rekeep <= 0) {
                    source.subSequence(start, start + keep)
                } else if (rekeep <= 0) {
                    source.subSequence(start, start + (source.length - 1))
                } else {
                    source.subSequence(start, start + rekeep) // source중 일부만입력 허용
                }
            }
        }

        protected fun plusMaxLength(expected: String, source: String, start: Int): Int {
            var keep = source.length
            val maxByte = maxByte - getByteLength(expected) //입력가능한 byte
            while (getByteLength(source.subSequence(start, start + keep).toString()) > maxByte) {
                keep--
            }
            return keep
        }

        protected fun calculateMaxLength(expected: String): Int {
            val expectedByte = getByteLength(expected)
            return if (expectedByte == 0) {
                0
            } else maxByte - (getByteLength(expected) - expected.length)
        }

        private fun getByteLength(str: String): Int {
            try {
                return str.toByteArray(charset(charset!!)).size
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return 0
        }
    }

    class MaxLengthFilter(max: Int, private val mOnValidListener: OnValidListener?) : LengthFilter(max) {
        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence {
            val res = super.filter(source, start, end, dest, dstart, dend)
            if (res != null) { // max length
                mOnValidListener?.onInvalidInput()
            }
            return res
        }
    }

    interface OnValidListener {
        fun onInvalidInput()
    }
}