package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.GymVerseViewModel

@Composable
fun ClassesAndTrainersScreen(
    viewModel: GymVerseViewModel,
    modifier: Modifier = Modifier
) {
    val groupClasses by viewModel.repository.groupClasses.collectAsState()
    val trainers by viewModel.repository.trainers.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0 = Classes, 1 = Trainers
    var selectedTrainerForBooking by remember { mutableStateOf<TrainerProfile?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Tab Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepCharcoal)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Group Classes (${groupClasses.size})", "Certified Trainers (${trainers.size})").forEachIndexed { index, title ->
                val isSel = activeTab == index
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
                        .clickable { activeTab = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSel) ElectricLime else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        if (activeTab == 0) {
            // Group Classes List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(groupClasses) { gc ->
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
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (gc.isBookedByMe) ElectricLimeContainer else GraphiteElevated)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = gc.category,
                                        color = if (gc.isBookedByMe) ElectricLime else TextIvory,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = gc.intensity,
                                    color = StatusInUse,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = gc.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextIvory
                            )
                            Text(
                                text = "Coach: ${gc.instructor} • ${gc.studio}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = gc.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextIvory,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Capacity bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Booked: ${gc.bookedCount} / ${gc.capacity} spots",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "${gc.durationMinutes} min",
                                    fontSize = 11.sp,
                                    color = TextSubtle
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { gc.bookedCount.toFloat() / gc.capacity },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (gc.bookedCount >= gc.capacity) StatusOutOfService else ElectricLime,
                                trackColor = ObsidianBlack
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = gc.time,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextIvory
                                    )
                                }

                                Button(
                                    onClick = { viewModel.toggleClassBooking(gc.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (gc.isBookedByMe) GraphiteElevated else ElectricLime,
                                        contentColor = if (gc.isBookedByMe) TextIvory else ObsidianBlack
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = if (gc.isBookedByMe) "Cancel Reservation" else "Reserve Spot",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Trainers Directory
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(trainers) { trainer ->
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(GraphiteElevated)
                                            .border(1.5.dp, ElectricLime, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = trainer.name.split(" ").map { it.take(1) }.joinToString(""),
                                            color = ElectricLime,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = trainer.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = TextIvory
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.Verified,
                                                contentDescription = "Verified Coach",
                                                tint = CyberCyan,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Text(
                                            text = trainer.specialty,
                                            fontSize = 12.sp,
                                            color = ElectricLime
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = StatusInUse, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(text = "${trainer.rating}", color = TextIvory, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Text(text = "${trainer.reviewCount} reviews", color = TextSubtle, fontSize = 10.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = trainer.bio,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Credentials: ${trainer.credentials}",
                                fontSize = 11.sp,
                                color = TextSubtle
                            )
                            Text(
                                text = "Languages: ${trainer.languages} • ${trainer.experience}",
                                fontSize = 11.sp,
                                color = TextSubtle
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = trainer.hourlyRate,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextIvory
                                )

                                Button(
                                    onClick = { selectedTrainerForBooking = trainer },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = ObsidianBlack),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Book Consultation", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Trainer Booking Modal
        selectedTrainerForBooking?.let { tr ->
            AlertDialog(
                onDismissRequest = { selectedTrainerForBooking = null },
                containerColor = DeepCharcoal,
                title = {
                    Text(text = "Schedule Consultation: ${tr.name}", color = TextIvory, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text(text = "Select an available slot at ${tr.hourlyRate}:", color = TextMuted, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        tr.availabilitySlots.forEach { slot ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        viewModel.showFeedback("Booked consultation with ${tr.name} for $slot")
                                        selectedTrainerForBooking = null
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = GraphiteElevated,
                                border = BorderStroke(1.dp, GraphiteBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = slot, color = TextIvory, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(text = "Confirm Slot", color = ElectricLime, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedTrainerForBooking = null }) {
                        Text("Close", color = TextMuted)
                    }
                }
            )
        }
    }
}
