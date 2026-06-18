package com.example.firsmadiun.ui.components.text

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.firsmadiun.ui.theme.*

/**
 * Komponen TextField reusable dengan styling DAMKAR.
 *
 * @param value Nilai teks saat ini
 * @param onValueChange Callback saat teks berubah
 * @param label Label di atas field
 * @param placeholder Placeholder teks di dalam field
 * @param visualTransformation Transformasi visual (misal: password)
 * @param trailingIcon Icon di kanan field (opsional)
 * @param keyboardOptions Opsi keyboard
 * @param keyboardActions Aksi keyboard
 * @param isError Status error
 * @param errorMessage Pesan error yang ditampilkan
 * @param modifier Modifier tambahan
 */
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    errorMessage: String = "",
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> EmergencyRed
            isFocused -> InputBorderFocused
            else -> InputBorder
        },
        animationSpec = tween(durationMillis = 200),
        label = "borderColorAnimation"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (isError) EmergencyRed else TextPrimary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Input Field
        BasicTextField(
            value = value,
            readOnly = !isEnabled,
            onValueChange = onValueChange,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(InputBackground)
                        .border(
                            width = 1.5.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextHint
                            )
                        }
                        innerTextField()
                    }
                    trailingIcon?.invoke()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }
        )

        // Error message
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