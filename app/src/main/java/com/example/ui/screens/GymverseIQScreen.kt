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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.model.ChatMessage
import com.example.ui.theme.*
import com.example.viewmodel.GymVerseViewModel
import kotlinx.coroutines.launch

@Composable
fun GymverseIQScreen(
    viewModel: GymVerseViewModel,
    modifier: Modifier = Modifier
) {
    val chatMessages by viewModel.repository.chatMessages.collectAsState()
    val chatInput by viewModel.chatInput.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // IQ Header Info Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DeepCharcoal,
            border = BorderStroke(1.dp, GraphiteBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ElectricLime),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ObsidianBlack,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "GYMVERSE IQ",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = TextIvory
                        )
                        Text(
                            text = "Ground Truth Engine • Indiranagar Digital Twin",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF14331C))
                        .border(1.dp, StatusOperational.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LIVE SYNC",
                        color = StatusOperational,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Chat messages stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatMessages) { msg ->
                ChatMessageItem(
                    message = msg,
                    onSuggestionClick = { suggestion ->
                        viewModel.sendUserChatMessage(suggestion)
                    }
                )
            }
        }

        // Quick Suggestion Chips
        val sampleQueries = listOf(
            "Is Hack Squat free?",
            "How busy is the gym right now?",
            "Alternative to Cable Chest Flyes?",
            "Any safety maintenance alerts?",
            "Who is teaching next class?"
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepCharcoal)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(sampleQueries) { q ->
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.sendUserChatMessage(q) },
                    shape = RoundedCornerShape(8.dp),
                    color = GraphiteElevated,
                    border = BorderStroke(1.dp, GraphiteBorder)
                ) {
                    Text(
                        text = q,
                        color = TextIvory,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Input bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DeepCharcoal,
            border = BorderStroke(1.dp, GraphiteBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { viewModel.setChatInput(it) },
                    placeholder = { Text("Ask about equipment, crowd, substitutions...", color = TextSubtle, fontSize = 12.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("iq_chat_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricLime,
                        unfocusedBorderColor = GraphiteBorder,
                        focusedTextColor = TextIvory,
                        unfocusedTextColor = TextIvory,
                        focusedContainerColor = GraphiteCard,
                        unfocusedContainerColor = GraphiteCard
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { viewModel.sendUserChatMessage() },
                    modifier = Modifier
                        .testTag("iq_send_btn")
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (chatInput.isNotBlank()) ElectricLime else GraphiteElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Message",
                        tint = if (chatInput.isNotBlank()) ObsidianBlack else TextSubtle,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onSuggestionClick: (String) -> Unit
) {
    val isAi = message.isAi

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isAi) Alignment.Start else Alignment.End
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            if (isAi) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = ElectricLime,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = "${message.sender} • ${message.timestamp}",
                fontSize = 10.sp,
                color = TextSubtle,
                fontWeight = FontWeight.SemiBold
            )
        }

        Surface(
            modifier = Modifier.widthIn(max = 320.dp),
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isAi) 2.dp else 14.dp,
                bottomEnd = if (isAi) 14.dp else 2.dp
            ),
            color = if (isAi) GraphiteCard else ElectricLimeContainer,
            border = BorderStroke(
                1.dp,
                if (isAi) GraphiteBorder else ElectricLime.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isAi) TextIvory else ElectricLime,
                    lineHeight = 18.sp
                )

                if (message.actionSuggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "SUGGESTED ACTIONS:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSubtle,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        message.actionSuggestions.forEach { act ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSuggestionClick(act) },
                                shape = RoundedCornerShape(6.dp),
                                color = GraphiteElevated,
                                border = BorderStroke(1.dp, ElectricLime.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = act, fontSize = 11.sp, color = ElectricLime, fontWeight = FontWeight.SemiBold)
                                    Icon(Icons.Default.ArrowOutward, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
