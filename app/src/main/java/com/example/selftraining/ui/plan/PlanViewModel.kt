package com.example.selftraining.ui.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selftraining.data.model.Exercise
import com.example.selftraining.data.model.WorkoutPlan
import com.example.selftraining.data.model.WorkoutSet
import com.example.selftraining.data.repository.ExerciseRepository
import com.example.selftraining.data.repository.WorkoutRepository
import com.example.selftraining.util.DAYS_OF_WEEK_JAPANESE
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
class PlanViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    val daysOfWeek = DAYS_OF_WEEK_JAPANESE

    val allExercises: StateFlow<List<Exercise>> = exerciseRepository.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _exercises = MutableStateFlow<Map<Int, Exercise>>(emptyMap())
    val exercises: StateFlow<Map<Int, Exercise>> = _exercises.asStateFlow()

    // Map from dayOfWeek -> list of (WorkoutPlan, WorkoutSet list)
    private val _plansByDay = MutableStateFlow<Map<String, Pair<WorkoutPlan?, List<WorkoutSet>>>>(emptyMap())
    val plansByDay: StateFlow<Map<String, Pair<WorkoutPlan?, List<WorkoutSet>>>> = _plansByDay.asStateFlow()

    init {
        loadExercises()
        loadPlansByDay()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            exerciseRepository.getAllExercises().collect { list ->
                _exercises.value = list.associateBy { it.id }
            }
        }
    }

    private fun loadPlansByDay() {
        viewModelScope.launch {
            // For each day of week, get its sets (joining through plans)
            workoutRepository.getAllPlans().collect { plans ->
                val result = mutableMapOf<String, Pair<WorkoutPlan?, List<WorkoutSet>>>()
                daysOfWeek.forEach { day ->
                    val latestPlan = plans.filter { it.dayOfWeek == day }.maxByOrNull { it.date }
                    if (latestPlan != null) {
                        val sets = workoutRepository.getSetsByPlanIdOnce(latestPlan.id)
                        result[day] = Pair(latestPlan, sets)
                    } else {
                        result[day] = Pair(null, emptyList())
                    }
                }
                _plansByDay.value = result
            }
        }
    }

    fun addWorkoutSetForDay(dayOfWeek: String, exerciseId: Int, reps: Int, weightKg: Float) {
        viewModelScope.launch {
            val existingPair = _plansByDay.value[dayOfWeek]
            val plan = existingPair?.first ?: run {
                val date = getNextDateForDay(dayOfWeek)
                val newPlan = WorkoutPlan(date = date, dayOfWeek = dayOfWeek)
                val id = workoutRepository.insertPlan(newPlan)
                newPlan.copy(id = id.toInt())
            }

            val existingSets = workoutRepository.getSetsByPlanIdOnce(plan.id)
            val setNumber = existingSets.count { it.exerciseId == exerciseId } + 1

            val newSet = WorkoutSet(
                planId = plan.id,
                exerciseId = exerciseId,
                setNumber = setNumber,
                reps = reps,
                weightKg = weightKg
            )
            workoutRepository.insertSet(newSet)
            loadPlansByDay()
        }
    }

    fun deleteSet(workoutSet: WorkoutSet) {
        viewModelScope.launch {
            workoutRepository.deleteSet(workoutSet)
            loadPlansByDay()
        }
    }

    private fun getNextDateForDay(dayOfWeek: String): String {
        val dayIndex = daysOfWeek.indexOf(dayOfWeek) + 1 // 1=月(Mon) ... 7=日(Sun)
        require(dayIndex in 1..7) { "Invalid dayOfWeek: $dayOfWeek" }
        val today = LocalDate.now()
        var date = today
        var attempts = 0
        while (date.dayOfWeek.value != dayIndex && attempts < 7) {
            date = date.plusDays(1)
            attempts++
        }
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}
