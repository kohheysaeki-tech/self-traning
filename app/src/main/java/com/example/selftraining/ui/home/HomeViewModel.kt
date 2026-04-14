package com.example.selftraining.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selftraining.data.model.Exercise
import com.example.selftraining.data.model.WorkoutPlan
import com.example.selftraining.data.model.WorkoutSet
import com.example.selftraining.data.repository.ExerciseRepository
import com.example.selftraining.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val today: LocalDate = LocalDate.now()
    val todayDateString: String = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val todayDisplayString: String = today.format(DateTimeFormatter.ofPattern("yyyy年M月d日"))
    val todayDayOfWeek: String = getDayOfWeekJapanese(today.dayOfWeek.value)

    private val _todayPlan = MutableStateFlow<WorkoutPlan?>(null)
    val todayPlan: StateFlow<WorkoutPlan?> = _todayPlan.asStateFlow()

    private val _todaySets = MutableStateFlow<List<WorkoutSet>>(emptyList())
    val todaySets: StateFlow<List<WorkoutSet>> = _todaySets.asStateFlow()

    val allExercises: StateFlow<List<Exercise>> = exerciseRepository.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _exercises = MutableStateFlow<Map<Int, Exercise>>(emptyMap())
    val exercises: StateFlow<Map<Int, Exercise>> = _exercises.asStateFlow()

    init {
        loadTodayPlan()
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            exerciseRepository.getAllExercises().collect { list ->
                _exercises.value = list.associateBy { it.id }
            }
        }
    }

    private fun loadTodayPlan() {
        viewModelScope.launch {
            val plan = workoutRepository.getPlanByDate(todayDateString)
                ?: run {
                    val newPlan = WorkoutPlan(
                        date = todayDateString,
                        dayOfWeek = todayDayOfWeek
                    )
                    val id = workoutRepository.insertPlan(newPlan)
                    newPlan.copy(id = id.toInt())
                }
            _todayPlan.value = plan
            workoutRepository.getSetsByPlanId(plan.id).collect { sets ->
                _todaySets.value = sets
            }
        }
    }

    fun toggleSetCompletion(workoutSet: WorkoutSet) {
        viewModelScope.launch {
            workoutRepository.updateSet(workoutSet.copy(isCompleted = !workoutSet.isCompleted))
        }
    }

    fun addWorkoutSet(exerciseId: Int, reps: Int, weightKg: Float, setNumber: Int) {
        viewModelScope.launch {
            val plan = _todayPlan.value ?: return@launch
            val newSet = WorkoutSet(
                planId = plan.id,
                exerciseId = exerciseId,
                setNumber = setNumber,
                reps = reps,
                weightKg = weightKg
            )
            workoutRepository.insertSet(newSet)
        }
    }

    fun deleteSet(workoutSet: WorkoutSet) {
        viewModelScope.launch {
            workoutRepository.deleteSet(workoutSet)
        }
    }

    private fun getDayOfWeekJapanese(dayValue: Int): String = when (dayValue) {
        1 -> "月曜日"
        2 -> "火曜日"
        3 -> "水曜日"
        4 -> "木曜日"
        5 -> "金曜日"
        6 -> "土曜日"
        7 -> "日曜日"
        else -> "月曜日"
    }
}
