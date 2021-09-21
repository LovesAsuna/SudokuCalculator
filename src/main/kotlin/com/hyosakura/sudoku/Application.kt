package com.hyosakura.sudoku

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ApplicationScope
import com.hyosakura.sudoku.window.MainWindow

@Composable
fun Application(state: ApplicationState) {
    MainWindow(state.windowState)
}