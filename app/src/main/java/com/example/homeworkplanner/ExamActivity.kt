package com.example.homeworkplanner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.homeworkplanner.ui.theme.HomeworkPlannerTheme

class ExamActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    val sharedPref = applicationContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    val isDark = sharedPref.getBoolean(applicationContext.getString(R.string.theme_key), true)


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
        setContent {
            HomeworkPlannerTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExamScene(themeViewModel, viewModel)
                }
            }
        }
    }
}

@Composable
fun ExamScene(
    themeViewModel: ThemeViewModel,
    dvm: DataModelViewModel
) {
    val context = LocalContext.current
//    val homeworkList = dvm.getAllExams().collectAsState(initial = emptyList())


    Column (
        modifier = Modifier.fillMaxSize()
    ){
        Box {

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
        }
//        LazyColumn{items(homeworkList.value)
//        {item ->
//            ExamCard(exam = item, dvm)
//        }
//        }
        Spacer(modifier = Modifier.weight(2f))
        Box (
            Modifier.fillMaxWidth()
        ){
            Button(
                onClick = {
                    context.startActivity(Intent(context, AddExamActivity::class.java))
                },
                shape = CircleShape,
                modifier = Modifier
                    .size(55.dp)
                    .padding(5.dp)
                    .align(Alignment.BottomEnd),
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
    }
}


@Composable
fun ExamCard(
    exam: Exam,
    dvm: DataModelViewModel) {

    val context = LocalContext.current
    var showDialogDelete by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp,
    ) {
        Box(contentAlignment = Alignment.TopEnd){
            Column {
                Button(
                    onClick = {
                        val intent = Intent(context, AddExamActivity::class.java)
                        intent.putExtra("id", exam.uid.toString())
                        intent.putExtra("subject", exam.subject)
                        intent.putExtra("title", exam.title)
                        intent.putExtra("description", exam.description)
                        intent.putExtra("deadline", exam.deadline.toString())
                        context.startActivity(intent)
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .size(55.dp)
                        .padding(5.dp),
                    contentPadding = PaddingValues(1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = stringResource(id = R.string.editHomework),
                        modifier = Modifier
                            .size(35.dp)
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
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "${stringResource(id = R.string.subject_label)}: ${exam.subject}")
            Text(text = "${stringResource(id = R.string.title_label)}: ${exam.title}")
            if (exam.description!!.length > 26){
                Text(text = "${stringResource(id = R.string.description_label)}: ${exam.description?.substring(0,25)}")
            }else{
                Text(text = "${stringResource(id = R.string.description_label)}: ${exam.description}")
            }
            Text(text = "${stringResource(id = R.string.deadline_label)}: ${exam.deadline}")
        }
    }
    if (showDialogDelete) {
        AlertDialog(
            onDismissRequest = { showDialogDelete = false },
            title = {
                Text(text = stringResource(id = R.string.infoTitle))
            },
            text = {
                Text(text = stringResource(id = R.string.dealetingInfo))
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialogDelete = false
//                        dvm.delete(exam)
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
