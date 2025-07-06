package com.example.share_note.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// 🟢 1. ViewModel Gibi Kullanılacak Toast Manager
class ToastManager {
    private val _toastState = MutableStateFlow<ToastData?>(null)
    val toastState: StateFlow<ToastData?> = _toastState

    fun showToast(message: String, icon: ImageVector = Icons.Default.Info) {
        _toastState.value = ToastData(message, icon)
    }

    fun clearToast() {
        _toastState.value = null
    }
}

// 🟢 2. Toast Mesajı Modeli
data class ToastData(val message: String, val icon: ImageVector)

// 🟢 3. Custom Toast Bileşeni (Ekranın En Üstüne Geliyor!)
@Composable
fun CustomToastHost(toastManager: ToastManager) {
    val toastState by toastManager.toastState.collectAsState()

    toastState?.let { toastData ->
        var isVisible by remember { mutableStateOf(true) }

        LaunchedEffect(toastData) {
            isVisible = true
            delay(2000) // 2 saniye sonra kaybolur
            isVisible = false
            delay(500)  // Animasyon süresi
            toastManager.clearToast()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f), // 🟢 **Diğer bileşenlerin ÜSTÜNDE olacak**
            contentAlignment = Alignment.TopCenter
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { -100 }),
                exit = fadeOut(animationSpec = tween(500)) + slideOutVertically(targetOffsetY = { -100 })
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .padding(top = 60.dp)
                        .wrapContentSize()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .wrapContentSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = toastData.icon ?: Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = toastData.message,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun deneme(){
    val toastManager = remember { ToastManager() }
    toastManager.showToast("Mert")
}
