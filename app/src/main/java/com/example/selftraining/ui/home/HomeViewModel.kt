package com.example.selftraining.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selftraining.data.model.WorkoutPlan
import com.example.selftraining.data.model.WorkoutSetWithExercise
import com.example.selftraining.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * ホーム画面のViewModel
 * 今日の曜日に対応したトレーニングメニューを管理する
 */
class HomeViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {

    /** 今日の曜日インデックス（0=月曜〜6=日曜）に変換 */
    private val todayDayIndex: Int
        get() {
            val dow = LocalDate.now().dayOfWeek
            return when (dow) {
                DayOfWeek.MONDAY    -> 0
                DayOfWeek.TUESDAY   -> 1
                DayOfWeek.WEDNESDAY -> 2
                DayOfWeek.THURSDAY  -> 3
                DayOfWeek.FRIDAY    -> 4
                DayOfWeek.SATURDAY  -> 5
                DayOfWeek.SUNDAY    -> 6
            }
        }

    /** 今日のプラン */
    val todayPlan: StateFlow<WorkoutPlan?> = workoutRepository
        .getPlanByDayOfWeek(todayDayIndex)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** 今日のセット一覧（種目情報付き） */
    private val _todaySets = MutableStateFlow<List<WorkoutSetWithExercise>>(emptyList())
    val todaySets: StateFlow<List<WorkoutSetWithExercise>> = _todaySets.asStateFlow()

    init {
        // プランIDが確定したらセットを取得
        viewModelScope.launch {
            todayPlan.collectLatest { plan ->
                if (plan != null) {
                    workoutRepository.getWorkoutSetsWithExercise(plan.id)
                        .collect { sets -> _todaySets.value = sets }
                } else {
                    _todaySets.value = emptyList()
                }
            }
        }
    }

    /** チェックボックスのトグル */
    fun toggleCompletion(setId: Long, currentCompleted: Boolean) {
        viewModelScope.launch {
            workoutRepository.updateCompletion(setId, !currentCompleted)
        }
    }

    /** 今日のメニューをすべてリセット */
    fun resetTodayCompletion() {
        viewModelScope.launch {
            todayPlan.value?.let { plan ->
                workoutRepository.resetCompletion(plan.id)
            }
        }
    }

    /** 今日の日付文字列 */
    fun getTodayLabel(): String {
        val date = LocalDate.now()
        val dayNames = listOf("月", "火", "水", "木", "金", "土", "日")
        val dayName = dayNames[todayDayIndex]
        return "${date.year}年${date.monthValue}月${date.dayOfMonth}日（${dayName}）"
    }
}
