package com.example.notenxt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notenxt.navigation.Navigation
import com.example.notenxt.screens.login.LoginScreen
import com.example.notenxt.screens.login.LoginViewModel
import com.example.notenxt.ui.theme.NoteNXTTheme
import dagger.hilt.android.AndroidEntryPoint


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteNXTTheme {
                val loginViewModel = viewModel(modelClass = LoginViewModel::class.java)
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Navigation(loginViewModel = loginViewModel)
                }
            }
        }
    }
}

