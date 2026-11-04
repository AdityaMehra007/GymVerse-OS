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
fun MemberHubScreen(
    viewModel: GymVerseViewModel,
    modifier: Modifier = Modifier
) {
    val memberProfile by viewModel.repository.memberProfile.collectAsState()
    val helpDeskTickets by viewModel.repository.helpDeskTickets.collectAsState()

    var showNewTicketDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Digital Access Pass Card (Turnstile / NFC)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("digital_access_pass_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GraphiteCard),
                border = BorderStroke(1.5.dp, ElectricLime.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GYMVERSE DIGITAL ACCESS PASS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricLime,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = memberProfile.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = TextIvory
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ElectricLimeContainer)
                                .border(1.dp, ElectricLime.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = memberProfile.tier,
                                color = ElectricLime,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Simulated Turnstile QR Code
                    Surface(
                        modifier = Modifier
                            .size(170.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        color = TextIvory
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "Digital Entry Pass",
                                tint = ObsidianBlack,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "ID: ${memberProfile.memberId} • Valid Thru ${memberProfile.validThrough}",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.showFeedback("Access Pass Scanned! Turnstile Gate 01 Unlocked. Welcome to GymVerse Indiranagar.")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("simulate_checkin_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Nfc, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simulate Entry Check-in", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Monthly Stats & Streak
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = GraphiteCard,
                    border = BorderStroke(1.dp, GraphiteBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "CHECK-INS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSubtle)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${memberProfile.checkInsThisMonth}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = ElectricLime)
                        Text(text = "This Month", fontSize = 11.sp, color = TextMuted)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = GraphiteCard,
                    border = BorderStroke(1.dp, GraphiteBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "ACTIVE STREAK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSubtle)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${memberProfile.streakDays} Days 🔥", fontSize = 24.sp, fontWeight = FontWeight.Black, color = StatusInUse)
                        Text(text = "Consistent", fontSize = 11.sp, color = TextMuted)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = GraphiteCard,
                    border = BorderStroke(1.dp, GraphiteBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "WORKOUTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSubtle)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "${memberProfile.totalWorkoutsLogged}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = CyberCyan)
                        Text(text = "Logged All-time", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }
        }

        // Membership Inclusions & Multi-Branch Access
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GraphiteCard),
                border = BorderStroke(1.dp, GraphiteBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "MEMBERSHIP INCLUSIONS & AMENITIES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricLime,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    listOf(
                        "All Bangalore Flagship Branches (Indiranagar, Koramangala, Whitefield)",
                        "Full Access to Recovery Lounge & Hyperice Mobility Deck",
                        "Unlimited Group Classes (Spin, HIIT, Mobility, Barbell)",
                        "2 Monthly Complimentary Guest Day Passes"
                    ).forEach { inc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusOperational, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = inc, color = TextIvory, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Help Desk & Support Center
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GYM HELP DESK & TICKETS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "+ New Ticket",
                    color = ElectricLime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showNewTicketDialog = true }
                )
            }
        }

        items(helpDeskTickets) { tkt ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GraphiteCard),
                border = BorderStroke(1.dp, GraphiteBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${tkt.id} • ${tkt.category}", color = ElectricLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (tkt.status == "Resolved") Color(0xFF14331C) else GraphiteElevated)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tkt.status,
                                color = if (tkt.status == "Resolved") StatusOperational else StatusInUse,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = tkt.subject, color = TextIvory, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(text = tkt.details, color = TextMuted, fontSize = 11.sp)
                }
            }
        }
    }

    // Submit Help Ticket Modal
    if (showNewTicketDialog) {
        var category by remember { mutableStateOf("Facility & Cleanliness") }
        var subject by remember { mutableStateOf("") }
        var details by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewTicketDialog = false },
            containerColor = DeepCharcoal,
            title = {
                Text(text = "Submit Help Request", color = TextIvory, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(text = "Category:", color = TextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        placeholder = { Text("Subject (e.g. Locker lock issue, billing question)", color = TextSubtle, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricLime,
                            unfocusedBorderColor = GraphiteBorder,
                            focusedTextColor = TextIvory,
                            unfocusedTextColor = TextIvory
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        placeholder = { Text("Describe the issue in detail...", color = TextSubtle, fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricLime,
                            unfocusedBorderColor = GraphiteBorder,
                            focusedTextColor = TextIvory,
                            unfocusedTextColor = TextIvory
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (subject.isNotBlank()) {
                            viewModel.repository.submitHelpDeskTicket(category, subject, details, "Normal")
                            viewModel.showFeedback("Help ticket submitted to Indiranagar desk staff.")
                        }
                        showNewTicketDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = ObsidianBlack)
                ) {
                    Text("Submit Ticket", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewTicketDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}
