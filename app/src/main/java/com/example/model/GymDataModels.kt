package com.example.model

enum class AppMode(val label: String) {
    MEMBER("Member OS"),
    CONTROL_CENTER("Staff & Owner Control"),
    INTELLIGENCE_IQ("GymVerse IQ")
}

enum class MemberTab(val label: String) {
    DASHBOARD("Gym Live"),
    EQUIPMENT_MAP("Floor & Machines"),
    WORKOUTS("Workout"),
    CLASSES_TRAINERS("Classes & Pros"),
    COMMUNITY("Community"),
    MEMBER_HUB("Pass & Hub")
}

enum class EquipmentStatus(val displayName: String) {
    OPERATIONAL("Operational"),
    IN_USE("In Use"),
    MAINTENANCE_SCHEDULED("Maintenance Scheduled"),
    UNDER_INSPECTION("Under Inspection"),
    FAULT_REPORTED("Reported Fault"),
    OUT_OF_SERVICE("Out of Service")
}

enum class EquipmentCategory(val displayName: String) {
    ALL("All Equipment"),
    STRENGTH_MACHINES("Strength Machines"),
    FREE_WEIGHTS("Free Weights"),
    CARDIO("Cardio"),
    FUNCTIONAL("Functional & Turf"),
    MOBILITY("Mobility & Recovery")
}

data class GymInfo(
    val name: String,
    val branch: String,
    val address: String,
    val city: String,
    val phone: String,
    val emergencyPhone: String,
    val openingHours: String,
    val isOpenNow: Boolean,
    val maxCapacity: Int
)

data class CrowdStatus(
    val occupancyPercentage: Int,
    val label: String,
    val activeMembersCount: Int,
    val capacityLimit: Int,
    val peakPeriod: String,
    val hourlyOccupancy: List<Pair<String, Int>> // e.g. "6 AM" to 15%
)

data class EquipmentAsset(
    val id: String,
    val name: String,
    val category: EquipmentCategory,
    val zone: String,
    val status: EquipmentStatus,
    val muscleGroups: List<String>,
    val instructions: String,
    val setupGuide: String,
    val safetyNotes: String,
    val alternatives: List<String>,
    val qrCode: String,
    val lastInspected: String,
    val manufacturer: String,
    val isFavorite: Boolean = false
)

data class WorkoutLogSet(
    val setNumber: Int,
    var weightKg: Double,
    var reps: Int,
    var isCompleted: Boolean = false
)

data class WorkoutExercise(
    val id: String,
    val name: String,
    val targetSets: Int,
    val targetReps: String,
    val equipmentId: String?,
    val alternatives: List<String>,
    val sets: MutableList<WorkoutLogSet> = mutableListOf()
)

data class WorkoutRoutine(
    val id: String,
    val title: String,
    val description: String,
    val level: String,
    val durationMinutes: Int,
    val exercises: List<WorkoutExercise>,
    val category: String
)

data class WorkoutHistoryRecord(
    val id: String,
    val routineTitle: String,
    val date: String,
    val totalVolumeKg: Double,
    val durationMinutes: Int,
    val completedExercisesCount: Int
)

data class PersonalRecord(
    val exerciseName: String,
    val weightKg: Double,
    val reps: Int,
    val date: String
)

data class GroupClass(
    val id: String,
    val title: String,
    val instructor: String,
    val time: String,
    val durationMinutes: Int,
    val studio: String,
    val capacity: Int,
    val bookedCount: Int,
    val isBookedByMe: Boolean,
    val intensity: String,
    val category: String,
    val description: String
)

data class TrainerProfile(
    val id: String,
    val name: String,
    val specialty: String,
    val credentials: String,
    val experience: String,
    val languages: String,
    val hourlyRate: String,
    val rating: Double,
    val reviewCount: Int,
    val availabilitySlots: List<String>,
    val bio: String
)

data class CommunityPost(
    val id: String,
    val authorName: String,
    val authorRole: String,
    val avatarInitials: String,
    val content: String,
    val timeAgo: String,
    val tag: String,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val commentsCount: Int
)

data class WorkoutPartnerRequest(
    val id: String,
    val memberName: String,
    val trainingGoal: String,
    val timeSlot: String,
    val preferredZone: String,
    val level: String,
    val hasResponded: Boolean = false
)

data class MaintenanceTicket(
    val id: String,
    val assetId: String,
    val assetName: String,
    val reportedBy: String,
    val category: String,
    val severity: String,
    val status: String,
    val reportedTime: String,
    val notes: String,
    val assignedTech: String
)

data class CleaningTaskItem(
    val id: String,
    val area: String,
    val taskName: String,
    val interval: String,
    val isDone: Boolean,
    val dueTime: String,
    val completedBy: String? = null
)

data class InventoryItem(
    val id: String,
    val name: String,
    val category: String,
    val currentQty: Int,
    val minThreshold: Int,
    val unit: String,
    val status: String
)

data class HelpDeskTicket(
    val id: String,
    val category: String,
    val subject: String,
    val details: String,
    val status: String,
    val priority: String,
    val date: String
)

data class MemberProfile(
    val memberId: String,
    val name: String,
    val email: String,
    val tier: String,
    val validThrough: String,
    val checkInsThisMonth: Int,
    val streakDays: Int,
    val digitalPassQr: String,
    val totalWorkoutsLogged: Int
)

data class ChatMessage(
    val id: String,
    val sender: String,
    val text: String,
    val timestamp: String,
    val isAi: Boolean,
    val actionSuggestions: List<String> = emptyList()
)
