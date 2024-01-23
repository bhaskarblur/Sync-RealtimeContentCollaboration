package com.bhaskarblur.sync_realtimecontentwriting.presentation.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.TextSizeModel
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.colorSecondary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.primaryColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorSecondary

@Composable
fun UnitDropDown(selectedValue: Int, listItems : List<TextSizeModel>, onClosed : () -> Unit,
                 onSelected : (TextUnit) -> Unit) {
    val expanded = remember {
        mutableStateOf(true)
    }
    DropdownMenu(
        modifier = Modifier.background(colorSecondary),
        expanded = expanded.value,
        onDismissRequest = {
            expanded.value = false
            onClosed()}
    ) {
        LazyColumn {
            items(listItems, key = {
                it.label
            }) {

                Text(it.label, color = when(selectedValue) {
                    it.size.value.toInt() -> primaryColor
                    else -> textColorSecondary
                },
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                    onSelected(it.size)
                    onClosed()
                })
            }
        }
    }
}