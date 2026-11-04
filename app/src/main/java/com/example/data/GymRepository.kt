package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

class GymRepository {

    private val _gymInfo = MutableStateFlow(
        GymInfo(
            name = "GymVerse Velocity & Iron Club",
            branch = "Indiranagar Flagship, 100 Feet Rd",
            address = "42 HAL 2nd Stage, Indiranagar, Bengaluru, Karnataka 560038",
            city = "Bengaluru",
            phone = "+91 80 4920 1800",
            emergencyPhone = "+91 80 4920 1899",
            openingHours = "05:30 AM – 11:00 PM (Daily)",
            isOpenNow = true,
            maxCapacity = 120
        )
    )
    val gymInfo: StateFlow<GymInfo> = _gymInfo.asStateFlow()

    private val _crowdStatus = MutableStateFlow(
        CrowdStatus(
            occupancyPercentage = 44,
            label = "Moderate Crowd",
            activeMembersCount = 53,
            capacityLimit = 120,
            peakPeriod = "06:00 PM – 08:30 PM",
            hourlyOccupancy = listOf(
                "6 AM" to 22,
                "8 AM" to 65,
                "10 AM" to 40,
                "12 PM" to 30,
                "2 PM" to 25,
                "4 PM" to 42,
                "6 PM" to 82,
                "8 PM" to 78,
                "10 PM" to 35
            )
        )
    )
    val crowdStatus: StateFlow<CrowdStatus> = _crowdStatus.asStateFlow()

    private val _equipmentList = MutableStateFlow<List<EquipmentAsset>>(initialEquipment())
    val equipmentList: StateFlow<List<EquipmentAsset>> = _equipmentList.asStateFlow()

    private val _groupClasses = MutableStateFlow<List<GroupClass>>(initialClasses())
    val groupClasses: StateFlow<List<GroupClass>> = _groupClasses.asStateFlow()

    private val _trainers = MutableStateFlow<List<TrainerProfile>>(initialTrainers())
    val trainers: StateFlow<List<TrainerProfile>> = _trainers.asStateFlow()

    private val _workoutRoutines = MutableStateFlow<List<WorkoutRoutine>>(initialRoutines())
    val workoutRoutines: StateFlow<List<WorkoutRoutine>> = _workoutRoutines.asStateFlow()

    private val _workoutHistory = MutableStateFlow<List<WorkoutHistoryRecord>>(initialHistory())
    val workoutHistory: StateFlow<List<WorkoutHistoryRecord>> = _workoutHistory.asStateFlow()

    private val _personalRecords = MutableStateFlow<List<PersonalRecord>>(initialPRs())
    val personalRecords: StateFlow<List<PersonalRecord>> = _personalRecords.asStateFlow()

    private val _communityPosts = MutableStateFlow<List<CommunityPost>>(initialPosts())
    val communityPosts: StateFlow<List<CommunityPost>> = _communityPosts.asStateFlow()

    private val _partnerRequests = MutableStateFlow<List<WorkoutPartnerRequest>>(initialPartners())
    val partnerRequests: StateFlow<List<WorkoutPartnerRequest>> = _partnerRequests.asStateFlow()

    private val _maintenanceTickets = MutableStateFlow<List<MaintenanceTicket>>(initialMaintenance())
    val maintenanceTickets: StateFlow<List<MaintenanceTicket>> = _maintenanceTickets.asStateFlow()

    private val _cleaningTasks = MutableStateFlow<List<CleaningTaskItem>>(initialCleaning())
    val cleaningTasks: StateFlow<List<CleaningTaskItem>> = _cleaningTasks.asStateFlow()

    private val _inventory = MutableStateFlow<List<InventoryItem>>(initialInventory())
    val inventory: StateFlow<List<InventoryItem>> = _inventory.asStateFlow()

    private val _helpDeskTickets = MutableStateFlow<List<HelpDeskTicket>>(initialHelpDesk())
    val helpDeskTickets: StateFlow<List<HelpDeskTicket>> = _helpDeskTickets.asStateFlow()

    private val _memberProfile = MutableStateFlow(
        MemberProfile(
            memberId = "GV-88294",
            name = "Rohan Varma",
            email = "rohan.varma@gymverse.io",
            tier = "GymVerse Black Elite",
            validThrough = "14 Nov 2027",
            checkInsThisMonth = 19,
            streakDays = 5,
            digitalPassQr = "GV-PASS-BLR-88294-AUTHENTICATED",
            totalWorkoutsLogged = 84
        )
    )
    val memberProfile: StateFlow<MemberProfile> = _memberProfile.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(initialChat())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Actions
    fun updateEquipmentStatus(assetId: String, newStatus: EquipmentStatus, techNotes: String? = null) {
        _equipmentList.update { list ->
            list.map { eq ->
                if (eq.id == assetId) {
                    eq.copy(status = newStatus, lastInspected = "Today by Staff")
                } else eq
            }
        }
        if (techNotes != null) {
            val asset = _equipmentList.value.find { it.id == assetId }
            addMaintenanceTicket(
                assetId = assetId,
                assetName = asset?.name ?: assetId,
                category = "Status Update",
                severity = if (newStatus == EquipmentStatus.OUT_OF_SERVICE) "High" else "Medium",
                notes = techNotes,
                reportedBy = "Staff Override"
            )
        }
    }

