package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.EquipmentCard
import com.example.ui.theme.*
import com.example.viewmodel.GymVerseViewModel

@Composable
fun MemberDashboardScreen(
    viewModel: GymVerseViewModel,
    modifier: Modifier = Modifier
) {
    val gymInfo by viewModel.repository.gymInfo.collectAsState()
    val crowdStatus by viewModel.repository.crowdStatus.collectAsState()
    val equipmentList by viewModel.repository.equipmentList.collectAsState()
    val groupClasses by viewModel.repository.groupClasses.collectAsState()
    val workoutRoutines by viewModel.repository.workoutRoutines.collectAsState()
    val partnerRequests by viewModel.repository.partnerRequests.collectAsState()

    val operationalCount = equipmentList.count { it.status == EquipmentStatus.OPERATIONAL }
    val inUseCount = equipmentList.count { it.status == EquipmentStatus.IN_USE }
    val maintenanceCount = equipmentList.count { it.status != EquipmentStatus.OPERATIONAL && it.status != EquipmentStatus.IN_USE }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Live Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GraphiteCard),
                border = BorderStroke(1.dp, GraphiteBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(GraphiteCard, DeepCharcoal)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(StatusOperational)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GYM OPEN NOW",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusOperational,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = gymInfo.openingHours,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "YOUR GYM. YOUR NEXT LEVEL.",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = TextIvory
                        )
                        Text(
                            text = "Real-time floor telemetry, equipment readiness & community.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick action bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.setMemberTab(MemberTab.EQUIPMENT_MAP) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("explore_floor_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricLime,
                                    contentColor = ObsidianBlack
                                )
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gym Map", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.openQrScanner(true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quick_scan_qr_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextIvory),
                                border = BorderStroke(1.dp, GraphiteBorder)
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scan Asset", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // Occupancy & Crowd Intelligence
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("crowd_intelligence_card"),
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
                                text = "LIVE CROWD INTELLIGENCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricLime,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = crowdStatus.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextIvory
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GraphiteElevated)
                                .border(1.dp, GraphiteBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${crowdStatus.occupancyPercentage}% Full",
                                color = StatusInUse,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { crowdStatus.occupancyPercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = StatusInUse,
                        trackColor = ObsidianBlack,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.People, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${crowdStatus.activeMembersCount} of ${crowdStatus.capacityLimit} Members Inside",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "Peak: ${crowdStatus.peakPeriod}",
                            color = StatusMaintenance,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Hourly graph curve preview
                    Text(
                        text = "TODAY'S HOURLY TRAFFIC CURVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSubtle,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        crowdStatus.hourlyOccupancy.forEach { (hour, pct) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(16.dp)
                                        .height((pct * 0.35).coerceAtLeast(4.0).dp)
                                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                        .background(
                                            if (pct > 70) StatusOutOfService
                                            else if (pct > 40) StatusInUse
                                            else ElectricLime
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = hour.take(2),
                                    fontSize = 9.sp,
                                    color = TextSubtle
                                )
                            }
                        }
                    }
                }
            }
        }

        // Equipment Health Snapshot
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setMemberTab(MemberTab.EQUIPMENT_MAP) },
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
                        Text(
                            text = "EQUIPMENT AVAILABILITY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricLime,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "View All (${equipmentList.size})",
                                fontSize = 12.sp,
                                color = ElectricLime,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = GraphiteElevated,
                            border = BorderStroke(1.dp, StatusOperational.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "$operationalCount", fontSize = 20.sp, fontWeight = FontWeight.Black, color = StatusOperational)
                                Text(text = "Operational", fontSize = 11.sp, color = TextMuted)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = GraphiteElevated,
                            border = BorderStroke(1.dp, StatusInUse.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "$inUseCount", fontSize = 20.sp, fontWeight = FontWeight.Black, color = StatusInUse)
                                Text(text = "In Use Now", fontSize = 11.sp, color = TextMuted)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = GraphiteElevated,
                            border = BorderStroke(1.dp, StatusMaintenance.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "$maintenanceCount", fontSize = 20.sp, fontWeight = FontWeight.Black, color = StatusMaintenance)
                                Text(text = "Servicing", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }

        // Today's Scheduled Group Classes
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "UPCOMING GROUP SESSIONS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextIvory,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Schedule",
                        fontSize = 12.sp,
                        color = ElectricLime,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { viewModel.setMemberTab(MemberTab.CLASSES_TRAINERS) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(groupClasses) { gc ->
                        Card(
                            modifier = Modifier
                                .width(250.dp)
                                .clickable { viewModel.setMemberTab(MemberTab.CLASSES_TRAINERS) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = GraphiteCard),
                            border = BorderStroke(1.dp, GraphiteBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (gc.isBookedByMe) ElectricLimeContainer else GraphiteElevated)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (gc.isBookedByMe) "Booked" else "${gc.capacity - gc.bookedCount} spots left",
                                            color = if (gc.isBookedByMe) ElectricLime else TextIvory,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(text = "${gc.durationMinutes}m", color = TextMuted, fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = gc.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextIvory,
                                    maxLines = 1
                                )
                                Text(
                                    text = gc.instructor,
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = gc.time,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ElectricLime
                                    )

                                    FilledTonalButton(
                                        onClick = { viewModel.toggleClassBooking(gc.id) },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = if (gc.isBookedByMe) "Cancel" else "Book",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Launch Workout
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
                        Text(
                            text = "START TODAY'S ROUTINE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricLime,
                            letterSpacing = 1.sp
                        )
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val pushRoutine = workoutRoutines.firstOrNull()
                    if (pushRoutine != null) {
                        Text(
                            text = pushRoutine.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextIvory
                        )
                        Text(
                            text = "${pushRoutine.exercises.size} Exercises • ${pushRoutine.durationMinutes} min • ${pushRoutine.level}",
                            fontSize = 12.sp,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.startRoutine(pushRoutine) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("start_workout_dashboard_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = ObsidianBlack)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Launch Live Workout Session", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Community Partner Activity
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setMemberTab(MemberTab.COMMUNITY) },
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PeopleOutline, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WORKOUT PARTNERS WANTED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "${partnerRequests.size} Active",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    partnerRequests.take(2).forEach { req ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(GraphiteElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = req.memberName.take(1),
                                    color = ElectricLime,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = req.memberName, color = TextIvory, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "${req.trainingGoal} • ${req.timeSlot}", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
