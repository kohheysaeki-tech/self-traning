package com.example.selftraining.data.db

import androidx.room.*
import com.example.selftraining.data.model.Exercise
import kotlinx.coroutines.flow.Flow

/**
 * 種目テーブルのDAO
 */
@Dao
interface ExerciseDao {

    /** 全種目を取得 */
    @Query("SELECT * FROM exercises ORDER BY category, name")
    fun getAllExercises(): Flow<List<Exercise>>

    /** カテゴリで絞り込んで取得 */
    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY name")
    fun getExercisesByCategory(category: String): Flow<List<Exercise>>

    /** IDで1件取得 */
    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getExerciseById(id: Long): Flow<Exercise?>

    /** 種目を追加 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long

    /** 複数種目をまとめて追加（初期データ投入用） */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)

    /** 種目を更新 */
    @Update
    suspend fun updateExercise(exercise: Exercise)

    /** 種目を削除 */
    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    /** 登録件数を取得（初期データ投入判定用） */
    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun getCount(): Int
}
