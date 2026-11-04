package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.GymVerseViewModel

@Composable
fun WorkoutTrackerScreen(
    viewModel: GymVerseViewModel,
    modifier: Modifier = Modifier
) {
    val activeWorkout by viewModel.activeWorkout.collectAsState()
    val workoutRoutines by viewModel.repository.workoutRoutines.collectAsState()
    val workoutHistory by viewModel.repository.workoutHistory.collectAsState()
    val personalRecords by viewModel.repository.personalRecords.collectAsState()

    var selectedTab by remember { mutableStateOf(if (activeWorkout.isRunning) 0 else 1) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Workout Sub-tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepCharcoal)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf("Active Session", "Routines", "PRs & History")
            tabs.forEachIndexed { index, title ->
                val isSel = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) GraphiteElevated else Color.Transparent)
                        .border(
                            1.dp,
                            if (isSel) ElectricLime.copy(alpha = 0.5f) else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedTab = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (index == 0 && activeWorkout.isRunning) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(StatusOperational)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = title,
                            color = if (isSel) ElectricLime else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        when (selectedTab) {
            0 -> ActiveSessionView(viewModel = viewModel, activeWorkout = activeWorkout)
            1 -> RoutinesListView(
                routines = workoutRoutines,
                onStartRoutine = {
                    viewModel.startRoutine(it)
                    selectedTab = 0
                }
            )
            2 -> HistoryAndPrsView(
                records = personalRecords,
                history = workoutHistory
            )
        }
    }
}

@Composable
fun ActiveSessionView(
    viewModel: GymVerseViewModel,
    activeWorkout: com.example.viewmodel.ActiveWorkoutState
) {
    val routine = activeWorkout.routine

    if (!activeWorkout.isRunning || routine == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(GraphiteElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = ElectricLime,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Active Workout Session",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextIvory
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Select a verified gym training routine from the Routines tab to track sets, volume, and smart machine substitutions.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Status Header & Stopwatch
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GraphiteCard),
                    border = BorderStroke(1.dp, GraphiteBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = routine.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextIvory
                                )
                                Text(
                                    text = "${routine.exercises.size} Exercises • ${routine.level}",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }

                            val mins = activeWorkout.elapsedSeconds / 60
                            val secs = activeWorkout.elapsedSeconds % 60
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ObsidianBlack)
                                    .border(1.dp, ElectricLime.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = String.format("%02d:%02d", mins, secs),
                                    color = ElectricLime,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Sets Completed: ${activeWorkout.completedSetsCount}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "Total Volume: ${activeWorkout.totalVolumeKg.toInt()} kg",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusOperational
                            )
                        }

                        // Rest Timer Banner if active
                        if (activeWorkout.isRestTimerActive) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = ObsidianBlack,
                                border = BorderStroke(1.dp, StatusInUse.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Timer, contentDescription = null, tint = StatusInUse, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Resting: ${activeWorkout.restTimerRemainingSeconds}s remaining",
                                            color = StatusInUse,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Text(
                                        text = "Skip",
                                        color = TextIvory,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { viewModel.cancelRestTimer() }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Exercise Cards with Sets Logging
            itemsIndexed(routine.exercises) { exIndex, exercise ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = GraphiteCard),
                    border = BorderStroke(1.dp, GraphiteBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = exercise.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextIvory
                                )
                                Text(
                                    text = "Target: ${exercise.targetSets} sets × ${exercise.targetReps}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            // Smart Substitution Button
                            if (exercise.alternatives.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.showFeedback("Substitutes for ${exercise.name}: ${exercise.alternatives.joinToString(", ")}")
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                                ) {
                                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Substitute", color = CyberCyan, fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Sets Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "SET", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSubtle, modifier = Modifier.width(36.dp))
                            Text(text = "WEIGHT (KG)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSubtle, modifier = Modifier.weight(1f))
                            Text(text = "REPS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSubtle, modifier = Modifier.weight(1f))
                            Text(text = "DONE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSubtle, modifier = Modifier.width(48.dp))
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        exercise.sets.forEachIndexed { setIndex, setLog ->
                            var weightText by remember { mutableStateOf(if (setLog.weightKg > 0) setLog.weightKg.toString() else "40") }
                            var repsText by remember { mutableStateOf(setLog.reps.toString()) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#${setLog.setNumber}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricLime,
                                    modifier = Modifier.width(36.dp)
                                )

                                OutlinedTextField(
                                    value = weightText,
                                    onValueChange = { weightText = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .padding(end = 6.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricLime,
                                        unfocusedBorderColor = GraphiteBorder,
                                        focusedTextColor = TextIvory,
                                        unfocusedTextColor = TextIvory
                                    ),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = repsText,
                                    onValueChange = { repsText = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .padding(end = 6.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricLime,
                                        unfocusedBorderColor = GraphiteBorder,
                                        focusedTextColor = TextIvory,
                                        unfocusedTextColor = TextIvory
                                    ),
                                    singleLine = true
                                )

                                IconButton(
                                    onClick = {
                                        val w = weightText.toDoubleOrNull() ?: 0.0
                                        val r = repsText.toIntOrNull() ?: 10
                                        viewModel.toggleSetCompleted(exIndex, setIndex, w, r)
                                    },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (setLog.isCompleted) StatusOperational else GraphiteElevated)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Complete Set",
                                        tint = if (setLog.isCompleted) ObsidianBlack else TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Finish Workout Button
            item {
                Button(
                    onClick = { viewModel.finishWorkout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("finish_workout_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusOperational, contentColor = ObsidianBlack)
                ) {
                    Icon(Icons.Default.DoneAll, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Complete & Save Workout Session", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun RoutinesListView(
    routines: List<WorkoutRoutine>,
    onStartRoutine: (WorkoutRoutine) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(routines) { routine ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GraphiteCard),
                border = BorderStroke(1.dp, GraphiteBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GraphiteElevated)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = routine.category,
                                color = ElectricLime,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${routine.durationMinutes} min • ${routine.level}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = routine.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextIvory
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = routine.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "EXERCISE SEQUENCE:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSubtle,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    routine.exercises.forEachIndexed { i, ex ->
                        Text(
                            text = "${i + 1}. ${ex.name} (${ex.targetSets} sets)",
                            fontSize = 12.sp,
                            color = TextIvory
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onStartRoutine(routine) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = ObsidianBlack)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start This Workout", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryAndPrsView(
    records: List<PersonalRecord>,
    history: List<WorkoutHistoryRecord>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // PRs Shelf
        item {
            Text(
                text = "PERSONAL RECORDS (VERIFIED LIFTS)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricLime,
                letterSpacing = 1.sp
            )
        }

        items(records) { pr ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GraphiteCard),
                border = BorderStroke(1.dp, GraphiteBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = pr.exerciseName, color = TextIvory, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Achieved ${pr.date}", color = TextMuted, fontSize = 11.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GraphiteElevated)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${pr.weightKg.toInt()} KG × ${pr.reps}",
                            color = ElectricLime,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // History
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "RECENT WORKOUT SESSIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricLime,
                letterSpacing = 1.sp
            )
        }

        items(history) { hist ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GraphiteCard),
                border = BorderStroke(1.dp, GraphiteBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = hist.routineTitle, color = TextIvory, fontWeight = FontWeight.Bold)
                        Text(text = hist.date, color = TextMuted, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${hist.completedExercisesCount} Exercises • ${hist.durationMinutes} min", color = TextMuted, fontSize = 12.sp)
                        Text(text = "${hist.totalVolumeKg.toInt()} kg volume", color = StatusOperational, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
