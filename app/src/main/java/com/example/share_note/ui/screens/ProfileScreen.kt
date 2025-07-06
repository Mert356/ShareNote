package com.example.share_note.ui.screens

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.share_note.ui.components.CustomToastHost
import com.example.share_note.ui.components.ToastManager
import com.example.share_note.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController,toastManager: ToastManager) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var department by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        name = document.getString("name") ?: ""
                        surname = document.getString("surname") ?: ""
                        department = document.getString("department") ?: ""
                        university = document.getString("university") ?: ""
                        grade = document.getString("grade") ?: ""
                    }
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Kullanıcı bilgileri alınamadı!", e)
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil", color = White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri Dön", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LightGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${name.firstOrNull()?.uppercase() ?: ""}${surname.firstOrNull()?.uppercase() ?: ""}",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ProfileInfoItem("Ad", name, Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        ProfileInfoItem("Soyad", surname, Modifier.weight(1f))
                    }
                    ProfileInfoItem("E-posta", email, Modifier.fillMaxWidth())
                    ProfileInfoItem("Üniversite", university, Modifier.fillMaxWidth())
                    ProfileInfoItem("Bölüm", department, Modifier.fillMaxWidth())
                    ProfileInfoItem("Sınıf", grade, Modifier.fillMaxWidth())
                }

                Button(
                    onClick = { navController.navigate("editProfile") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Profili Düzenle", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }

                var isLoggingOut by remember { mutableStateOf(false) }
                val buttonColor by animateColorAsState(
                    targetValue = if (isLoggingOut) Color.Gray else Color.Red,
                    animationSpec = tween(durationMillis = 500),
                    label = "Logout Button Animation"
                )

                Button(
                    onClick = {
                        isLoggingOut = true
                        auth.signOut()
                        navController.navigate("login") {
                            popUpTo("profile") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isLoggingOut) "Çıkış Yapılıyor..." else "Çıkış Yap",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
    CustomToastHost(toastManager)
}

@Composable
fun ProfileInfoItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
