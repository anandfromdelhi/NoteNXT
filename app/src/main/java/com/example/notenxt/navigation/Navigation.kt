package com.example.notenxt.navigation

import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notenxt.screens.home.Home
import com.example.notenxt.screens.login.LoginScreen
import com.example.notenxt.screens.login.LoginViewModel
import com.example.notenxt.screens.login.SignUpScreen

enum class LoginRoutes {
    SignUp,
    SignIn
}

enum class HomeRoutes {
    Home,
    Detail
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel
) {
    NavHost(
        navController = navController,
        startDestination = LoginRoutes.SignIn.name
    ) {
        composable(route = LoginRoutes.SignIn.name) {
            LoginScreen(
                onNavToHomePage = {
                    navController.navigate(HomeRoutes.Home.name) {
                        launchSingleTop = true
                        popUpTo(route = LoginRoutes.SignIn.name) { inclusive = true }
                    }
                }, loginViewModel = loginViewModel
            ) {
                navController.navigate(LoginRoutes.SignUp.name) {
                    launchSingleTop = true
                    popUpTo(LoginRoutes.SignIn.name) { inclusive = true }
                }
            }
        }

        composable(route = LoginRoutes.SignUp.name){
            SignUpScreen(onNavToHomePage = {
                navController.navigate(HomeRoutes.Home.name){
                    popUpTo(LoginRoutes.SignUp.name){
                        inclusive = true
                    }
                }
            }, loginViewModel = loginViewModel) {
                    navController.navigate(LoginRoutes.SignIn.name){

                    }
            }
        }

        composable(route = HomeRoutes.Home.name){
            Home(homeViewModel = null, onNoteClick = {}, navToDetailPage = { /*TODO*/ }) {

            }
        }

    }
}