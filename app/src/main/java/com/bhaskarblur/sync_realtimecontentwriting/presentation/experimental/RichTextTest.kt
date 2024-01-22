package com.bhaskarblur.sync_realtimecontentwriting.presentation.experimental

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.toSpannable
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.ColorHelper
import com.bhaskarblur.sync_realtimecontentwriting.core.utils.buildAnnotatedStringWithColors
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModelCursor
import com.bhaskarblur.sync_realtimecontentwriting.presentation.experimental.ui.theme.SyncRealtimeContentWritingTheme
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import kotlin.random.Random
import kotlin.random.nextInt

class RichTextTest : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SyncRealtimeContentWritingTheme {
                val state = rememberRichTextState()
                val sampleField = remember {
                    mutableStateOf(TextFieldValue(""))
                }
                val mutableCursorList = remember {
                    mutableListOf(UserModelCursor(null, Color.Red, 15),
                        UserModelCursor(null, Color.Yellow, 46),
                        UserModelCursor(null, Color.Green, 8))
                }

                LaunchedEffect(state.selection) {
                }
                LaunchedEffect(state.annotatedString.text) {
                    sampleField.value = TextFieldValue(
                        text = state.annotatedString.text,
                        selection = state.selection
                    )
                }
                Column(
                    Modifier
                        .fillMaxSize()
                        .clickable {
                            state.selection = TextRange(4)
                        }) {

                    Box {
                        TextField(
                            readOnly = true,
                            value = sampleField.value, onValueChange = {
                                sampleField.value = it
                            },
                            colors = TextFieldDefaults.colors(
                                disabledTextColor = Color.Transparent,
                                focusedTextColor = Color.Transparent,
                                unfocusedTextColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
                            ),
                            visualTransformation = {
                                TransformedText(
                                    buildAnnotatedStringWithColors(
                                        mutableCursorList, sampleField.value.text),
                                    OffsetMapping.Identity
                                )
                            },
                            textStyle = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier.fillMaxWidth())
                        RichTextEditor(
                            state = state, modifier = Modifier.fillMaxWidth(),
                            colors = RichTextEditorDefaults.richTextEditorColors(
                                containerColor = Color.Transparent,
                            ),
                            textStyle = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                lineHeight = 22.sp
                            ),
                        )
                    }
                    Text(text = "Make bOld", modifier = Modifier.clickable {
                        state.toggleSpanStyle((SpanStyle(fontWeight = FontWeight.Bold)))
                    })

                    Text(text = "Make Color blue", modifier = Modifier.clickable {
                        state.toggleSpanStyle((SpanStyle(color = Color.Blue)))
                    })

                    Text(text = "Make Bg Yellow", modifier = Modifier.clickable {
                        state.toggleSpanStyle((SpanStyle(background = Color.Yellow)))
                    })

                    Text(text = "Make text biger", modifier = Modifier.clickable {
                        state.toggleSpanStyle(
                            (SpanStyle(
                                fontSize = 24.sp
                            ))
                        )
                    })

                    Text(text = "Make text smaller", modifier = Modifier.clickable {
                        state.toggleSpanStyle(
                            (SpanStyle(
                                fontSize = 12.sp
                            ))
                        )
                    })
                    Text(text = "Add Link!", modifier = Modifier.clickable {
                        state.addLink(
                            text = "Compose Rich Editor",
                            url = "https://github.com/MohamedRejeb/Compose-Rich-Editor"
                        )
                    })

                    Text(text = "Add Code Block!", modifier = Modifier.clickable {
                        state.toggleCodeSpan()
//                        state.add
//                        state.addLink(
//                        )
                    })

                    Text(text = "Place random user cursor", modifier = Modifier.clickable {
                        val number = Random.nextInt(100 - 50 + 1) + 50
                        mutableCursorList.add(
                            UserModelCursor(null,
                            Color(ColorHelper.generateColor()), number)
                        )
                    })
                }
            }
        }
    }
}
