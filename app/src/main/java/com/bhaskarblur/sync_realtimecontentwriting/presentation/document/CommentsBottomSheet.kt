package com.bhaskarblur.sync_realtimecontentwriting.presentation.document

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.CommentsModel
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.DocumentModel
import com.bhaskarblur.sync_realtimecontentwriting.presentation.document.widgets.CommentItem
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.colorSecondary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.primaryColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorSecondary

@Composable
fun CommentsBottomSheet(
    viewModel: DocumentViewModel, data: DocumentModel, onAddComment: () -> Unit,
    onDeleteComment: (id: String) -> Unit
) {
    val commentsScrollState = rememberLazyListState()

    LaunchedEffect(key1 = data.commentsList) {
        Log.d("newCommentAdded","yes")
    }

    val configuration = LocalConfiguration.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorSecondary)
            .padding(horizontal = 18.dp, vertical = 6.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        )
        {

            Column(Modifier.fillMaxWidth(0.6f)) {
                Text(
                    text = "Comments", color = textColorPrimary,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "All the comments by contributors appear here.",
                    color = textColorSecondary,
                    style = TextStyle(
                        fontSize = 13.sp
                    )
                )
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                ),
                onClick = {
                },
                modifier = Modifier
                    .background(primaryColor, RoundedCornerShape(80.dp))
            ) {
                Text(
                    text = "+ Add New",
                    color = textColorPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        onAddComment()
                    }
                )

            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(state = commentsScrollState,
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeightIn(max = configuration.screenHeightDp.dp / 2)) {
            items(data.commentsList.reversed()) { comment ->
                Column {

                    CommentItem(
                        comment, canDeleteComment =
                        viewModel.userDetails.value.id == comment.commentBy?.id
                    ) { id ->
                        onDeleteComment(id)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

            }
        }
    }
}
