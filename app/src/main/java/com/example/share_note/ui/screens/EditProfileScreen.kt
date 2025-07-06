package com.example.share_note.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.share_note.ui.components.CustomTextField
import com.example.share_note.ui.components.CustomToastHost
import com.example.share_note.ui.components.SelectionCard
import com.example.share_note.ui.components.ToastManager
import com.example.share_note.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController, context: android.content.Context,toastManager: ToastManager) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var university by remember { mutableStateOf<String?>(null) }
    var department by remember { mutableStateOf<Pair<String, Int>?>(null) }
    var grade by remember { mutableStateOf<String?>(null) }

    var universitiesList by remember { mutableStateOf<Map<String, List<Pair<String, Int>>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        universitiesList = loadUniversities(context)

        currentUser?.let { user ->
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        name = document.getString("name") ?: ""
                        surname = document.getString("surname") ?: ""
                        university = document.getString("university")
                        val departmentName = document.getString("department")
                        grade = document.getString("grade")

                        university?.let { uni ->
                            department = universitiesList[uni]?.firstOrNull { it.first == departmentName }
                        }
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
                title = { Text("Profili Düzenle", color = White) },
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LightGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(value = name, onValueChange = { name = it }, label = "Ad")
                CustomTextField(value = surname, onValueChange = { surname = it }, label = "Soyad")

                SelectionCard(
                    title = "Üniversite Seçiniz",
                    items = universitiesList.keys.toList(),
                    selectedItem = university,
                    onItemSelected = { selectedUniversity ->
                        university = selectedUniversity
                        department = null
                        grade = null
                    }
                )

                university?.let { selectedUniversity ->
                    universitiesList[selectedUniversity]?.let { departments ->
                        SelectionCard(
                            title = "Bölüm Seçiniz",
                            items = departments.map { it.first },
                            selectedItem = department?.first,
                            onItemSelected = { selectedDepartment ->
                                department = departments.first { it.first == selectedDepartment.split(" (")[0] }
                                grade = null
                            }
                        )
                    }
                }

                department?.let { selectedDepartment ->
                    val totalYears = selectedDepartment.second
                    val gradeOptions = mutableListOf("Hazırlık")
                    for (year in 1..totalYears) {
                        gradeOptions.add("$year. Sınıf")
                    }

                    SelectionCard(
                        title = "Sınıf Seçiniz",
                        items = gradeOptions,
                        selectedItem = grade,
                        onItemSelected = { selectedGrade ->
                            grade = selectedGrade
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("İptal", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            updateUserProfile(
                                navController,
                                name, surname, university, department?.first, grade, context,toastManager
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Kaydet", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
    CustomToastHost(toastManager)
}

fun updateUserProfile(
    navController: NavController,
    name: String,
    surname: String,
    university: String?,
    department: String?,
    grade: String?,
    context: android.content.Context,
    toastManager: ToastManager
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser

    user?.let {
        val userDocRef = db.collection("users").document(user.uid)
        val updatedUserData = hashMapOf(
            "name" to name,
            "surname" to surname,
            "university" to university,
            "department" to department,
            "grade" to grade
        )

        userDocRef.update(updatedUserData as Map<String, Any?>)
            .addOnSuccessListener {
                toastManager.showToast("Profil başarıyla güncellendi!")
                navController.popBackStack()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Profil güncellenirken hata oluştu!", e)
                toastManager.showToast("Güncelleme başarısız!")
            }
    }
}
