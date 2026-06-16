package com.example.firsmadiun.ui.screen.auth.signup

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firsmadiun.ui.components.button.PrimaryButton
import com.example.firsmadiun.ui.components.img.Logo
import com.example.firsmadiun.ui.components.text.PasswordField
import com.example.firsmadiun.ui.components.text.TextField
import com.example.firsmadiun.ui.theme.*
import com.example.firsmadiun.viewModel.auth.RegisterUiState
import com.example.firsmadiun.viewModel.auth.RegisterViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onLogin: () -> Unit = {},
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigasi ke home setelah daftar sukses
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onRegisterSuccess()
    }

    RegisterContent(
        uiState = uiState,
        onNama = viewModel::onNama,
        onEmail = viewModel::onEmail,
        onNoTelepon = viewModel::onNoTelepon,
        onPassword = viewModel::onPassword,
        onKonfirmasiPassword = viewModel::onKonfirmasiPassword,
        onDaftar = viewModel::onDaftar,
        onLogin = onLogin
    )
}

@Composable
fun RegisterContent(
    uiState: RegisterUiState,
    onNama: (String) -> Unit,
    onEmail: (String) -> Unit,
    onNoTelepon: (String) -> Unit,
    onPassword: (String) -> Unit,
    onKonfirmasiPassword: (String) -> Unit,
    onDaftar: () -> Unit,
    onLogin: () -> Unit
) {
    val scrollState = rememberScrollState()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundLight, Color(0xFFEEF2FF))
                )
            )
    ) {
        // Dekorasi background atas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DamkarBlue.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            // === Logo + Judul ===
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -40 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Logo(size = 90.dp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Buat Akun",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = DamkarBlue
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "FIRS MADIUN",
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 3.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = DamkarAccent
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Daftar untuk mulai melaporkan kejadian darurat.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // === Card Form ===
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700, delayMillis = 200)) +
                        slideInVertically(tween(600, delayMillis = 200)) { 60 }
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Nama Lengkap
                        TextField(
                            value = uiState.nama,
                            onValueChange = onNama,
                            label = "Nama Lengkap",
                            placeholder = "Nama",
                            isError = uiState.namaError.isNotEmpty(),
                            errorMessage = uiState.namaError
                        )

                        // Email
                        TextField(
                            value = uiState.email,
                            onValueChange = onEmail,
                            label = "Email",
                            placeholder = "nama@email.com",
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            isError = uiState.emailError.isNotEmpty(),
                            errorMessage = uiState.emailError
                        )

                        // No. Telepon
                        TextField(
                            value = uiState.noTelepon,
                            onValueChange = onNoTelepon,
                            label = "No. Telepon",
                            placeholder = "08xxxxxxxxxx",
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone
                            ),
                            isError = uiState.teleponError.isNotEmpty(),
                            errorMessage = uiState.teleponError
                        )

                        // Kata Sandi
                        PasswordField(
                            value = uiState.password,
                            onValueChange = onPassword,
                            label = "Kata sandi",
                            placeholder = "Minimal 6 karakter",
                            isError = uiState.passwordError.isNotEmpty(),
                            errorMessage = uiState.passwordError
                        )

                        // Konfirmasi Kata Sandi
                        PasswordField(
                            value = uiState.konfirmasiPassword,
                            onValueChange = onKonfirmasiPassword,
                            label = "Konfirmasi kata sandi",
                            placeholder = "Ulangi kata sandi",
                            isError = uiState.konfirmasiError.isNotEmpty(),
                            errorMessage = uiState.konfirmasiError
                        )

                        // Error umum
                        AnimatedVisibility(
                            visible = uiState.registerError.isNotEmpty(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EmergencyRed.copy(alpha = 0.08f))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = uiState.registerError,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EmergencyRed
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Tombol Daftar
                        PrimaryButton(
                            text = "Daftar",
                            onClick = onDaftar,
                            isLoading = uiState.isLoading,
                            enabled = !uiState.isLoading
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // === Link ke Login ===
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700, delayMillis = 400))
            ) {
                val loginText = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextSecondary)) {
                        append("Sudah punya akun? ")
                    }
                    pushStringAnnotation(tag = "LOGIN", annotation = "login")
                    withStyle(SpanStyle(color = DamkarBlue, fontWeight = FontWeight.Bold)) {
                        append("Masuk")
                    }
                    pop()
                }

                ClickableText(
                    text = loginText,
                    style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                    onClick = { offset ->
                        loginText.getStringAnnotations(tag = "LOGIN", start = offset, end = offset)
                            .firstOrNull()?.let {
                                onLogin()
                            }
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// ============================================================
// PREVIEW
// ============================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    DamkarTheme {
        RegisterContent(
            uiState = RegisterUiState(),
            onNama = {},
            onEmail = {},
            onNoTelepon = {},
            onPassword = {},
            onKonfirmasiPassword = {},
            onDaftar = {},
            onLogin = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Register Error")
@Composable
fun RegisterScreenErrorPreview() {
    DamkarTheme {
        RegisterContent(
            uiState = RegisterUiState(
                nama = "Budi",
                email = "budi@email.com",
                konfirmasiError = "Kata sandi tidak cocok",
                registerError = "Email sudah terdaftar. Gunakan email lain."
            ),
            onNama = {},
            onEmail = {},
            onNoTelepon = {},
            onPassword = {},
            onKonfirmasiPassword = {},
            onDaftar = {},
            onLogin = {}
        )
    }
}