    fun reportEquipmentFault(assetId: String, faultDescription: String, severity: String) {
        _equipmentList.update { list ->
            list.map { eq ->
                if (eq.id == assetId) {
                    eq.copy(status = EquipmentStatus.FAULT_REPORTED)
                } else eq
            }
        }
        val asset = _equipmentList.value.find { it.id == assetId }
        addMaintenanceTicket(
            assetId = assetId,
            assetName = asset?.name ?: assetId,
            category = "Member Fault Report",
            severity = severity,
            notes = faultDescription,
            reportedBy = _memberProfile.value.name
        )
    }

    fun toggleClassBooking(classId: String) {
        _groupClasses.update { list ->
            list.map { gc ->
                if (gc.id == classId) {
                    val wasBooked = gc.isBookedByMe
                    val newCount = if (wasBooked) (gc.bookedCount - 1).coerceAtLeast(0) else gc.bookedCount + 1
                    gc.copy(isBookedByMe = !wasBooked, bookedCount = newCount)
                } else gc
            }
        }
    }

    fun toggleEquipmentFavorite(assetId: String) {
        _equipmentList.update { list ->
            list.map { eq ->
                if (eq.id == assetId) eq.copy(isFavorite = !eq.isFavorite) else eq
            }
        }
    }

    fun addMaintenanceTicket(
        assetId: String,
        assetName: String,
        category: String,
        severity: String,
        notes: String,
        reportedBy: String
    ) {
        val newId = "MNT-${Random().nextInt(9000) + 1000}"
        val ticket = MaintenanceTicket(
            id = newId,
            assetId = assetId,
            assetName = assetName,
            reportedBy = reportedBy,
            category = category,
            severity = severity,
            status = "Assigned",
            reportedTime = "Just now",
            notes = notes,
            assignedTech = "Kishore (Biomech Tech)"
        )
        _maintenanceTickets.update { listOf(ticket) + it }
    }

    fun resolveMaintenanceTicket(ticketId: String, resolutionNotes: String) {
        _maintenanceTickets.update { list ->
            list.map { ticket ->
                if (ticket.id == ticketId) {
                    ticket.copy(status = "Resolved", notes = "${ticket.notes} | Resolution: $resolutionNotes")
                } else ticket
            }
        }
    }

    fun toggleCleaningTask(taskId: String) {
        _cleaningTasks.update { list ->
            list.map { task ->
                if (task.id == taskId) {
                    val nextDone = !task.isDone
                    task.copy(
                        isDone = nextDone,
                        completedBy = if (nextDone) "Staff Verified" else null
                    )
                } else task
            }
        }
    }

    fun restockInventory(itemId: String, addQty: Int) {
        _inventory.update { list ->
            list.map { item ->
                if (item.id == itemId) {
                    val newQty = item.currentQty + addQty
                    val newStatus = if (newQty >= item.minThreshold) "Optimal Stock" else "Low Stock Alert"
                    item.copy(currentQty = newQty, status = newStatus)
                } else item
            }
        }
    }

    fun submitHelpDeskTicket(category: String, subject: String, details: String, priority: String) {
        val newId = "TKT-${Random().nextInt(8000) + 1000}"
        val tkt = HelpDeskTicket(
            id = newId,
            category = category,
            subject = subject,
            details = details,
            status = "Open",
            priority = priority,
            date = "Today"
        )
        _helpDeskTickets.update { listOf(tkt) + it }
    }

    fun logCompletedWorkout(routineTitle: String, durationMinutes: Int, volumeKg: Double, count: Int) {
        val record = WorkoutHistoryRecord(
            id = "WKT-${System.currentTimeMillis()}",
            routineTitle = routineTitle,
            date = "Today",
            totalVolumeKg = volumeKg,
            durationMinutes = durationMinutes,
            completedExercisesCount = count
        )
        _workoutHistory.update { listOf(record) + it }
        _memberProfile.update { it.copy(totalWorkoutsLogged = it.totalWorkoutsLogged + 1) }
    }

    fun postCommunityMessage(content: String, tag: String) {
        val post = CommunityPost(
            id = "POST-${System.currentTimeMillis()}",
            authorName = _memberProfile.value.name,
            authorRole = "Gym Member",
            avatarInitials = "RV",
            content = content,
            timeAgo = "Just now",
            tag = tag,
            likesCount = 0,
            commentsCount = 0
        )
        _communityPosts.update { listOf(post) + it }
    }

    fun sendChatMessage(query: String) {
        val userMsg = ChatMessage(
            id = "MSG-${System.currentTimeMillis()}",
            sender = "You",
            text = query,
            timestamp = "Just now",
            isAi = false
        )
        _chatMessages.update { it + userMsg }

        // Process with GymVerse IQ knowledge engine
        val aiReply = generateAiResponse(query)
        _chatMessages.update { it + aiReply }
    }

