package com.bhaskarblur.sync_realtimecontentwriting.core.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.FontFamilyModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.TextSizeModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.tabScreens.TabScreenBottomModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.tabScreens.TabScreens

object UIValuesConstant {
    val textSizeList = listOf(
        TextSizeModel("0 sp",0.sp), TextSizeModel("2 sp",2.sp),
        TextSizeModel("4 sp", 4.sp), TextSizeModel("6 sp", 6.sp),
        TextSizeModel("8 sp",8.sp), TextSizeModel("10 sp", 10.sp),
        TextSizeModel("12 sp", 12.sp), TextSizeModel("14 sp", 14.sp),
        TextSizeModel("16 sp", 16.sp), TextSizeModel("18 sp", 18.sp),
        TextSizeModel("20 sp", 20.sp), TextSizeModel("22 sp", 22.sp),
        TextSizeModel("24 sp", 24.sp), TextSizeModel("26 sp", 26.sp),
        TextSizeModel("28 sp", 28.sp), TextSizeModel("30 sp", 30.sp),
        TextSizeModel("32 sp", 32.sp), TextSizeModel("34 sp", 34.sp),
        TextSizeModel("36 sp", 36.sp), TextSizeModel("38 sp", 38.sp),
        TextSizeModel("40 sp", 40.sp), TextSizeModel("42 sp", 42.sp),
        TextSizeModel("44 sp", 44.sp), TextSizeModel("46 sp", 46.sp),
        TextSizeModel("48 sp", 48.sp), TextSizeModel("50 sp", 50.sp),
    )

    val fontsList = listOf(
        FontFamilyModel("Default", FontFamily.Default),
        FontFamilyModel("Monospace", FontFamily.Monospace),
        FontFamilyModel("Sans-Serif", FontFamily.SansSerif),
        FontFamilyModel("Serif", FontFamily.Serif),
        FontFamilyModel("Cursive", FontFamily.Cursive),
    )

    val bottomItemsList = listOf<TabScreenBottomModel>(
        TabScreenBottomModel("Home", TabScreens.HomePage.route, Icons.Filled.Home),
        TabScreenBottomModel("My Documents", TabScreens.MyDocumentsPage.route, Icons.Filled.Dataset),
        TabScreenBottomModel("Profile", TabScreens.ProfilePage.route, Icons.Filled.AccountCircle),
    )
}