package com.example.share_note.ui.components

import com.google.firebase.Timestamp

data class Note(
    val id: String = "",
    val courseName: String = "", // Ders Adı
    val courseCode: String = "", // Ders Kodu
    val teacherName: String = "", // Öğretmen Adı
    val topic: String = "", // Konu
    val noteDescription: String = "", // Not Açıklaması
    val university: String = "",
    val department: String = "",
    val userId: String = "",
    val rating: Double = 0.0,
    val votes: Int = 0,
    val timestamp: Timestamp? = null,
    val files: List<String>? = null,
    val ratedBy: Map<String, Double> = emptyMap(),
    val slideUrls: List<Map<String, String>> = emptyList(),
    val pdfUrls: List<Map<String, String>> = emptyList(),
    val imageUrls: List<String>? = null,
    val wordUrls: List<Map<String, String>> = emptyList()
)
