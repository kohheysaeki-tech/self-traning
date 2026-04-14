package com.example.selftraining.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selftraining.data.model.Exercise
import com.example.selftraining.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 種目一覧・詳細画面のViewModel
 */
class ExerciseViewModel(private val exerciseRepository: ExerciseRepository) : ViewModel() {

    /** 選択中のカテゴリ（タブ） */
    private val _selectedCategory = MutableStateFlow("下半身")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    /** 全種目リスト */
    val allExercises: StateFlow<List<Exercise>> = exerciseRepository.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** 選択中カテゴリの種目リスト */
    val filteredExercises: StateFlow<List<Exercise>> = combine(
        allExercises,
        selectedCategory
    ) { exercises, category ->
        exercises.filter { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** 詳細表示中の種目 */
    private val _selectedExercise = MutableStateFlow<Exercise?>(null)
    val selectedExercise: StateFlow<Exercise?> = _selectedExercise.asStateFlow()

    /** カテゴリタブを選択 */
    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    /** 種目をIDで取得して詳細表示 */
    fun loadExercise(id: Long) {
        viewModelScope.launch {
            exerciseRepository.getExerciseById(id).collect { exercise ->
                _selectedExercise.value = exercise
            }
        }
    }

    companion object {
        /** カテゴリ一覧 */
        val categories = listOf("下半身", "上半身", "体幹")
    }
}
