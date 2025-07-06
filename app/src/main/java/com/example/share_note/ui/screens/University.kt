package com.example.share_note.ui.screens

import android.content.Context
import org.json.JSONObject
import java.io.InputStream

data class University(val name: String, val departments: List<String>)

fun loadUniversities(context: Context): Map<String, List<Pair<String, Int>>> {
    val inputStream: InputStream = context.assets.open("universities2.json")
    val jsonString = inputStream.bufferedReader().use { it.readText() }

    val jsonObject = JSONObject(jsonString)
    val universitiesMap = mutableMapOf<String, List<Pair<String, Int>>>()

    jsonObject.keys().forEach { university ->
        val departmentObject = jsonObject.getJSONObject(university)
        val departmentList = mutableListOf<Pair<String, Int>>()

        departmentObject.keys().forEach { department ->
            val duration = departmentObject.getInt(department)
            departmentList.add(Pair(department, duration))
        }

        universitiesMap[university] = departmentList
    }

    return universitiesMap
}


