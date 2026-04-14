package com.example.selftraining.ui.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.selftraining.data.model.Exercise
import com.example.selftraining.data.model.WorkoutSetWithExercise

/**
 * プラン管理画面
 * 曜日別のトレーニングメニューを表示・編集できる
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(viewModel: PlanViewModel) {
    val selectedDayIndex by viewModel.selectedDayIndex.collectAsStateWithLifecycle()
    val selectedPlan by viewModel.selectedPlan.collectAsStateWithLifecycle()
    val planSets by viewModel.selectedPlanSets.collectAsStateWithLifecycle()
    val allExercises by viewModel.allExercises.collectAsStateWithLifecycle()

    // セット追加ダイアログ表示フラグ
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("プラン管理") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            if (selectedPlan != null && !selectedPlan!!.isRestDay) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "種目を追加")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 曜日タブ
            ScrollableTabRow(
                selectedTabIndex = selectedDayIndex,
                edgePadding = 8.dp
            ) {
                PlanViewModel.dayNames.forEachIndexed { index, day ->
                    Tab(
                        selected = selectedDayIndex == index,
                        onClick = { viewModel.selectDay(index) },
                        text = { Text(day) }
                    )
                }
            }

            // プランの内容
            if (selectedPlan == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (selectedPlan!!.isRestDay) {
                RestDayContent(planName = selectedPlan!!.planName)
            } else {
                PlanContent(
                    planName = selectedPlan!!.planName,
                    sets = planSets,
                    onDeleteSet = { viewModel.deleteWorkoutSet(it.workoutSet) }
                )
            }
        }
    }

    // セット追加ダイアログ
    if (showAddDialog) {
        AddWorkoutSetDialog(
            exercises = allExercises,
            onDismiss = { showAddDialog = false },
            onConfirm = { exerciseId, sets, reps, weight ->
                viewModel.addWorkoutSet(exerciseId, sets, reps, weight)
                showAddDialog = false
            }
        )
    }
}

/** 休息日コンテンツ */
@Composable
private fun RestDayContent(planName: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("😴", style = MaterialTheme.typography.displayMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = planName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/** プランコンテンツ */
@Composable
private fun PlanContent(
    planName: String,
    sets: List<WorkoutSetWithExercise>,
    onDeleteSet: (WorkoutSetWithExercise) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = planName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (sets.isEmpty()) {
            item {
                Text(
                    text = "＋ボタンから種目を追加してください",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(sets, key = { it.workoutSet.id }) { setWithExercise ->
                PlanSetItem(
                    setWithExercise = setWithExercise,
                    onDelete = { onDeleteSet(setWithExercise) }
                )
            }
        }
    }
}

/** プランセットアイテム */
@Composable
private fun PlanSetItem(
    setWithExercise: WorkoutSetWithExercise,
    onDelete: () -> Unit
) {
    val set = setWithExercise.workoutSet
    val exercise = setWithExercise.exercise

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                val weightText = if (set.weightKg > 0f) " × ${set.weightKg}kg" else " （自重）"
                Text(
                    text = "${set.sets}セット × ${set.reps}回$weightText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = exercise.targetMuscle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "削除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/** セット追加ダイアログ */
@Composable
private fun AddWorkoutSetDialog(
    exercises: List<Exercise>,
    onDismiss: () -> Unit,
    onConfirm: (exerciseId: Long, sets: Int, reps: Int, weight: Float) -> Unit
) {
    var selectedExercise by remember { mutableStateOf(exercises.firstOrNull()) }
    var setsText by remember { mutableStateOf("3") }
    var repsText by remember { mutableStateOf("15") }
    var weightText by remember { mutableStateOf("0") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("種目を追加") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // 種目選択
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedExercise?.name ?: "種目を選択",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("種目") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        exercises.forEach { exercise ->
                            DropdownMenuItem(
                                text = { Text(exercise.name) },
                                onClick = {
                                    selectedExercise = exercise
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // セット数
                OutlinedTextField(
                    value = setsText,
                    onValueChange = { setsText = it },
                    label = { Text("セット数") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // 回数
                OutlinedTextField(
                    value = repsText,
                    onValueChange = { repsText = it },
                    label = { Text("回数") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // 重量
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("重量（kg、自重なら0）") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val exercise = selectedExercise ?: return@TextButton
                    val sets = setsText.toIntOrNull() ?: 3
                    val reps = repsText.toIntOrNull() ?: 15
                    val weight = weightText.toFloatOrNull() ?: 0f
                    onConfirm(exercise.id, sets, reps, weight)
                }
            ) {
                Text("追加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        }
    )
}
