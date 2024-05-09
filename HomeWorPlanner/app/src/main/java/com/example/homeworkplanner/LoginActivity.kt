package com.example.homeworkplanner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homeworkplanner.ui.theme.HomeworkPlannerTheme

class LoginActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Načítajte boolean hodnotu zo SharedPreferences
        val sharedPref = applicationContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val isDarkModeEnabled = sharedPref.getBoolean(applicationContext.getString(R.string.theme_key), true)

        setContent {
            HomeworkPlannerTheme(darkTheme = isDarkModeEnabled) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(themeViewModel)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(themeViewModel: ThemeViewModel) {
    val context = LocalContext.current
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "HomeWork",
            fontSize = 30.sp,
            modifier = Modifier
                .offset(16.dp)
        )
        Text(
            text = "Planner",
            fontSize = 30.sp,
            modifier = Modifier
                .offset(16.dp)
        )
        Column {

            TextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                modifier = Modifier
                    .padding(16.dp),
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { /* Handle next action */ })
            )
            TextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                modifier = Modifier
                    .padding(16.dp),
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    // Perform login here
                    // For now, just navigate to MainActivity
//                context.startActivity(Intent(context, MainActivity::class.java))
                })
            )
            Button(
                onClick = {
                    // Perform login here
                    // For now, just navigate to MainActivity
                    context.startActivity(Intent(context, MenuActivity::class.java))
                },
                modifier = Modifier
                    .padding(16.dp),
            ) {
                Text(text = stringResource(id = R.string.Exams))
            }
        }
    }
}

