package com.example.firsmadiun.ui.components.section

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.firsmadiun.ui.components.text.PasswordField
import com.example.firsmadiun.ui.theme.DamkarBlue
import com.example.firsmadiun.ui.theme.DividerColor
import com.example.firsmadiun.ui.theme.TextPrimary
import com.example.firsmadiun.ui.theme.TextSecondary
import com.example.firsmadiun.ui.theme.White
import com.example.firsmadiun.viewModel.feature.ProfileUiState

@Composable
fun ChangePasswordDialog(
    uiState: ProfileUiState,
    onPasswordLamaChange: (String) -> Unit,
    onPasswordBaruChange: (String) -> Unit,
    onKonfirmasiChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Dialog
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Ubah Kata Sandi", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                        Text(text = "Masukkan kata sandi lama dan kata sandi baru Anda.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Outlined.Close, contentDescription = "Tutup", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }
                }

                HorizontalDivider(color = DividerColor)

                // Field kata sandi saat ini
                PasswordField(
                    value = uiState.passwordLama,
                    onValueChange = onPasswordLamaChange,
                    label = "Kata Sandi Saat Ini",
                    placeholder = "Masukkan kata sandi lama",
                    isError = uiState.passwordLamaError.isNotEmpty(),
                    errorMessage = uiState.passwordLamaError
                )

                // Field kata sandi baru
                PasswordField(
                    value = uiState.passwordBaru,
                    onValueChange = onPasswordBaruChange,
                    label = "Kata Sandi Baru",
                    placeholder = "Minimal 6 karakter",
                    isError = uiState.passwordBaruError.isNotEmpty(),
                    errorMessage = uiState.passwordBaruError
                )

                // Field konfirmasi kata sandi baru
                PasswordField(
                    value = uiState.konfirmasiPasswordBaru,
                    onValueChange = onKonfirmasiChange,
                    label = "Konfirmasi Kata Sandi Baru",
                    placeholder = "Ulangi kata sandi baru",
                    isError = uiState.konfirmasiError.isNotEmpty(),
                    errorMessage = uiState.konfirmasiError
                )

                // Tombol Aksi Batal & Simpan
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, DividerColor
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Text(text = "Batal", style = MaterialTheme.typography.labelLarge)
                    }
                    Button(
                        onClick = onConfirm,
                        enabled = !uiState.isUpdatingPassword,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DamkarBlue
                        )
                    ) {
                        if (uiState.isUpdatingPassword) {
                            CircularProgressIndicator(
                                color = White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = "Simpan Kata Sandi",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogPasswordField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1A202C))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1E4DB7),
                unfocusedBorderColor = Color(0xFFCBD5E0)
            ),
            singleLine = true
        )
    }
}