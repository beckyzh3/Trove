package com.example.trove

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.trove.data.signInWithGoogle
import com.example.trove.ui.navigation.UserNavigation
import com.example.trove.ui.screens.LoginScreen
import com.example.trove.ui.theme.TroveTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TroveTheme {
                TroveApp()
            }
        }
    }
}

@Composable
fun TroveApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var firebaseUser by remember {
        mutableStateOf(
            FirebaseAuth.getInstance().currentUser
        )
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    if (firebaseUser == null) {
        LoginScreen(
            isLoading = isLoading,
            errorMessage = errorMessage,
            onGoogleSignInClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = null

                    try {
                        signInWithGoogle(context)
                        firebaseUser = FirebaseAuth.getInstance().currentUser
                    } catch (error: Exception) {
                        errorMessage =
                            error.message ?: "Google sign-in failed"
                    } finally {
                        isLoading = false
                    }
                }
            }
        )
    } else {
        firebaseUser?.let { signedInUser ->
            UserNavigation(
                initialUser = User(
                    uid = signedInUser.uid,
                    name = signedInUser.displayName.orEmpty(),
                    email = signedInUser.email.orEmpty()
                )
            )
        }
    }
}
