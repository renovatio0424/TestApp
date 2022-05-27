package com.herry.test.app.sample.home.form

import androidx.annotation.IdRes
import com.herry.test.R

enum class HomeBottomNavScreenId(@IdRes val id: Int) {
    HOT_FEEDS (R.id.sample_new_navigation),
    FEEDS (R.id.sample_feeds_navigation),
    CREATE (R.id.sample_create_navigation),
    ME (R.id.sample_me_navigation);

    companion object {
        fun generate(id: Int): HomeBottomNavScreenId? = values().firstOrNull { it.id == id }
    }
}