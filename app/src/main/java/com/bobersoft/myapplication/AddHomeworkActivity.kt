package com.bobersoft.myapplication

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
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.bobersoft.myapplication.Data.HomeworkDatabase
import com.bobersoft.homeworkplanner.R

import com.bobersoft.myapplication.ui.theme.HomeworkPlannerTheme
import com.bobersoft.myapplication.Data.DataModelViewModel
import java.time.LocalDate
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AddHomeworkActivity : ComponentActivity(){

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
                    return DataModelViewModel(db.dao, db.examDao) as T
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
        val time = intent.getStringExtra("time")
        val finished = intent.getBooleanExtra("finished", true)

        var homework = Homework("", "", description, LocalDate.now(), LocalTime.MIDNIGHT, false)

        if (id != null) {
            homework = Homework(subject!!, title!!, description, LocalDate.parse(deadline), LocalTime.parse(time), finished)
            homework.uid = id.toInt()
        }


        setContent {
            HomeworkPlannerTheme(darkTheme = isDark){
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AddHomeworkScreen(viewModel, homework)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHomeworkScreen(
    dvm: DataModelViewModel,
    homework: Homework
) {
    var subject by remember { mutableStateOf(homework.subject) }
    var title by remember { mutableStateOf(homework.title) }
    var description by remember { mutableStateOf(homework.description) }
    val selectedDate = remember { mutableStateOf<LocalDate?>(homework.deadline) }
    val selectedTime = remember { mutableStateOf<LocalTime?>(homework.time) }
    val finished = remember { mutableStateOf(homework.finished) }

    val datePickerState = rememberUseCaseState()
    val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val timePickerState = rememberUseCaseState()

    var showDialogError by remember { mutableStateOf(false)}
    var showDialogLeaving by remember { mutableStateOf(false)}

    val context = LocalContext.current

    CalendarDialog(
        state = datePickerState,
        config = CalendarConfig(
            yearSelection = true,
            monthSelection = true,
            style = CalendarStyle.WEEK
        ),
        selection = CalendarSelection.Date { newDate ->
            selectedDate.value = newDate
        }
    )

    ClockDialog(
        state = timePickerState,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            selectedTime.value = LocalTime.of(hours, minutes, 0)
        },
        config = ClockConfig(
            defaultTime = selectedTime.value,
            is24HourFormat = true
        )
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp)
    ) {
        //step back Icon
        Box(modifier = Modifier.fillMaxWidth()){
            Button(
                onClick = {
                    showDialogLeaving = true
                },
                shape = CircleShape,
                modifier = Modifier
                    .size(55.dp)
                    .padding(4.dp),
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
        Text(
            text = stringResource(id = R.string.addHomework),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        //subject textField
        Row (
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        ){
            Text(
                text = stringResource(id = R.string.subject_label),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                value = subject,
                onValueChange = { subject = it },
                singleLine = true,
                modifier = Modifier.weight(3f),
                label = { Text(text = stringResource(id = R.string.subject)) }
            )
        }
        //title TextField
        Row (
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        ){
            Text(
                text = stringResource(id = R.string.title_label),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                value = title,
                onValueChange = { title = it },
                singleLine = true,
                modifier = Modifier.weight(3f),
                label = { Text(text = stringResource(id = R.string.title)) }
            )
        }
        //description TextField
        Row(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.description),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                value = description ?: "",
                onValueChange = { description = it },
                singleLine = true,
                modifier = Modifier.weight(3f),
                label = { Text(text = stringResource(id = R.string.description)) }
            )
        }
        //datePickerCard
        Row(
            modifier = Modifier.padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.deadline_label),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = selectedDate.value?.format(outputFormatter).toString(),
                modifier = Modifier
                    .clickable {
                        datePickerState.show()
                    }
                    .align(Alignment.CenterVertically)
                    .weight(1f),
                textAlign = TextAlign.Center
            )
        }
        //timePickerCard
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp), // add this to make the row full width
        ) {
            Text(
                text = stringResource(id = R.string.time_label),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = selectedTime.value.toString(),
                modifier = Modifier
                    .clickable {
                        timePickerState.show()
                    }
                    .align(Alignment.CenterVertically)
                    .weight(1f),
                textAlign = TextAlign.Center
            )
        }
        // finished checkbox
        Row {
            Text(
                text = stringResource(id = R.string.finished_label),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
            Checkbox(
                checked = finished.value,
                onCheckedChange = {
                    finished.value = !finished.value
                },
                modifier = Modifier.size(45.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
        }
        Text(text = stringResource(id = R.string.supportingText),
            style = MaterialTheme.typography.bodySmall)
    }
    Box (
        Modifier.fillMaxWidth()
    ){
        Button(
            onClick = {
                if (subject.isBlank() || title.isBlank() || selectedDate.value == null || selectedTime.value == null) {
                    showDialogError = true
                    return@Button
                }

                if(description == null || description?.isBlank() == true){
                    description = ""
                }

                homework.subject = subject
                homework.title = title
                homework.description = description
                homework.deadline = selectedDate.value!!
                homework.time = selectedTime.value!!
                homework.finished = finished.value

                dvm.saveHomework(homework)
                context.startActivity(Intent(context, HomeworkActivity::class.java))
            },
            modifier = Modifier
                .padding(5.dp)
                .align(Alignment.BottomEnd)
        ) {
            Text(
                text = stringResource(id = R.string.saveHomework),
                style = MaterialTheme.typography.titleLarge)
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
                    Text(text = stringResource(id = R.string.cancel_label))
                }
            }
        )
    }
}
