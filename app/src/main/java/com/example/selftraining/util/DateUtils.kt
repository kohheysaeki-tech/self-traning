package com.example.selftraining.util

val DAYS_OF_WEEK_JAPANESE = listOf("月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日", "日曜日")

fun getDayOfWeekJapanese(dayValue: Int): String =
    if (dayValue in 1..7) DAYS_OF_WEEK_JAPANESE[dayValue - 1] else DAYS_OF_WEEK_JAPANESE[0]
