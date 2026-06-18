package com.example.firsmadiun.ui.screen.feature

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.firsmadiun.ui.components.section.ChangePasswordDialog
import com.example.firsmadiun.ui.components.section.SecurityMenuRow
import com.example.firsmadiun.ui.components.text.TextField
import com.example.firsmadiun.ui.theme.*
import com.example.firsmadiun.viewModel.feature.ProfileViewModel

private val BlueGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1E4DB7), Color(0xFF0F2C70))
)

@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel() // Menggunakan Hilt injection
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Menampilkan toast jika ada pesan sukses/error simpan data
    LaunchedEffect(uiState.message) {
        if (uiState.message.isNotBlank()) {
            Toast.makeText(context, uiState.message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF4F6FA),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(DamkarBlue, Color(0xFF1E4DB7)))
                    )
                    .padding(horizontal = 4.dp)
                    .padding(top = 48.dp, bottom = 20.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Kembali", tint = White)
                }
                Column(
                    modifier = Modifier
                        .padding(start = 56.dp, end = 16.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Text(text = "Profil Saya", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = White)
                    Text(text = "Kelola informasi akun Anda", style = MaterialTheme.typography.bodySmall, color = White.copy(alpha = 0.75f))
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF4F6FA))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.onLogout()
                        onLogoutSuccess()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Icon(imageVector = Icons.Outlined.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Keluar dari Akun", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = DamkarBlue)
            } else {
                val user = uiState.userProfile

                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // === 1. CARD BANNER IDENTITAS UTAMA ===
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFF1E4DB7))
                            ) {
                                Text(
                                    text = user.nama.split(" ").map { it.take(1) }.joinToString("").take(2).uppercase(),
                                    color = White, fontWeight = FontWeight.Bold, fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = user.nama.ifBlank { "User" }, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                Text(text = user.email, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }

                    // === 2. CARD SEKSI FORM INFORMASI PROFIL ===
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Outlined.Person, contentDescription = null, tint = Color(0xFF1E4DB7), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Informasi Profil", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1A202C))
                            }

                            TextField(
                                value = uiState.formNama,
                                onValueChange = viewModel::onNamaChange,
                                label = "Nama Lengkap",
                                placeholder = "Nama Lengkap",
                            )

                            TextField(
                                value = uiState.formNoTelepon,
                                onValueChange = viewModel::onNoTeleponChange,
                                label = "No. Telepon",
                                placeholder = "Nomor Telepon",
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = viewModel::onSimpanPerubahan,
                                enabled = !uiState.isUpdating,
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E4DB7))
                            ) {
                                if (uiState.isUpdating) {
                                    CircularProgressIndicator(color = White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(imageVector = Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Simpan Perubahan", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = White)
                                }
                            }
                        }
                    }

                    // === 3. CARD SEKSI KEAMANAN AKUN ===
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Outlined.Security, contentDescription = null, tint = Color(0xFF1E4DB7), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Keamanan", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1A202C))
                            }

                            SecurityMenuRow(
                                icon = Icons.Outlined.Key, badgeColor = Color(0xFF1E4DB7),
                                deskripsi = "Perbarui kata sandi akun Anda",
                                onClick = { viewModel.togglePasswordDialog(true) },
                                judul = "Ubah Kata Sandi",
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (uiState.isDialogPasswordVisible) {
        ChangePasswordDialog(
            onDismiss = { viewModel.togglePasswordDialog(false) },
            onConfirm = viewModel::onSimpanPassword,
            onPasswordLamaChange = viewModel::onPasswordLamaChange,
            onPasswordBaruChange = viewModel::onPasswordBaruChange,
            onKonfirmasiChange = viewModel::onKonfirmasiPasswordBaruChange,
            uiState = uiState
        )
    }
}

@Composable
private fun ProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold), color = Color(0xFF4A5568))
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1E4DB7),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = if (enabled) Color(0xFFF8FAFC) else Color(0xFFEDF2F7)
            ),
            singleLine = true
        )
    }
}
