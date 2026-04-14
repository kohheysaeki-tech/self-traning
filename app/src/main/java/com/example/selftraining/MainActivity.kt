package com.example.selftraining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.selftraining.data.db.AppDatabase
import com.example.selftraining.data.repository.ExerciseRepository
import com.example.selftraining.data.repository.WorkoutRepository
import com.example.selftraining.navigation.AppNavigation
import com.example.selftraining.ui.exercise.ExerciseViewModel
import com.example.selftraining.ui.home.HomeViewModel
import com.example.selftraining.ui.plan.PlanViewModel
import com.example.selftraining.ui.theme.SelfTrainingTheme

/**
 * アプリのエントリーポイント
 * Roomデータベースとリポジトリを初期化し、ViewModelを生成してナビゲーションを起動する
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // データベース・リポジトリの初期化
        val database = AppDatabase.getDatabase(applicationContext)
        val exerciseRepository = ExerciseRepository(database.exerciseDao())
        val workoutRepository = WorkoutRepository(database.workoutDao())

        // ViewModelの生成（ViewModelProviderを使って生成する）
        val homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(workoutRepository)
        )[HomeViewModel::class.java]

        val exerciseViewModel = ViewModelProvider(
            this,
            ExerciseViewModelFactory(exerciseRepository)
        )[ExerciseViewModel::class.java]

        val planViewModel = ViewModelProvider(
            this,
            PlanViewModelFactory(workoutRepository, exerciseRepository)
        )[PlanViewModel::class.java]

        setContent {
            SelfTrainingTheme {
                AppNavigation(
                    homeViewModel = homeViewModel,
                    exerciseViewModel = exerciseViewModel,
                    planViewModel = planViewModel
                )
            }
        }
    }
}

// ── ViewModelFactory 定義 ─────────────────────────────────────────────────

/** HomeViewModel用のファクトリ */
class HomeViewModelFactory(private val workoutRepository: WorkoutRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(workoutRepository) as T
    }
}

/** ExerciseViewModel用のファクトリ */
class ExerciseViewModelFactory(private val exerciseRepository: ExerciseRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ExerciseViewModel(exerciseRepository) as T
    }
}

/** PlanViewModel用のファクトリ */
class PlanViewModelFactory(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PlanViewModel(workoutRepository, exerciseRepository) as T
    }
}
