package com.example.selftraining.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_plans")
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val dayOfWeek: String,
    val memo: String = ""
)
