package com.hyosakura.sudoku.window

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainWindow(state: WindowState) {
    val scope = rememberCoroutineScope()

    fun exit() = scope.launch { state.exit() }

    Window(
        state = state.window,
        title = "数独计算器",
        onCloseRequest = { exit() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(630.dp, 630.dp)
                ) {
                    for (i in 0..8) {
                        Row(modifier = Modifier.offset(y = (i * 70).dp).height(70.dp)) {
                            for (j in 0..8) {
                                Column {
                                    var active by remember { mutableStateOf(false) }
                                    OutlinedTextField(
                                        value = state.texts[i][j].text,
                                        onValueChange = {
                                            if (it.length >= 2) {
                                                state.texts[i][j].text = it.substring(1, 2)
                                            } else {
                                                state.texts[i][j].text = it
                                            }
                                        },
                                        modifier = Modifier.size(70.dp, 70.dp).pointerMoveFilter(
                                            onEnter = {
                                                active = true
                                                false
                                            },
                                            onExit = {
                                                active = false
                                                false
                                            }
                                        ),
                                        textStyle = TextStyle(
                                            color = Color.Black,
                                            fontSize = @OptIn(ExperimentalUnitApi::class) TextUnit(
                                                2.5F,
                                                TextUnitType.Em
                                            ),
                                            textAlign = TextAlign.Center
                                        ),
                                        colors = if (active) {
                                            TextFieldDefaults.outlinedTextFieldColors(
                                                unfocusedBorderColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high)
                                            )
                                        } else {
                                            TextFieldDefaults.outlinedTextFieldColors(backgroundColor = state.texts[i][j].color)
                                        }
                                    )

                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                var job by remember { mutableStateOf<Job?>(null) }
                Row {
                    Button(
                        onClick = {
                            scope.launch {
                                job?.cancel()
                                state.texts.forEach { row ->
                                    row.forEach {
                                        it.text = ""
                                    }
                                }
                                clear(state)
                            }
                        },
                    ) {
                        Text("Clear!")
                    }
                    Spacer(modifier = Modifier.width(100.dp))
                    Button(
                        onClick = {
                            job = scope.launch {
                                backtrack(state, 0, 0)
                            }
                        },
                    ) {
                        Text("Run!")
                    }
                }
            }
        }
    }
}

fun clear(state: WindowState) {
    state.texts.forEach { row ->
        row.forEach {
            it.color = Color.Transparent
        }
    }
}

suspend fun tag(state: WindowState, i: Int, j: Int) {
    if (i !in 0..8 || j !in 0..8) {
        return
    }
    fun clear() {
        state.texts.forEach { row ->
            row.forEach {
                it.color = Color.Transparent
            }
        }
    }
    clear()
    for (k in 0..8) {
        state.texts[i][k].color = Color.Gray
    }
    for (k in 0..8) {
        state.texts[k][j].color = Color.Gray
    }
    if (i - 1 >= 0 && j - 1 >= 0) {
        state.texts[i - 1][j - 1].color = Color.Gray
    }
    if (i - 1 >= 0 && j + 1 <= 8) {
        state.texts[i - 1][j + 1].color = Color.Gray
    }
    if (i + 1 <= 8 && j - 1 >= 0) {
        state.texts[i + 1][j - 1].color = Color.Gray
    }
    if (i + 1 <= 8 && j + 1 <= 8) {
        state.texts[i + 1][j + 1].color = Color.Gray
    }
    delay(1)
}

suspend fun backtrack(state: WindowState, i: Int, j: Int): Boolean {
    tag(state, i, j)
    val board = state.texts
    if (j == 9) {
        // 穷举到最后一列的话就换到下一行重新开始。
        return backtrack(state, i + 1, 0)
    }
    if (i == 9) {
        // 找到一个可行解，触发 base case
        return true
    }
    if (board[i][j].text.isNotEmpty()) {
        // 如果有预设数字，不用我们穷举
        return backtrack(state, i, j + 1)
    }
    var ch = '1'
    while (ch <= '9') {
        // 如果遇到不合法的数字，就跳过
        if (!isValid(state, i, j, ch.toString())) {
            ch++
            continue
        }
        board[i][j].text = ch.toString()
        // 如果找到一个可行解，立即结束
        if (backtrack(state, i, j + 1)) {
            return true
        }
        board[i][j].text = ""
        ch++
    }
    // 穷举完 1~9，依然没有找到可行解，此路不通
    return false
}

fun isValid(state: WindowState, r: Int, c: Int, n: String): Boolean {
    val board = state.texts
    for (i in 0..8) {
        // 判断行是否存在重复
        if (board[r][i].text == n) return false
        // 判断列是否存在重复
        if (board[i][c].text == n) return false
        // 判断 3 x 3 方框是否存在重复
        if (board[r / 3 * 3 + i / 3][c / 3 * 3 + i % 3].text == n) return false
    }
    return true
}