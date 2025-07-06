package com.example.share_note.ui.screens

import FileEarmarkPdf
import FileEarmarkPpt
import FileEarmarkWord
import Report
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Star
import com.example.share_note.ui.components.Note
import com.example.share_note.ui.theme.Green
import androidx.core.net.toUri
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.window.Dialog
import com.example.share_note.ui.components.CustomToastHost
import com.example.share_note.ui.components.ToastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class NoteDetailViewModel(
    private val noteId: String,
    private val userId: String,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState

    init {
        fetchNoteDetails()
    }

    private fun fetchNoteDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val document = firestore.collection("notes").document(noteId).get().await()

                if (document.exists()) {
                    val data = document.data ?: return@launch

                    val courseName = data["courseName"] as? String ?: ""
                    val courseCode = data["courseCode"] as? String ?: ""
                    val teacherName = data["teacherName"] as? String ?: ""
                    val topic = data["topic"] as? String ?: ""
                    val noteDescription = data["noteDescription"] as? String ?: ""
                    val university = data["university"] as? String ?: ""
                    val department = data["department"] as? String ?: ""
                    val rating = (data["rating"] as? Number)?.toDouble() ?: 0.0
                    val votes = (data["votes"] as? Number)?.toInt() ?: 0
                    val ratedBy = data["ratedBy"] as? Map<String, Number> ?: emptyMap()
                    val userRating = ratedBy[userId]?.toFloat() ?: 0f
                    val ownerId = data["userId"] as? String ?: ""
                    val imageUrls = data["imageUrls"] as? List<String> ?: emptyList()

                    val pdfUrls = (data["pdfUrls"] as? List<Map<String, String>>)?.map {
                        (it["url"] ?: "") to (it["name"] ?: "")
                    } ?: emptyList()

                    val wordUrls = (data["wordUrls"] as? List<Map<String, String>>)?.map {
                        (it["url"] ?: "") to (it["name"] ?: "")
                    } ?: emptyList()

                    val slideUrls = (data["slideUrls"] as? List<Map<String, String>>)?.map {
                        (it["url"] ?: "") to (it["name"] ?: "")
                    } ?: emptyList()

                    val note = Note(
                        id = document.id,
                        courseName = courseName,
                        courseCode = courseCode,
                        teacherName = teacherName,
                        topic = topic,
                        noteDescription = noteDescription,
                        university = university,
                        department = department,
                        userId = ownerId,
                        rating = rating,
                        votes = votes,
                        timestamp = data["timestamp"] as? com.google.firebase.Timestamp,
                        imageUrls = imageUrls,
                        pdfUrls = pdfUrls.map { mapOf("url" to it.first, "name" to it.second) },
                        wordUrls = wordUrls.map { mapOf("url" to it.first, "name" to it.second) },
                        slideUrls = slideUrls.map { mapOf("url" to it.first, "name" to it.second) },
                        ratedBy = ratedBy.mapValues { it.value.toDouble() }
                    )

                    _uiState.value = NoteDetailUiState(
                        note = note,
                        imageUrls = imageUrls,
                        pdfFiles = pdfUrls,
                        wordFiles = wordUrls,
                        slideFiles = slideUrls,
                        avgRating = rating,
                        totalVotes = votes,
                        userRating = userRating,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun updateRating(newRating: Float) {
        val noteRef = firestore.collection("notes").document(noteId)
        viewModelScope.launch {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(noteRef)
                val currentRating = snapshot.getDouble("rating") ?: 0.0
                val currentVotes = snapshot.getLong("votes")?.toInt() ?: 0
                val ratedBy = snapshot.get("ratedBy") as? MutableMap<String, Double> ?: mutableMapOf()

                val previousRating = ratedBy[userId]?.toFloat() ?: 0f

                val newTotalVotes = if (previousRating > 0) currentVotes else currentVotes + 1
                val newAvgRating = if (previousRating > 0) {
                    ((currentRating * currentVotes) - previousRating + newRating) / currentVotes
                } else {
                    ((currentRating * currentVotes) + newRating) / newTotalVotes
                }

                ratedBy[userId] = newRating.toDouble()
                transaction.update(noteRef, "rating", newAvgRating)
                transaction.update(noteRef, "votes", newTotalVotes)
                transaction.update(noteRef, "ratedBy", ratedBy)

                _uiState.value = _uiState.value.copy(
                    avgRating = newAvgRating,
                    totalVotes = newTotalVotes,
                    userRating = newRating
                )
            }.await()
        }
    }
    fun checkIfUserAlreadyReported(
        onAlreadyReported: () -> Unit,
        onNotReported: () -> Unit
    ) {
        val reportRef = firestore
            .collection("reportedNotes")
            .document(noteId)
            .collection("reportsBy")
            .document(userId)

        reportRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onAlreadyReported()
                } else {
                    onNotReported()
                }
            }
            .addOnFailureListener {
                onNotReported()
            }
    }

}

