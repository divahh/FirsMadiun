package com.example.firsmadiun.ui.screen.feature

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firsmadiun.ui.components.img.Logo
import com.example.firsmadiun.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash Screen DAMKAR.
 * Menampilkan logo, nama, dan tagline dengan animasi,
 * lalu otomatis navigasi ke halaman Login setelah 2.5 detik.
 *
 * @param onSplashFinished Callback navigasi setelah splash selesai
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit = {}
) {
    // ---- Animasi Logo ----
    val logoScale = remember { Animatable(0.4f) }
    val logoAlpha = remember { Animatable(0f) }

    // ---- Animasi Teks ----
    val textOffsetY = remember { Animatable(30f) }
    val textAlpha = remember { Animatable(0f) }

    // ---- Animasi Tagline ----
    val taglineAlpha = remember { Animatable(0f) }

    // ---- Animasi garis bawah ----
    val lineWidth = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Logo muncul dengan spring bounce
        delay(100)
        launch {
            logoAlpha.animateTo(1f, tween(400))
        }
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )

        // Nama DAMKAR slide up
        delay(100)
        launch {
            textAlpha.animateTo(1f, tween(400))
        }
        textOffsetY.animateTo(0f, tween(400, easing = EaseOutCubic))

        // Garis bawah expand
        delay(50)
        lineWidth.animateTo(1f, tween(500, easing = EaseOutCubic))

        // Tagline fade in
        delay(100)
        taglineAlpha.animateTo(1f, tween(500))

        // Tunggu lalu navigasi
        delay(1000)
        onSplashFinished()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DamkarBlue,
                        Color(0xFF1E4DB7),
                        Color(0xFF0D2A6E)
                    )
                )
            )
    ) {
        // Lingkaran dekorasi background
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-80).dp)
                .alpha(0.07f)
                .background(
                    brush = Brush.radialGradient(listOf(White, Color.Transparent)),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 60.dp)
                .alpha(0.05f)
                .background(
                    brush = Brush.radialGradient(listOf(White, Color.Transparent)),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )

        // Konten tengah
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Logo(
                size = 110.dp,
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Nama DAMKAR
            Text(
                text = "FIRS MADIUN",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 36.sp,
                    letterSpacing = 6.sp
                ),
                color = White,
                modifier = Modifier
                    .offset(y = textOffsetY.value.dp)
                    .alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Garis pemisah animasi
            Box(
                modifier = Modifier
                    .fillMaxWidth(lineWidth.value * 0.45f)
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                White.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline
            Text(
                text = "SIAGA 24 JAM",
                style = MaterialTheme.typography.titleMedium.copy(
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = White.copy(alpha = 0.85f),
                modifier = Modifier.alpha(taglineAlpha.value)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Cepat. Sigap. Selamat.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 1.sp
                ),
                color = White.copy(alpha = 0.55f),
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }

        // Loading dots bawah
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .alpha(taglineAlpha.value)
        ) {
            repeat(3) { index ->
                LoadingDot(delayMillis = index * 180)
            }
        }
    }
}

/**
 * Titik animasi loading (pulse).
 */
@Composable
private fun LoadingDot(delayMillis: Int) {
    val alpha = remember { Animatable(0.3f) }

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        while (true) {
            alpha.animateTo(1f, tween(500))
            alpha.animateTo(0.3f, tween(500))
        }
    }

    Box(
        modifier = Modifier
            .size(6.dp)
            .alpha(alpha.value)
            .background(White, shape = androidx.compose.foundation.shape.CircleShape)
    )
}

// ============================================================
// PREVIEW
// ============================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    DamkarTheme {
        SplashScreen()
    }
}