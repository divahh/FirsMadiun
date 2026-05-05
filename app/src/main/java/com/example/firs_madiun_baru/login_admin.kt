package com.example.firs_madiun_baru

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class login_admin : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginAdminScreen()
        }
    }
}

@Composable
fun LoginAdminScreen() {

    val isPreview = LocalInspectionMode.current

    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        // ✅ Background (aman untuk preview)
        if (!isPreview) {
            Image(
                painter = painterResource(id = R.drawable.bg_login_new),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // fallback preview
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A237E))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // marginTop 428dp
            Spacer(modifier = Modifier.height(460.dp))

            // INPUT NAMA
            TextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Please input your name") },
                modifier = Modifier
                    .width(288.dp)
                    .height(39.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0x69081E78),
                    unfocusedContainerColor = Color(0x69081E78),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(0.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            // INPUT PASSWORD
            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Please input your password") },
                modifier = Modifier
                    .width(287.dp)
                    .height(40.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0x69081E78),
                    unfocusedContainerColor = Color(0x69081E78),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(0.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // BUTTON LOGIN
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A237E)
                ),
                modifier = Modifier
                    .width(154.dp)
                    .height(40.dp)
            ) {
                Text("Login", color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // REGISTER
            Text(
                text = "register now!",
                color = Color(0xFF7F85AB),
                fontWeight = FontWeight.Bold,
                // Modifier.clickable can sometimes cause NoSuchMethodError in Previews due to binary incompatibility 
                // in the Layoutlib environment. Wrapping it with isPreview check avoids this issue.
                modifier = if (isPreview) Modifier else Modifier.clickable { }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginAdmin() {
    MaterialTheme {
        LoginAdminScreen()
    }
}