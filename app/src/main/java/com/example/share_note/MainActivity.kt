package com.example.share_note

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.example.share_note.ui.screens.*
import com.example.share_note.ui.theme.MyAppTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.share_note.ui.components.ToastManager
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val context = this

            val isUserLoggedIn = remember { mutableStateOf(false) }
            val firebaseAuth = FirebaseAuth.getInstance()

            LaunchedEffect(Unit) {
                isUserLoggedIn.value = firebaseAuth.currentUser != null
            }

            var isDarkTheme by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                firebaseAuth.currentUser?.let { user ->
                    FirebaseFirestore.getInstance().collection("users")
                        .document(user.uid)
                        .collection("settings")
                        .document("preferences")
                        .addSnapshotListener { document, _ ->
                            if (document != null && document.exists()) {
                                isDarkTheme = document.getBoolean("theme") == true
                            }
                        }
                }
            }
            val toastManager = remember { ToastManager() }
            MyAppTheme(isDarkTheme) {
                AppNavHost(navController, context, isUserLoggedIn.value, toastManager = toastManager) { newTheme ->
                    isDarkTheme = newTheme
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    context: Context,
    isUserLoggedIn: Boolean,
    toastManager: ToastManager,
    onThemeChange: (Boolean) -> Unit
) {
    @Suppress("DEPRECATION")
    AnimatedNavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) "home" else "login",
        modifier = Modifier.fillMaxSize()
    ) {
        composable(
            "login",
            enterTransition = {
                fadeIn(animationSpec = tween(300, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(250, easing = FastOutLinearInEasing))
            }
        ) {
            LoginScreen(navController,toastManager)
        }

        composable(
            "signup",
            enterTransition = {
                scaleIn(initialScale = 0.9f, animationSpec = tween(350, easing = FastOutSlowInEasing))
            },
            exitTransition = {
                scaleOut(targetScale = 1.1f, animationSpec = tween(300, easing = FastOutLinearInEasing))
            }
        ) {
            SignupScreen(navController,toastManager)
        }

        composable(
            "home",
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it / 2 }, animationSpec = tween(400, easing = FastOutSlowInEasing)) +
                        fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it / 2 }, animationSpec = tween(400, easing = FastOutLinearInEasing)) +
                        fadeOut(animationSpec = tween(300))
            }
        ) {
            HomeScreen(navController,context,toastManager)
        }

        composable(
            "profile",
            enterTransition = {
                slideInVertically(initialOffsetY = { -it / 2 }, animationSpec = tween(400, easing = FastOutSlowInEasing)) +
                        fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutVertically(targetOffsetY = { -it / 2 }, animationSpec = tween(400, easing = FastOutLinearInEasing)) +
                        fadeOut(animationSpec = tween(300))
            }
        ) {
            ProfileScreen(navController,toastManager)
        }

        composable(
            "settings",
            enterTransition = {
                fadeIn(animationSpec = tween(300, easing = LinearOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(250, easing = FastOutLinearInEasing))
            }
        ) {
            SettingsScreen(navController, toastManager,onThemeChange)
        }

        composable(
            "myNotes",
            enterTransition = {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(350, easing = FastOutSlowInEasing))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(350, easing = FastOutLinearInEasing))
            }
        ) {
            MyNotesScreen(navController,toastManager)
        }

        composable(
            "addNote",
            enterTransition = {
                slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(350, easing = FastOutSlowInEasing)) +
                        fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutVertically(targetOffsetY = { it / 2 }, animationSpec = tween(350, easing = FastOutLinearInEasing)) +
                        fadeOut(animationSpec = tween(300))
            }
        ) {
            AddNoteScreen(navController,toastManager)
        }

        composable(
            "noteDetail/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType }),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it / 2 }, animationSpec = tween(400, easing = FastOutSlowInEasing)) +
                        fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it / 2 }, animationSpec = tween(400, easing = FastOutLinearInEasing)) +
                        fadeOut(animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
            val userId = getCurrentUserId()

            NoteDetailScreen(navController, noteId, userId,toastManager)
        }
        composable("completeProfile",
            enterTransition = {
                slideInVertically(initialOffsetY = { it }, animationSpec = tween(500, easing = FastOutSlowInEasing)) +
                        fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(500, easing = FastOutLinearInEasing)) +
                        fadeOut(animationSpec = tween(300))
            }
        ) {
            CompleteProfileScreen(navController,context,toastManager)
        }
        composable("editProfile",
            enterTransition = {
                slideInVertically(initialOffsetY = { -it }, animationSpec = tween(500, easing = FastOutSlowInEasing)) +
                        fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutVertically(targetOffsetY = { it }, animationSpec = tween(500, easing = FastOutLinearInEasing)) +
                        fadeOut(animationSpec = tween(300))
            }
        ) {
            EditProfileScreen(navController, context,toastManager)
        }
    }
}

fun getCurrentUserId(): String {
    return FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"
}
