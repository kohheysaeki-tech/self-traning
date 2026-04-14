package com.example.selftraining.di

import android.content.Context
import androidx.room.Room
import com.example.selftraining.data.db.AppDatabase
import com.example.selftraining.data.db.ExerciseDao
import com.example.selftraining.data.db.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        // Hiltが提供するシングルトンのため、ラムダからdatabaseを参照できるようlateinitで保持する
        lateinit var database: AppDatabase
        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "self_training.db"
        ).addCallback(object : androidx.room.RoomDatabase.Callback() {
            override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                super.onCreate(db)
                // 初回DB作成時にプリセット種目を挿入する
                CoroutineScope(Dispatchers.IO).launch {
                    database.exerciseDao().insertExercises(AppDatabase.PRESET_EXERCISES)
                }
            }
        }).build()
        return database
    }

    @Provides
    @Singleton
    fun provideExerciseDao(db: AppDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    @Singleton
    fun provideWorkoutDao(db: AppDatabase): WorkoutDao = db.workoutDao()
}
