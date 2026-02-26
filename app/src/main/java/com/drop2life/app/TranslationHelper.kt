package com.drop2life.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object TranslationHelper {

    suspend fun translate(text: String, targetLang: String): String {

        if (targetLang == "en") return text

        return withContext(Dispatchers.IO) {

            try {

                val url =
                    "https://api.mymemory.translated.net/get?q=$text&langpair=en|$targetLang"

                val response = URL(url).readText()

                val jsonObject = JSONObject(response)

                jsonObject
                    .getJSONObject("responseData")
                    .getString("translatedText")

            } catch (e: Exception) {

                text
            }
        }
    }
}