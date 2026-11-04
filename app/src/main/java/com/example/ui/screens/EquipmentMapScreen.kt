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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.components.EquipmentCard
import com.example.ui.theme.*
import com.example.viewmodel.GymVerseViewModel

@Composable
fun EquipmentMapScreen(
    viewModel: GymVerseViewModel,
    modifier: Modifier = Modifier
) {
    val equipmentList by viewModel.repository.equipmentList.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var selectedZone by remember { mutableStateOf<String?>(null) }

    val filteredList = equipmentList.filter { eq ->
        val matchesCategory = selectedCategory == EquipmentCategory.ALL || eq.category == selectedCategory
        val matchesZone = selectedZone == null || eq.zone == selectedZone
        val matchesSearch = searchQuery.isBlank() ||
                eq.name.contains(searchQuery, ignoreCase = true) ||
                eq.id.contains(searchQuery, ignoreCase = true) ||
                eq.muscleGroups.any { it.contains(searchQuery, ignoreCase = true) } ||
                eq.zone.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesZone && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search & QR Scan row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search equipment, muscle (e.g. Hack Squat, Hamstrings)...", fontSize = 12.sp, color = TextSubtle) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("equipment_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricLime,
                        unfocusedBorderColor = GraphiteBorder,
                        focusedTextColor = TextIvory,
                        unfocusedTextColor = TextIvory,
                        focusedContainerColor = GraphiteCard,
                        unfocusedContainerColor = GraphiteCard
                    ),
                    singleLine = true
                )

                IconButton(
                    onClick = { viewModel.openQrScanner(true) },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GraphiteElevated)
                        .border(1.dp, ElectricLime.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Scan QR",
                        tint = ElectricLime
                    )
                }
            }
        }

        // Digital Interactive Gym Floor Map
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
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
                            Icon(Icons.Default.Layers, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "INTERACTIVE GYM FLOOR PLAN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricLime,
                                letterSpacing = 1.sp
                            )
                        }

                        if (selectedZone != null) {
                            Text(
                                text = "Clear Filter",
                                fontSize = 11.sp,
                                color = StatusInUse,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { selectedZone = null }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Floor Grid Layout Representing the physical zones
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ObsidianBlack)
                            .border(1.dp, GraphiteBorder, RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Top Floor Zone Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ZoneBox(
                                title = "Zone A: Lower Body",
                                subtitle = "Leg Press, Hack Squats",
                                isSelected = selectedZone == "Lower Body Deck",
                                color = StatusOperational,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedZone = if (selectedZone == "Lower Body Deck") null else "Lower Body Deck" }
                            )

                            ZoneBox(
                                title = "Zone B: Cables & Upper",
                                subtitle = "Cybex Crossovers, Lat Towers",
                                isSelected = selectedZone == "Upper Body & Cables",
                                color = CyberCyan,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedZone = if (selectedZone == "Upper Body & Cables") null else "Upper Body & Cables" }
                            )
                        }

                        // Middle Floor Zone Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ZoneBox(
                                title = "Zone C: Free Weights",
                                subtitle = "Eleiko Racks, Watson DBs",
                                isSelected = selectedZone == "Free Weights & Racks",
                                color = ElectricLime,
                                modifier = Modifier.weight(1.3f),
                                onClick = { selectedZone = if (selectedZone == "Free Weights & Racks") null else "Free Weights & Racks" }
                            )

                            ZoneBox(
                                title = "Zone D: Cardio Loft",
                                subtitle = "Woodway, RowErg",
                                isSelected = selectedZone == "Cardio Loft",
                                color = StatusInUse,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedZone = if (selectedZone == "Cardio Loft") null else "Cardio Loft" }
                            )
                        }

                        // Bottom Floor Zone Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ZoneBox(
                                title = "Zone E: Functional Turf",
                                subtitle = "25m Sled Track, Rings",
                                isSelected = selectedZone == "Functional & Turf",
                                color = IntelligenceViolet,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedZone = if (selectedZone == "Functional & Turf") null else "Functional & Turf" }
                            )

                            ZoneBox(
                                title = "Zone F: Recovery",
                                subtitle = "Hypervolt & Mobility",
                                isSelected = selectedZone == "Recovery & Mobility",
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.weight(1f),
                                onClick = { selectedZone = if (selectedZone == "Recovery & Mobility") null else "Recovery & Mobility" }
                            )
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(EquipmentCategory.values()) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCategory(category) },
                        label = {
                            Text(
                                text = category.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricLime,
                            selectedLabelColor = ObsidianBlack,
                            containerColor = GraphiteCard,
                            labelColor = TextIvory
                        ),
                        border = BorderStroke(1.dp, if (isSelected) ElectricLime else GraphiteBorder)
                    )
                }
            }
        }

        // Equipment List Header & Counter
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MACHINES & STATIONS (${filteredList.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                if (selectedZone != null) {
                    Text(
                        text = "Filtered by: $selectedZone",
                        fontSize = 11.sp,
                        color = ElectricLime
                    )
                }
            }
        }

        // Equipment items
        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = GraphiteCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextSubtle, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "No matching equipment found", color = TextIvory, fontWeight = FontWeight.SemiBold)
                        Text(text = "Try clearing search or selecting a different category.", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(filteredList) { asset ->
                EquipmentCard(
                    asset = asset,
                    onClick = { viewModel.selectEquipment(asset) },
                    onFavoriteToggle = { viewModel.toggleFavorite(asset.id) }
                )
            }
        }
    }
}

@Composable
fun ZoneBox(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = if (isSelected) color.copy(alpha = 0.2f) else GraphiteCard,
        border = BorderStroke(
            1.5.dp,
            if (isSelected) color else GraphiteBorder
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) color else TextIvory,
                    maxLines = 1
                )
            }
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = TextMuted,
                maxLines = 1
            )
        }
    }
}
