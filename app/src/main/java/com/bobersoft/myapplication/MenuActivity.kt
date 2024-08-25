package com.bobersoft.myapplication

import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.bobersoft.homeworkplanner.Exam
import com.bobersoft.myapplication.Data.HomeworkDatabase
import com.bobersoft.homeworkplanner.R
 import com.bobersoft.myapplication.ui.theme.HomeworkPlannerTheme
import com.bobersoft.myapplication.Data.DataModelViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class MenuActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    private val db by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = HomeworkDatabase::class.java,
            name = "datamodel.db"
        ).addMigrations(HomeworkDatabase.MIGRATION_1_2)
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

            themeViewModel.isDarkTheme = isDark

            HomeworkPlannerTheme(darkTheme = themeViewModel.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    MainMenu(
                        themeViewModel,
                        viewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainMenu(
    themeViewModel: ThemeViewModel,
    dvm : DataModelViewModel
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    val sharedPref = context.getSharedPreferences("language_preferences", Context.MODE_PRIVATE)
    var selectedLanguage = sharedPref.getString(context.getString(R.string.language_key), "default_language_value")

    val homeworkList = dvm.getNotFinishedHw().collectAsState(initial = emptyList()).value
    val examList = dvm.getAllExams().collectAsState(initial = emptyList()).value
    val lineChartData = createGraph(homeworkList = homeworkList, examList)

    Column(
        Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ){
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

        LineChart(
            modifier = Modifier
                .height(200.dp),
            lineChartData = lineChartData,
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

@Composable
fun createGraph(homeworkList: List<Homework>, examList: List<Exam>): LineChartData {
    val homeworkData: MutableMap<Int, Int> = mutableMapOf()
    val examData: MutableMap<Int, Int> = mutableMapOf()

    for (i in 1 until 8){
        homeworkData[i] = 0
        examData[i] = 0
    }

    if (homeworkList.isNotEmpty()) {
        for (item in homeworkList){
            if (item.deadline >= LocalDate.now() && item.deadline < LocalDate.now().plusDays(7)) {
                Log.d("day", "${item.deadline} + ${LocalDate.now().plusDays(6)}" )
                val count = homeworkData.getOrDefault(item.deadline.dayOfWeek.value, 0)
                homeworkData[item.deadline.dayOfWeek.value] = count + 1
            }
        }
    }

    if (examData.isNotEmpty()) {
        for (item in examList){
            if (item.deadline >= LocalDate.now() && item.deadline < LocalDate.now().plusDays(7)) {
                Log.d("day", "${item.deadline} + ${LocalDate.now().plusDays(6)}" )
                val count = examData.getOrDefault(item.deadline.dayOfWeek.value, 0)
                examData[item.deadline.dayOfWeek.value] = count + 1
            }
        }
    }

    val today = LocalDate.now()
    val numbersList = listOf(1, 2, 3, 4, 5, 6, 7)
    var max = 1

    val sortedNumList = numbersList.subList(today.dayOfWeek.value - 1, numbersList.size) +
            numbersList.subList(0, today.dayOfWeek.value - 1)

    val pointsDataHw: MutableList<Point> = mutableListOf()
    val pointsDataExam: MutableList<Point> = mutableListOf()

    for ((index, i) in sortedNumList.withIndex()){
        pointsDataHw.add(Point(numbersList[index].toFloat(),homeworkData[i]!!.toFloat()))
        pointsDataExam.add(Point(numbersList[index].toFloat(),examData[i]!!.toFloat()))
        if(homeworkData[i]!!.toFloat() > max){
            max = homeworkData[i]!!
        }
        if(examData[i]!!.toFloat() > max){
            max = examData[i]!!
        }
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(40.dp)
        .steps(pointsDataHw.size-1)
        .labelAndAxisLinePadding(15.dp)
        .labelData { i ->
            val label = if (i != 0) {
                LocalDate.now().plusDays(i.toLong()).dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
            } else {
                "     ${LocalDate.now().dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()}"
            }
            return@labelData label        }
        .backgroundColor(MaterialTheme.colorScheme.surface)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(max)
        .axisStepSize(15.dp)
        .startPadding(8.dp)
        .labelData { i -> i.toString() }
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsDataHw,
                    LineStyle(LineType.SmoothCurve(), MaterialTheme.colorScheme.secondary),
                    null, null,
                    ShadowUnderLine(MaterialTheme.colorScheme.onSecondaryContainer)
                ),
                Line(
                    dataPoints = pointsDataExam,
                    LineStyle(LineType.SmoothCurve(), MaterialTheme.colorScheme.onTertiaryContainer),
                    null, null,
                    ShadowUnderLine(MaterialTheme.colorScheme.onSecondaryContainer)
                )
            ),
        ),
        backgroundColor = MaterialTheme.colorScheme.surface,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = MaterialTheme.colorScheme.onSecondaryContainer, enableHorizontalLines = false),
    )

    return lineChartData
}

