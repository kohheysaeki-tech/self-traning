package com.example.selftraining.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.selftraining.data.model.WorkoutPlan
import com.example.selftraining.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // WorkoutPlan queries
    @Query("SELECT * FROM workout_plans ORDER BY date DESC")
    fun getAllPlans(): Flow<List<WorkoutPlan>>

    @Query("SELECT * FROM workout_plans WHERE date = :date LIMIT 1")
    suspend fun getPlanByDate(date: String): WorkoutPlan?

    @Query("SELECT * FROM workout_plans WHERE dayOfWeek = :dayOfWeek ORDER BY date DESC")
    fun getPlansByDayOfWeek(dayOfWeek: String): Flow<List<WorkoutPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: WorkoutPlan): Long

    @Update
    suspend fun updatePlan(plan: WorkoutPlan)

    @Delete
    suspend fun deletePlan(plan: WorkoutPlan)

    // WorkoutSet queries
    @Query("SELECT * FROM workout_sets WHERE planId = :planId ORDER BY exerciseId, setNumber")
    fun getSetsByPlanId(planId: Int): Flow<List<WorkoutSet>>

    @Query("SELECT * FROM workout_sets WHERE planId = :planId ORDER BY exerciseId, setNumber")
    suspend fun getSetsByPlanIdOnce(planId: Int): List<WorkoutSet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(workoutSet: WorkoutSet): Long

    @Update
    suspend fun updateSet(workoutSet: WorkoutSet)

    @Delete
    suspend fun deleteSet(workoutSet: WorkoutSet)

    @Query("DELETE FROM workout_sets WHERE planId = :planId")
    suspend fun deleteSetsByPlanId(planId: Int)

    @Query("SELECT * FROM workout_sets WHERE planId IN (SELECT id FROM workout_plans WHERE dayOfWeek = :dayOfWeek) ORDER BY exerciseId, setNumber")
    fun getSetsByDayOfWeek(dayOfWeek: String): Flow<List<WorkoutSet>>
}
