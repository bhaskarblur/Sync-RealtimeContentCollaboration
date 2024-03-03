package com.bhaskarblur.sync_realtimecontentwriting.presentation.document.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.backgroundColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.colorSecondary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.primaryColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorSecondary
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddCommentDialog(
    onDismiss: () -> Unit,
    onPostComment: (String, String) -> Unit
) {
    var commentSubject by remember { mutableStateOf("") }
    var commentDescription by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    Dialog(
        onDismissRequest = {
            focusManager.clearFocus()
            softwareKeyboardController?.hide()
            onDismiss.invoke()
        }
    ) {
        Column(Modifier
            .fillMaxWidth()
            .background(colorSecondary, shape = RoundedCornerShape(14.dp))
            .padding(18.dp)
        ) {
            Text(
                text = "Add Comment", color = textColorPrimary,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

        TextField(
            shape = RoundedCornerShape(10.dp),
            value = commentSubject,
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = textColorPrimary,
                focusedTextColor = textColorPrimary,
                focusedPlaceholderColor = textColorSecondary,
                unfocusedPlaceholderColor = textColorSecondary,
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            textStyle = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 22.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    shape = RoundedCornerShape(90.dp),
                    color = colorSecondary
                ),
            onValueChange = { value ->
                commentSubject = value
            },
            placeholder = {
                Text("Write subject/context for which the comment is about")
            })

            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                shape = RoundedCornerShape(10.dp),
                value = commentDescription,
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = textColorPrimary,
                    focusedTextColor = textColorPrimary,
                    focusedPlaceholderColor = textColorSecondary,
                    unfocusedPlaceholderColor = textColorSecondary,
                    focusedContainerColor = backgroundColor,
                    unfocusedContainerColor = backgroundColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                ),
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 22.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        shape = RoundedCornerShape(90.dp),
                        color = colorSecondary
                    ),
                onValueChange = { value ->
                    commentDescription = value
                },
                placeholder = {
                    Text("A detailed description or the actual comment.")
                })

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                ),
                onClick = {
                    onPostComment(commentSubject, commentDescription)
                },
                modifier = Modifier
                    .background(primaryColor, RoundedCornerShape(80.dp))
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Post Comment",
                    color = textColorPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
        }
        }
    }
}
