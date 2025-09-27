package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Meow()
            }
        }
    }
}

@Composable
fun Meow() {
    Scaffold(
        containerColor = Color.Transparent // Changed from blue to transparent
    ) { paddingValues ->
        WeatherScreen(paddingValues = paddingValues)
    }
}

@Preview(showBackground = true)
@Composable
fun See() {
    WeatherAppTheme {
        Meow()
    }
}