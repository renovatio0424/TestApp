package com.herry.test.app.sample.data

@Suppress("unused")
enum class FeedCategory(val id: Int, val title: String) {
    ALL(0, "All"),
    MARKETING(1, "Marketing"),
    CORPORATE(2, "Corporate"),
    EDUCATION(3, "Education"),
    CELEBRATIONS(4, "Celebrations"),
    FESTIVAL(5, "Festival"),
    SOCIAL_MEDIA(6, "Social media"),
    VLOG(7, "Vlog"),
    REVIEW(8, "Review"),
    MEMES(9, "Memes"),
    INTRO(10, "Intro");

    companion object {
        fun generate(id: Int): FeedCategory? = values().firstOrNull { it.id == id }
    }
}