package com.herry.libs.text

import android.util.Patterns
import java.util.*

/**
 * Created by herry.park
 */
@Suppress("unused", "MemberVisibilityCanBePrivate", "UNUSED_VALUE", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "LocalVariableName")
object ValidChecker {
    /**
     * Check valid mobile phone number. This check Korean mobile phone number current.
     * @param phoneNumber phone number
     * @return if input phone number is valid, it is true.
     */
    fun isValidMobilePhoneNumber(phoneNumber: String?): Boolean {
        return isValidMobilePhoneNumber(phoneNumber, Locale.KOREA)
    }

    fun isValidMobilePhoneNumber(phoneNumber: String?, locale: Locale): Boolean {
        if (null == phoneNumber) {
            return false
        }
        val phoneNo = phoneNumber.trim { it <= ' ' }
        if (phoneNo.isEmpty()) {
            return false
        }
        var regex: String? = null
        if (Locale.KOREA === locale) {
            regex = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$"
        }
        return null != regex && phoneNo.matches(Regex(regex))
    }

    fun isValidPhoneNumber(phoneNumber: String?): Boolean {
        return isValidPhoneNumber(phoneNumber, Locale.KOREA)
    }

    fun isValidPhoneNumber(phoneNumber: String?, locale: Locale): Boolean {
        if (null == phoneNumber) {
            return false
        }
        val phoneNo = phoneNumber.trim { it <= ' ' }
        if (phoneNo.isEmpty()) {
            return false
        }
        var regex: String? = null
        if (Locale.KOREA === locale) {
            regex = "^(0(2|3[1-3]|4[1-4]|5[1-5]|6[1-4]))(\\d{3,4})(\\d{4})$"
        }
        return null != regex && phoneNo.matches(Regex(regex))
    }

    fun isAlphabet(codePoint: Int): Boolean {
        return 'A'.code <= codePoint && codePoint <= 'Z'.code || 'a'.code <= codePoint && codePoint <= 'z'.code
    }

    fun isSpecialCharacter(codePoint: Int): Boolean {
        return codePoint in 0x20..0x2F ||
                codePoint in 0x3A..0x40 ||
                codePoint in 0x5B..0x60 ||
                codePoint in 0x7B..0x7E
    }

    fun isSpaceCharacter(codePoint: Int): Boolean {
        return 0x20 == codePoint
    }

    fun isDigit(codePoint: Int): Boolean {
        return Character.isDigit(codePoint)
    }

    fun isKoreanCharacter(codePoint: Int): Boolean {
        return codePoint in 0xAC00..0xD7AF
    }

    fun isNumeric(str: String?): Boolean {
        return str?.matches(Regex("-?\\d+(\\.\\d+)?")) //match a number with optional '-' and decimal.
            ?: false
    }

    fun isCharacterType(ch: Int, vararg charTypes: Int): Boolean {
        if (charTypes.isEmpty()) {
            return false
        }
        val type = Character.getType(ch)
        for (charType in charTypes) {
            if (charType == type) {
                return true
            }
        }
        return false
    }

    fun checkValidationKeyword(keyword: String): Boolean {
        return !keyword.matches(Regex(".*[ㄱ-ㅎㅏ-ㅣ]+.*"))
    }

