package com.example.selftraining.data.repository

import com.example.selftraining.data.db.WorkoutDao
import com.example.selftraining.data.model.WorkoutPlan
import com.example.selftraining.data.model.WorkoutSet
import com.example.selftraining.data.model.WorkoutSetWithExercise
import kotlinx.coroutines.flow.Flow

/**
 * ワークアウトプラン・セットのリポジトリ
 */
class WorkoutRepository(private val workoutDao: WorkoutDao) {

    // ── WorkoutPlan ──────────────────────────────────────────

    /** 全プランを取得 */
    fun getAllPlans(): Flow<List<WorkoutPlan>> = workoutDao.getAllPlans()

    /** 曜日インデックスでプランを取得 */
    fun getPlanByDayOfWeek(dayOfWeek: Int): Flow<WorkoutPlan?> =
        workoutDao.getPlanByDayOfWeek(dayOfWeek)

    /** プランを追加 */
    suspend fun insertPlan(plan: WorkoutPlan): Long = workoutDao.insertPlan(plan)

    /** プランを更新 */
    suspend fun updatePlan(plan: WorkoutPlan) = workoutDao.updatePlan(plan)

    /** プランを削除 */
    suspend fun deletePlan(plan: WorkoutPlan) = workoutDao.deletePlan(plan)

    // ── WorkoutSet ───────────────────────────────────────────

    /**
     * プランIDに紐づくセットと種目情報を結合して取得
     */
    fun getWorkoutSetsWithExercise(planId: Long): Flow<List<WorkoutSetWithExercise>> =
        workoutDao.getWorkoutSetsWithExercise(planId)

    /** セットを追加 */
    suspend fun insertWorkoutSet(workoutSet: WorkoutSet): Long =
        workoutDao.insertWorkoutSet(workoutSet)

    /** セットを更新 */
    suspend fun updateWorkoutSet(workoutSet: WorkoutSet) = workoutDao.updateWorkoutSet(workoutSet)

    /** 完了フラグを更新 */
    suspend fun updateCompletion(setId: Long, isCompleted: Boolean) =
        workoutDao.updateCompletion(setId, isCompleted)

    /** プランの全セット完了フラグをリセット */
    suspend fun resetCompletion(planId: Long) = workoutDao.resetCompletion(planId)

    /** セットを削除 */
    suspend fun deleteWorkoutSet(workoutSet: WorkoutSet) = workoutDao.deleteWorkoutSet(workoutSet)
}
