package com.example.firsmadiun.ui.screen.auth.login

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
import com.example.firsmadiun.viewModel.auth.LoginUiState
import com.example.firsmadiun.viewModel.auth.LoginViewModel

/**
 * Screen halaman Login DAMKAR.
 *
 * @param onLoginSuccess Callback dipanggil saat login berhasil
 * @param viewModel ViewModel yang mengelola state login
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    viewModel: LoginViewModel = viewModel(),
    onSignUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigasi setelah login sukses
    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onLoginSuccess()
        }
    }

    LoginContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::onLoginClick,
        onSignUp = onSignUp
    )
}

/**
 * Konten visual dari halaman Login (stateless).
 * Dipisahkan agar mudah di-preview dan di-test.
 */
@Composable
fun LoginContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignUp: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Animasi masuk
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundLight,
                        Color(0xFFEEF2FF)
                    )
                )
            )
    ) {
        // Dekorasi background atas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DamkarBlue.copy(alpha = 0.08f),
                            Color.Transparent
                        )
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
            Spacer(modifier = Modifier.height(60.dp))

            // === Logo Section ===
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(
                    tween(600),
                    initialOffsetY = { -40 }
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(100.dp))

                    Logo(size = 100.dp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "FIRS MADIUN",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 3.sp
                        ),
                        color = DamkarBlue
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "SIAGA 24 JAM",
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = DamkarAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // === Subtitle ===
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700, delayMillis = 100))
            ) {
                Text(
                    text = "Masuk untuk melapor & memantau kejadian darurat.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // === Card Form ===
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700, delayMillis = 200)) + slideInVertically(
                    tween(600, delayMillis = 200),
                    initialOffsetY = { 60 }
                )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp,
                        hoveredElevation = 8.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Field Nama / NIP
                        TextField(
                            value = uiState.email,
                            onValueChange = onEmailChange,
                            label = "Email",
                            placeholder = "Masukkan email",
                            isError = uiState.namaError.isNotEmpty(),
                            errorMessage = uiState.namaError
                        )

                        // Field Kata Sandi
                        PasswordField(
                            value = uiState.password,
                            onValueChange = onPasswordChange,
                            isError = uiState.passwordError.isNotEmpty(),
                            errorMessage = uiState.passwordError
                        )

                        // Error login umum
                        AnimatedVisibility(
                            visible = uiState.loginError.isNotEmpty(),
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
                                    text = uiState.loginError,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EmergencyRed
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Tombol Masuk
                        PrimaryButton(
                            text = "Masuk",
                            onClick = onLoginClick,
                            isLoading = uiState.isLoading,
                            enabled = !uiState.isLoading
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val context = LocalContext.current

                // === Sign Up ===
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(700, delayMillis = 400))
                ) {
                    val registerText = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = TextSecondary)) {
                            append("Belum punya akun? ")
                        }
                        pushStringAnnotation(tag = "SIGN_UP", annotation = "signup")
                        withStyle(style = SpanStyle(
                            color = DamkarBlue,
                            fontWeight = FontWeight.Bold
                        )
                        ) {
                            append("Daftar")
                        }
                        pop()
                    }

                    ClickableText(
                        text = registerText,
                        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                        onClick = { offset ->
                            registerText.getStringAnnotations(tag = "SIGN_UP", start = offset, end = offset)
                                .firstOrNull()?.let {
                                    onSignUp() // Memicu callback navigasi daftar
                                }
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // === Emergency Call ===
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(700, delayMillis = 400))
                ) {
                    val emergencyText = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = TextSecondary)) {
                            append("Darurat? Hubungi ")
                        }
                        pushStringAnnotation(tag = "CALL", annotation = "tel:113")
                        withStyle(style = SpanStyle(color = DamkarBlue, fontWeight = FontWeight.Bold)) {
                            append("113")
                        }
                        pop()
                    }

                    ClickableText(
                        text = emergencyText,
                        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                        onClick = { offset ->
                            emergencyText.getStringAnnotations(tag = "CALL", start = offset, end = offset)
                                .firstOrNull()?.let {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:113")
                                    }
                                    context.startActivity(intent)
                                }
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ============================================================
// PREVIEW
// ============================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    DamkarTheme {
        LoginContent(
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onSignUp = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Login - Loading State")
@Composable
fun LoginScreenLoadingPreview() {
    DamkarTheme {
        LoginContent(
            uiState = LoginUiState(
                email = "Budi Santoso",
                password = "password123",
                isLoading = true
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onSignUp = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Login - Error State")
@Composable
fun LoginScreenErrorPreview() {
    DamkarTheme {
        LoginContent(
            uiState = LoginUiState(
                email = "Budi",
                password = "123",
                passwordError = "Kata sandi minimal 6 karakter",
                loginError = "Nama/NIP atau kata sandi salah. Silakan coba lagi."
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onSignUp = {}
        )
    }
}