    fun checkValidationNickname(nickname: String?): CheckerNickname {
        // new nickname rule
        if (nickname.isNullOrEmpty()) {
            return CheckerNickname.EMPTY
        }
        if (nickname.trim { it <= ' ' }.isEmpty()) {
            return CheckerNickname.SPACING
        }

        // checks Character.SURROGATE, Character.PRIVATE_USE
        return if (StringUtil.hasRestrictCharacterTypes(nickname, Character.SURROGATE.toInt(), Character.PRIVATE_USE.toInt())) {
            CheckerNickname.INVALID
        } else CheckerNickname.VALID
        /*
        if (null == nickname || TextUtils.isEmpty(nickname.trim())) {
            return CheckerNickname.EMPTY;
        }

        // check id length
        if (2 > nickname.trim().length()) {
            return CheckerNickname.SHORT;
        }

        if (spaceCheck(nickname)) {
            return CheckerNickname.SPACEING;
        }

        // check character
        String numberRange = "0-9"; // 0x0030 - 0x0039
        String alphabetLowerRange = "a-z"; // 0X0061 - 0X007A
        String alphabetUpperRange = "A-Z"; // 0x0041 - 0x005a
        String koreanRange = "가-힣"; // 0xAC00 - 0xD7AF
        String chinese1Range   = "\\u2E80-\\u2EFF";
        String chinese2Range   = "\\u3400-\\u4DBF";
        String chinese3Range   = "\\u4E00-\\u9FBF";
        String chinese4Range   = "\\uF900-\\uFAFF";
//        String chinese5Range   = "\\u20000-\\u2A6DF";
//        String chinese6Range   = "\\u2F800-\\u2FA1F";
        String japanese1Range = "\\u3040-\\u309F"; // hiragana
        String japanese2Range = "\\u30A0-\\u30FF"; // katakana
        String japanese3Range = "\\u31F0-\\u31FF"; // japanese expends

        String regexp = "^["
                + numberRange
                + alphabetLowerRange
                + alphabetUpperRange
                + koreanRange
                + chinese1Range
                + chinese2Range
                + chinese3Range
                + chinese4Range
                + japanese1Range
                + japanese2Range
                + japanese3Range
                + "]+$";

        Pattern pattern = Pattern.compile(regexp);
        Matcher match = pattern.matcher(nickname);
        if (!match.matches()) {
            return CheckerNickname.INVALID;
        }

        return CheckerNickname.VALID;
        */
    }

    fun checkValidationPassword(password: String?): CheckerPassword {
        if (password.isNullOrEmpty()) {
            return CheckerPassword.EMPTY
        }

        // check password length
        val passwordLength = password.length
        if (6 > passwordLength) {
            return CheckerPassword.SHORT
        }
        var hasAlphabet = false
        var hasNumber = false
        var hasSpecialCharacter = false
        for (index in 0 until passwordLength) {
            if (isAlphabet(password[index].code)) {
                hasAlphabet = true
            } else if (Character.isDigit(password[index].code)) {
                hasNumber = true
            } else if (!isSpaceCharacter(password[index].code) && isSpecialCharacter(password[index].code)) {
                hasSpecialCharacter = true
            } else {
                return CheckerPassword.INVALID
            }
        }
        return if (hasAlphabet && hasNumber) {
            CheckerPassword.VALID
        } else CheckerPassword.INVALID
    }

    fun isValidWebURL(url: String?): Boolean {
        if (null == url) {
            return false
        }
        val webUrl = url.trim { it <= ' ' }
        if (webUrl.isEmpty()) {
            return false
        }
        val regex = "\\b(http|https|Http|Https|rtsp|Rtsp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
        return webUrl.matches(Regex(regex))
    }

