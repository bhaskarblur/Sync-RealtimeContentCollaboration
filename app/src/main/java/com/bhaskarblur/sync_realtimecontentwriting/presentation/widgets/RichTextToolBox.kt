package com.bhaskarblur.sync_realtimecontentwriting.presentation.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.colorSecondary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.primaryColor
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorPrimary
import com.bhaskarblur.sync_realtimecontentwriting.ui.theme.textColorSecondary
import com.mohamedrejeb.richeditor.model.RichTextState

@Composable
fun RichTextToolBox(
    state: RichTextState, onBoldClick: () -> Unit, onItalicClick: () -> Unit,
    onUnderlineClick: () -> Unit, onLineThroughClick: () -> Unit,
    onTextFontChange : () -> Unit, onTextSizeClick: () -> Unit, onTextBgClick: () -> Unit,
    onTextShadowClick: () -> Unit, onTextColorClick: () -> Unit,
    onTextLeftClick: () -> Unit, onTextRightClick: () -> Unit,
    onTextCenterClick: () -> Unit, onOrderedListClick: () -> Unit,
    onUnOrderedListClick: () -> Unit, onCodeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(colorSecondary)
    ) {
        Row(
            Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colorSecondary)
                .padding(vertical = 6.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = when (state.currentSpanStyle.fontWeight) {
                            FontWeight.Bold -> primaryColor
                            else -> colorSecondary
                        },
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onBoldClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    Icons.Filled.FormatBold, contentDescription = "Bold text",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = when (state.currentSpanStyle.fontStyle) {
                            FontStyle.Italic -> primaryColor
                            else -> colorSecondary
                        },
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onItalicClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    Icons.Filled.FormatItalic, contentDescription = "Italic text",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = when (state.currentSpanStyle.textDecoration) {
                            TextDecoration.Underline -> primaryColor
                            else -> colorSecondary
                        },
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onUnderlineClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    Icons.Filled.FormatUnderlined, contentDescription = "Underline text",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = when (state.currentSpanStyle.textDecoration) {
                            TextDecoration.LineThrough -> primaryColor
                            else -> colorSecondary
                        },
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onLineThroughClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    Icons.Filled.FormatStrikethrough, contentDescription = "Linethrough text",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = colorSecondary,
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onTextFontChange()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.FontDownload, contentDescription = "Font change text",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }


            Spacer(modifier = Modifier.width(8.dp))
            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = colorSecondary,
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onTextSizeClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.FormatSize, contentDescription = "Increase size text",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }


//            Spacer(modifier = Modifier.width(8.dp))
//            Column(
//                Modifier
//                    .height(32.dp)
//                    .width(32.dp)
//                    .background(
//                        color = colorSecondary,
//                        shape = RoundedCornerShape(90.dp)
//                    )
//                    .clickable {
//                    },
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Icon(
//                    Icons.Filled.TextDecrease, contentDescription = "Decrease size text",
//                    modifier = Modifier.size(22.dp),
//                    tint = textColorPrimary
//                )
//            }

            Spacer(modifier = Modifier.width(8.dp))
            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = colorSecondary,
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onTextColorClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.FormatColorText, contentDescription = "Color text",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = colorSecondary,
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onTextBgClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.FormatColorFill, contentDescription = "Color background text",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }


            Spacer(modifier = Modifier.width(8.dp))
            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = colorSecondary,
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onTextShadowClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.InvertColors, contentDescription = "Color shadow text",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Divider(
                color = textColorSecondary,
                modifier = Modifier
                    .height(20.dp)
                    .width(1.4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = when (state.currentParagraphStyle.textAlign) {
                            TextAlign.Left -> primaryColor
                            else -> colorSecondary
                        },
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onTextLeftClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.FormatAlignLeft, contentDescription = "Align left text",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = when (state.currentParagraphStyle.textAlign) {
                            TextAlign.Center -> primaryColor
                            else -> colorSecondary
                        },
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onTextCenterClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.FormatAlignCenter, contentDescription = "Align center text",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = when (state.currentParagraphStyle.textAlign) {
                            TextAlign.Right -> primaryColor
                            else -> colorSecondary
                        },
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onTextRightClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.FormatAlignRight, contentDescription = "Align right text",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Divider(
                color = textColorSecondary,
                modifier = Modifier
                    .height(20.dp)
                    .width(1.4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = when (state.isUnorderedList) {
                            true -> primaryColor
                            else -> colorSecondary
                        },
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onUnOrderedListClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.FormatListBulleted, contentDescription = "Unordered list",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = when (state.isOrderedList) {
                            true -> primaryColor
                            else -> colorSecondary
                        },
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onOrderedListClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.FormatListNumbered, contentDescription = "Ordered list",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Divider(
                color = textColorSecondary,
                modifier = Modifier
                    .height(20.dp)
                    .width(1.4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .background(
                        color = when (state.isCodeSpan) {
                            true -> primaryColor
                            else -> colorSecondary
                        },
                        shape = RoundedCornerShape(90.dp)
                    )
                    .clickable {
                        onCodeClick()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.Code, contentDescription = "Code block",
                    modifier = Modifier.size(22.dp),
                    tint = textColorPrimary
                )
            }
        }
    }

}