data class NoteDetailUiState(
    val note: Note? = null,
    val imageUrls: List<String> = emptyList(),
    val pdfFiles: List<Pair<String, String>> = emptyList(),
    val wordFiles: List<Pair<String, String>> = emptyList(),
    val slideFiles: List<Pair<String, String>> = emptyList(),
    val avgRating: Double = 0.0,
    val totalVotes: Int = 0,
    val userRating: Float = 0f,
    val isLoading: Boolean = true,
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    navController: NavController,
    noteId: String,
    userId: String,
    toastManager: ToastManager,
    viewModel: NoteDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NoteDetailViewModel(noteId, userId) as T
            }
        }
    )
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val currentUser = FirebaseAuth.getInstance().currentUser
    val isOwner = currentUser?.uid == uiState.note?.userId
    val showReportDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.note?.topic ?: "Yükleniyor...",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (!isOwner && !uiState.isLoading) {
                        IconButton(
                            onClick = {
                                viewModel.checkIfUserAlreadyReported(
                                    onAlreadyReported = {
                                        toastManager.showToast("Bu notu zaten şikayet ettiniz")
                                    },
                                    onNotReported = {
                                        showReportDialog.value = true
                                    }
                                )
                            },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(
                                imageVector = Report,
                                contentDescription = "Notu Şikayet Et",
                                tint = Color.Yellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        uiState.note?.let { note ->
                            Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
                                NoteInfoCard(note)
                                Spacer(modifier = Modifier.height(16.dp))
                                NoteDescriptionCard(note.noteDescription)
                            }
                        }
                    }

                    if (uiState.imageUrls.isNotEmpty()) {
                        item {
                            FileSection(title = "Eklenen Fotoğraflar") {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(uiState.imageUrls, key = { it }) { imageUrl ->
                                        ImagePreviewItem(imageUrl, context)
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.pdfFiles.isNotEmpty()) {
                        item {
                            FileSection(title = "PDF Dosyaları") {
                                Column {
                                    uiState.pdfFiles.forEach { (url, name) ->
                                        FileDownloadItem(url, name, "PDF", context)
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.wordFiles.isNotEmpty()) {
                        item {
                            FileSection(title = "Word Dosyaları") {
                                Column {
                                    uiState.wordFiles.forEach { (url, name) ->
                                        FileDownloadItem(url, name, "Word", context)
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.slideFiles.isNotEmpty()) {
                        item {
                            FileSection(title = "Slayt Dosyaları") {
                                Column {
                                    uiState.slideFiles.forEach { (url, name) ->
                                        FileDownloadItem(url, name, "PPT", context)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        RatingSection(
                            avgRating = uiState.avgRating,
                            totalVotes = uiState.totalVotes,
                            userRating = uiState.userRating,
                            isOwner = isOwner
                        ) { newRating ->
                            viewModel.updateRating(newRating)
                        }
                    }
                }
            }
        }
    }

    CustomToastHost(toastManager)
    if (showReportDialog.value) {
        ReportDialog(
            noteId = noteId,
            onDismiss = { showReportDialog.value = false },
            toastManager = toastManager
        )
    }

}


@Composable
fun ReportDialog(
    noteId: String,
    onDismiss: () -> Unit,
    toastManager: ToastManager
) {
    val reportCategories = listOf(
        "Uygunsuz İçerik",
        "Alakasız Not",
        "Yanıltıcı Bilgi",
        "Spam & Reklam",
        "Telif Hakkı İhlali",
        "Diğer"
    )
    val selectedCategory = remember { mutableStateOf(reportCategories[0]) }
    val description = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Notu Şikayet Et",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                reportCategories.forEach { category ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategory.value = category }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = (category == selectedCategory.value),
                            onClick = { selectedCategory.value = category },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (selectedCategory.value == "Diğer") {
                    OutlinedTextField(
                        value = description.value,
                        onValueChange = { if (it.length <= 50) description.value = it },
                        label = { Text("Açıklama (Opsiyonel, max 50 karakter)") },
                        maxLines = 2,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("İptal")
                    }

                    Button(
                        onClick = {
                            isLoading.value = true
                            reportNote(
                                firestore, noteId, userId,
                                selectedCategory.value, description.value, toastManager,onDismiss
                            )
                        },
                        enabled = !isLoading.value,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isLoading.value) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Gönder")
                        }
                    }
                }
            }
        }
    }
}



fun reportNote(
    firestore: FirebaseFirestore,
    noteId: String,
    userId: String,
    category: String,
    description: String,
    toastManager: ToastManager,
    onDismiss: () -> Unit
) {
    val reportDocRef = firestore.collection("reportedNotes").document(noteId)
    val userReportRef = reportDocRef.collection("reportsBy").document(userId)

    userReportRef.get().addOnSuccessListener { userDoc ->

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(reportDocRef)

            if (!snapshot.exists()) {
                val initialData = hashMapOf(
                    "noteId" to noteId,
                    "totalReports" to 1,
                    "categories" to mapOf(category to 1),
                    "firstReportedAt" to FieldValue.serverTimestamp()
                )
                transaction.set(reportDocRef, initialData)
            } else {
                val currentCategories = snapshot.get("categories") as? Map<String, Long> ?: emptyMap()
                val updatedCategories = currentCategories.toMutableMap()
                updatedCategories[category] = (updatedCategories[category] ?: 0L) + 1

                val currentTotal = snapshot.getLong("totalReports") ?: 0L

                transaction.update(reportDocRef, mapOf(
                    "categories" to updatedCategories,
                    "totalReports" to currentTotal + 1
                ))
            }

            val userReportData = hashMapOf(
                "userId" to userId,
                "category" to category,
                "description" to description,
                "timestamp" to FieldValue.serverTimestamp()
            )
            transaction.set(userReportRef, userReportData)
        }.addOnSuccessListener {
            toastManager.showToast("Şikayet başarıyla gönderildi.")
            onDismiss()
        }.addOnFailureListener {
            toastManager.showToast("Şikayet gönderilirken hata oluştu!")
            onDismiss()
        }
    }.addOnFailureListener {
        toastManager.showToast("Şikayet durumu kontrol edilemedi!")
        onDismiss()
    }
}



@Composable
fun FileDownloadItem(fileUrl: String, fileName: String, fileType: String, context: Context) {
    val (icon, color) = when {
        fileName.endsWith(".pdf", true) -> Pair(FileEarmarkPdf, Color.Red)
        fileName.endsWith(".doc", true) || fileName.endsWith(".docx", true) -> Pair(FileEarmarkWord, Color.Blue)
        fileName.endsWith(".ppt", true) || fileName.endsWith(".pptx", true) -> Pair(FileEarmarkPpt, Color(0xFFFF9800))
        else -> Pair(Icons.Default.Warning, MaterialTheme.colorScheme.primary)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = { openUrl(context, fileUrl) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = "$fileType Aç",
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                ScrollingText(fileName)
                Text(
                    "Tıkla ve Aç",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ImagePreviewItem(imageUrl: String, context: Context) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = { openUrl(context, imageUrl) },
        modifier = Modifier.size(130.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Eklenen Fotoğraf",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun FileSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}


fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}

@Composable
fun RatingSection(
    avgRating: Double,
    totalVotes: Int,
    userRating: Float,
    isOwner: Boolean,
    onRatingChanged: (Float) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Puan: ${"%.1f".format(avgRating)}",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "($totalVotes oy)",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isOwner) {
                Text(
                    text = "Kendi notunu oylayamazsın!",
                    color = Color.Red,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    maxItemsInEachRow = 10
                ) {
                    for (i in 1..10) {
                        IconButton(
                            onClick = { onRatingChanged(i.toFloat()) },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = if (i <= userRating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "$i Yıldız",
                                tint = if (i <= userRating) Color(0xFFFFD700) else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (userRating > 0) {
                    Text(
                        text = "Senin Puanın: ${userRating.toInt()}",
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun NoteInfoCard(note: Note) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoRow("Konu", note.topic)
            InfoRow("Ders Adı", note.courseName)
            InfoRow("Ders Kodu", note.courseCode)
            InfoRow("Öğretmen Adı", note.teacherName)
            InfoRow("Üniversite", note.university)
            InfoRow("Bölüm", note.department)
        }
    }
}

@Composable
fun NoteDescriptionCard(description: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Açıklama: $description",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}
