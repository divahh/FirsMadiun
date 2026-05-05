package com.example.firs_madiun_baru

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class drawer_detail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DashDetailScreen()
        }
    }
}

@Composable
fun DashDetailScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1F2F5A))
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "FIRE COMPLAIN",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Image(
                painter = painterResource(id = R.drawable.btn_navwhite),
                contentDescription = "Menu",
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Divider(
            color = Color.White.copy(alpha = 0.5f),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // IMAGE
        Image(
            painter = painterResource(id = R.drawable.pic_dummyact),
            contentDescription = "Fire Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CATEGORY
        DetailRow("Category", "Land Fire")

        Spacer(modifier = Modifier.height(14.dp))

        // REPORTER
        DetailRow("Reporter", "Byun Baekhyun")

        Spacer(modifier = Modifier.height(14.dp))

        // DESCRIPTION
        DetailRow(
            "Description",
            "Kebakaran terjadi dari pukul 14.00 WIB dengan kemungkinan terjadi karena puntung rokok."
        )

        Spacer(modifier = Modifier.height(14.dp))

        // LOCATION
        DetailRow("Location", "RT 22, RW 4, Pohon Gede")
    }
}

@Composable
fun DetailRow(label: String, value: String) {

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "$label :",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(100.dp)
        )

        Text(
            text = value,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DrawerDetailPreview() {
    MaterialTheme {
        DashDetailScreen()
    }
}