package com.example.trove

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import com.example.trove.data.UserRepo
import com.example.trove.ui.screens.ProfileFormScreen
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
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import com.example.trove.ui.screens.ProfileFormMode
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enable and re-enable to test sign in
        FirebaseAuth.getInstance().signOut()
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

    var appUser by remember {
        mutableStateOf<User?>(null)
    }

    var needsProfile by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(firebaseUser?.uid) {
        val signedInUser = firebaseUser

        if (signedInUser != null) {
            isLoading = true
            errorMessage = null

            try {
                val existingProfile =
                    UserRepo.getUser(signedInUser.uid)

                if (existingProfile != null) {
                    appUser = existingProfile
                    needsProfile = false
                } else {
                    appUser = User(
                        uid = signedInUser.uid,
                        name = signedInUser.displayName.orEmpty(),
                        email = signedInUser.email.orEmpty()
                    )

                    needsProfile = true
                }
            } catch (error: Exception) {
                errorMessage =
                    error.message ?: "Could not load profile"
            } finally {
                isLoading = false
            }
        }
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

                        firebaseUser =
                            FirebaseAuth.getInstance().currentUser
                    } catch (error: Exception) {
                        errorMessage =
                            error.message ?: "Google sign-in failed"

                        isLoading = false
                    }
                }
            }
        )
    } else if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (needsProfile) {
        appUser?.let { initialProfile ->
            ProfileFormScreen(
                initialProfile = initialProfile,
                mode = ProfileFormMode.CREATE,
                onSave = { completedProfile ->
                    scope.launch {
                        isLoading = true
                        errorMessage = null

                        try {
                            UserRepo.saveUser(completedProfile)

                            appUser = completedProfile
                            needsProfile = false
                        } catch (error: Exception) {
                            errorMessage =
                                error.message
                                    ?: "Could not save profile"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                onCancel = {
                    FirebaseAuth.getInstance().signOut()

                    firebaseUser = null
                    appUser = null
                    needsProfile = false
                },
            )
        }
    } else {
        appUser?.let { loadedUser ->
            UserNavigation(
                initialUser = loadedUser
            )
        }
    }
}

