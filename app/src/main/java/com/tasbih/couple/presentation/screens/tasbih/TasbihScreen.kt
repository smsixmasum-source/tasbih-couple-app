package com.tasbih.couple.presentation.screens.tasbih

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tasbih.couple.presentation.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TasbihScreen(
    zikrId: String,
    zikrName: String,
    arabicText: String,
    targetCount: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    var count by remember { mutableIntStateOf(0) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Button animation
    val scale = remember { Animatable(1f) }

    // Progress
    val progress = if (targetCount > 0) (count.toFloat() / targetCount).coerceIn(0f, 1f) else 0f
    val isCompleted = count >= targetCount && targetCount > 0

    // Gradient based on progress
    val bgGradient = if (isCompleted) {
        Brush.verticalGradient(listOf(Color(0xFF1B5E20), GoldAccent, Color(0xFF2E7D32)))
    } else {
        Brush.verticalGradient(listOf(IslamicGreen, Color(0xFF2E7D32), CreamWhite))
    }

    fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun onCount() {
        scope.launch {
            count++
            vibrate()
            scale.animateTo(0.92f, animationSpec = tween(80))
            scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    // History-তে save করে back
                    if (count > 0) {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@IconButton
                        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val entry = mapOf("zikrId" to zikrId, "zikrName" to zikrName, "count" to count, "timestamp" to System.currentTimeMillis())
                        val ref = FirebaseFirestore.getInstance().collection("history").document(uid).collection("records").document(today)
                        ref.get().addOnSuccessListener { doc ->
                            if (doc.exists()) {
                                val entries = (doc.get("entries") as? List<*>)?.toMutableList() ?: mutableListOf()
                                entries.add(entry)
                                ref.update("entries", entries, "totalCount", (doc.getLong("totalCount") ?: 0) + count)
                            } else {
                                ref.set(mapOf("date" to today, "entries" to listOf(entry), "totalCount" to count))
                            }
                        }
                    }
                    onBack()
                }) {
                    Icon(Icons.Default.ArrowBack, "ফিরে যান", tint = Color.White)
                }

                Text(
                    text = zikrName,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { showResetDialog = true }) {
                    Icon(Icons.Default.Refresh, "রিসেট", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Arabic text
            Card(
                modifier = Modifier.padding(horizontal = 24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
            ) {
                Text(
                    text = arabicText,
                    fontSize = 36.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Progress indicator
            Box(modifier = Modifier.padding(horizontal = 32.dp)) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = GoldAccent,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(progress * 100).toInt()}% সম্পন্ন",
                color = GoldAccent,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Main Count Display
            AnimatedContent(
                targetState = count,
                transitionSpec = {
                    slideInVertically { -it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut()
                },
                label = "count"
            ) { targetCount ->
                Text(
                    text = targetCount.toString(),
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = "/ $targetCount",
                fontSize = 24.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            if (isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = GoldAccent),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "মাশাআল্লাহ! লক্ষ্য পূরণ হয়েছে 🌟",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // BIG TAP BUTTON
            Box(
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .size(200.dp)
                    .scale(scale.value)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(GoldAccent, Color(0xFFB8860B))
                        )
                    )
                    .clickable { onCount() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📿", fontSize = 48.sp)
                    Text(
                        "ট্যাপ করুন",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Reset Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("কাউন্ট রিসেট করবেন?", fontWeight = FontWeight.Bold) },
            text = { Text("এই জিকিরের কাউন্ট $count থেকে ০ হয়ে যাবে।") },
            confirmButton = {
                Button(
                    onClick = { count = 0; showResetDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("হ্যাঁ, রিসেট করুন") }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("বাতিল") }
            }
        )
    }
}
