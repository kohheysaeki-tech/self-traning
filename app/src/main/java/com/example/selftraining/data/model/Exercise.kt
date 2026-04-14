package com.example.selftraining.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * トレーニング種目エンティティ
 * 種目名・対象部位・カテゴリ・説明・やり方ステップを保持する
 */
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 種目名（例: ウォーキングランジ） */
    val name: String,

    /** 鍛えられる筋肉部位（例: 大腿四頭筋・臀部） */
    val targetMuscle: String,

    /** カテゴリ（下半身 / 上半身 / 体幹） */
    val category: String,

    /** 種目の説明文 */
    val description: String,

    /** やり方のステップを改行区切りで保存 */
    val steps: String
)
