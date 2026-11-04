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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.GymVerseViewModel

@Composable
fun CommunityAndSocialScreen(
    viewModel: GymVerseViewModel,
    modifier: Modifier = Modifier
) {
    val partnerRequests by viewModel.repository.partnerRequests.collectAsState()
    val communityPosts by viewModel.repository.communityPosts.collectAsState()

    var showNewPostDialog by remember { mutableStateOf(false) }
    var showPartnerRequestDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Community Banner & Status
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
                                text = "YOUR GYM COMMUNITY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricLime,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Train Together. Stay Accountable.",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextIvory
                            )
                        }

                        Button(
                            onClick = { showPartnerRequestDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = ObsidianBlack),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Find Partner", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Workout Partner Matchmaking Section
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WORKOUT PARTNERS TODAY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Opt-in only",
                        fontSize = 10.sp,
                        color = TextSubtle
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(partnerRequests) { req ->
                        Card(
                            modifier = Modifier.width(260.dp),
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
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(GraphiteElevated),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = req.memberName.take(1),
                                                color = ElectricLime,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(text = req.memberName, color = TextIvory, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(text = req.level, color = TextSubtle, fontSize = 10.sp)
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(GraphiteElevated)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = req.timeSlot, color = ElectricLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = req.trainingGoal,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextIvory,
                                    lineHeight = 16.sp
                                )
                                Text(
                                    text = "Location: ${req.preferredZone}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = {
                                        viewModel.showFeedback("Connected with ${req.memberName}! Invitation sent to train together.")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, ElectricLime.copy(alpha = 0.5f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricLime)
                                ) {
                                    Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Connect & Train", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Gym Groups
        item {
            Column {
                Text(
                    text = "OFFICIAL GYM HUBS & CHANNELS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "🌅 Morning Iron Crew" to "5:30 AM Lifters",
                        "⚡ Evening Power Squad" to "Heavy Squats & Bench"
                    ).forEach { (name, desc) ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.showFeedback("Joined $name channel")
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = GraphiteCard,
                            border = BorderStroke(1.dp, GraphiteBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = name, color = TextIvory, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = desc, color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // Community Feed Header & New Post Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GYM FEED & PR CELEBRATIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "+ Share Update",
                    color = ElectricLime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showNewPostDialog = true }
                )
            }
        }

        // Community Posts
        items(communityPosts) { post ->
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
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(GraphiteElevated)
                                    .border(1.dp, ElectricLime, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = post.avatarInitials,
                                    color = ElectricLime,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = post.authorName, color = TextIvory, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "${post.authorRole} • ${post.timeAgo}", color = TextSubtle, fontSize = 10.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GraphiteElevated)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = post.tag, color = StatusOperational, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = post.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextIvory,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                viewModel.showFeedback("Liked post by ${post.authorName}")
                            }
                        ) {
                            Icon(
                                imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (post.isLiked) Color(0xFFF43F5E) else TextSubtle,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${post.likesCount}", color = TextMuted, fontSize = 12.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Comments",
                                tint = TextSubtle,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${post.commentsCount}", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Share Update Dialog
    if (showNewPostDialog) {
        var postText by remember { mutableStateOf("") }
        var postTag by remember { mutableStateOf("Training Update") }

        AlertDialog(
            onDismissRequest = { showNewPostDialog = false },
            containerColor = DeepCharcoal,
            title = {
                Text(text = "Share with Gym Community", color = TextIvory, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = postText,
                        onValueChange = { postText = it },
                        placeholder = { Text("Share a PR, equipment tip, or workout motivation...", color = TextSubtle, fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(10.dp),
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
                        if (postText.isNotBlank()) {
                            viewModel.repository.postCommunityMessage(postText, postTag)
                            viewModel.showFeedback("Posted to gym community!")
                        }
                        showNewPostDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = ObsidianBlack)
                ) {
                    Text("Post", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewPostDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // Partner Request Dialog
    if (showPartnerRequestDialog) {
        var goalText by remember { mutableStateOf("") }
        var timeSlotText by remember { mutableStateOf("Today 6:00 PM") }

        AlertDialog(
            onDismissRequest = { showPartnerRequestDialog = false },
            containerColor = DeepCharcoal,
            title = {
                Text(text = "Post Partner Request", color = TextIvory, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(text = "What are you training?", color = TextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = goalText,
                        onValueChange = { goalText = it },
                        placeholder = { Text("e.g. Chest & Bench Spotter, 5k Row Partner", color = TextSubtle, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
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
                        if (goalText.isNotBlank()) {
                            viewModel.showFeedback("Partner request published to gym board!")
                        }
                        showPartnerRequestDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricLime, contentColor = ObsidianBlack)
                ) {
                    Text("Publish Request", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPartnerRequestDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}