    private fun generateAiResponse(query: String): ChatMessage {
        val lower = query.lowercase()
        val text: String
        val suggestions: MutableList<String> = mutableListOf()

        when {
            lower.contains("hack squat") || (lower.contains("squat") && lower.contains("available")) -> {
                val hackSquat = _equipmentList.value.find { it.id == "EQ-LEG-03" }
                val status = hackSquat?.status?.displayName ?: "Operational"
                text = "Arsenal Strength Hack Squat (ID: EQ-LEG-03) in the Lower Body Deck is currently **$status**.\n\n" +
                        "If you need alternatives for quad development, our **Hammer Strength Leg Press** (EQ-LEG-01) and **Eleiko Power Rack 01** (EQ-RACK-01) are both Operational."
                suggestions.add("View Hack Squat Specs")
                suggestions.add("Navigate to Lower Body Deck")
            }
            lower.contains("busy") || lower.contains("crowd") || lower.contains("time") -> {
                val crowd = _crowdStatus.value
                text = "The gym is currently at **${crowd.occupancyPercentage}% capacity (${crowd.activeMembersCount} members)** — categorized as **${crowd.label}**.\n\n" +
                        "Peak rush occurs between **${crowd.peakPeriod}**. The best quiet windows today are 1:30 PM – 4:00 PM and after 9:30 PM."
                suggestions.add("View Full Crowd Graph")
                suggestions.add("Check Cardio Availability")
            }
            lower.contains("cable") || lower.contains("chest") -> {
                text = "Our Cybex Cable Crossover (EQ-CAB-01) is operational in the Upper Body & Cables zone.\n\n" +
                        "Recommended chest movements right now: Incline Dumbbell Press (Zone C has 20kg–38kg pairs free), Cable Flyes, and Hammer Strength Flat Chest Press."
                suggestions.add("Find Chest Machines")
                suggestions.add("Start Push Workout")
            }
            lower.contains("class") || lower.contains("spin") || lower.contains("hiit") -> {
                val nextClass = _groupClasses.value.firstOrNull { !it.isBookedByMe }
                text = if (nextClass != null) {
                    "Next available class: **${nextClass.title}** with ${nextClass.instructor} at **${nextClass.time}** (${nextClass.capacity - nextClass.bookedCount} spots left in ${nextClass.studio})."
                } else {
                    "All upcoming group sessions are currently at capacity. Would you like to join the waitlist for Velocity Spin?"
                }
                suggestions.add("View All Classes")
                suggestions.add("Book Next Class")
            }
            lower.contains("maintenance") || lower.contains("safety") || lower.contains("fault") -> {
                val openIssues = _maintenanceTickets.value.filter { it.status != "Resolved" }
                text = "GymVerse Safety Log: There are **${openIssues.size} open work orders**. " +
                        (openIssues.firstOrNull()?.let { "Notice: ${it.assetName} (${it.assetId}) has '${it.notes}' reported." } ?: "All critical systems normal.")
                suggestions.add("Report New Issue")
                suggestions.add("View Maintenance Board")
            }
            lower.contains("trainer") || lower.contains("coach") -> {
                val t = _trainers.value.first()
                text = "Our certified training team includes **${t.name}** (${t.specialty}, CSCS), Priya Nair (Olympic Lifting), and Devansh Patel (Metabolic Conditioning). Next available slot: ${t.availabilitySlots.firstOrNull()}."
                suggestions.add("Browse Trainers")
                suggestions.add("Request Consultation")
            }
            else -> {
                text = "GymVerse IQ: I have access to real-time equipment statuses (46/50 machines operational), current crowd levels (44% occupancy), class rosters, and exercise biomechanics.\n\n" +
                        "You can ask about machine availability, exercise substitutions, optimal training times, or report equipment concerns."
                suggestions.add("Which machines are free?")
                suggestions.add("Best time to train today?")
                suggestions.add("Alternative to Lat Pulldown?")
            }
        }

        return ChatMessage(
            id = "AI-${System.currentTimeMillis()}",
            sender = "GymVerse IQ",
            text = text,
            timestamp = "Just now",
            isAi = true,
            actionSuggestions = suggestions
        )
    }

