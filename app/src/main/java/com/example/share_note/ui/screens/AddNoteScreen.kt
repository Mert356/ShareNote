package com.example.share_note.ui.screens

import Add_photo_alternate
import FileEarmarkPdf
import FileEarmarkPpt
import FileEarmarkWord
import FileSearch
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.example.share_note.ui.components.CustomToastHost
import com.example.share_note.ui.components.ToastManager
import com.example.share_note.ui.theme.Green
import com.example.share_note.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(navController: NavController,toastManager: ToastManager) {
    val context = LocalContext.current

    var courseName by remember { mutableStateOf("") }
    var courseCode by remember { mutableStateOf("") }
    var teacherName by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var noteDescription by remember { mutableStateOf("") }

    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var pdfUris by remember { mutableStateOf<List<Pair<Uri, String>>>(emptyList()) }
    var wordUris by remember { mutableStateOf<List<Pair<Uri, String>>>(emptyList()) }
    var slideUris by remember { mutableStateOf<List<Pair<Uri, String>>>(emptyList()) }

    var isLoading by remember { mutableStateOf(false) }

    var courseNameError by remember { mutableStateOf<String?>(null) }
    var teacherNameError by remember { mutableStateOf<String?>(null) }
    var topicError by remember { mutableStateOf<String?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { imageUris = imageUris + it }
    }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        uris?.forEach { uri ->
            val fileName = getFileName(context, uri)
            val fileSize = getFileSize(context, uri)

            if (fileSize > 5 * 1024 * 1024) {
                toastManager.showToast("Dosya boyutu 5mb den büyük olamaz!")
                return@forEach
            }

            when {
                fileName.endsWith(".pdf", ignoreCase = true) -> pdfUris = pdfUris + Pair(uri, fileName)
                fileName.endsWith(".doc", ignoreCase = true) || fileName.endsWith(".docx", ignoreCase = true) -> wordUris = wordUris + Pair(uri, fileName)
                fileName.endsWith(".ppt", ignoreCase = true) || fileName.endsWith(".pptx", ignoreCase = true) -> slideUris = slideUris + Pair(uri, fileName)
                else -> toastManager.showToast("Desteklenmeyen dosya formatı!")
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Not Ekle", style = MaterialTheme.typography.titleLarge, color = White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = courseName,
                onValueChange = {
                    courseName = it
                    courseNameError = if (it.isBlank()) "Bu alan zorunludur" else null
                },
                label = { Text("Ders Adı *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = courseNameError != null
            )
            courseNameError?.let { Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall) }

            OutlinedTextField(
                value = courseCode,
                onValueChange = { courseCode = it },
                label = { Text("Ders Kodu (Opsiyonel)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = teacherName,
                onValueChange = {
                    teacherName = it
                    teacherNameError = if (it.isBlank()) "Bu alan zorunludur" else null
                },
                label = { Text("Öğretmen Adı *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = teacherNameError != null
            )
            teacherNameError?.let { Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall) }

            OutlinedTextField(
                value = topic,
                onValueChange = {
                    topic = it
                    topicError = if (it.isBlank()) "Bu alan zorunludur" else null
                },
                label = { Text("Konu *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = topicError != null
            )
            topicError?.let { Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall) }

            OutlinedTextField(
                value = noteDescription,
                onValueChange = { noteDescription = it },
                label = { Text("Eklenen Not Açıklaması (Opsiyonel)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ElevatedButton(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Add_photo_alternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Fotoğraf Ekle")
                }

                ElevatedButton(
                    onClick = {
                        filePicker.launch(arrayOf(
                            "application/pdf",
                            "application/msword",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                            "application/vnd.ms-powerpoint",
                            "application/vnd.openxmlformats-officedocument.presentationml.presentation"
                        ))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(FileSearch, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dosya Seç")
                }
            }
            Button(
                onClick = {
                    courseNameError = if (courseName.isBlank()) "Bu alan zorunludur" else null
                    teacherNameError = if (teacherName.isBlank()) "Bu alan zorunludur" else null
                    topicError = if (topic.isBlank()) "Bu alan zorunludur" else null

                    if (courseNameError != null || teacherNameError != null || topicError != null) {
                        toastManager.showToast("Lütfen zorunlu alanları doldurun!")
                        return@Button
                    }

                    isLoading = true
                    uploadNote(
                        courseName, courseCode, teacherName, topic, noteDescription,
                        imageUris, pdfUris, wordUris, slideUris
                    ) {
                        isLoading = false
                        toastManager.showToast("Not başarıyla eklendi!")
                        navController.popBackStack()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) CircularProgressIndicator() else Text("Kaydet")
            }


            if (imageUris.isNotEmpty()) {
                Text("Eklenen Fotoğraflar", style = MaterialTheme.typography.titleMedium)

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(imageUris.size) { index ->
                        val uri = imageUris[index]
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .size(130.dp)
                                .padding(4.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Eklenen Fotoğraf",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .background(MaterialTheme.colorScheme.error, shape = RoundedCornerShape(50))
                                ) {
                                    IconButton(
                                        onClick = { imageUris = imageUris.toMutableList().apply { remove(uri) } },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Close,
                                            contentDescription = "Sil",
                                            tint = MaterialTheme.colorScheme.onError,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            PreviewFileItems("Eklenen PDF'ler", pdfUris) { uri ->
                pdfUris = pdfUris - uri
            }
            PreviewFileItems("Eklenen Word Dosyaları", wordUris) { uri ->
                wordUris = wordUris - uri
            }
            PreviewFileItems("Eklenen Slayt Dosyaları", slideUris) { uri ->
                slideUris = slideUris - uri
            }
        }
        CustomToastHost(toastManager = toastManager)

    }
}

@Composable
fun ScrollingText(text: String) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .width(240.dp)
            .horizontalScroll(scrollState)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PreviewFileItems(
    title: String,
    fileUris: List<Pair<Uri, String>>,
    onRemove: (Pair<Uri, String>) -> Unit
) {
    if (fileUris.isNotEmpty()) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            fileUris.forEach { (uri, name) ->

                val (icon, color) = when {
                    name.endsWith(".pdf", true) -> Pair(FileEarmarkPdf, Color.Red)
                    name.endsWith(".doc", true) || name.endsWith(".docx", true) -> Pair(FileEarmarkWord, Color.Blue)
                    name.endsWith(".ppt", true) || name.endsWith(".pptx", true) -> Pair(FileEarmarkPpt, Color(0xFFFF9800))
                    else -> Pair(Icons.Default.Warning, MaterialTheme.colorScheme.primary)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            ScrollingText(name)
                        }
                        IconButton(onClick = { onRemove(uri to name) }) {
                            Icon(Icons.Filled.Close, contentDescription = "Sil")
                        }
                    }
                }
            }
        }
    }
}


fun getFileSize(context: Context, uri: Uri): Long {
    var fileSize: Long = -1
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1) {
                fileSize = it.getLong(sizeIndex)
            }
        }
    }
    return fileSize
}



fun getFileName(context: Context, uri: Uri): String {
    var fileName = "Bilinmeyen Dosya"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                fileName = it.getString(nameIndex)
            }
        }
    }
    return fileName
}


fun uploadNote(
    courseName: String,
    courseCode: String,
    teacherName: String,
    topic: String,
    noteDescription: String,
    imageUris: List<Uri>,
    pdfUris: List<Pair<Uri, String>>,
    wordUris: List<Pair<Uri, String>>,
    slideUris: List<Pair<Uri, String>>,
    onComplete: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val user = auth.currentUser ?: return

    val userRef = db.collection("users").document(user.uid)

    userRef.get().addOnSuccessListener { document ->
        if (document.exists()) {
            val university = document.getString("university") ?: "Bilinmeyen Üniversite"
            val department = document.getString("department") ?: "Bilinmeyen Bölüm"

            val noteId = UUID.randomUUID().toString()
            val noteRef = db.collection("notes").document(noteId)

            val imageUrls = mutableListOf<String>()
            val pdfUrls = mutableListOf<Pair<String, String>>()
            val wordUrls = mutableListOf<Pair<String, String>>()
            val slideUrls = mutableListOf<Pair<String, String>>()

            val noteData = hashMapOf(
                "courseName" to courseName,
                "courseCode" to courseCode,
                "teacherName" to teacherName,
                "topic" to topic,
                "noteDescription" to noteDescription,
                "userId" to user.uid,
                "university" to university,
                "department" to department,
                "timestamp" to com.google.firebase.Timestamp.now(),
                "rating" to 0.0,
                "votes" to 0,
                "ratedBy" to emptyMap<String, Double>(),
                "imageUrls" to imageUrls,
                "pdfUrls" to pdfUrls.map { mapOf("url" to it.first, "name" to it.second) },
                "wordUrls" to wordUrls.map { mapOf("url" to it.first, "name" to it.second) },
                "slideUrls" to slideUrls.map { mapOf("url" to it.first, "name" to it.second) }
            )

            val totalUploads = imageUris.size + pdfUris.size + wordUris.size + slideUris.size
            var completedUploads = 0

            fun checkAndSaveNote() {
                if (completedUploads == totalUploads) {
                    noteRef.set(noteData)
                        .addOnSuccessListener {
                            println("Not başarıyla Firestore'a eklendi!")
                            onComplete()
                        }
                        .addOnFailureListener { e ->
                            println("Firestore hata: ${e.message}")
                        }
                }
            }

            fun uploadFile(uri: Uri, path: String, fileName: String, onSuccess: (Pair<String, String>) -> Unit) {
                val ref = storage.reference.child(path)
                ref.putFile(uri)
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { throw it }
                        }
                        ref.downloadUrl
                    }
                    .addOnSuccessListener { downloadUrl ->
                        onSuccess(downloadUrl.toString() to fileName)
                        completedUploads++
                        checkAndSaveNote()
                    }
                    .addOnFailureListener { e ->
                        println("Dosya yükleme hatası: ${e.message}")
                        completedUploads++
                        checkAndSaveNote()
                    }
            }

            imageUris.forEachIndexed { index, uri ->
                uploadFile(uri, "notes/$noteId/image_$index.jpg", "image_$index.jpg") { url ->
                    imageUrls.add(url.first)
                }
            }

            pdfUris.forEachIndexed { index, (uri, name) ->
                uploadFile(uri, "notes/$noteId/document_$index.pdf", name) { url ->
                    pdfUrls.add(url)
                    noteData["pdfUrls"] = pdfUrls.map { mapOf("url" to it.first, "name" to it.second) }
                }
            }

            wordUris.forEachIndexed { index, (uri, name) ->
                uploadFile(uri, "notes/$noteId/word_$index.doc", name) { url ->
                    wordUrls.add(url)
                    noteData["wordUrls"] = wordUrls.map { mapOf("url" to it.first, "name" to it.second) }
                }
            }

            slideUris.forEachIndexed { index, (uri, name) ->
                uploadFile(uri, "notes/$noteId/slide_$index.ppt", name) { url ->
                    slideUrls.add(url)
                    noteData["slideUrls"] = slideUrls.map { mapOf("url" to it.first, "name" to it.second) }
                }
            }

            if (totalUploads == 0) {
                noteRef.set(noteData)
                    .addOnSuccessListener { onComplete() }
                    .addOnFailureListener { e -> println("Firestore hata: ${e.message}") }
            }
        } else {
            println("Kullanıcı bilgileri bulunamadı!")
        }
    }.addOnFailureListener { e ->
        println("Kullanıcı bilgileri alınırken hata oluştu: ${e.message}")
    }
}

