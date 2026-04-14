package com.example.selftraining.data.db

import androidx.room.*
import com.example.selftraining.data.model.WorkoutPlan
import com.example.selftraining.data.model.WorkoutSet
import com.example.selftraining.data.model.WorkoutSetWithExercise
import kotlinx.coroutines.flow.Flow

/**
 * ワークアウトプラン・セットテーブルのDAO
 */
@Dao
interface WorkoutDao {

    // ── WorkoutPlan ──────────────────────────────────────────

    /** 全プランを曜日順で取得 */
    @Query("SELECT * FROM workout_plans ORDER BY dayOfWeek")
    fun getAllPlans(): Flow<List<WorkoutPlan>>

    /** 曜日インデックスでプランを取得 */
    @Query("SELECT * FROM workout_plans WHERE dayOfWeek = :dayOfWeek LIMIT 1")
    fun getPlanByDayOfWeek(dayOfWeek: Int): Flow<WorkoutPlan?>

    /** プランを追加 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: WorkoutPlan): Long

    /** 複数プランをまとめて追加（初期データ投入用） */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<WorkoutPlan>)

    /** プランを更新 */
    @Update
    suspend fun updatePlan(plan: WorkoutPlan)

    /** プランを削除 */
    @Delete
    suspend fun deletePlan(plan: WorkoutPlan)

    /** プランの登録件数（初期データ投入判定用） */
    @Query("SELECT COUNT(*) FROM workout_plans")
    suspend fun getPlanCount(): Int

    // ── WorkoutSet ───────────────────────────────────────────

    /**
     * プランIDに紐づくセットと種目情報を結合して取得
     * @Transaction + @Relation を使って自動的にJOINする
     */
    @Transaction
    @Query("SELECT * FROM workout_sets WHERE planId = :planId ORDER BY sortOrder")
    fun getWorkoutSetsWithExercise(planId: Long): Flow<List<WorkoutSetWithExercise>>

    /** セットを追加 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSet(workoutSet: WorkoutSet): Long

    /** 複数セットをまとめて追加（初期データ投入用） */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSets(workoutSets: List<WorkoutSet>)

    /** セットを更新 */
    @Update
    suspend fun updateWorkoutSet(workoutSet: WorkoutSet)

    /** 完了フラグを更新 */
    @Query("UPDATE workout_sets SET isCompleted = :isCompleted WHERE id = :setId")
    suspend fun updateCompletion(setId: Long, isCompleted: Boolean)

    /** プランIDに紐づく全セットの完了フラグをリセット */
    @Query("UPDATE workout_sets SET isCompleted = 0 WHERE planId = :planId")
    suspend fun resetCompletion(planId: Long)

    /** セットを削除 */
    @Delete
    suspend fun deleteWorkoutSet(workoutSet: WorkoutSet)
}