    companion object {
        private fun initialEquipment(): List<EquipmentAsset> = listOf(
            EquipmentAsset(
                id = "EQ-LEG-01",
                name = "Hammer Strength ISO Leg Press",
                category = EquipmentCategory.STRENGTH_MACHINES,
                zone = "Lower Body Deck",
                status = EquipmentStatus.OPERATIONAL,
                muscleGroups = listOf("Quadriceps", "Glutes", "Hamstrings"),
                instructions = "Sit back firmly against the angled backrest. Position feet shoulder-width apart on the plate. Disengage the safety pins and lower smoothly to 90 degrees knee flexion, then press upward without locking out knees.",
                setupGuide = "Adjust backrest angle to 45°. Keep lower back flat against pad at all times.",
                safetyNotes = "Never lock knees at full extension. Keep safety release levers in grasp during heavy loads.",
                alternatives = listOf("Arsenal Hack Squat (EQ-LEG-03)", "Barbell Squat (EQ-RACK-01)", "Dumbbell Goblet Squat"),
                qrCode = "QR-EQ-LEG-01",
                lastInspected = "Yesterday by MechTech",
                manufacturer = "Hammer Strength USA",
                isFavorite = true
            ),
            EquipmentAsset(
                id = "EQ-LEG-02",
                name = "Precor Prone Leg Curl",
                category = EquipmentCategory.STRENGTH_MACHINES,
                zone = "Lower Body Deck",
                status = EquipmentStatus.OPERATIONAL,
                muscleGroups = listOf("Hamstrings", "Calves"),
                instructions = "Lie face down with roller pad positioned just below calf muscles. Grasp handles and curl heels toward glutes with controlled tempo.",
                setupGuide = "Align knee axis with red rotational pivot indicator.",
                safetyNotes = "Avoid lifting hips off the bench pad during the peak contraction.",
                alternatives = listOf("Seated Leg Curl", "Romanian Deadlift with Dumbbells", "Cable Pull-Through"),
                qrCode = "QR-EQ-LEG-02",
                lastInspected = "2 days ago",
                manufacturer = "Precor Commercial"
            ),
            EquipmentAsset(
                id = "EQ-LEG-03",
                name = "Arsenal Strength Alpha Hack Squat",
                category = EquipmentCategory.STRENGTH_MACHINES,
                zone = "Lower Body Deck",
                status = EquipmentStatus.OPERATIONAL,
                muscleGroups = listOf("Quadriceps", "Glutes"),
                instructions = "Shoulders under pads, back pressed flat against backboard. Disengage carriage safety lever, descend until thighs hit parallel with platform.",
                setupGuide = "Dual safety stops adjustable at 4 heights. Set bottom stop at deepest comfortable depth.",
                safetyNotes = "Engage safety catches before dismounting machine.",
                alternatives = listOf("Hammer Strength Leg Press (EQ-LEG-01)", "Smith Machine Squat", "Bulgarian Split Squat"),
                qrCode = "QR-EQ-LEG-03",
                lastInspected = "Today at 07:00 AM",
                manufacturer = "Arsenal Strength"
            ),
            EquipmentAsset(
                id = "EQ-CAB-01",
                name = "Cybex Dual Cable Crossover Station",
                category = EquipmentCategory.STRENGTH_MACHINES,
                zone = "Upper Body & Cables",
                status = EquipmentStatus.OPERATIONAL,
                muscleGroups = listOf("Chest", "Back", "Shoulders", "Triceps"),
                instructions = "Adjust pulleys to desired height. Attach appropriate stirrup or rope accessories. Maintain core bracing through full range of motion.",
                setupGuide = "Numbered pin adjustments from 1 to 24 along each column.",
                safetyNotes = "Ensure pulley selector pin is securely clicked into weight stack.",
                alternatives = listOf("Dumbbell Flyes", "Incline Cable Bench", "Resistance Band Crossovers"),
                qrCode = "QR-EQ-CAB-01",
                lastInspected = "3 days ago",
                manufacturer = "Cybex International",
                isFavorite = true
            ),
            EquipmentAsset(
                id = "EQ-CAB-02",
                name = "Life Fitness Lat Pulldown & Low Row",
                category = EquipmentCategory.STRENGTH_MACHINES,
                zone = "Upper Body & Cables",
                status = EquipmentStatus.IN_USE,
                muscleGroups = listOf("Latissimus Dorsi", "Biceps", "Rhomboids"),
                instructions = "Adjust thigh pads so knees are locked snugly. Grip wide bar, lean slightly back (10-15°), drive elbows down to chest level.",
                setupGuide = "Thigh pad height adjustment with quick pull pin.",
                safetyNotes = "Do not pull behind the neck to avoid shoulder impingement.",
                alternatives = listOf("Assisted Pull-Up Machine", "Barbell Bent-Over Row", "Single Arm Dumbbell Row"),
                qrCode = "QR-EQ-CAB-02",
                lastInspected = "Yesterday",
                manufacturer = "Life Fitness Signature"
            ),
            EquipmentAsset(
                id = "EQ-CAB-03",
                name = "Atlantis Standing Lateral Raise Machine",
                category = EquipmentCategory.STRENGTH_MACHINES,
                zone = "Upper Body & Cables",
                status = EquipmentStatus.UNDER_INSPECTION,
                muscleGroups = listOf("Lateral Deltoids"),
                instructions = "Rest forearms against contoured pads, keep chest against pad and raise arms until upper arms are parallel to floor.",
                setupGuide = "Chest pad depth adjustable for torso length.",
                safetyNotes = "Cable undergoing routine tension verification.",
                alternatives = listOf("Dumbbell Lateral Raise", "Single Cable Lateral Raise (EQ-CAB-01)"),
                qrCode = "QR-EQ-CAB-03",
                lastInspected = "This morning (Inspection pending)",
                manufacturer = "Atlantis Strength"
            ),
            EquipmentAsset(
                id = "EQ-RACK-01",
                name = "Eleiko IPF Power Rack 01",
                category = EquipmentCategory.FREE_WEIGHTS,
                zone = "Free Weights & Racks",
                status = EquipmentStatus.OPERATIONAL,
                muscleGroups = listOf("Full Body", "Quadriceps", "Posterior Chain", "Core"),
                instructions = "Precision numbered holes. Use magnetic J-cups and steel safety flip-down pins. Perfect for Squats, Overhead Press, and Rack Pulls.",
                setupGuide = "Safety bar height must be set 2 inches below bottom of movement.",
                safetyNotes = "Always clamp weight plates with certified collars.",
                alternatives = listOf("Eleiko Power Rack 02", "Smith Machine", "Leg Press"),
                qrCode = "QR-EQ-RACK-01",
                lastInspected = "Today 06:00 AM",
                manufacturer = "Eleiko Sweden",
                isFavorite = true
            ),
            EquipmentAsset(
                id = "EQ-RACK-02",
                name = "Eleiko IPF Power Rack 02 & Deadlift Deck",
                category = EquipmentCategory.FREE_WEIGHTS,
                zone = "Free Weights & Racks",
                status = EquipmentStatus.IN_USE,
                muscleGroups = listOf("Glutes", "Hamstrings", "Back", "Traps"),
                instructions = "Full rubber & oak wood dampening platform. Supports heavy barbell work with noise and vibration dampening.",
                setupGuide = "Chalk bowl available adjacent to platform.",
                safetyNotes = "Check platform surface for chalk buildup before heavy lifts.",
                alternatives = listOf("Trap Bar Deadlift", "Romanian Deadlift at Dumbbell Zone"),
                qrCode = "QR-EQ-RACK-02",
                lastInspected = "Yesterday",
                manufacturer = "Eleiko Sweden"
            ),
            EquipmentAsset(
                id = "EQ-DB-01",
                name = "Watson Heavy Dumbbell Suite (2kg – 60kg)",
                category = EquipmentCategory.FREE_WEIGHTS,
                zone = "Free Weights & Racks",
                status = EquipmentStatus.OPERATIONAL,
                muscleGroups = listOf("Upper Body", "Chest", "Arms", "Shoulders"),
                instructions = "Revolving thick stainless handles. Always re-rack pairs in designated numerical slots.",
                setupGuide = "5 commercial adjustable incline/flat benches situated in front of mirror.",
                safetyNotes = "Do not drop dumbbells onto rubber floor; lower to thighs or floor with control.",
                alternatives = listOf("Fixed Barbell Suite", "Cable Crossover (EQ-CAB-01)"),
                qrCode = "QR-EQ-DB-01",
                lastInspected = "Daily re-rack audit completed",
                manufacturer = "Watson Gym Equipment UK"
            ),
            EquipmentAsset(
                id = "EQ-CAR-01",
                name = "Woodway 4Front Slat-Belt Treadmill 01",
                category = EquipmentCategory.CARDIO,
                zone = "Cardio Loft",
                status = EquipmentStatus.OPERATIONAL,
                muscleGroups = listOf("Cardiovascular", "Calves", "Quads"),
                instructions = "Dynamic slat belt technology absorbs 90% of joint impact. Touch console to select Hill, Interval, or Dynamic Mode.",
                setupGuide = "Clip red emergency safety lanyard to waistband before initiating belt.",
                safetyNotes = "Emergency stop cord must be attached.",
                alternatives = listOf("Woodway 4Front 02", "Concept2 RowErg", "Assault AirBike"),
                qrCode = "QR-EQ-CAR-01",
                lastInspected = "Yesterday",
                manufacturer = "Woodway USA"
            ),
            EquipmentAsset(
                id = "EQ-CAR-02",
                name = "Concept2 RowErg (PM5 Console)",
                category = EquipmentCategory.CARDIO,
                zone = "Cardio Loft",
                status = EquipmentStatus.OPERATIONAL,
                muscleGroups = listOf("Cardio", "Back", "Legs", "Arms"),
                instructions = "Legs drive first, then body leans back, then arms pull handle to sternum. Reverse order on return recovery.",
                setupGuide = "Adjust damper lever between 3 and 5 for optimal drag factor (120-130).",
                safetyNotes = "Keep back straight during catch position.",
                alternatives = listOf("Concept2 SkiErg", "Assault AirBike (EQ-CAR-03)"),
                qrCode = "QR-EQ-CAR-02",
                lastInspected = "3 days ago",
                manufacturer = "Concept2 USA"
            ),
            EquipmentAsset(
                id = "EQ-CAR-03",
                name = "Assault Fitness AirBike Elite",
                category = EquipmentCategory.CARDIO,
                zone = "Cardio Loft",
                status = EquipmentStatus.MAINTENANCE_SCHEDULED,
                muscleGroups = listOf("Full Body Conditioning", "Cardio"),
                instructions = "High intensity air resistance increases exponentially with effort. Dual action handles.",
                setupGuide = "Seat height and fore/aft adjustment with indexed pop-pins.",
                safetyNotes = "Drive belt undergoing scheduled 500-hour tensioning service.",
                alternatives = listOf("Concept2 RowErg (EQ-CAR-02)", "Woodway Treadmill (EQ-CAR-01)"),
                qrCode = "QR-EQ-CAR-03",
                lastInspected = "Scheduled maintenance active",
                manufacturer = "Assault Fitness"
            ),
            EquipmentAsset(
                id = "EQ-FUNC-01",
                name = "Custom 25m Turf & Dog Sled Track",
                category = EquipmentCategory.FUNCTIONAL,
                zone = "Functional & Turf",
                status = EquipmentStatus.OPERATIONAL,
                muscleGroups = listOf("Legs", "Core", "Cardiovascular Power"),
                instructions = "High-friction sprint turf with distance markings every 5 meters. Load Olympic plates onto center peg.",
                setupGuide = "Push handles or pull strap harness available in equipment chest.",
                safetyNotes = "Clear lane before sprint pushes.",
                alternatives = listOf("Farmers Carry Barbell Track", "Battle Ropes"),
                qrCode = "QR-EQ-FUNC-01",
                lastInspected = "Today",
                manufacturer = "GymVerse Custom Facilities"
            ),
            EquipmentAsset(
                id = "EQ-REC-01",
                name = "Hyperice Hypervolt Pro & Mobility Deck",
                category = EquipmentCategory.MOBILITY,
                zone = "Recovery & Mobility",
                status = EquipmentStatus.OPERATIONAL,
                muscleGroups = listOf("Fascia Recovery", "Hamstrings", "Back", "Quads"),
                instructions = "High density foam rollers, yoga mats, resistance stretching bands, and percussion therapy tools.",
                setupGuide = "Sanitize percussion attachments after each use with supplied antibacterial wipes.",
                safetyNotes = "Do not apply directly onto spine or neck bones.",
                alternatives = listOf("Static Stretching Wall", "Yoga Studio Floor"),
                qrCode = "QR-EQ-REC-01",
                lastInspected = "Sanitized 1 hour ago",
                manufacturer = "Hyperice & Rogue Fitness"
            )
        )

        private fun initialClasses(): List<GroupClass> = listOf(
            GroupClass(
                id = "CLS-01",
                title = "Apex HIIT Inferno",
                instructor = "Karan Singhania (CSCS)",
                time = "Today, 06:30 PM",
                durationMinutes = 45,
                studio = "Studio A (High Impact)",
                capacity = 24,
                bookedCount = 21,
                isBookedByMe = true,
                intensity = "High (80-90% HR)",
                category = "Cardio & Conditioning",
                description = "High energy interval rounds combining sled pushes, kettlebell complexes, rowing sprints, and core burnout."
            ),
            GroupClass(
                id = "CLS-02",
                title = "Heavy Iron Barbell Club",
                instructor = "Marcus Chen",
                time = "Tomorrow, 07:00 AM",
                durationMinutes = 60,
                studio = "Strength Bay",
                capacity = 12,
                bookedCount = 10,
                isBookedByMe = false,
                intensity = "Heavy Strength",
                category = "Powerlifting & Technique",
                description = "Master your squat, bench, and deadlift mechanics with video feedback and progressive overload programming."
            ),
            GroupClass(
                id = "CLS-03",
                title = "Velocity Rhythm Spin",
                instructor = "Ananya Sharma",
                time = "Tomorrow, 08:30 AM",
                durationMinutes = 50,
                studio = "Cycling Studio",
                capacity = 28,
                bookedCount = 28,
                isBookedByMe = false,
                intensity = "Medium-High",
                category = "Spinning",
                description = "Music-driven endurance climbs, sprint intervals, and rhythm cadence riding on Stages SC3 bikes."
            ),
            GroupClass(
                id = "CLS-04",
                title = "Mobility & Spinal Decompression",
                instructor = "Priya Nair",
                time = "Tomorrow, 06:00 PM",
                durationMinutes = 45,
                studio = "Zen Recovery Loft",
                capacity = 18,
                bookedCount = 9,
                isBookedByMe = false,
                intensity = "Low (Restorative)",
                category = "Recovery",
                description = "Joint freeing drills, hip capsule openers, thoracic rotation, and guided diaphragmatic breathing."
            )
        )

        private fun initialTrainers(): List<TrainerProfile> = listOf(
            TrainerProfile(
                id = "TRN-01",
                name = "Karan Singhania",
                specialty = "Hypertrophy & Biomechanics",
                credentials = "CSCS, Precision Nutrition L2, EXOS Specialist",
                experience = "9 Years Experience",
                languages = "English, Hindi, Kannada",
                hourlyRate = "₹1,800 / hr",
                rating = 4.95,
                reviewCount = 114,
                availabilitySlots = listOf("Today 4:00 PM", "Tomorrow 10:00 AM", "Tomorrow 5:00 PM"),
                bio = "Former competitive powerlifter specializing in muscle architecture optimization, injury-free progressive overload, and body recomposition."
            ),
            TrainerProfile(
                id = "TRN-02",
                name = "Priya Nair",
                specialty = "Olympic Weightlifting & Mobility",
                credentials = "USAW Level 2, FMS Certified",
                experience = "7 Years Experience",
                languages = "English, Malayalam, Hindi",
                hourlyRate = "₹1,600 / hr",
                rating = 4.92,
                reviewCount = 88,
                availabilitySlots = listOf("Today 6:00 PM", "Tomorrow 7:30 AM", "Tomorrow 4:00 PM"),
                bio = "Dedicated to building explosive power, kinetic chain alignment, snatch and clean & jerk mastery for athletes and lifters."
            ),
            TrainerProfile(
                id = "TRN-03",
                name = "Devansh Patel",
                specialty = "Metabolic Conditioning & Functional Strength",
                credentials = "ACE Master Trainer, Crossfit Level 2",
                experience = "6 Years Experience",
                languages = "English, Gujarati, Hindi",
                hourlyRate = "₹1,500 / hr",
                rating = 4.88,
                reviewCount = 76,
                availabilitySlots = listOf("Tomorrow 9:00 AM", "Tomorrow 11:30 AM", "Tomorrow 6:30 PM"),
                bio = "Focuses on athletic longevity, work capacity, cardiovascular vo2 max enhancement, and functional kettlebell training."
            )
        )

        private fun initialRoutines(): List<WorkoutRoutine> = listOf(
            WorkoutRoutine(
                id = "RTN-PUSH",
                title = "Hypertrophy Push Day (Chest / Shoulders / Triceps)",
                description = "Targeted anterior chain building using compound barbell work, isolation cable flyes, and triceps overload.",
                level = "Intermediate",
                durationMinutes = 60,
                category = "Hypertrophy",
                exercises = listOf(
                    WorkoutExercise("EX-1", "Incline Dumbbell Bench Press", 4, "8-10 reps", "EQ-DB-01", listOf("Barbell Incline Press", "Machine Chest Press")),
                    WorkoutExercise("EX-2", "Cybex Cable Chest Flyes", 3, "12-15 reps", "EQ-CAB-01", listOf("Pec Deck", "Dumbbell Flyes")),
                    WorkoutExercise("EX-3", "Standing Dumbbell Lateral Raise", 4, "12-15 reps", "EQ-DB-01", listOf("Cable Lateral Raise (EQ-CAB-03)", "Machine Lateral Raise")),
                    WorkoutExercise("EX-4", "Triceps Cable Rope Pushdowns", 3, "10-12 reps", "EQ-CAB-01", listOf("Overhead Dumbbell Extension", "Dips"))
                )
            ),
            WorkoutRoutine(
                id = "RTN-LEGS",
                title = "Quad & Hamstring Power (Lower Body Deck)",
                description = "High volume leg day structured around heavy compound squats, hack squats, and hamstring curls.",
                level = "Advanced",
                durationMinutes = 65,
                category = "Strength & Mass",
                exercises = listOf(
                    WorkoutExercise("EX-L1", "Eleiko Barbell Back Squats", 4, "6-8 reps", "EQ-RACK-01", listOf("Hammer Leg Press (EQ-LEG-01)", "Hack Squat (EQ-LEG-03)")),
                    WorkoutExercise("EX-L2", "Arsenal Strength Hack Squat", 3, "10-12 reps", "EQ-LEG-03", listOf("Leg Press (EQ-LEG-01)", "Bulgarian Split Squats")),
                    WorkoutExercise("EX-L3", "Precor Prone Leg Curls", 4, "10-12 reps", "EQ-LEG-02", listOf("Dumbbell RDL", "Seated Leg Curl")),
                    WorkoutExercise("EX-L4", "Walking Sled Push Sprints", 3, "25m sprints", "EQ-FUNC-01", listOf("Farmer Carries", "Leg Extension"))
                )
            ),
            WorkoutRoutine(
                id = "RTN-PULL",
                title = "Back & Biceps Thickness (Posterior Chain)",
                description = "Lat development and spinal erector density using deadlifts, chest-supported rows, and cable pulldowns.",
                level = "Intermediate",
                durationMinutes = 55,
                category = "Hypertrophy",
                exercises = listOf(
                    WorkoutExercise("EX-P1", "Deadlift Platform Pulls", 4, "5 reps", "EQ-RACK-02", listOf("Trap Bar Deadlift", "Barbell Rack Pulls")),
                    WorkoutExercise("EX-P2", "Life Fitness Lat Pulldown", 3, "10-12 reps", "EQ-CAB-02", listOf("Pull-Ups", "Single Arm High Row")),
                    WorkoutExercise("EX-P3", "Chest Supported Dumbbell Row", 3, "8-10 reps", "EQ-DB-01", listOf("Barbell Row", "Cable Seated Row")),
                    WorkoutExercise("EX-P4", "Incline Dumbbell Bicep Curls", 3, "12 reps", "EQ-DB-01", listOf("Cable Bar Curls", "Hammer Curls"))
                )
            )
        )

        private fun initialHistory(): List<WorkoutHistoryRecord> = listOf(
            WorkoutHistoryRecord("H-1", "Hypertrophy Push Day", "Yesterday", 8450.0, 58, 4),
            WorkoutHistoryRecord("H-2", "Back & Biceps Thickness", "3 days ago", 9820.0, 54, 4),
            WorkoutHistoryRecord("H-3", "Quad & Hamstring Power", "5 days ago", 11200.0, 64, 4)
        )

        private fun initialPRs(): List<PersonalRecord> = listOf(
            PersonalRecord("Barbell Back Squat", 150.0, 5, "18 Oct 2026"),
            PersonalRecord("Conventional Deadlift", 185.0, 3, "24 Oct 2026"),
            PersonalRecord("Incline Dumbbell Press", 38.0, 8, "Yesterday"),
            PersonalRecord("Hammer Leg Press", 320.0, 10, "12 Oct 2026")
        )

        private fun initialPosts(): List<CommunityPost> = listOf(
            CommunityPost(
                id = "P-1",
                authorName = "Aditya Rao",
                authorRole = "Member • 2 Yr Streak",
                avatarInitials = "AR",
                content = "Just hit 180kg deadlift on the Eleiko platform! The new calibrated plates in Zone C feel unreal. Shoutout to Coach Marcus for the hip wedge tip.",
                timeAgo = "2h ago",
                tag = "Personal Record",
                likesCount = 28,
                isLiked = true,
                commentsCount = 6
            ),
            CommunityPost(
                id = "P-2",
                authorName = "GymVerse Operations",
                authorRole = "Official Staff Announcement",
                avatarInitials = "GV",
                content = "Cardio Loft Update: Brand new Woodway Slat-belt treadmills 01 & 02 have been installed and tested. Feel free to ask reception for Bluetooth HRM syncing guidance.",
                timeAgo = "5h ago",
                tag = "Facility Update",
                likesCount = 45,
                isLiked = false,
                commentsCount = 3
            )
        )

        private fun initialPartners(): List<WorkoutPartnerRequest> = listOf(
            WorkoutPartnerRequest("PR-1", "Siddharth K.", "Heavy Bench Press Spotter & Push Routine", "Today 06:30 PM", "Zone C (Free Weights)", "Advanced"),
            WorkoutPartnerRequest("PR-2", "Sneha Menon", "Sled Sprints & Conditioning Circuit", "Tomorrow 07:00 AM", "Zone E (Turf)", "Intermediate"),
            WorkoutPartnerRequest("PR-3", "Kabir Roy", "Morning Squat Partner (120kg-140kg working sets)", "Tomorrow 06:30 AM", "Zone A (Racks)", "Advanced")
        )

        private fun initialMaintenance(): List<MaintenanceTicket> = listOf(
            MaintenanceTicket(
                id = "MNT-1042",
                assetId = "EQ-CAB-03",
                assetName = "Atlantis Lateral Raise",
                reportedBy = "Staff Triage",
                category = "Cable Tension Verification",
                severity = "Medium",
                status = "Inspection",
                reportedTime = "Today, 08:15 AM",
                notes = "Cable guide pulley inspected for friction. Replacement bearing dispatched.",
                assignedTech = "Kishore (Biomech Tech)"
            ),
            MaintenanceTicket(
                id = "MNT-1039",
                assetId = "EQ-CAR-03",
                assetName = "Assault AirBike Elite",
                reportedBy = "Trainer Devansh",
                category = "Drive Belt Tension",
                severity = "Medium",
                status = "Repair in Progress",
                reportedTime = "Yesterday, 04:30 PM",
                notes = "Scheduled 500h service. Fan wheel belt alignment in progress.",
                assignedTech = "Sundar (Cardio Lead)"
            )
        )

        private fun initialCleaning(): List<CleaningTaskItem> = listOf(
            CleaningTaskItem("CLN-01", "Cardio Loft", "Woodway & Rower Sanitization", "Every 2 Hours", true, "11:00 AM", "Ramu (Lead Housekeeping)"),
            CleaningTaskItem("CLN-02", "Free Weights Zone C", "Dumbbell Re-racking & Chalk Sweep", "Hourly", true, "12:00 PM", "Manjunath"),
            CleaningTaskItem("CLN-03", "Showers & Locker Suite", "Steam Sanitation & Towel Restock", "Every 3 Hours", false, "02:00 PM", null),
            CleaningTaskItem("CLN-04", "Water Filtration Stations", "Filter Pressure & UV Sanitization Check", "Twice Daily", true, "08:00 AM", "Technical Facility Staff"),
            CleaningTaskItem("CLN-05", "Turf & Functional Zone", "Anti-microbial Turf Spray & Disinfection", "Daily", false, "04:30 PM", null)
        )

        private fun initialInventory(): List<InventoryItem> = listOf(
            InventoryItem("INV-01", "Gym Grade Disinfectant Wipes", "Hygiene Supplies", 42, 20, "Canisters", "Optimal Stock"),
            InventoryItem("INV-02", "Pure Magnesium Carbonate Chalk", "Lifting Consumables", 12, 10, "Blocks", "Optimal Stock"),
            InventoryItem("INV-03", "Heavy Duty Steel Carabiners (Cable)", "Replacement Parts", 4, 8, "Pieces", "Low Stock Alert"),
            InventoryItem("INV-04", "Plush Cotton Microfiber Towels", "Amenities", 180, 50, "Units", "Optimal Stock"),
            InventoryItem("INV-05", "Eleiko 20kg Calibrated Bumper Plates", "Weights & Hardware", 18, 12, "Pairs", "Optimal Stock"),
            InventoryItem("INV-06", "Electrolyte & Whey Single Sachets", "Fuel Bar Consumables", 14, 25, "Boxes", "Low Stock Alert")
        )

        private fun initialHelpDesk(): List<HelpDeskTicket> = listOf(
            HelpDeskTicket("TKT-201", "Membership", "Requesting complimentary guest pass for weekend", "Eligible for 2 passes under Black Elite tier.", "Resolved", "Normal", "01 Nov"),
            HelpDeskTicket("TKT-204", "Facility", "Locker #44 lock pin sticky", "Assigned maintenance staff to lubricate tumblers.", "Resolved", "Normal", "02 Nov")
        )

        private fun initialChat(): List<ChatMessage> = listOf(
            ChatMessage(
                id = "AI-INIT",
                sender = "GymVerse IQ",
                text = "Welcome to **GymVerse OS**. I am connected directly to the Indiranagar Flagship operations brain.\n\n" +
                        "Current Status: **Open** (44% occupancy). 46 of 50 machines operational. Today's group sessions are running on schedule.\n\n" +
                        "Ask me anything about equipment availability, exercise substitutions, or facility operations.",
                timestamp = "Online",
                isAi = true,
                actionSuggestions = listOf("Is Hack Squat free?", "How busy is the gym?", "Find Chest Machines", "Class Schedule")
            )
        )
    }
}
