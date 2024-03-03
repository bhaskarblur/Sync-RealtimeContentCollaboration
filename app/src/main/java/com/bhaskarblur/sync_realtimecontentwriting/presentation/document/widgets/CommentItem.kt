package com.bhaskarblur.sync_realtimecontentwriting.presentation.document.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.CommentsModel
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.chatBoxColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorSecondary

@Composable
fun CommentItem(
    comment: CommentsModel,
    canDeleteComment: Boolean,
    onDelete: (message: String) -> Unit
) {
    Row {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    chatBoxColor,
                    RoundedCornerShape(10.dp)
                )
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Text(
                    comment.commentBy?.fullName ?: "User",
                    color = textColorPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Default,
                    lineHeight = 18.sp
                )

                Text(
                    UiUtils.getDateTime(comment.commentDateTime.toString()).toString(),
                    color = textColorPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Default,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Text(
                    comment.commentText, color = textColorPrimary, fontSize = 15.sp,
                    fontWeight = FontWeight.Medium, fontFamily = FontFamily.Default,
                    lineHeight = 18.sp
                )

                if (canDeleteComment) {
                    Icon(Icons.Filled.Delete,
                        contentDescription = "Delete", tint = Color.Red,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable {
                                onDelete(comment.commentId)
                            })
                }
            }

            Spacer(modifier = Modifier.width(6.dp))
            Text(
                comment.description, color = textColorSecondary, fontSize = 14.sp,
                fontWeight = FontWeight.W400, fontFamily = FontFamily.Default,
                lineHeight = 16.sp
            )
            Spacer(Modifier.height(8.dp))



        }
    }
}


