package com.example.testsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testsapp.ui.theme.TestsAppTheme
import java.util.*

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestsAppTheme {
                Surface(
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colors.background) {
                    val state = viewModel.uiState.observeAsState().value
                    when (state) {
                        is TestState.Test -> TestScreen(
                            state,
                            onDonePressed = {
                                viewModel.computeResult(state)
                            })
                        is TestState.Result -> {
                            ResultScreen(state) {
                                viewModel.clearTest()
                            }
                        }
                        else -> {
                            var canonical by rememberSaveable { mutableStateOf(true) }
                            Greeting(name = "КАКОИ ЖИВОТНЕ ВЫ КУСАИТИ",
                            onButtonPressed = {
                                viewModel.startTest(canonical)
                            },
                            useCanonical = canonical,
                            onCanonicalChange = {
                                canonical = !canonical
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String,
             onButtonPressed: () -> Unit,
             useCanonical: Boolean,
             onCanonicalChange: () -> Unit) {
    Scaffold(
        topBar = {
            TopBar(name = name)
        },
        content = {
            Row(modifier = Modifier
                .clickable { onCanonicalChange() }
                .padding(8.dp)) {
                Checkbox(checked = useCanonical,
                    onCheckedChange = null)
                Text(text = " Каноничное написание")
            }
        },
        bottomBar = {
            BottomButton(
                text = "Пройти тест",
                onButtonPressed = onButtonPressed
            )
        }
    )
}

@Composable
fun TestScreen(test: TestState.Test,
               onDonePressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopBar(name = test.testName)
        },
        content = {
            Column {
                Question(questionState = test.questions[test.currentQuestion],
                    useCanonical = test.useCanonical)
            }
        },
        bottomBar = {
            BottomButtons(
                state = test.questions[test.currentQuestion],
                onPreviousPressed = { test.currentQuestion-- },
                onNextPressed = { test.currentQuestion++ },
                onDonePressed = onDonePressed
            )
        }
    )
}

@Composable
fun ResultScreen(testResult: TestState.Result,
                 onButtonPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(name = "Результаты теста")
        },
        content = {
            Column() {
                Text(text = testResult.outcomeText)
                Image(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    painter = painterResource(id = testResult.outcomeImage),
                    contentScale = ContentScale.Crop,
                    contentDescription = null, // decorative element

                )
            }

        },
        bottomBar = {
            BottomButton(
                text = "Вернуться обратно",
                onButtonPressed = onButtonPressed
            )
        }
    )
}

@Composable
fun TopBar(name: String) {
    Text(
        modifier = Modifier
            .padding(bottom = 24.dp)
            .background(
                color = Color(0xffaaaaaa),
                shape = MaterialTheme.shapes.small
            )
            .padding(top = 24.dp, bottom = 20.dp)
            .fillMaxWidth(),
        fontSize = 22.sp,
        textAlign = TextAlign.Center,
        text = name)
}

@Composable
fun BottomButtons(
    state: QuestionState,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    onDonePressed: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        if (state.showPrevious) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onPreviousPressed
            ) {
                Text(text = "Назад")
            }
            Spacer(modifier = Modifier.width(16.dp))
        }

        if (state.showDone) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onDonePressed
            ) {
                Text(text = "Результат")
            }
        } else {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onNextPressed
            ) {
                Text(text = "Дальше")
            }
        }
    }
}

@Composable
fun BottomButton(text: String,
                 onButtonPressed: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onButtonPressed
        ) {
            Text(text = text)
        }
    }
}

@Composable
fun Question(questionState: QuestionState,
             useCanonical: Boolean) {
    if (useCanonical) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            fontSize = 18.sp,
            text = questionState.question.questionText)
    } else {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            fontSize = 18.sp,
            text = questionState.question.questionText.toLowerCase(Locale.ROOT))
    }
    LazyColumn(
        modifier = Modifier.padding(top = 24.dp, bottom = 58.dp)
    ) {
        item {
            val options = questionState.question.options
            for ((index, option) in options.withIndex()) {
                val selected = questionState.answered?.let {
                    questionState.answered == index
                } ?: false
                Surface(
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .selectable(
                            selected = selected
                        ) {
                            questionState.answered = index
                        },
                ) {
                    Option(option.text, lowerCase = !useCanonical, selected = selected)
                }
            }
        }
    }
}

@Composable
fun Option(text: String,
           lowerCase: Boolean,
           selected: Boolean) {
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null)

        Text(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
            text = if (lowerCase) {
                text.toLowerCase(Locale.ROOT)
            } else {
                text
            })
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val question = QuestionState(
        Question(questionText = "ЕСЛИ ЖВОТНЕ ЗАНЯТО КУСАНИЕМ В ДАННЫЙ МОМЕНТ ДОЖДЁТЕСЬ ЛИ ВЫ ПОКА ОНО ЗАКОНЧИТ ИЛИ ПРЕРВЁТЕ ЕВО?",
            options = listOf(
                Option("Я ДОЖДУСЬ ПОКА КУСАНИЕ БУДЕТ ЗАКОНЧЕНО", 0),
                Option("Я НЕДОЖДУС И БУДУ КУСАТ ЖВОТНЕ ПОВЕРХ ЕГО КУСАНИЯ", 0)
            )),
        showPrevious = false,
        showDone = false,
    )
    TestsAppTheme {
        Surface(color = MaterialTheme.colors.surface) {
            Column(modifier = Modifier.padding(12.dp)) {
                Question(question, useCanonical = true)
            }
        }
    }
}