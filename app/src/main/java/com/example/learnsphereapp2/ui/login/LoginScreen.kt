package com.example.learnsphereapp2.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.learnsphereapp2.R
import com.example.learnsphereapp2.data.repository.AuthRepository
import com.example.learnsphereapp2.network.RetrofitClient
import com.example.learnsphereapp2.ui.Destinations
import com.example.learnsphereapp2.util.PreferencesHelper
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController, // Tambahkan parameter navController
    onLoginSuccess: (String) -> Unit,
    preferencesHelper: PreferencesHelper,
    viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            AuthRepository(RetrofitClient.apiService, preferencesHelper),
            preferencesHelper
        )
    )
) {
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val loginSuccess by viewModel.loginSuccess
    val usernameError by viewModel.usernameError
    val passwordError by viewModel.passwordError

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Handle navigation
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            val role = preferencesHelper.getRole()
            if (role != null) {
                onLoginSuccess(role)
                viewModel.resetLoginSuccess()
            } else {
                // Handle case where role is null if needed
            }
        }
    }

    // Show error message in Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = "Close",
                    duration = SnackbarDuration.Long
                )
                viewModel.clearErrorMessage()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo LearnSphere
            Image(
                painter = painterResource(id = R.drawable.learnsphere_logo),
                contentDescription = "Logo LearnSphere",
                modifier = Modifier
                    .size(180.dp)
                    .padding(bottom = 16.dp)
            )

            // Sub-column for text and input fields
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Selamat Datang!",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Username field
                OutlinedTextField(
                    value = viewModel.username,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    label = { Text("Nama Pengguna") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .padding(vertical = 4.dp),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
                    isError = usernameError != null,
                    supportingText = {
                        usernameError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1976D2),
                        unfocusedBorderColor = Color(0xFF90CAF9),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        focusedLabelColor = Color(0xFF1976D2),
                        unfocusedLabelColor = Color(0xFF90CAF9),
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        focusedSupportingTextColor = MaterialTheme.colorScheme.error,
                        unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Password field
                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Kata Sandi") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 36.dp)
                        .padding(vertical = 4.dp),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
                    isError = passwordError != null,
                    supportingText = {
                        passwordError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1976D2),
                        unfocusedBorderColor = Color(0xFF90CAF9),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        focusedLabelColor = Color(0xFF1976D2),
                        unfocusedLabelColor = Color(0xFF90CAF9),
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        focusedSupportingTextColor = MaterialTheme.colorScheme.error,
                        unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Forgot Password link
            TextButton(
                onClick = { navController.navigate(Destinations.FORGOT_PASSWORD) }, // Perbaiki onClick
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Lupa Kata Sandi?", color = Color(0xFF1976D2))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            Button(
                onClick = { viewModel.login() },
                enabled = !isLoading && viewModel.isInputValid(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                contentPadding = PaddingValues(horizontal = 32.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Login")
                }
            }
        }
    }
}