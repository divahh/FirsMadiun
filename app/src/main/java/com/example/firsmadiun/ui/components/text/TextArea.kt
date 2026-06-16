package com.example.firsmadiun.ui.components.text

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.example.firsmadiun.ui.theme.*

/**
 * Komponen textarea multiline reusable.
 *
 * @param value Nilai teks saat ini
 * @param onValueChange Callback saat teks berubah
 * @param label Label di atas field
 * @param placeholder Placeholder teks
 * @param minLines Jumlah baris minimal tampil
 * @param maxLines Maksimal baris sebelum scroll
 * @param isError Status error
 * @param errorMessage Pesan error
 * @param modifier Modifier tambahan
 */
@Composable
fun TextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    minLines: Int = 4,
    maxLines: Int = 8,
    isError: Boolean = false,
    errorMessage: String = "",
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> EmergencyRed
            isFocused -> InputBorderFocused
            else -> InputBorder
        },
        animationSpec = tween(200),
        label = "textAreaBorderColor"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (isError) EmergencyRed else TextPrimary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            minLines = minLines,
            maxLines = maxLines,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(InputBackground)
                        .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextHint
                        )
                    }
                    innerTextField()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused }
        )

        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelMedium,
                color = EmergencyRed,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}