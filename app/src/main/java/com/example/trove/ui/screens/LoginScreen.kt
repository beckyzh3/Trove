package com.example.trove.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.trove.ui.theme.TroveTheme

@Composable
fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onGoogleSignInClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // app title

            Text(
                text = "trove",
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "save your travel memories in one place",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))


            // ignore; decided to do sign in with Google
            // email input

            /*
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // password input

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))


             */
            // login button

            Button(
                onClick = onGoogleSignInClick,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isLoading) {
                        "Signing in..."
                    } else {
                        "Sign in with Google"
                    }
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            /*
            Spacer(modifier = Modifier.height(12.dp))

            // create account button

            OutlinedButton(
                onClick = onCreateAccountClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("create account")
            }

             */
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TroveTheme {
        LoginScreen(
            isLoading = false,
            errorMessage = null,
            onGoogleSignInClick = {}
        )
    }
}