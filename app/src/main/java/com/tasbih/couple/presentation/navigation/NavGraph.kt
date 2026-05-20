package com.tasbih.couple.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.tasbih.couple.presentation.screens.auth.LoginScreen
import com.tasbih.couple.presentation.screens.auth.RegisterScreen
import com.tasbih.couple.presentation.screens.home.HomeScreen
import com.tasbih.couple.presentation.screens.tasbih.TasbihScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Tasbih : Screen("tasbih/{zikrId}/{zikrName}/{arabicText}/{target}") {
        fun go(zikrId: String, zikrName: String, arabicText: String, target: Int) =
            "tasbih/$zikrId/${zikrName.encode()}/${arabicText.encode()}/$target"
    }
}

fun String.encode() = java.net.URLEncoder.encode(this, "UTF-8")

@Composable
fun AppNavGraph(navController: NavHostController) {
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null)
        Screen.Home.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onGoToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onZikrClick = { zikr ->
                    navController.navigate(
                        Screen.Tasbih.go(zikr.id, zikr.name, zikr.arabicText, zikr.targetCount)
                    )
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Tasbih.route,
            arguments = listOf(
                navArgument("zikrId") { type = NavType.StringType },
                navArgument("zikrName") { type = NavType.StringType },
                navArgument("arabicText") { type = NavType.StringType },
                navArgument("target") { type = NavType.IntType }
            )
        ) { back ->
            TasbihScreen(
                zikrId = back.arguments?.getString("zikrId") ?: "",
                zikrName = java.net.URLDecoder.decode(back.arguments?.getString("zikrName") ?: "", "UTF-8"),
                arabicText = java.net.URLDecoder.decode(back.arguments?.getString("arabicText") ?: "", "UTF-8"),
                targetCount = back.arguments?.getInt("target") ?: 33,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
