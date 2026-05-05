package com.example.firs_madiun_baru

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }

    // ================= MAIN SCREEN =================
    @Composable
    fun MainScreen() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            // Background
            Image(
                painter = painterResource(id = R.drawable.bg_menu),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                // ===== TOP RIGHT =====
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Image(
                            painter = painterResource(id = R.drawable.btn_navwhite),
                            contentDescription = "Menu Button",
                            modifier = Modifier
                                .padding(top = 45.dp, end = 30.dp)
                                .size(width = 40.dp, height = 20.dp)
                        )

                        Spacer(modifier = Modifier.height(44.dp))

                        Button(
                            onClick = {},
                            modifier = Modifier
                                .size(width = 70.dp, height = 40.dp)
                                .offset(x = (-27).dp) // geser ke kiri
                                .size(70.dp, 40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Report",
                                color = Color.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ===== MENU =====
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MenuItem(R.drawable.btn_category, "Category")
                    MenuItem(R.drawable.btn_education, "Education")
                    MenuItem(R.drawable.btn_emergencycall, "Emergency Call")
                    MenuItem(R.drawable.btn_tips, "Tips")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ===== GRID CONTENT =====
                val data = listOf(1, 2, 3, 4)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(data) {
                        ContentItem()
                    }
                }
            }
        }
    }

    // ================= MENU ITEM =================
    @Composable
    fun MenuItem(imageRes: Int, title: String) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }

    // ================= CONTENT ITEM =================
    @Composable
    fun ContentItem() {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Image(
                painter = painterResource(id = R.drawable.pic_dummyact),
                contentDescription = null,
                modifier = Modifier
                    .height(90.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Land Fire",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            Text(
                text = "Kutoharjo",
                color = Color.White,
                fontSize = 11.sp
            )

            Text(
                text = "20/3/24",
                color = Color.White,
                fontSize = 11.sp
            )
        }
    }
}

// ================= PREVIEW =================
@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MaterialTheme {
        // NOTE: Preview bisa error kalau drawable tidak ditemukan
        // jadi pastikan semua drawable ada
        MainActivity().MainScreen()
    }
}