// app/src/main/java/com/example/learnsphereapp2/ui/login/LoginScreen.kt
package com.example.learnsphereapp2.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learnsphereapp2.R
import com.example.learnsphereapp2.data.repository.AuthRepository
import com.example.learnsphereapp2.util.PreferencesHelper

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    preferencesHelper: PreferencesHelper,
    viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            AuthRepository(preferencesHelper),
            preferencesHelper
        )
    )
) {
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val loginSuccess by viewModel.loginSuccess

    viewModel.handleLoginNavigation(onLoginSuccess)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top, // Elemen mulai dari atas
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp)) // Jarak dari atas layar

        // Logo LearnSphere
        Image(
            painter = painterResource(id = R.drawable.learnsphere_logo),
            contentDescription = "Logo LearnSphere",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 16.dp)
        )

        // Sub-kolom untuk teks "Selamat Datang!" dan kolom teks, agar rata kiri
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start // Rata kiri
        ) {
            // Teks Selamat Datang
            Text(
                text = "Selamat Datang!",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Kolom Nama Pengguna
            OutlinedTextField(
                value = viewModel.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                label = { Text("Nama Pengguna") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start), // Teks rata kiri
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color(0xFF1976D2),
                    focusedLabelColor = Color(0xFF1976D2),
                    unfocusedLabelColor = Color(0xFF1976D2)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Kolom Kata Sandi
            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Kata Sandi") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start), // Teks rata kiri
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color(0xFF1976D2),
                    focusedLabelColor = Color(0xFF1976D2),
                    unfocusedLabelColor = Color(0xFF1976D2)
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp)) // Jarak antara "Kata Sandi" dan tombol "Login" ditambah

        // Tombol Login
        Button(
            onClick = { viewModel.login() },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
            contentPadding = PaddingValues(horizontal = 32.dp) // Jarak horizontal untuk teks "Login"
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Login")
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}