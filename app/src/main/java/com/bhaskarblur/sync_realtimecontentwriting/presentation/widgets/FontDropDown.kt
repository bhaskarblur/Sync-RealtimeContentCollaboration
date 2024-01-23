package com.bhaskarblur.sync_realtimecontentwriting.presentation.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.FontFamilyModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.TextSizeModel
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.colorSecondary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.primaryColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary

@Composable
fun FontDropDown(selectedValue: FontFamily, listItems : List<FontFamilyModel>, onClosed : () -> Unit,
                 onSelected : (FontFamily) -> Unit) {

    val expanded = remember {
        mutableStateOf(true)
    }
    Box(
        modifier = Modifier
            .background(colorSecondary)
            .padding(6.dp),
    ) {
        LazyColumn(Modifier.height(140.dp)) {
            items(listItems, key = {
                it.label
            }) {

                Text(it.label, color = when(selectedValue) {
                    it.fontFamily -> primaryColor
                    else -> textColorPrimary
                },
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            onSelected(it.fontFamily)
                            onClosed()
                        })
            }
        }
    }
}