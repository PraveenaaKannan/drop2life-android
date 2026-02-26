@file:OptIn(ExperimentalMaterial3Api::class)

package com.drop2life.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// ✅ IMPORTANT IMPORTS (FIXES YOUR ERROR)
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import coil.compose.rememberAsyncImagePainter

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import org.json.JSONObject
import java.net.URL


class RegisterActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RegisterScreen(auth, db, storage)
        }
    }
}

@Composable
fun RegisterScreen(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    storage: FirebaseStorage
) {

    val context = LocalContext.current

    val language =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("language", "en") ?: "en"

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var subLocality by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var panchayat by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var registerText by remember { mutableStateOf("Register") }

    // ✅ Translation
    LaunchedEffect(language) {
        if (language == "ta") {
            registerText = translate("Register", language)
        }
    }

    // ✅ Image picker launcher (FIXED)
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            imageUri = uri
        }


    Scaffold(

        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(registerText) }
            )
        }

    ) { padding ->

        Column(

            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // Image Preview
            if (imageUri != null) {

                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )

            } else {

                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    launcher.launch("image/*")
                }
            ) {
                Text("Select Image")
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = street,
                onValueChange = { street = it },
                label = { Text("Street") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = subLocality,
                onValueChange = { subLocality = it },
                label = { Text("Sub Locality") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("District") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = panchayat,
                onValueChange = { panchayat = it },
                label = { Text("Panchayat") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(

                modifier = Modifier.fillMaxWidth(),

                onClick = {

                    registerUser(
                        context,
                        auth,
                        db,
                        storage,
                        name,
                        email,
                        password,
                        street,
                        subLocality,
                        district,
                        panchayat,
                        imageUri
                    )
                }

            ) {

                Text(registerText)
            }
        }
    }
}

fun registerUser(
    context: Context,
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    storage: FirebaseStorage,
    name: String,
    email: String,
    password: String,
    street: String,
    subLocality: String,
    district: String,
    panchayat: String,
    imageUri: Uri?
) {

    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {

        Toast.makeText(context, "Fill required fields", Toast.LENGTH_SHORT).show()
        return
    }

    auth.createUserWithEmailAndPassword(email, password)

        .addOnSuccessListener { result ->

            val uid = result.user!!.uid

            if (imageUri != null) {

                val ref = storage.reference.child("profiles/$uid.jpg")

                ref.putFile(imageUri)

                    .addOnSuccessListener {

                        ref.downloadUrl.addOnSuccessListener { url ->

                            saveUser(
                                context,
                                db,
                                uid,
                                name,
                                email,
                                street,
                                subLocality,
                                district,
                                panchayat,
                                url.toString()
                            )
                        }
                    }

            } else {

                saveUser(
                    context,
                    db,
                    uid,
                    name,
                    email,
                    street,
                    subLocality,
                    district,
                    panchayat,
                    ""
                )
            }
        }

        .addOnFailureListener {

            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
}

fun saveUser(
    context: Context,
    db: FirebaseFirestore,
    uid: String,
    name: String,
    email: String,
    street: String,
    subLocality: String,
    district: String,
    panchayat: String,
    imageUrl: String
) {

    val user = hashMapOf(

        "uid" to uid,
        "name" to name,
        "email" to email,
        "street" to street,
        "subLocality" to subLocality,
        "district" to district,
        "panchayat" to panchayat,
        "image" to imageUrl
    )

    db.collection("users")
        .document(uid)
        .set(user)

        .addOnSuccessListener {

            Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()

            context.startActivity(
                Intent(context, DashboardActivity::class.java)
            )
        }
}

suspend fun translate(text: String, lang: String): String {

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