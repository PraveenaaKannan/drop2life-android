package com.drop2life.app

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.launch

@Composable
fun TranslatedText(originalText: String) {

    var translatedText by remember {
        mutableStateOf(originalText)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

        scope.launch {

            translatedText =
                TranslationHelper.translate(
                    originalText,
                    LanguageManager.selectedLanguage
                )
        }
    }

    Text(text = translatedText)
}