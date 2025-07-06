package com.example.share_note.ui.components

import Eye
import EyeOff
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Enter text",
    placeholder: String = "Type here...",
    isError: Boolean = false,
    isPassword: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val colors = MaterialTheme.colorScheme // 🌟 Tema renklerini al

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> colors.error // 🔥 Hata rengi
            isFocused -> colors.primary // 🔥 Odaklanınca ana renk
            else -> colors.outline // 🔥 Normal durumda outline rengi
        },
        label = "Border Color Animation"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .onFocusChanged { isFocused = it.isFocused },
        label = {
            Text(
                text = label,
                color = if (isError) colors.error else colors.onSurface
            )
        },
        placeholder = { Text(placeholder, color = colors.onSurface.copy(alpha = 0.5f)) }, // 🌟 Placeholder rengi temaya uyumlu
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) EyeOff else Eye,
                        contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                        tint = when {
                            isError -> colors.error
                            isFocused -> colors.primary
                            else -> colors.onSurface.copy(alpha = 0.7f) // 🔥 Hafif gri tonu
                        }
                    )
                }
            }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.primary, // 🔥 Odaklanınca ana renk
            unfocusedBorderColor = colors.outline, // 🔥 Normal durumda outline rengi
            cursorColor = colors.primary, // 🔥 Cursor rengi
            errorBorderColor = colors.error // 🔥 Hata rengi
        ),
        shape = RoundedCornerShape(12.dp)
    )
}
