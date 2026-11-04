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
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.GymVerseViewModel

@Composable
fun ControlCenterScreen(
    viewModel: GymVerseViewModel,
    modifier: Modifier = Modifier
) {
    val crowdStatus by viewModel.repository.crowdStatus.collectAsState()
    val equipmentList by viewModel.repository.equipmentList.collectAsState()
    val maintenanceTickets by viewModel.repository.maintenanceTickets.collectAsState()
    val cleaningTasks by viewModel.repository.cleaningTasks.collectAsState()
    val inventory by viewModel.repository.inventory.collectAsState()

    var activeStaffTab by remember { mutableStateOf(0) } // 0 = Work Orders, 1 = Cleaning, 2 = Inventory

    val operationalCount = equipmentList.count { it.status == EquipmentStatus.OPERATIONAL }
    val openWorkOrders = maintenanceTickets.count { it.status != "Resolved" }
    val pendingCleaning = cleaningTasks.count { !it.isDone }
    val lowStockItems = inventory.count { it.currentQty <= it.minThreshold }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Staff Header
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(StatusOperational)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "OWNER & STAFF COMMAND CENTER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricLime,
                                letterSpacing = 1.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GraphiteElevated)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "Manager Auth: Active", color = TextIvory, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Core Operations Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCard(
                            label = "FLOOR OCCUPANCY",
                            value = "${crowdStatus.activeMembersCount}",
                            sub = "${crowdStatus.occupancyPercentage}% Limit",
                            color = StatusInUse,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "EQUIPMENT HEALTH",
                            value = "$operationalCount/${equipmentList.size}",
                            sub = "92% Operational",
                            color = StatusOperational,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricCard(
                            label = "WORK ORDERS",
                            value = "$openWorkOrders",
                            sub = if (openWorkOrders > 0) "Action Required" else "All Cleared",
                            color = if (openWorkOrders > 0) StatusOutOfService else StatusOperational,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "LOW INVENTORY",
                            value = "$lowStockItems",
                            sub = "Threshold Warnings",
                            color = if (lowStockItems > 0) StatusMaintenance else StatusOperational,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Staff Operation Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepCharcoal)
                    .clip(RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "Work Orders ($openWorkOrders)",
                    "Housekeeping ($pendingCleaning)",
                    "Supplies ($lowStockItems)"
                ).forEachIndexed { index, title ->
                    val isSel = activeStaffTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) GraphiteElevated else Color.Transparent)
                            .border(1.dp, if (isSel) ElectricLime.copy(alpha = 0.5f) else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { activeStaffTab = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
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

        when (activeStaffTab) {
            0 -> {
                // Maintenance & Work Orders Tab
                item {
                    Text(
                        text = "EQUIPMENT MAINTENANCE TICKETS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextIvory,
                        letterSpacing = 1.sp
                    )
                }

                items(maintenanceTickets) { ticket ->
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = ticket.id, color = ElectricLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "• ${ticket.assetId}", color = TextMuted, fontSize = 11.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (ticket.status == "Resolved") Color(0xFF14331C) else Color(0xFF382A10))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = ticket.status,
                                        color = if (ticket.status == "Resolved") StatusOperational else StatusInUse,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = ticket.assetName, color = TextIvory, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Category: ${ticket.category} • Severity: ${ticket.severity}", color = StatusOutOfService, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = ticket.notes, color = TextMuted, fontSize = 12.sp, lineHeight = 16.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Assigned Tech: ${ticket.assignedTech} • Reported ${ticket.reportedTime}", color = TextSubtle, fontSize = 10.sp)

                            if (ticket.status != "Resolved") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        viewModel.repository.resolveMaintenanceTicket(ticket.id, "Safety inspection passed & verified")
                                        viewModel.showFeedback("Ticket ${ticket.id} marked Resolved.")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusOperational, contentColor = ObsidianBlack),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Mark Resolved & Returned to Service", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Housekeeping & Cleaning Tasks
                item {
                    Text(
                        text = "ZONE CLEANING & RESTROOM INSPECTIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextIvory,
                        letterSpacing = 1.sp
                    )
                }

                items(cleaningTasks) { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.repository.toggleCleaningTask(task.id) },
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
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = task.area, color = ElectricLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "• ${task.interval}", color = TextSubtle, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = task.taskName, color = TextIvory, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text(
                                    text = if (task.isDone) "Verified by ${task.completedBy}" else "Due by ${task.dueTime}",
                                    color = if (task.isDone) StatusOperational else StatusInUse,
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (task.isDone) StatusOperational else GraphiteElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (task.isDone) Icons.Default.Check else Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (task.isDone) ObsidianBlack else TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                // Inventory & Supply Management
                item {
                    Text(
                        text = "GYM CONSUMABLES & INVENTORY AUDIT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextIvory,
                        letterSpacing = 1.sp
                    )
                }

                items(inventory) { item ->
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.name, color = TextIvory, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "Category: ${item.category}", color = TextMuted, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${item.currentQty} ${item.unit} in stock",
                                        color = if (item.currentQty <= item.minThreshold) StatusOutOfService else StatusOperational,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "(Min: ${item.minThreshold})", color = TextSubtle, fontSize = 10.sp)
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.repository.restockInventory(item.id, 10)
                                    viewModel.showFeedback("Restocked +10 units of ${item.name}")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GraphiteElevated, contentColor = ElectricLime),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("+10 Restock", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = GraphiteElevated,
        border = BorderStroke(1.dp, GraphiteBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSubtle, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = sub, fontSize = 10.sp, color = TextMuted)
        }
    }
}
