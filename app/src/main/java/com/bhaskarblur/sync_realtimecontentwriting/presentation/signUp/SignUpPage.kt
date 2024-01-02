package com.bhaskarblur.sync_realtimecontentwriting.presentation.signUp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignUpPage(viewModel: SignUpViewModel) {

    val userName = remember { mutableStateOf("") }
    val fullName = remember { mutableStateOf("") }
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(Modifier.height(6.dp))
        Text("Welcome to Sync", fontSize = 22.sp, fontWeight = FontWeight.SemiBold,
            color = Color.White)

        Spacer(Modifier.height(24.dp))

        TextField(value = userName.value,
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
                focusedContainerColor = Color(0xFF151516),
                unfocusedContainerColor = Color(0xFF151516)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(shape = RoundedCornerShape(90.dp), color = Color(0xFF151516)),
            onValueChange = { value ->
                userName.value = value
            },
            placeholder = {
                Text("Enter username")
            })

        Spacer(Modifier.height(12.dp))

        TextField(value = fullName.value,
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
                focusedContainerColor = Color(0xFF151516),
                unfocusedContainerColor = Color(0xFF151516)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(shape = RoundedCornerShape(90.dp), color = Color(0xFF151516)),
            onValueChange = { value ->
                fullName.value = value
            },
            placeholder = {
                Text("Enter full name")
            })
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                viewModel.signUpUser(userName.value, fullName.value)
            },
            Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Create account", fontSize = 16.sp)

        }
    }
}