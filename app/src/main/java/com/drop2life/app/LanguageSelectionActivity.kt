package com.drop2life.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class LanguageSelectionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            LanguageSelectionScreen { language ->

                LanguageManager.selectedLanguage = language

                startActivity(
                    Intent(this, HomeActivity::class.java)
                )


                finish()
            }
        }
    }
}

@Composable
fun LanguageSelectionScreen(onLanguageSelected: (String) -> Unit) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Select Language",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { onLanguageSelected("en") }
            ) {
                Text("English")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onLanguageSelected("ta") }
            ) {
                Text("தமிழ்")
            }
        }
    }
}