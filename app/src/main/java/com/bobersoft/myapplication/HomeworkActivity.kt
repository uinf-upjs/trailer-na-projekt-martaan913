package com.bobersoft.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.bobersoft.homeworkplanner.R
import com.bobersoft.myapplication.Data.HomeworkDatabase.Companion.MIGRATION_1_2
import com.bobersoft.myapplication.ui.theme.HomeworkPlannerTheme
import com.bobersoft.myapplication.Data.DataModelViewModel
import com.bobersoft.myapplication.Data.HomeworkDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeworkActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = HomeworkDatabase::class.java,
            name = "datamodel.db"
        ).addMigrations(MIGRATION_1_2)
            .build()
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
        setContent {

            val sharedPref = applicationContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val isDark = sharedPref.getBoolean(applicationContext.getString(R.string.theme_key), true)

            HomeworkPlannerTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeworkScene(viewModel)
                }
            }
        }
    }
}

@Composable
fun HomeworkScene(
    dvm: DataModelViewModel
) {
    val context = LocalContext.current
    val homeworkList = dvm.getAllHomeworks().collectAsState(initial = emptyList())

    val sortedHw = homeworkList.value.sortedWith(compareBy<Homework> {it.deadline}.thenBy { it.time})

    val upcomingHomeWorkList: MutableList<Homework> = mutableListOf()
    val pastHomeWorkList: MutableList<Homework> = mutableListOf()

    for (homework: Homework in sortedHw) {
        if (homework.deadline<= LocalDate.now() && homework.finished){
            pastHomeWorkList.add(homework)
        }
        else{
            upcomingHomeWorkList.add(homework)
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
    ){
        Box (
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
            Button(
                onClick = {
                    context.startActivity(Intent(context, AddHomeworkActivity::class.java))
                },
                shape = CircleShape,
                modifier = Modifier
                    .size(55.dp)
                    .padding(5.dp)
                    .align(Alignment.TopEnd),
                contentPadding = PaddingValues(1.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(id = R.string.addHomework),
                    modifier = Modifier
                        .size(35.dp)
                )
            }
        }
        LazyColumn{items(upcomingHomeWorkList){item ->
                if (!item.finished){
                    HomeworkCard(homework = item, dvm)
                }else{
                    HomeworkCard(homework = item, dvm)
                }
            }
        }
        Text(text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(stringResource(id = R.string.pastHomeworks))
            }
        },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        LazyColumn{items(pastHomeWorkList){item ->
            if (!item.finished){
                HomeworkCard(homework = item, dvm)
            }else{
                HomeworkCard(homework = item, dvm)
            }
        }
        }
//        Spacer(modifier = Modifier.weight(2f))
        Box (
            Modifier.fillMaxWidth()
        ){

        }
    }
}

@Composable
fun HomeworkCard(
    homework: Homework,
    dvm: DataModelViewModel
) {

    val context = LocalContext.current
    var showDialogDelete by remember { mutableStateOf(false) }
    val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    var checked by remember { mutableStateOf(homework.finished) }
    val lineColor = MaterialTheme.colorScheme.onTertiaryContainer


    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, AddHomeworkActivity::class.java)
                intent.putExtra("id", homework.uid.toString())
                intent.putExtra("subject", homework.subject)
                intent.putExtra("title", homework.title)
                intent.putExtra("description", homework.description)
                intent.putExtra("deadline", homework.deadline.toString())
                intent.putExtra("time", homework.time.toString())
                intent.putExtra("finished", homework.finished)
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp,
    ) {
        Box(contentAlignment = Alignment.TopEnd){
            Row(
                modifier = Modifier.fillMaxHeight()
            ){
                if ((homework.deadline < LocalDate.now()) && !homework.finished){
                    Column (
                        modifier = Modifier.fillMaxHeight()
                    ){
                        Icon(
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = stringResource(id = R.string.lateHomework),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(75.dp)
                        )
                    }
                }
                Column {
                    Button(
                        onClick = {},
                        shape = CircleShape,
                        modifier = Modifier
                            .size(55.dp)
                            .padding(5.dp),
                        contentPadding = PaddingValues(1.dp)
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                checked = !checked
                                homework.finished = !homework.finished
                                dvm.saveHomework(homework)
                            },
                        )
                    }
                    Button(
                        onClick = {
                            showDialogDelete = true
                        },
                        shape = CircleShape,
                        modifier = Modifier
                            .size(55.dp)
                            .padding(5.dp),
                        contentPadding = PaddingValues(1.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(id = R.string.deleteHomework),
                            modifier = Modifier
                                .size(35.dp)
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            if (homework.subject.length > 20 && homework.deadline<= LocalDate.now() && homework.finished){
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.subject))
                        }
                        append(": ${homework.subject.substring(0,20)} ...")
                    }
                )
            }
            if (homework.subject.length > 25){
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.subject))
                        }
                        append(": ${homework.subject.substring(0,25)} ...")
                    }
                )
            }
            else{
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.subject))
                        }
                        append(": ${homework.subject}")
                    }
                )
            }

            if (homework.title.length > 20 && homework.deadline<= LocalDate.now() && homework.finished){
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.title))
                        }
                        append(": ${homework.title.substring(0,20)} ...")
                    }
                )
            }
            if (homework.title.length > 25){
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.title))
                        }
                        append(": ${homework.title.substring(0,25)} ...")
                    }
                )
            }
            else{
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.title))
                        }
                        append(": ${homework.title}")
                    }
                )
            }
            if (homework.description != null && homework.description!!.length > 35){
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.description))
                        }
                        append(": ${homework.description?.substring(0,35)} ...")
                    }
                )
            }
            else{
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.description))
                        }
                        append(": ${homework.description}")
                    }
                )
            }
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(id = R.string.deadline))
                    }
                    append(": ${homework.deadline.format(outputFormatter)}, ${homework.time}")
                }
            )
        }


        if (checked) {
            Canvas(modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)) {
                val strokeWidth = 4.dp.toPx()
                val y = size.height / 2
                drawLine(
                    color = lineColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
        }
    }

    if (showDialogDelete) {
        AlertDialog(
            onDismissRequest = { showDialogDelete = false },
            title = {
                Text(text = stringResource(id = R.string.infoTitle))
            },
            text = {
                Text(text = stringResource(id = R.string.deletingInfo))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialogDelete = false
                        dvm.delete(homework)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialogDelete = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

