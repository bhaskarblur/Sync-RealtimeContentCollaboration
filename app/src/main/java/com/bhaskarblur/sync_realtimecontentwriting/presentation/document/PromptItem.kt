package com.bhaskarblur.sync_realtimecontentwriting.presentation.document

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.UiUtils
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.PromptModel

@Composable
fun PromptItem(
    promptMessage: PromptModel,
    onAdd: (message: String) -> Unit
) {
    Row {

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = when (promptMessage.role) {
                "user" -> Alignment.End
                else -> Alignment.Start
            },
        ) {

                Text(
                    when (promptMessage.sentBy?.fullName) {
                        "" -> "AI Assistant"
                        null -> "AI Assistant"
                        else -> promptMessage.sentBy.fullName.toString()
                    }, color = Color.White, fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Default,
                    lineHeight = 20.sp
                )

            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .background(
                        if (promptMessage.role.equals("user")) Color(0xFF222324) else Color(
                            0xFF6105E2
                        ),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    promptMessage.message, color = Color.White, fontSize = 14.sp,
                    fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default,
                    lineHeight = 20.sp
                )

                if (promptMessage.role.equals("assistant")) {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        Modifier
                            .border(2.dp, Color.White, RoundedCornerShape(90.dp)).
                            padding(vertical = 8.dp, horizontal = 12.dp)
                            .clickable {
                                onAdd(promptMessage.message)
                            }
                    ) {

                        Icon(
                            Icons.Filled.AddCircle,
                            contentDescription = "Apply to board",
                            tint = Color.White,
                            modifier = Modifier
                                .height(16.dp)

                        )
                        Spacer(Modifier.width(4.dp))

                        Text(
                            "Add to board", color = Color.White, fontSize = 12.sp,
                            fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default,
                            lineHeight = 20.sp
                        )

                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(
                    UiUtils.getDateTime(promptMessage.timeStamp.toString())!!,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )

            }

            Spacer(modifier = Modifier.height(24.dp))

        }
    }


}

