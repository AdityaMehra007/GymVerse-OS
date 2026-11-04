package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GymRepository
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ActiveWorkoutState(
    val routine: WorkoutRoutine? = null,
    val isRunning: Boolean = false,
    val elapsedSeconds: Int = 0,
    val restTimerRemainingSeconds: Int = 0,
    val isRestTimerActive: Boolean = false,
    val completedSetsCount: Int = 0,
    val totalVolumeKg: Double = 0.0
)

class GymVerseViewModel(
    val repository: GymRepository = GymRepository()
) : ViewModel() {

    // Global Operating Mode
    private val _currentMode = MutableStateFlow(AppMode.MEMBER)
    val currentMode: StateFlow<AppMode> = _currentMode.asStateFlow()

    // Member Navigation Tab
    private val _currentMemberTab = MutableStateFlow(MemberTab.DASHBOARD)
    val currentMemberTab: StateFlow<MemberTab> = _currentMemberTab.asStateFlow()

    // Equipment Filters & Selection
    private val _selectedCategory = MutableStateFlow(EquipmentCategory.ALL)
    val selectedCategory: StateFlow<EquipmentCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedEquipment = MutableStateFlow<EquipmentAsset?>(null)
    val selectedEquipment: StateFlow<EquipmentAsset?> = _selectedEquipment.asStateFlow()

    private val _showQrScanner = MutableStateFlow(false)
    val showQrScanner: StateFlow<Boolean> = _showQrScanner.asStateFlow()

    private val _showReportFaultDialog = MutableStateFlow<EquipmentAsset?>(null)
    val showReportFaultDialog: StateFlow<EquipmentAsset?> = _showReportFaultDialog.asStateFlow()

    // Active Workout Tracker State
    private val _activeWorkout = MutableStateFlow(ActiveWorkoutState())
    val activeWorkout: StateFlow<ActiveWorkoutState> = _activeWorkout.asStateFlow()

    private var workoutTimerJob: Job? = null
    private var restTimerJob: Job? = null

    // Chat Input State
    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput.asStateFlow()

    // Status snackbar / feedback
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    fun setMode(mode: AppMode) {
        _currentMode.value = mode
    }

    fun setMemberTab(tab: MemberTab) {
        _currentMemberTab.value = tab
    }

    fun setCategory(category: EquipmentCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectEquipment(asset: EquipmentAsset?) {
        _selectedEquipment.value = asset
    }

    fun openQrScanner(show: Boolean) {
        _showQrScanner.value = show
    }

    fun openReportFault(asset: EquipmentAsset?) {
        _showReportFaultDialog.value = asset
    }

    fun setChatInput(text: String) {
        _chatInput.value = text
    }

    fun dismissFeedback() {
        _feedbackMessage.value = null
    }

    fun showFeedback(msg: String) {
        _feedbackMessage.value = msg
        viewModelScope.launch {
            delay(3000)
            if (_feedbackMessage.value == msg) {
                _feedbackMessage.value = null
            }
        }
    }

    // Equipment Actions
    fun toggleFavorite(assetId: String) {
        repository.toggleEquipmentFavorite(assetId)
        // If current modal is open for this asset, update selection
        _selectedEquipment.value = repository.equipmentList.value.find { it.id == assetId }
    }

    fun submitFaultReport(assetId: String, desc: String, severity: String) {
        repository.reportEquipmentFault(assetId, desc, severity)
        _showReportFaultDialog.value = null
        showFeedback("Fault reported. Safety team dispatched to inspect asset.")
        _selectedEquipment.value = repository.equipmentList.value.find { it.id == assetId }
    }

    fun updateAssetStatusStaff(assetId: String, newStatus: EquipmentStatus, notes: String?) {
        repository.updateEquipmentStatus(assetId, newStatus, notes)
        showFeedback("Status updated to ${newStatus.displayName}")
    }

    // Workout Routine Actions
    fun startRoutine(routine: WorkoutRoutine) {
        // Initialize sets for exercises
        val initializedRoutine = routine.copy(
            exercises = routine.exercises.map { ex ->
                val setsList = (1..ex.targetSets).map { num ->
                    WorkoutLogSet(setNumber = num, weightKg = 0.0, reps = 10, isCompleted = false)
                }.toMutableList()
                ex.copy(sets = setsList)
            }
        )

        workoutTimerJob?.cancel()
        _activeWorkout.value = ActiveWorkoutState(
            routine = initializedRoutine,
            isRunning = true,
            elapsedSeconds = 0
        )

        workoutTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _activeWorkout.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
        _currentMemberTab.value = MemberTab.WORKOUTS
        showFeedback("Started session: ${routine.title}")
    }

    fun toggleSetCompleted(exerciseIndex: Int, setIndex: Int, weight: Double, reps: Int) {
        val currentRoutine = _activeWorkout.value.routine ?: return
        val exercises = currentRoutine.exercises.toMutableList()
        val ex = exercises[exerciseIndex]
        val setList = ex.sets.toMutableList()

        val set = setList[setIndex]
        val wasDone = set.isCompleted
        setList[setIndex] = set.copy(
            isCompleted = !wasDone,
            weightKg = weight,
            reps = reps
        )
        exercises[exerciseIndex] = ex.copy(sets = setList)

        var completedCount = 0
        var totalVol = 0.0
        exercises.forEach { e ->
            e.sets.forEach { s ->
                if (s.isCompleted) {
                    completedCount++
                    totalVol += s.weightKg * s.reps
                }
            }
        }

        _activeWorkout.update {
            it.copy(
                routine = currentRoutine.copy(exercises = exercises),
                completedSetsCount = completedCount,
                totalVolumeKg = totalVol
            )
        }

        if (!wasDone) {
            startRestTimer(60) // Standard 60 second rest
        }
    }

    fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _activeWorkout.update {
            it.copy(restTimerRemainingSeconds = seconds, isRestTimerActive = true)
        }
        restTimerJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _activeWorkout.update { it.copy(restTimerRemainingSeconds = remaining) }
            }
            _activeWorkout.update { it.copy(isRestTimerActive = false) }
        }
    }

    fun cancelRestTimer() {
        restTimerJob?.cancel()
        _activeWorkout.update { it.copy(isRestTimerActive = false, restTimerRemainingSeconds = 0) }
    }

    fun finishWorkout() {
        val current = _activeWorkout.value
        val routine = current.routine
        if (routine != null) {
            val mins = (current.elapsedSeconds / 60).coerceAtLeast(1)
            repository.logCompletedWorkout(
                routineTitle = routine.title,
                durationMinutes = mins,
                volumeKg = current.totalVolumeKg,
                count = current.completedSetsCount
            )
            showFeedback("Workout completed! Logged ${current.totalVolumeKg.toInt()} kg total volume.")
        }
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
        _activeWorkout.value = ActiveWorkoutState()
    }

    // Class Booking
    fun toggleClassBooking(classId: String) {
        repository.toggleClassBooking(classId)
        val cls = repository.groupClasses.value.find { it.id == classId }
        val msg = if (cls?.isBookedByMe == true) "Reserved spot in ${cls.title}" else "Cancelled reservation"
        showFeedback(msg)
    }

    // Chat AI
    fun sendUserChatMessage(prompt: String? = null) {
        val text = prompt ?: _chatInput.value.trim()
        if (text.isNotBlank()) {
            _chatInput.value = ""
            repository.sendChatMessage(text)
        }
    }
}
