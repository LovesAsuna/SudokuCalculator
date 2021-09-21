package com.hyosakura.sudoku

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hyosakura.sudoku.window.WindowState

/**
 * @author LovesAsuna
 **/
@Composable
fun rememberApplicationState(onCloseRequest: () -> Unit) = remember {
    ApplicationState(onCloseRequest)
}

class ApplicationState(onCloseRequest: () -> Unit) {
    val windowState = WindowState(onCloseRequest)
}