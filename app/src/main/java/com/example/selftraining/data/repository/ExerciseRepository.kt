package com.example.selftraining.data.repository

import com.example.selftraining.data.db.ExerciseDao
import com.example.selftraining.data.model.Exercise
import kotlinx.coroutines.flow.Flow

/**
 * 種目データのリポジトリ
 * ViewModelとDAOの中間層として、データアクセスロジックをカプセル化する
 */
class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    /** 全種目を取得 */
    fun getAllExercises(): Flow<List<Exercise>> = exerciseDao.getAllExercises()

    /** カテゴリで絞り込み */
    fun getExercisesByCategory(category: String): Flow<List<Exercise>> =
        exerciseDao.getExercisesByCategory(category)

    /** IDで1件取得 */
    fun getExerciseById(id: Long): Flow<Exercise?> = exerciseDao.getExerciseById(id)

    /** 種目を追加 */
    suspend fun insertExercise(exercise: Exercise): Long = exerciseDao.insertExercise(exercise)

    /** 種目を更新 */
    suspend fun updateExercise(exercise: Exercise) = exerciseDao.updateExercise(exercise)

    /** 種目を削除 */
    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.deleteExercise(exercise)
}
