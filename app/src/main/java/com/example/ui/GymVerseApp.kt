package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.GymVerseViewModel

data class NavItem(
    val tab: MemberTab,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymVerseApp(
    viewModel: GymVerseViewModel = viewModel()
) {
    val currentMode by viewModel.currentMode.collectAsState()
    val currentMemberTab by viewModel.currentMemberTab.collectAsState()
    val gymInfo by viewModel.repository.gymInfo.collectAsState()
    val selectedEquipment by viewModel.selectedEquipment.collectAsState()
    val showQrScanner by viewModel.showQrScanner.collectAsState()
    val showReportFaultDialog by viewModel.showReportFaultDialog.collectAsState()
    val feedbackMessage by viewModel.feedbackMessage.collectAsState()

    val navItems = listOf(
        NavItem(MemberTab.DASHBOARD, "Live", Icons.Default.Home, Icons.Outlined.Home),
        NavItem(MemberTab.EQUIPMENT_MAP, "Floor", Icons.Default.Map, Icons.Outlined.Map),
        NavItem(MemberTab.WORKOUTS, "Workout", Icons.Default.FitnessCenter, Icons.Outlined.FitnessCenter),
        NavItem(MemberTab.CLASSES_TRAINERS, "Classes", Icons.Default.Event, Icons.Outlined.CalendarToday),
        NavItem(MemberTab.COMMUNITY, "Social", Icons.Default.Groups, Icons.Outlined.Groups),
        NavItem(MemberTab.MEMBER_HUB, "Pass", Icons.Default.Badge, Icons.Outlined.Badge)
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        topBar = {
            ModeSwitcherHeader(
                currentMode = currentMode,
                onModeSelected = { viewModel.setMode(it) },
                gymInfo = gymInfo,
                onQrClick = { viewModel.openQrScanner(true) }
            )
        },
        bottomBar = {
            if (currentMode == AppMode.MEMBER) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("member_bottom_nav"),
                    containerColor = DeepCharcoal,
                    contentColor = TextIvory,
                    tonalElevation = 8.dp
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentMemberTab == item.tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setMemberTab(item.tab) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                    tint = if (isSelected) ElectricLime else TextMuted
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    color = if (isSelected) ElectricLime else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = ElectricLimeContainer
                            )
                        )
                    }
                }
            }
        },
        snackbarHost = {
            feedbackMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = GraphiteElevated,
                    contentColor = TextIvory,
                    action = {
                        TextButton(onClick = { viewModel.dismissFeedback() }) {
                            Text("OK", color = ElectricLime, fontWeight = FontWeight.Bold)
                        }
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = msg, fontSize = 12.sp)
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentMode) {
                AppMode.MEMBER -> {
                    when (currentMemberTab) {
                        MemberTab.DASHBOARD -> MemberDashboardScreen(viewModel = viewModel)
                        MemberTab.EQUIPMENT_MAP -> EquipmentMapScreen(viewModel = viewModel)
                        MemberTab.WORKOUTS -> WorkoutTrackerScreen(viewModel = viewModel)
                        MemberTab.CLASSES_TRAINERS -> ClassesAndTrainersScreen(viewModel = viewModel)
                        MemberTab.COMMUNITY -> CommunityAndSocialScreen(viewModel = viewModel)
                        MemberTab.MEMBER_HUB -> MemberHubScreen(viewModel = viewModel)
                    }
                }
                AppMode.CONTROL_CENTER -> {
                    ControlCenterScreen(viewModel = viewModel)
                }
                AppMode.INTELLIGENCE_IQ -> {
                    GymverseIQScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Equipment Detail Dialog
    selectedEquipment?.let { asset ->
        EquipmentDetailDialog(
            asset = asset,
            onDismiss = { viewModel.selectEquipment(null) },
            onReportFault = {
                viewModel.selectEquipment(null)
                viewModel.openReportFault(asset)
            },
            onSelectAlternative = { altName ->
                viewModel.showFeedback("Selected substitute: $altName")
                viewModel.selectEquipment(null)
            },
            isStaffMode = currentMode == AppMode.CONTROL_CENTER,
            onStaffStatusChange = { newStatus ->
                viewModel.updateAssetStatusStaff(asset.id, newStatus, "Staff override via Detail Modal")
                viewModel.selectEquipment(null)
            }
        )
    }

    // QR Scanner Simulation Dialog
    if (showQrScanner) {
        QrScannerSimulationDialog(
            onDismiss = { viewModel.openQrScanner(false) },
            onSelectAsset = { assetId ->
                val asset = viewModel.repository.equipmentList.value.find { it.id == assetId }
                if (asset != null) {
                    viewModel.selectEquipment(asset)
                } else {
                    viewModel.showFeedback("Scanned unknown machine QR code: $assetId")
                }
            }
        )
    }

    // Report Fault Dialog
    showReportFaultDialog?.let { asset ->
        ReportFaultDialog(
            asset = asset,
            onDismiss = { viewModel.openReportFault(null) },
            onSubmit = { desc, sev ->
                viewModel.submitFaultReport(asset.id, desc, sev)
            }
        )
    }
}
