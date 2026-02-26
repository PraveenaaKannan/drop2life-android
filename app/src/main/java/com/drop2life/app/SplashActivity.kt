package com.drop2life.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen()
        }
    }
}

@Composable
fun SplashScreen() {

    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    val dropOffset = remember { Animatable(-200f) }

    var titleText by remember { mutableStateOf("Save Water, Save Life 💧") }

    var subtitleText by remember {
        mutableStateOf("Loading your smart solution...")
    }

    LaunchedEffect(Unit) {

        // Drop animation
        dropOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 2000,
                easing = EaseOutBounce
            )
        )

        // Translate using API
        titleText =
            TranslationHelper.translate(
                titleText,
                LanguageManager.selectedLanguage
            )

        subtitleText =
            TranslationHelper.translate(
                subtitleText,
                LanguageManager.selectedLanguage
            )

        // Wait before navigating
        delay(4000)

        // Navigate safely
        context.startActivity(
            Intent(context, LanguageSelectionActivity::class.java)
        )

        (context as ComponentActivity).finish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD))
    ) {

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .offset(y = dropOffset.value.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = titleText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = subtitleText,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.BottomCenter)
        ) {

            val path = Path()
            val waveHeight = 40f

            path.moveTo(0f, size.height - waveHeight)

            for (x in 0..size.width.toInt()) {

                val y =
                    size.height -
                            waveHeight *
                            (1 + 0.5f *
                                    (1 + sin(
                                        (x / size.width * 2 * PI + waveOffset).toFloat()
                                    )))

                path.lineTo(x.toFloat(), y)
            }

            path.lineTo(size.width, size.height)
            path.lineTo(0f, size.height)
            path.close()

            drawPath(path, color = Color(0xFF42A5F5))
        }
    }
}