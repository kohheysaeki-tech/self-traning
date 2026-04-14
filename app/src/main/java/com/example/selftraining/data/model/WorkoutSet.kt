package com.example.selftraining.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * ワークアウトセットエンティティ
 * プランに紐づく種目・セット数・回数・重量・完了状態を管理する
 */
@Entity(
    tableName = "workout_sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlan::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("planId"),
        Index("exerciseId")
    ]
)
data class WorkoutSet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 紐づくプランID */
    val planId: Long,

    /** 種目ID */
    val exerciseId: Long,

    /** セット数 */
    val sets: Int = 3,

    /** 1セットあたりの回数 */
    val reps: Int = 15,

    /** 重量（kg）。自重の場合は0 */
    val weightKg: Float = 0f,

    /** 完了フラグ（今日のチェック管理用） */
    val isCompleted: Boolean = false,

    /** 表示順 */
    val sortOrder: Int = 0
)

/**
 * WorkoutSetと種目情報を結合したデータクラス（UI表示用）
 * Room の @Relation を使って自動的にJOINする
 */
data class WorkoutSetWithExercise(
    @androidx.room.Embedded val workoutSet: WorkoutSet,
    @androidx.room.Relation(
        parentColumn = "exerciseId",
        entityColumn = "id"
    )
    val exercise: Exercise
)
