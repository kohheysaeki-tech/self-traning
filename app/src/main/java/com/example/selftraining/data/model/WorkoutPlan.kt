package com.example.selftraining.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 週間プランエンティティ
 * 曜日ごとのトレーニングプランを管理する
 */
@Entity(tableName = "workout_plans")
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * 曜日インデックス（0=月曜, 1=火曜, 2=水曜, 3=木曜, 4=金曜, 5=土曜, 6=日曜）
     */
    val dayOfWeek: Int,

    /** プラン名（例: 下半身引き締め） */
    val planName: String,

    /** 休息日フラグ */
    val isRestDay: Boolean = false
)
