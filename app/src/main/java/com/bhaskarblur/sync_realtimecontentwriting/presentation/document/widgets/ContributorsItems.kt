package com.bhaskarblur.sync_realtimecontentwriting.presentation.document.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModelCursor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary

@Composable
fun ContributorsItems(item : UserModelCursor, onClick: (position: Int) -> Unit) {
    Spacer(modifier = Modifier.width(8.dp))
    Row(
        Modifier
            .background(item.color, RoundedCornerShape(90.dp))
            .border(1.5.dp, color = textColorPrimary, shape = RoundedCornerShape(90.dp))
            .width(36.dp)
            .height(36.dp)
            .clickable { onClick(item.position?: -1) },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {

        item.userDetails?.fullName?.let {
            if((item.userDetails.fullName?.length ?: 0) > 0) {
                Text(
                    item.userDetails.fullName!![0].toString().uppercase(), fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, color = textColorPrimary
                )
            }
        }
    }

}