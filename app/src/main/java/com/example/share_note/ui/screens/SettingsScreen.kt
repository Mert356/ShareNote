package com.example.share_note.ui.screens

import Dark_mode
import Language
import Light_mode
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.share_note.ui.adds.AdBannerView
import com.example.share_note.ui.components.CustomToastHost
import com.example.share_note.ui.components.ToastManager
import com.example.share_note.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, toastManager: ToastManager,onThemeChange: (Boolean) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var selectedLanguage by remember { mutableStateOf("Türkçe") }
    var isDarkTheme by remember { mutableStateOf(false) }
    var isNotificationsEnabled by remember { mutableStateOf(true) }

    // 🔥 Firestore’dan ayarları al
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            val userDoc = db.collection("users").document(user.uid)
            userDoc.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    name = document.getString("name") ?: ""
                }
            }

            val settingsDoc = userDoc.collection("settings").document("preferences")
            settingsDoc.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    selectedLanguage = document.getString("language") ?: "Türkçe"
                    isDarkTheme = document.getBoolean("theme") ?: false
                    isNotificationsEnabled = document.getBoolean("notifications") ?: true

                    onThemeChange(isDarkTheme) // İsteğe bağlı, eğer anında temayı uygula dersen
                }
            }.addOnFailureListener { e ->
                toastManager.showToast("Ayarlar yüklenirken hata oluştu: ${e.localizedMessage}")
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar", color = White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri Dön", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Kullanıcı Bilgileri Kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Hesap Bilgileri", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGray)
                    SettingsItem(icon = Icons.Filled.Person, title = "Ad", value = name)
                    SettingsItem(icon = Icons.Filled.Email, title = "E-posta", value = email)
                }
            }

            // 🔹 Koyu Tema Ayarı
            SettingSwitch(
                icon = if (isDarkTheme) Dark_mode else Light_mode,
                title = "Tema",
                state = isDarkTheme,
                onToggle = { newTheme ->
                    isDarkTheme = newTheme
                    onThemeChange(newTheme)

                    saveSettingsToFirestore(
                        selectedLanguage,
                        isDarkTheme = newTheme,
                        isNotificationsEnabled,
                        toastManager
                    )
                }
            )


            // 🔹 Bildirim Ayarı
            SettingSwitch(
                icon = Icons.Filled.Notifications,
                title = "Bildirimler",
                state = isNotificationsEnabled,
                onToggle = { newValue ->
                    isNotificationsEnabled = newValue

                    saveSettingsToFirestore(
                        selectedLanguage,
                        isDarkTheme,
                        isNotificationsEnabled = newValue,
                        toastManager
                    )
                }
            )


            // 🔹 Dil Seçimi
            SettingDropdown(
                icon = Language,
                title = "Dil Seçimi",
                selectedOption = selectedLanguage,
                options = listOf("Türkçe", "İngilizce", "Almanca"),
                onOptionSelected = { newLanguage ->
                    selectedLanguage = newLanguage

                    saveSettingsToFirestore(
                        selectedLanguage = newLanguage,
                        isDarkTheme,
                        isNotificationsEnabled,
                        toastManager
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            AdBannerView()

        }
    }
    CustomToastHost(toastManager)
}




@Composable
fun SettingsItem(icon: ImageVector, title: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = title, tint = DarkGray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun SettingSwitch(icon: ImageVector, title: String, state: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = DarkGray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Switch(checked = state, onCheckedChange = onToggle)
    }
}

fun saveSettingsToFirestore(
    selectedLanguage: String,
    isDarkTheme: Boolean,
    isNotificationsEnabled: Boolean,
    toastManager: ToastManager
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser

    user?.let {
        val settingsRef = db.collection("users").document(it.uid).collection("settings").document("preferences")

        val updatedSettings = hashMapOf(
            "language" to selectedLanguage,
            "theme" to isDarkTheme,
            "notifications" to isNotificationsEnabled
        )

        settingsRef.set(updatedSettings)
            .addOnSuccessListener {
                toastManager.showToast("Ayarlar Firestore'a kaydedildi!")
            }
            .addOnFailureListener { e ->
                toastManager.showToast("Firestore'a kaydetme hatası: ${e.localizedMessage}")
            }
    } ?: run {
        toastManager.showToast("Kullanıcı oturumu açık değil!")
    }
}



@Composable
fun SettingDropdown(
    icon: ImageVector,
    title: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val colors = MaterialTheme.colorScheme // 🌟 Tema renklerini al

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface), 
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 🔹 İkon
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = colors.onSurface, // 🔥 İkon rengi temaya uygun olacak
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                // 🔹 Başlık
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurface, // 🔥 Başlık rengi temaya uygun
                    modifier = Modifier.weight(1f)
                )

                // 🔹 Seçili Dil
                Text(
                    text = selectedOption,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary // 🔥 Seçili öğe rengi tema ana rengi
                )
            }

            // 🔹 Açılır Menü
            if (expanded) {
                options.forEach { option ->
                    Text(
                        text = option,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOptionSelected(option)
                                expanded = false
                            }
                            .padding(8.dp),
                        color = colors.onSurface // 🔥 Menü metin rengi temaya uygun
                    )
                }
            }
        }
    }
}
