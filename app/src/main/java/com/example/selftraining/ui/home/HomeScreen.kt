package com.example.selftraining.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.selftraining.data.model.WorkoutSetWithExercise

/**
 * ホーム画面
 * 今日の日付・曜日と今日のトレーニングメニュー（チェックボックス付き）を表示する
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val todayPlan by viewModel.todayPlan.collectAsStateWithLifecycle()
    val todaySets by viewModel.todaySets.collectAsStateWithLifecycle()
    val todayLabel by viewModel.todayLabel.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ホーム",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // 日付カード
            item {
                DateCard(label = todayLabel)
            }

            // 今日のメニュータイトル
            item {
                Text(
                    text = "今日のメニュー",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (todayPlan != null) {
                    Text(
                        text = todayPlan!!.planName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (todayPlan == null || todayPlan!!.isRestDay) {
                // 休息日 or データなし
                item {
                    RestDayCard()
                }
            } else if (todaySets.isEmpty()) {
                // メニューが登録されていない場合
                item {
                    EmptyMenuCard()
                }
            } else {
                // メニューアイテム一覧
                items(todaySets, key = { it.workoutSet.id }) { setWithExercise ->
                    WorkoutSetItem(
                        setWithExercise = setWithExercise,
                        onToggle = {
                            viewModel.toggleCompletion(
                                setWithExercise.workoutSet.id,
                                setWithExercise.workoutSet.isCompleted
                            )
                        }
                    )
                }

                // 完了状況サマリー
                item {
                    val completedCount = todaySets.count { it.workoutSet.isCompleted }
                    ProgressSummary(completed = completedCount, total = todaySets.size)
                }

                // リセットボタン
                item {
                    OutlinedButton(
                        onClick = { viewModel.resetTodayCompletion() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("チェックをリセット")
                    }
                }
            }
        }
    }
}

/** 日付表示カード */
@Composable
private fun DateCard(label: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/** 休息日カード */
@Composable
private fun RestDayCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "😴",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "今日は休息日です",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ゆっくり体を休めましょう",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

/** メニューなしカード */
@Composable
private fun EmptyMenuCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "プラン管理画面からメニューを追加してください",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/** ワークアウトセット1件の表示 */
@Composable
private fun WorkoutSetItem(
    setWithExercise: WorkoutSetWithExercise,
    onToggle: () -> Unit
) {
    val set = setWithExercise.workoutSet
    val exercise = setWithExercise.exercise

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (set.isCompleted)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // チェックアイコン
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (set.isCompleted)
                        Icons.Filled.CheckCircle
                    else
                        Icons.Outlined.Circle,
                    contentDescription = if (set.isCompleted) "完了" else "未完了",
                    tint = if (set.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = buildSetDetail(set.sets, set.reps, set.weightKg),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = exercise.targetMuscle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

/** セット詳細テキストを生成 */
private fun buildSetDetail(sets: Int, reps: Int, weightKg: Float): String {
    val weight = if (weightKg > 0f) " × ${weightKg}kg" else " （自重）"
    return "${sets}セット × ${reps}回$weight"
}

/** 進捗サマリー */
@Composable
private fun ProgressSummary(completed: Int, total: Int) {
    val progress = if (total > 0) completed.toFloat() / total else 0f
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "完了: $completed / $total",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )
    }
}
