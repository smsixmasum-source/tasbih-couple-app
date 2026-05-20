package com.tasbih.couple.presentation.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.tasbih.couple.domain.model.Zikr
import com.tasbih.couple.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onZikrClick: (Zikr) -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val defaultZikr by viewModel.defaultZikr.collectAsState()
    val customZikr by viewModel.customZikr.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(colors = listOf(IslamicGreen, IslamicGreenLight)))
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "السَّلَامُ عَلَيْكُمْ",
                                color = GoldAccent,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                viewModel.userName,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row {
                            IconButton(onClick = { showAddDialog = true }) {
                                Icon(Icons.Default.Add, "জিকির যোগ", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                            IconButton(onClick = {
                                viewModel.logout()
                                onLogout()
                            }) {
                                Icon(Icons.Default.Logout, "লগআউট", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = IslamicGreen,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("ডিফল্ট জিকির") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("আমার জিকির") })
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val zikrList = if (selectedTab == 0) defaultZikr else customZikr

                if (zikrList.isEmpty() && selectedTab == 1) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📿", fontSize = 48.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("কোনো কাস্টম জিকির নেই", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen)
                            ) { Text("জিকির যোগ করুন") }
                        }
                    }
                }

                items(zikrList) { zikr ->
                    ZikrCard(zikr = zikr, onClick = { onZikrClick(zikr) })
                }
            }
        }
    }

    if (showAddDialog) {
        AddZikrDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { zikr ->
                viewModel.addCustomZikr(zikr)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ZikrCard(zikr: Zikr, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Arabic circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(IslamicGreenSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = zikr.arabicText.take(3),
                    fontSize = 14.sp,
                    color = IslamicGreen,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(zikr.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(zikr.arabicText, style = MaterialTheme.typography.bodyLarge, color = IslamicGreen, fontWeight = FontWeight.Medium)
                Text(zikr.meaning, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${zikr.targetCount}", fontWeight = FontWeight.Bold, color = IslamicGreen, fontSize = 18.sp)
                Text("বার", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ChevronRight, null, tint = IslamicGreen)
        }
    }
}

@Composable
fun AddZikrDialog(onDismiss: () -> Unit, onAdd: (Zikr) -> Unit) {
    var name by remember { mutableStateOf("") }
    var arabic by remember { mutableStateOf("") }
    var transliteration by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("33") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন জিকির যোগ করুন", color = IslamicGreen) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("জিকিরের নাম") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = arabic, onValueChange = { arabic = it }, label = { Text("আরবি টেক্সট") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = transliteration, onValueChange = { transliteration = it }, label = { Text("উচ্চারণ") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = meaning, onValueChange = { meaning = it }, label = { Text("অর্থ") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("লক্ষ্য সংখ্যা") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(Zikr(name = name, arabicText = arabic, transliteration = transliteration, meaning = meaning, targetCount = target.toIntOrNull() ?: 33, isDefault = false))
                },
                enabled = name.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen)
            ) { Text("যোগ করুন") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("বাতিল") } }
    )
}
