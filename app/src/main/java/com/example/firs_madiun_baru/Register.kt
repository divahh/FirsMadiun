package com.example.firs_madiun_baru

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class Register : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen()
        }
    }
}

@Composable
fun RegisterScreen() {

    var name by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        // BACKGROUND
        Image(
            painter = painterResource(id = R.drawable.bg_register),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // BACK BUTTON
        Image(
            painter = painterResource(id = R.drawable.btn_back),
            contentDescription = "Back",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 28.dp, start = 16.dp)
                .size(50.dp, 37.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // etNama (252dp)
            Spacer(modifier = Modifier.height(290.dp))

            TextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Please input your name") },
                modifier = Modifier
                    .width(288.dp)
                    .height(39.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0x2A081E78),
                    unfocusedContainerColor = Color(0x2A081E78),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(40.dp)) // 320 - 252

            // etId
            TextField(
                value = id,
                onValueChange = { id = it },
                placeholder = { Text("Please input your ID") },
                modifier = Modifier
                    .width(288.dp)
                    .height(39.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0x2A081E78),
                    unfocusedContainerColor = Color(0x2A081E78),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(45.dp)) // 392 - 320

            // etEmail
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Please input your email") },
                modifier = Modifier
                    .width(288.dp)
                    .height(39.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0x2A081E78),
                    unfocusedContainerColor = Color(0x2A081E78),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(45.dp)) // 460 - 392

            // etPassword
            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("*******") },
                modifier = Modifier
                    .width(288.dp)
                    .height(39.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0x2A081E78),
                    unfocusedContainerColor = Color(0x2A081E78),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // BUTTON REGISTER (bottom 172dp)
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A237E)
                ),
                modifier = Modifier
                    .padding(bottom = 172.dp)
                    .width(154.dp)
                    .height(40.dp)
            ) {
                Text("Register", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    RegisterScreen()
}