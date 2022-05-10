package com.herry.test.app.bottomnav.home.form

import androidx.annotation.IdRes
import com.herry.test.R

enum class HomeBottomNavScreenId(@IdRes val id: Int) {
    HOT_FEEDS (R.id.bottom_nav_new_navigation),
    FEEDS (R.id.bottom_nav_feeds_navigation),
    CREATE (R.id.bottom_nav_create_navigation),
    ME (R.id.bottom_nav_me_navigation);

    companion object {
        fun generate(id: Int): HomeBottomNavScreenId? = values().firstOrNull { it.id == id }
    }
}