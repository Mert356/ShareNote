package com.example.share_note.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SelectionCard(
    title: String,
    items: List<String>,
    selectedItem: String?,
    onItemSelected: (String) -> Unit,
    isSearchBarActive: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }

    val filteredItems = if (searchText.isEmpty()) items else items.filter { it.contains(searchText, ignoreCase = true) }

    val colors = MaterialTheme.colorScheme // 🎨 Tema renklerini al

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = colors.surface), // 🌟 Arka planı tema yüzeyine göre ayarla
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 🔹 Başlık
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface // 🖌️ Tema yüzeyine uygun başlık rengi
            )
            Spacer(modifier = Modifier.height(4.dp))

            // 🔹 Seçili öğe veya placeholder
            Text(
                text = selectedItem ?: "Seçiniz",
                fontSize = 16.sp,
                color = colors.onSurface.copy(alpha = 0.7f) // 🔥 Hafif gri tonlu metin
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // 🔹 Arama Çubuğu
                if (isSearchBarActive) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Ara...", color = colors.onSurface.copy(alpha = 0.5f)) }, // 🔥 Placeholder rengi
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = colors.onSurface.copy(alpha = 0.7f) // 🔍 İkon rengi temaya uygun
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary, // 🔥 Odaklanınca ana renk
                            unfocusedBorderColor = colors.outline // 🔥 Normal durumda outline rengi
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 🔹 Liste
                if (filteredItems.isNotEmpty()) {
                    LazyColumn {
                        items(filteredItems) { item ->
                            Text(
                                text = item,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = null
                                    ) {
                                        onItemSelected(item)
                                        expanded = false
                                        searchText = ""
                                    }
                                    .padding(vertical = 6.dp),
                                color = colors.onSurface // 🔥 Liste elemanları için tema uyumlu renk
                            )
                        }
                    }
                }
            }
        }
    }
}
