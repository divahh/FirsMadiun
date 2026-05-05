package com.example.firs_madiun_baru

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class EmergencyCall : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EmergencyCallScreen()
        }
    }

    // ================= UI SCREEN =================
    @Composable
    fun EmergencyCallScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Background
            Image(
                painter = painterResource(id = R.drawable.bg_emergencycall),
                contentDescription = "Background About Us",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Image di atas kanan, posisi mirip horizontal_bias & vertical_bias

            Image(
                painter = painterResource(id = R.drawable.btn_navwhite),
                contentDescription = "Menu Button",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 55.dp, end = 30.dp)
                    .size(width = 40.dp, height = 20.dp)
            )
        }
    }
}

// ================= PREVIEW =================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewEmergencyCall() {
    EmergencyCall().EmergencyCallScreen()
}