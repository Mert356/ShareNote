package com.example.share_note.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.share_note.ui.components.CustomTextField
import com.example.share_note.ui.components.CustomToastHost
import com.example.share_note.ui.components.SelectionCard
import com.example.share_note.ui.components.ToastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CompleteProfileScreen(navController: NavController, context: Context,toastManager: ToastManager) {
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var university by remember { mutableStateOf<String?>(null) }
    var department by remember { mutableStateOf<Pair<String, Int>?>(null) }
    var grade by remember { mutableStateOf<String?>(null) }

    var universitiesList by remember { mutableStateOf<Map<String, List<Pair<String, Int>>>>(emptyMap()) }

    LaunchedEffect(Unit) {
        universitiesList = loadUniversities(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profilini Tamamla", fontSize = 24.sp, fontWeight = FontWeight.Bold)

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

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (name.isBlank() || surname.isBlank() || university == null || department == null || grade.isNullOrBlank()) {
                toastManager.showToast("Lütfen tüm alanları doldurun!")
                return@Button
            }
            user?.let {
                val userProfile = hashMapOf(
                    "name" to name,
                    "surname" to surname,
                    "university" to university,
                    "department" to department?.first,
                    "grade" to grade,
                    "email" to user.email
                )

                db.collection("users").document(user.uid).set(userProfile)
                    .addOnSuccessListener {
                        toastManager.showToast("Profil Kaydedildi!")
                        navController.navigate("home") {
                            popUpTo("completeProfile") { inclusive = true }
                        }
                    }
                    .addOnFailureListener {
                        toastManager.showToast("Bir hata oluştu!")
                    }
            }
        }) {
            Text("Kaydet")
        }
    }
    CustomToastHost(toastManager)
}
