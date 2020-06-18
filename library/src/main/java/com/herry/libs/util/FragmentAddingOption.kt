package com.herry.libs.util

import androidx.annotation.AnimRes

class FragmentAddingOption(
    val isReplace: Boolean = true,
    val tag: String? = null,
    val isAddToBackStack: Boolean = false,
    val customAnimations: CustomAnimations? = null,
    /**
     * FragmentTransaction.TRANSIT_UNSET = -1;
     * FragmentTransaction.TRANSIT_NONE = 0,
     * FragmentTransaction.TRANSIT_FRAGMENT_OPEN = 1 | TRANSIT_ENTER_MASK;,
     * FragmentTransaction.TRANSIT_FRAGMENT_CLOSE = 2 | TRANSIT_ENTER_MASK;,
     * FragmentTransaction.TRANSIT_FRAGMENT_FADE = 3 | TRANSIT_ENTER_MASK; */
    val transit: Int = -1
) {
    /**
     * Set specific animation resources to run for the fragments that are
     * entering and exiting in this transaction. The `popEnter`
     * and `popExit` animations will be played for enter/exit
     * operations specifically when popping the back stack.
     */
    class CustomAnimations(
        @AnimRes val enter: Int = 0,
        @AnimRes val exit: Int = 0,
        @AnimRes val popEnter: Int = 0,
        @AnimRes val popExit: Int = 0
    )
}