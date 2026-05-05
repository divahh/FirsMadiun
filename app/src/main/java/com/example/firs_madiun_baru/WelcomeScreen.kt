package com.example.firs_madiun_baru

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class WelcomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WelcomeScreenContent {
                // TODO: Aksi saat tombol Next diklik
            }
        }
    }
}

@Composable
fun WelcomeScreenContent(onNextClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg_welcome),
            contentDescription = "Background Welcome",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Button di bawah
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .width(200.dp)
        ) {
            Text(text = "Next")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreenContent(onNextClick = {})
}