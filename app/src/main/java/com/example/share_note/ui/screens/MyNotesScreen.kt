package com.example.share_note.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.share_note.ui.components.CustomToastHost
import com.example.share_note.ui.components.Note
import com.example.share_note.ui.components.NoteItem
import com.example.share_note.ui.components.ToastManager
import com.example.share_note.ui.theme.Green
import com.example.share_note.ui.theme.White
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyNotesScreen(navController: NavController,toastManager: ToastManager) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }
    val firestore = FirebaseFirestore.getInstance()

    LaunchedEffect(userId) {
        if (userId != null) {
            firestore.collection("notes")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    val fetchedNotes = documents.map { doc ->
                        @Suppress("UNCHECKED_CAST")
                        Note(
                                id = doc.id,
                                courseName = doc.getString("courseName") ?: "Bilinmeyen Ders",
                                courseCode = doc.getString("courseCode") ?: "",
                                teacherName = doc.getString("teacherName") ?: "Bilinmeyen Öğretmen",
                                topic = doc.getString("topic") ?: "",
                                noteDescription = doc.getString("noteDescription") ?: "",
                                userId = doc.getString("userId") ?: "",
                                university = doc.getString("university") ?: "Bilinmeyen Üniversite",
                                department = doc.getString("department") ?: "Bilinmeyen Bölüm",
                                rating = doc.getDouble("rating") ?: 0.0,
                                votes = doc.getLong("votes")?.toInt() ?: 0,
                                timestamp = doc.getTimestamp("timestamp"),
                                imageUrls = doc.get("imageUrls") as? List<String> ?: emptyList(),
                            files = doc.get("files") as? List<String> ?: emptyList()
                        )
                    }
                    notes = fetchedNotes
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Benim Notlarım", color = White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Green)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (notes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Henüz not eklemediniz.", style = MaterialTheme.typography.headlineSmall)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(0.dp, 8.dp, 0.dp, 0.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(notes) { note ->
                    NoteItem(
                        note = note,
                        onClick = { navController.navigate("noteDetail/${note.id}") },
                        onDelete = { noteToDelete = note }
                    )
                }
            }
        }
    }
    CustomToastHost(toastManager)

    if (noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Notu Sil") },
            text = { Text("Bu notu silmek istediğinizden emin misiniz?") },
            confirmButton = {
                TextButton(onClick = {
                    noteToDelete?.let { deleteNote(it.id) }
                    notes = notes.filter { it.id != noteToDelete?.id }
                    noteToDelete = null
                }) {
                    Text("Evet")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    noteToDelete = null
                }) {
                    Text("Hayır", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }
}

fun deleteNote(noteId: String) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection("notes").document(noteId)
        .delete()
        .addOnSuccessListener {
            println("Not başarıyla silindi!")
        }
        .addOnFailureListener { e ->
            println("Not silinirken hata oluştu: ${e.message}")
        }
}