    fun isValidEmail(email: String?): Boolean {
        return if (null == email) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    fun spaceCheck(spaceCheck: String): Boolean {
        for (element in spaceCheck) {
            if (element == ' ') return true
        }
        return false
    }

    // 한글초성
    private val KoreanFirstSounds = charArrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ',
        'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ',
        'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    //한글 중성
    private val KoreanMiddleSounds = charArrayOf(
        'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ',
        'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ',
        'ㅢ', 'ㅣ'
    )

    //한글 종성
    private val KoreanLastSounds = charArrayOf(
        ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ',
        'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ',
        'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    fun getFirstElement(string: String): String? {
        var result: String? = null
        if (string.isNotEmpty()) {
            val firstChar = string[0]
            result = if (isKoreanCharacter(firstChar.code)) {
                // Korean first sound : (firstChar - 0xAC00) / (21*28)
                KoreanFirstSounds[(firstChar.code - 0xAC00) / (21 * 28)].toString()

                // Korean middle sound : ((firstChar - 0xAC00) % (21*28)) / 28
                // Korean last sound : (firstChar - 0xAC00) % 28;
            } else {
                firstChar.toString().uppercase(Locale.getDefault())
            }
        }
        return result
    }

    fun isValidUrl(url: String): Boolean {
        if (url.isEmpty()) {
            return false
        }
        val pattern = Patterns.WEB_URL
        val matcher = pattern.matcher(url.lowercase(Locale.getDefault()))
        return matcher.matches()
    }

    /*
     * Checks valid business number
     * 사업자 등록번호는 123-45-67890 의 10 자리 구조입니다.
     * 123 : 국세청 / 세무서별 코드
     * 45 : 개인 법인 구분코드
     * 6789 : 과세/면세/법인 사업자 등록/지정일자 일련번호
     * 0 : 검증번호
     * 검증방법
     * 예제번호 : 123-45-67891
     * 하이픈 제거
     * 1234567891 [사업자 10자리]
     * 인증키 배열 - 고정된값이다.
     * 137137135 [인증키 9자리]
     * 사업자 앞 9자리와 인증키 앞 9자리를 각각 곱하여 모두 더합니다.
     * 123456789 [사업자 앞9자리]
     * 137137135 [인증키 9자리]
     * 합계 = (1 * 2) + (2 * 3) + (3 * 7)..... + (9 * 5) = 165
     * 합계 = 165
     * 앞 9자리의 마지막 값을 다시 곱하고 10으로 나눕니다.
     * ((9 * 5) / 10) = 4.5 소수점제거 = 4
     * 합계에 바로위 값을 더해준다.
     * 합계 = (합계165) + 4
     * 합계 = 169
     * 합계를 10으로 나누어 나머지를 구합니다.
     * (169) % 10 = 9
     * 값 = 9
     * 10 - 값을한다.
     * 10 - 값(9) = 1
     * 마지막 자리수가 1이면 사업자등록번호입니다.
     * 그럼으로 예제는 사업자 등록번호입니다.
     * @param number
     * @return
     */
    fun isValidBusinessNumber(number: String): Boolean {
        if (number.isEmpty()) {
            return false
        }
        val _number = number.replace("-".toRegex(), "")
        // checks number
        if (!isNumeric(_number)) {
            return false
        }
        // checks number length
        if (_number.length != 10) {
            return false
        }
        // checks valid code
        var sum: Long = 0
        val checkKeys = intArrayOf(1, 3, 7, 1, 3, 7, 1, 3, 5)
        val _numbers = ArrayList<Int>()
        for (ch in _number.toCharArray()) {
            try {
                _numbers.add(Integer.valueOf(ch.toString()))
            } catch (ex: NumberFormatException) {
                return false
            }
        }
        if (_numbers.size != 10) {
            return false
        }

        // 0 ~ 8 까지 9개의 숫자를 체크키와 곱하여 합에더합니다.
        for (index in 0..8) {
            sum += (checkKeys[index] * _numbers[index]).toLong()
        }

        // 각 8번배열의 값을 곱한 후 10으로 나누고 내림하여 기존 합에 더합니다.
        // 다시 10의 나머지를 구한후 그 값을 10에서 빼면 이것이 검증번호 이며 기존 검증번호와 비교하면됩니다.
        val cal = (10 - (sum + checkKeys[8] * _numbers[8] / 10) % 10).toInt()
        return cal == _numbers[9]
    }

    enum class CheckerID {
        VALID, EMPTY, SHORT, INVALID
    }

    //    public static CheckerID checkValidationEmail(String email) {
    //        if (null == email || TextUtils.isEmpty(email.trim())) {
    //            return CheckerID.EMPTY;
    //        }
    //
    //        // check id length
    //        if (6 > email.trim().length()) {
    //            return CheckerID.SHORT;
    //        }
    //
    //        // check character
    //        String regex = "^[0-9a-zA-Z]+[0-9a-zA-Z\\.\\@]*?$";
    //        Pattern pattern = Pattern.compile(regex);
    //        Matcher match = pattern.matcher(email);
    //        if (!match.matches()) {
    //            return CheckerID.INVALID;
    //        } else {
    //            int atCounts = 0;
    //            for (int index = 0; index < email.length(); index++) {
    //                char ch = email.charAt(index);
    //                if (ch == '@') {
    //                    atCounts++;
    //                }
    //            }
    //
    //            if (2 <= atCounts) {
    //                return CheckerID.INVALID;
    //            }
    //        }
    //
    //        return CheckerID.VALID;
    //    }
    enum class CheckerNickname {
        VALID, EMPTY, SHORT, INVALID, SPACING
    }

    enum class CheckerPassword {
        VALID, EMPTY, SHORT, INVALID
    }
}