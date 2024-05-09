package com.example.homeworkplanner

import android.content.Intent
import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.homeworkplanner.HomeworkDatabase.Companion.MIGRATION_1_2

import com.example.homeworkplanner.ui.theme.HomeworkPlannerTheme
import java.time.LocalDate
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalTime


class AddHomeworkActivity : ComponentActivity(){
    private val themeViewModel: ThemeViewModel by viewModels()

    private val db by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = HomeworkDatabase::class.java,
            name = "datamodel.db"
        ).build()
    }
    private val viewModel by viewModels<DataModelViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DataModelViewModel(db.dao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = applicationContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val isDark = sharedPref.getBoolean(applicationContext.getString(R.string.theme_key), true)

        val id = intent.getStringExtra("id")
        val subject = intent.getStringExtra("subject")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val deadline = intent.getStringExtra("deadline")

        var homework = Homework("", "", description, LocalDate.now(), LocalTime.now(), false)

        if (id != null) {
            homework = Homework(subject!!, title!!, description, LocalDate.parse(deadline), LocalTime.now(), false)
            homework.uid = id.toInt()
        }


        setContent {
            HomeworkPlannerTheme(darkTheme = isDark){
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting2(themeViewModel, viewModel, homework)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting2(
    themeViewModel: ThemeViewModel,
    dvm: DataModelViewModel,
    homework: Homework
) {
    var subject by remember { mutableStateOf(homework.subject) }
    var title by remember { mutableStateOf(homework.title) }
    var description by remember { mutableStateOf(homework.description) }
    val selectedDate = remember { mutableStateOf<LocalDate?>(homework.deadline) }

    val datePickerState = rememberUseCaseState()
    var showDialogError by remember { mutableStateOf(false)}
    var showDialogLeaving by remember { mutableStateOf(false)}

    val context = LocalContext.current

    CalendarDialog(
        state = datePickerState,
        config = CalendarConfig(
            yearSelection = true,
            monthSelection = true,
            style = CalendarStyle.MONTH
        ),
        selection = CalendarSelection.Date { newDate ->
            selectedDate.value = newDate
        }
    )


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxWidth()){
            Button(
                onClick = {
                    showDialogLeaving = true
                },
                shape = CircleShape,
                modifier = Modifier
                    .size(55.dp)
                    .padding(5.dp),
                contentPadding = PaddingValues(1.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.back),
                    modifier = Modifier
                        .size(35.dp)
                )
            }
        }
        Row {
            Text(
                text = stringResource(id = R.string.subject_label),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                value = subject,
                onValueChange = { subject = it },
                singleLine = true,
                modifier = Modifier.weight(3f),
                label = { Text(text = stringResource(id = R.string.subject_label)) }
            )
        }
        Row {
            Text(
                text = stringResource(id = R.string.title_label),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                value = title,
                onValueChange = { title = it },
                singleLine = true,
                modifier = Modifier.weight(3f),
                label = { Text(text = stringResource(id = R.string.title_label)) }
            )
        }
        Row {
            Text(text = stringResource(id = R.string.description_label))

            TextField(
                value = description ?: "",
                onValueChange = { description = it },
                singleLine = true,
                label = { Text(text = stringResource(id = R.string.description_label)) }
            )
        }
        Row {
            Text(text = stringResource(id = R.string.deadline))
            Text(
                text = selectedDate.value.toString(),
                modifier = Modifier.clickable{
                    datePickerState.show()
                }
            )
            Text(text = stringResource(id = R.string.supportingText))
        }
        Button(onClick = {
            if (subject.isBlank() || title.isBlank() || selectedDate.value == null) {
                showDialogError = true
                return@Button
            }

            if(description?.isBlank() == true){
                description = ""
            }

            homework.subject = subject
            homework.title = title
            homework.description = description
            homework.deadline = selectedDate.value!!

            dvm.saveHomework(homework)
            context.startActivity(Intent(context, HomeworkActivity::class.java))
        }) {
            Text("Save Homework")
        }
    }

    if (showDialogError) {
        AlertDialog(
            onDismissRequest = { showDialogError = false },
            title = {
                Text(text = stringResource(id = R.string.errorTittle))
            },
            text = {
                Text(text = stringResource(id = R.string.errorCreatingHomework))
            },
            confirmButton = {
                Button(
                    onClick = { showDialogError = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
    if (showDialogLeaving) {
        AlertDialog(
            onDismissRequest = { showDialogLeaving = false },
            title = {
                Text(text = stringResource(id = R.string.infoTitle))
            },
            text = {
                Text(text = stringResource(id = R.string.leavingInfo))
            },
            confirmButton = {
                Button(
                    onClick = { context.startActivity(Intent(context, HomeworkActivity::class.java)) }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialogLeaving = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}