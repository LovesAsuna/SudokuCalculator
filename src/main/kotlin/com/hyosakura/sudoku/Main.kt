package com.hyosakura.sudoku

import androidx.compose.ui.window.application

fun main() = application {
    Application(rememberApplicationState(::exitApplication))
}
