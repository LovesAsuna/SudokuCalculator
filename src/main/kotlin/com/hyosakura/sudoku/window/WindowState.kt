package com.hyosakura.sudoku.window

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState

/**
 * @author LovesAsuna
 **/
class WindowState(private val onCloseRequest: () -> Unit) {
    val window = WindowState(width = 700.dp, height = 750.dp)
    var texts = mutableStateListOf<RowState>().also {
        for (i in 0..8) {
            it.add(RowState())
        }
    }

    fun exit() {
        onCloseRequest()
    }
}

class RowState : Iterable<ColumnState> {
    operator fun get(i: Int): ColumnState {
        return columns[i]
    }

    private val columns = mutableStateListOf<ColumnState>().also {
        for (i in 0..8) {
            it.add(ColumnState())
        }
    }

    override fun iterator() = columns.iterator()
}

class ColumnState {
    var text by mutableStateOf("")
    var color by mutableStateOf(Color.Transparent)
}