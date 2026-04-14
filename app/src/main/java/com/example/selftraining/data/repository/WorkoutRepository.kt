package com.example.selftraining.data.repository

import com.example.selftraining.data.db.WorkoutDao
import com.example.selftraining.data.model.WorkoutPlan
import com.example.selftraining.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkoutDao
) {
    fun getAllPlans(): Flow<List<WorkoutPlan>> = workoutDao.getAllPlans()

    suspend fun getPlanByDate(date: String): WorkoutPlan? = workoutDao.getPlanByDate(date)

    fun getPlansByDayOfWeek(dayOfWeek: String): Flow<List<WorkoutPlan>> =
        workoutDao.getPlansByDayOfWeek(dayOfWeek)

    suspend fun insertPlan(plan: WorkoutPlan): Long = workoutDao.insertPlan(plan)

    suspend fun updatePlan(plan: WorkoutPlan) = workoutDao.updatePlan(plan)

    suspend fun deletePlan(plan: WorkoutPlan) = workoutDao.deletePlan(plan)

    fun getSetsByPlanId(planId: Int): Flow<List<WorkoutSet>> = workoutDao.getSetsByPlanId(planId)

    suspend fun getSetsByPlanIdOnce(planId: Int): List<WorkoutSet> =
        workoutDao.getSetsByPlanIdOnce(planId)

    suspend fun insertSet(workoutSet: WorkoutSet): Long = workoutDao.insertSet(workoutSet)

    suspend fun updateSet(workoutSet: WorkoutSet) = workoutDao.updateSet(workoutSet)

    suspend fun deleteSet(workoutSet: WorkoutSet) = workoutDao.deleteSet(workoutSet)

    fun getSetsByDayOfWeek(dayOfWeek: String): Flow<List<WorkoutSet>> =
        workoutDao.getSetsByDayOfWeek(dayOfWeek)
}
