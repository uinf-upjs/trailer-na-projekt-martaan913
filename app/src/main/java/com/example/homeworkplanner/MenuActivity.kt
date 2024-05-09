package com.example.homeworkplanner

import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.example.homeworkplanner.ui.theme.HomeworkPlannerTheme

class MenuActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val sharedPref = applicationContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val isDark = sharedPref.getBoolean(applicationContext.getString(R.string.theme_key), true)

            themeViewModel.isDarkTheme = isDark

            HomeworkPlannerTheme(darkTheme = themeViewModel.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    MainMenu(
                        themeViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MainMenu(
        themeViewModel: ThemeViewModel) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    val sharedPref = context.getSharedPreferences("language_preferences", Context.MODE_PRIVATE)
    var selectedLanguage = sharedPref.getString(context.getString(R.string.language_key), "default_language_value")

    Column(
        Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ){
            Button(
                onClick = {
                    context.startActivity(Intent(context, MenuActivity::class.java))
                },
                shape = CircleShape,
                modifier = Modifier
                    .size(55.dp)
                    .padding(5.dp),
                contentPadding = PaddingValues(1.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.back),
                    modifier = Modifier
                        .size(35.dp)
                )
            }
            Box {
                Button(
                    onClick = { expanded = !expanded },
                    shape = CircleShape,
                    modifier = Modifier
                        .size(55.dp)
                        .padding(5.dp),
                    contentPadding = PaddingValues(1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = stringResource(id = R.string.settings),
                        modifier = Modifier
                            .size(35.dp)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text( text = stringResource(id = R.string.language)) },
                        onClick = {
                            selectedLanguage = if (selectedLanguage == "en"){
                                "sk"
                            }else{
                                "en"
                            }
                            changeLocale(context, selectedLanguage!!)
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.theme)) },
                        onClick = {
                            themeViewModel.isDarkTheme = !themeViewModel.isDarkTheme
                            changeTheme(context, themeViewModel.isDarkTheme)
                        }
                    )
                }
            }
        }
        Text(
            text = "HomeWork",
            fontSize = 48.sp,
            modifier = Modifier
                .offset(16.dp)
                .padding(top = 126.dp)
        )
        Text(
            text = "Planner",
            fontSize = 48.sp,
            modifier = Modifier
                .offset(16.dp)
        )
        Spacer(modifier = Modifier.weight(2f))
        Column (){
            Button(
                onClick = {
                    context.startActivity(Intent(context, HomeworkActivity::class.java))
                          },
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.Homework),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Button(
                onClick = {
                    context.startActivity(Intent(context, ExamActivity::class.java))
                           },
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.Exams),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

fun changeLocale(context: Context, localeString: String){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        context.getSystemService(LocaleManager::class.java)
            .applicationLocales = LocaleList.forLanguageTags(localeString)
    }
    else{
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeString))
    }
    val sharedPref = context.getSharedPreferences("language_preferences", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString(context.getString(R.string.language_key), localeString)
    editor.apply()
}

fun changeTheme(context: Context, isDark: Boolean){
    val sharedPref = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putBoolean(context.getString(R.string.theme_key), isDark)
    editor.apply()
}
