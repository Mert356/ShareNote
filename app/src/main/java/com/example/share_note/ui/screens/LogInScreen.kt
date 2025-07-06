package com.example.share_note.ui.screens

import Google
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.share_note.BuildConfig
import com.example.share_note.R
import com.example.share_note.ui.components.CustomTextField
import com.example.share_note.ui.components.CustomToastHost
import com.example.share_note.ui.components.ToastManager
import com.example.share_note.ui.theme.LightGreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(navController: NavController,toastManager: ToastManager) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val googleSignInClient = remember { getGoogleSignInClient(context) }
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        checkUserProfile(navController,toastManager)
                    } else {
                        errorMessage = "Google ile giriş başarısız oldu!"
                    }
                }
        } catch (e: Exception) {
            errorMessage = "Google ile giriş başarısız: ${e.localizedMessage}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bilgi paylaştıkça değerlenir", style = MaterialTheme.typography.displaySmall, textAlign = TextAlign.Center)
        Image(
            painter = painterResource(id = R.drawable.note),
            contentDescription = "Login Illustration",
            modifier = Modifier
                .size(250.dp)
        )

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "E-mail",
            placeholder = "E-mailinizi giriniz",
            isError = isEmailError
        )

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Şifre",
            placeholder = "Şifrenizi giriniz",
            isPassword = true,
            isError = isPasswordError
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    isEmailError = email.isBlank()
                    isPasswordError = password.isBlank()
                    errorMessage = "E-mail ve şifre boş olamaz!"
                    return@Button
                }

                isLoading = true
                loginWithEmailPassword(email, password, navController,toastManager) { success, error ->
                    isLoading = false
                    if (success) {
                        isEmailError = false
                        isPasswordError = false
                        errorMessage = null
                    } else {
                        isEmailError = true
                        isPasswordError = true
                        errorMessage = error
                    }
                }
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = LightGreen, modifier = Modifier.size(20.dp))
            } else {
                Text("Giriş Yap", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Text(
                text = "Veya Google ile giriş yap",
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }

        Button(
            onClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Google,
                contentDescription = "Google Sign-In",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text("Google ile Giriş Yap", style = MaterialTheme.typography.bodyLarge)
        }
    }
    CustomToastHost(toastManager)
}




fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("555403013417-hqvgaaib2vit7qnpv0kvk2ngq46s1koo.apps.googleusercontent.com")
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, options)
}


fun checkUserProfile(navController: NavController,toastManager: ToastManager) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    user?.let {
        val userDocRef = db.collection("users").document(user.uid)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    navController.navigate("completeProfile") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            .addOnFailureListener { e ->

                Log.e("Firestore", "Veri okunamadı!", e)
                toastManager.showToast("Bağlantı hatası, tekrar deneyin!")
            }
    }
}


fun loginWithEmailPassword(
    email: String,
    password: String,
    navController: NavController,
    toastManager: ToastManager,
    onResult: (Boolean, String?) -> Unit,
) {
    val auth = FirebaseAuth.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkUserProfile(navController,toastManager)
                onResult(true, null)
            } else {
                val errorMessage = task.exception?.localizedMessage ?: "Giriş başarısız oldu!"
                Log.e("FirebaseAuth", errorMessage)
                onResult(false, errorMessage)
            }
        }
}

