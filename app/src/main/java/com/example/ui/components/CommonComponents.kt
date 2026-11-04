package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun StatusBadge(status: EquipmentStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, dotColor) = when (status) {
        EquipmentStatus.OPERATIONAL -> Triple(Color(0xFF14331C), StatusOperational, StatusOperational)
        EquipmentStatus.IN_USE -> Triple(Color(0xFF382A10), StatusInUse, StatusInUse)
        EquipmentStatus.MAINTENANCE_SCHEDULED -> Triple(Color(0xFF361E14), StatusMaintenance, StatusMaintenance)
        EquipmentStatus.UNDER_INSPECTION -> Triple(Color(0xFF132A3B), StatusInspection, StatusInspection)
        EquipmentStatus.FAULT_REPORTED -> Triple(Color(0xFF3B1515), StatusOutOfService, StatusOutOfService)
        EquipmentStatus.OUT_OF_SERVICE -> Triple(Color(0xFF331010), StatusOutOfService, StatusOutOfService)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, dotColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = status.displayName,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ModeSwitcherHeader(
    currentMode: AppMode,
    onModeSelected: (AppMode) -> Unit,
    gymInfo: GymInfo,
    onQrClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DeepCharcoal,
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, GraphiteBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElectricLime),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = "GymVerse Logo",
                            tint = ObsidianBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "GYMVERSE",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = TextIvory,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ElectricLime.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "OS",
                                    color = ElectricLime,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = gymInfo.branch,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = onQrClick,
                    modifier = Modifier
                        .testTag("qr_scanner_btn")
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GraphiteElevated)
                        .border(1.dp, GraphiteBorder, RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Scan Equipment QR",
                        tint = ElectricLime,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mode Selector Pill Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianBlack)
                    .border(1.dp, GraphiteBorder, RoundedCornerShape(10.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AppMode.values().forEach { mode ->
                    val isSelected = currentMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GraphiteElevated else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) ElectricLime.copy(alpha = 0.5f) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onModeSelected(mode) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (mode) {
                                AppMode.MEMBER -> "Member"
                                AppMode.CONTROL_CENTER -> "Staff Control"
                                AppMode.INTELLIGENCE_IQ -> "GymVerse IQ"
                            },
                            color = if (isSelected) ElectricLime else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentCard(
    asset: EquipmentAsset,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("equipment_card_${asset.id}"),
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
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GraphiteElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = asset.id,
                            color = ElectricLime,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = asset.zone,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (asset.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (asset.isFavorite) Color(0xFFF43F5E) else TextSubtle,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = asset.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextIvory,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Muscle tag chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                asset.muscleGroups.take(2).forEach { muscle ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(GraphiteElevated)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = muscle,
                            color = TextIvory,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = asset.status)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Inspect",
                        color = ElectricLime,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = ElectricLime,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EquipmentDetailDialog(
    asset: EquipmentAsset,
    onDismiss: () -> Unit,
    onReportFault: () -> Unit,
    onSelectAlternative: (String) -> Unit,
    isStaffMode: Boolean = false,
    onStaffStatusChange: ((EquipmentStatus) -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp)),
            color = DeepCharcoal,
            border = BorderStroke(1.dp, GraphiteBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GraphiteElevated)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = asset.id,
                                color = ElectricLime,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = asset.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextIvory
                        )
                        Text(
                            text = "${asset.zone} • ${asset.manufacturer}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current Status Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GraphiteCard)
                        .border(1.dp, GraphiteBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EQUIPMENT STATUS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSubtle,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        StatusBadge(status = asset.status)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "LAST VERIFIED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSubtle,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = asset.lastInspected,
                            fontSize = 11.sp,
                            color = TextIvory,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Setup & Usage
                Text(
                    text = "BIOMECHANICAL INSTRUCTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricLime,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = asset.instructions,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextIvory,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "SAFETY SETUP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusInUse,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = asset.safetyNotes,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Alternatives
                if (asset.alternatives.isNotEmpty()) {
                    Text(
                        text = "EQUIPMENT SUBSTITUTIONS (IF BUSY)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        asset.alternatives.take(2).forEach { alt ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GraphiteElevated)
                                    .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .clickable { onSelectAlternative(alt) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = alt,
                                    color = TextIvory,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Staff Controls if Staff Mode
                if (isStaffMode && onStaffStatusChange != null) {
                    Text(
                        text = "STAFF OVERRIDE STATUS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusMaintenance,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            EquipmentStatus.OPERATIONAL,
                            EquipmentStatus.UNDER_INSPECTION,
                            EquipmentStatus.OUT_OF_SERVICE
                        ).forEach { st ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (asset.status == st) ElectricLime else GraphiteCard)
                                    .clickable { onStaffStatusChange(st) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = st.displayName.take(8),
                                    color = if (asset.status == st) ObsidianBlack else TextIvory,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReportFault,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("report_fault_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusOutOfService),
                        border = BorderStroke(1.dp, StatusOutOfService.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReportProblem,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Report Fault", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dismiss_modal_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Done", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun QrScannerSimulationDialog(
    onDismiss: () -> Unit,
    onSelectAsset: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp)),
            color = DeepCharcoal,
            border = BorderStroke(1.dp, GraphiteBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = ElectricLime,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Scan Equipment QR",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextIvory
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Simulated Viewfinder
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ObsidianBlack)
                        .border(2.dp, ElectricLime, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CropFree,
                            contentDescription = null,
                            tint = ElectricLime.copy(alpha = 0.8f),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Align machine tag",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Quick Scan Test Assets",
                    color = TextIvory,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "EQ-LEG-03" to "Arsenal Strength Hack Squat",
                        "EQ-LEG-01" to "Hammer Strength Leg Press",
                        "EQ-CAB-01" to "Cybex Cable Crossover",
                        "EQ-RACK-01" to "Eleiko IPF Power Rack 01"
                    ).forEach { (id, name) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectAsset(id)
                                    onDismiss()
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = GraphiteElevated,
                            border = BorderStroke(1.dp, GraphiteBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = name, color = TextIvory, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Code: $id", color = ElectricLime, fontSize = 10.sp)
                                }
                                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportFaultDialog(
    asset: EquipmentAsset,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var notes by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("Medium") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp)),
            color = DeepCharcoal,
            border = BorderStroke(1.dp, GraphiteBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Report Machine Fault",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextIvory
                )
                Text(
                    text = "${asset.name} (${asset.id})",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricLime
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "SEVERITY LEVEL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSubtle
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Low", "Medium", "High Safety Hazard").forEach { sev ->
                        val isSel = severity == sev
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) StatusOutOfService else GraphiteElevated)
                                .clickable { severity = sev }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sev.take(6),
                                color = if (isSel) Color.White else TextIvory,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "FAULT DETAILS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSubtle
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("e.g. Unusual grinding noise on cable pulley, cracked clamp pin...", color = TextSubtle) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("fault_description_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricLime,
                        unfocusedBorderColor = GraphiteBorder,
                        focusedTextColor = TextIvory,
                        unfocusedTextColor = TextIvory
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val desc = if (notes.isBlank()) "Reported mechanical irregularity" else notes
                            onSubmit(desc, severity)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("submit_fault_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusOutOfService, contentColor = Color.White)
                    ) {
                        Text("Dispatch Tech", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
