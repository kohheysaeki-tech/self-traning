package com.example.selftraining.ui.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selftraining.data.model.Exercise
import com.example.selftraining.data.model.WorkoutPlan
import com.example.selftraining.data.model.WorkoutSet
import com.example.selftraining.data.model.WorkoutSetWithExercise
import com.example.selftraining.data.repository.ExerciseRepository
import com.example.selftraining.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * プラン管理画面のViewModel
 * 曜日別メニューの登録・編集・削除を管理する
 */
class PlanViewModel(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    /** 全プラン（曜日順） */
    val allPlans: StateFlow<List<WorkoutPlan>> = workoutRepository.getAllPlans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** 選択中の曜日インデックス */
    private val _selectedDayIndex = MutableStateFlow(0)
    val selectedDayIndex: StateFlow<Int> = _selectedDayIndex.asStateFlow()

    /** 選択中の曜日のプラン */
    val selectedPlan: StateFlow<WorkoutPlan?> = combine(
        allPlans,
        selectedDayIndex
    ) { plans, dayIndex ->
        plans.find { it.dayOfWeek == dayIndex }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** 選択中プランのセット一覧 */
    private val _selectedPlanSets = MutableStateFlow<List<WorkoutSetWithExercise>>(emptyList())
    val selectedPlanSets: StateFlow<List<WorkoutSetWithExercise>> = _selectedPlanSets.asStateFlow()

    /** 全種目一覧（セット追加ダイアログ用） */
    val allExercises: StateFlow<List<Exercise>> = exerciseRepository.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            selectedPlan.collectLatest { plan ->
                if (plan != null && !plan.isRestDay) {
                    workoutRepository.getWorkoutSetsWithExercise(plan.id)
                        .collect { sets -> _selectedPlanSets.value = sets }
                } else {
                    _selectedPlanSets.value = emptyList()
                }
            }
        }
    }

    /** 曜日を選択 */
    fun selectDay(dayIndex: Int) {
        _selectedDayIndex.value = dayIndex
    }

    /** セットを追加 */
    fun addWorkoutSet(exerciseId: Long, sets: Int, reps: Int, weightKg: Float) {
        viewModelScope.launch {
            val plan = selectedPlan.value ?: return@launch
            val sortOrder = _selectedPlanSets.value.size
            workoutRepository.insertWorkoutSet(
                WorkoutSet(
                    planId = plan.id,
                    exerciseId = exerciseId,
                    sets = sets,
                    reps = reps,
                    weightKg = weightKg,
                    sortOrder = sortOrder
                )
            )
        }
    }

    /** セットを更新 */
    fun updateWorkoutSet(workoutSet: WorkoutSet) {
        viewModelScope.launch {
            workoutRepository.updateWorkoutSet(workoutSet)
        }
    }

    /** セットを削除 */
    fun deleteWorkoutSet(workoutSet: WorkoutSet) {
        viewModelScope.launch {
            workoutRepository.deleteWorkoutSet(workoutSet)
        }
    }

    companion object {
        /** 曜日名リスト */
        val dayNames = listOf("月", "火", "水", "木", "金", "土", "日")
        val dayFullNames = listOf("月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日", "日曜日")
    }
}
