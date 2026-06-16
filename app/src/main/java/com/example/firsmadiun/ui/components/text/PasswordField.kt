package com.example.firsmadiun.ui.components.text

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import com.example.firsmadiun.ui.theme.DamkarBlue
import com.example.firsmadiun.ui.theme.TextHint
import com.example.firsmadiun.ui.theme.*

/**
 * Komponen Password TextField reusable.
 * Meng-extend DamkarTextField dengan fitur show/hide password.
 *
 * @param value Nilai password saat ini
 * @param onValueChange Callback saat password berubah
 * @param label Label field (default: "Kata sandi")
 * @param placeholder Placeholder teks
 * @param isError Status error
 * @param errorMessage Pesan error
 * @param modifier Modifier tambahan
 */
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Kata sandi",
    placeholder: String = "Masukkan kata sandi",
    isError: Boolean = false,
    errorMessage: String = "",
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        visualTransformation = if (passwordVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff,
                    contentDescription = if (passwordVisible)
                        "Sembunyikan password"
                    else
                        "Tampilkan password",
                    tint = if (passwordVisible) DamkarBlue else TextHint
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = isError,
        errorMessage = errorMessage,
        modifier = modifier
    )
}