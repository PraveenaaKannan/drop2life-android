@file:OptIn(ExperimentalMaterial3Api::class)

package com.drop2life.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class DashboardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DashboardScreen()
        }
    }
}

@Composable
fun DashboardScreen() {

    val context = LocalContext.current

    val language =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("language", "en") ?: "en"

    var welcomeText by remember {
        mutableStateOf("Welcome to Drop2Life 💧")
    }

    LaunchedEffect(language) {

        if (language == "ta") {

            welcomeText =
                translateText(
                    "Welcome to Drop2Life 💧",
                    language
                )
        }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text("Dashboard")
                }
            )
        }

    ) { padding ->

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentAlignment = Alignment.Center

        ) {

            Text(

                text = welcomeText,

                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

suspend fun translateText(
    text: String,
    lang: String
): String {

    return withContext(Dispatchers.IO) {

        try {

            val url =
                "https://api.mymemory.translated.net/get?q=$text&langpair=en|$lang"

            val response = URL(url).readText()

            val json = JSONObject(response)

            json.getJSONObject("responseData")
                .getString("translatedText")

        } catch (e: Exception) {

            text
        }
    }
}