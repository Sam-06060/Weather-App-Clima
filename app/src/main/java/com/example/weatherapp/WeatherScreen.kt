package com.example.weatherapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel(), paddingValues: PaddingValues) {
    val uiState by viewModel.weatherState.collectAsState()
    var cityInput by remember { mutableStateOf("Mumbai") }

    LaunchedEffect(Unit) {
        viewModel.fetchWeather(cityInput)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1E2E),
                        Color(0xFF2D1B69),
                        Color(0xFF1E1E2E)
                    )
                )
            )
            .padding(paddingValues)
    ) {
        // Background decorative elements
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-50).dp, y = 150.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .blur(60.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Top bar with menu and more options
            TopBar(onCityChange = { newCity ->
                cityInput = newCity
                viewModel.fetchWeather(newCity)
            })

            Spacer(modifier = Modifier.height(20.dp))

            // Content based on state
            when (val state = uiState) {
                is WeatherUiState.Empty -> EmptyWeatherContent()
                is WeatherUiState.Loading -> LoadingWeatherContent()
                is WeatherUiState.Success -> WeatherContent(data = state.data)
                is WeatherUiState.Error -> ErrorWeatherContent(state.message)
            }
        }
    }
}

@Composable
fun TopBar(onCityChange: (String) -> Unit) {
    var showCityDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) {
            Icon(
                Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        TextButton(onClick = { showCityDialog = true }) {
            Text(
                "Choose city",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }

        IconButton(onClick = { }) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "More",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // City input dialog
    if (showCityDialog) {
        CityInputDialog(
            onDismiss = { showCityDialog = false },
            onCitySelected = { city ->
                onCityChange(city)
                showCityDialog = false
            }
        )
    }
}

@Composable
fun WeatherContent(data: WeatherResponse) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // City Name header
        Text(
            text = data.name, // <<<< THE CHANGE IS HERE
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Current date
        val currentDate = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(Date())
        Text(
            text = currentDate,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // Main weather display with repositioned temperature
        Box {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    // Temperature moved to left side
                    Text(
                        text = "${data.main.temp.roundToInt()}°",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    // Weather icon moved to right
                    val iconUrl = "https://openweathermap.org/img/wn/${data.weather.first().icon}@4x.png"
                    Image(
                        painter = rememberAsyncImagePainter(iconUrl),
                        contentDescription = data.weather.first().description,
                        modifier = Modifier.size(120.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp)) // Increased spacing to push cards lower

        // Weather details cards moved lower
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailCard(
                iconText = "💨",
                value = "${data.wind.speed.roundToInt()}",
                unit = "km/h",
                label = "Wind"
            )

            WeatherDetailCard(
                iconText = "💧",
                value = "${data.main.humidity}",
                unit = "%",
                label = "Humidity"
            )

            WeatherDetailCard(
                iconText = "🌡️",
                value = "${data.main.feelsLike.roundToInt()}",
                unit = "°",
                label = "Feels like"
            )
        }
    }
}

@Composable
fun WeatherDetailCard(
    iconText: String,
    value: String,
    unit: String,
    label: String
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = iconText,
                fontSize = 24.sp
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = value,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = unit,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CityInputDialog(
    onDismiss: () -> Unit,
    onCitySelected: (String) -> Unit
) {
    var cityInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (cityInput.isNotBlank()) {
                        onCitySelected(cityInput)
                    }
                }
            ) {
                Text("Search", color = Color(0xFF4FC3F7))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        title = {
            Text("Choose City", color = Color.White)
        },
        text = {
            OutlinedTextField(
                value = cityInput,
                onValueChange = { cityInput = it },
                label = { Text("Enter city name") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White.copy(alpha = 0.7f),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                )
            )
        },
        containerColor = Color(0xFF2D1B69)
    )
}

@Composable
fun EmptyWeatherContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🌤️",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Choose a city to see weather",
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LoadingWeatherContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Color(0xFF4FC3F7),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Loading weather data...",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ErrorWeatherContent(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚠️",
            fontSize = 48.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Unable to load weather",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Please check connection and try again",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExactWeatherUIPreview() {
    val fakeWeatherData = WeatherResponse(
        name = "Mumbai",
        main = Main(temp = 19.0, feelsLike = 21.0, humidity = 73),
        weather = listOf(Weather(description = "overcast clouds", icon = "04d")),
        wind = Wind(speed = 4.2)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1E2E),
                        Color(0xFF2D1B69),
                        Color(0xFF1E1E2E)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            WeatherContent(data = fakeWeatherData)
        }
    }
}