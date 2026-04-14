package com.example.selftraining.data.repository

import com.example.selftraining.data.db.ExerciseDao
import com.example.selftraining.data.model.Exercise
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {
    fun getAllExercises(): Flow<List<Exercise>> = exerciseDao.getAllExercises()

    fun getExercisesByCategory(category: String): Flow<List<Exercise>> =
        exerciseDao.getExercisesByCategory(category)

    suspend fun getExerciseById(id: Int): Exercise? = exerciseDao.getExerciseById(id)

    suspend fun insertExercise(exercise: Exercise): Long = exerciseDao.insertExercise(exercise)

    suspend fun updateExercise(exercise: Exercise) = exerciseDao.updateExercise(exercise)

    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.deleteExercise(exercise)
}
