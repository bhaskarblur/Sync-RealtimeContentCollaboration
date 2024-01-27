package com.bhaskarblur.sync_realtimecontentwriting.presentation.widgets

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatShapes
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
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
    onTextSizeClick: () -> Unit,
    onTextBgClick: () -> Unit, onTextColorClick: () -> Unit,
    onTextLeftClick: () -> Unit, onTextRightClick: () -> Unit,
    onTextCenterClick: () -> Unit, onOrderedListClick: () -> Unit,
    onUnOrderedListClick: () -> Unit, onCodeClick: () -> Unit,
    onLineHeightClick: () -> Unit, onLetterSpacingClick: () -> Unit
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

            ToolBoxIcon(icon = Icons.Filled.FormatBold, contentDescription = "Bold Text",
                onClick = {
                    onBoldClick()
                },isHighlighted = when (state.currentSpanStyle.fontWeight) {
                    FontWeight.Bold -> true
                    else -> false
                })

            Spacer(modifier = Modifier.width(8.dp))

            ToolBoxIcon(icon = Icons.Filled.FormatItalic, contentDescription = "Italic Text",
                onClick = {
                    onItalicClick()
                },isHighlighted =  when (state.currentSpanStyle.fontStyle) {
                    FontStyle.Italic -> true
                    else -> false
                })
            Spacer(modifier = Modifier.width(8.dp))

            ToolBoxIcon(icon = Icons.Filled.FormatUnderlined, contentDescription = "Underline Text",
                onClick = {
                    onUnderlineClick()
                },isHighlighted = when (state.currentSpanStyle.textDecoration) {
                    TextDecoration.Underline -> true
                    else -> false
                })

            Spacer(modifier = Modifier.width(8.dp))

            ToolBoxIcon(icon = Icons.Filled.FormatStrikethrough, contentDescription = "Linethrough Text",
                onClick = {
                    onLineThroughClick()
                },isHighlighted = when (state.currentSpanStyle.textDecoration) {
                    TextDecoration.LineThrough ->  true
                    else -> false
                })

            Spacer(modifier = Modifier.width(8.dp))

            ToolBoxIcon(icon = Icons.Filled.FormatSize, contentDescription = "Increase size Text",
                onClick = {
                    onTextSizeClick()
                },isHighlighted = false)


            Spacer(modifier = Modifier.width(8.dp))


            Column(
                Modifier
                    .height(34.dp)
                    .width(34.dp)
                    .background(
                        color = when (state.currentSpanStyle.color) {
                            Color.White -> colorSecondary
                            Color.Transparent -> colorSecondary
                            else -> state.currentSpanStyle.color
                        },
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
                    modifier = Modifier.size(24.dp),
                    tint = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Column(
                Modifier
                    .height(34.dp)
                    .width(34.dp)
                    .background(
                        color = when (state.currentSpanStyle.background) {
                            Color.White -> colorSecondary
                            Color.Transparent -> colorSecondary
                            else -> state.currentSpanStyle.background
                        },
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
                    modifier = Modifier.size(24.dp),
                    tint = textColorPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Divider(
                color = textColorSecondary,
                modifier = Modifier
                    .height(22.dp)
                    .width(1.4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            ToolBoxIcon(icon = Icons.Filled.FormatLineSpacing, contentDescription = "Line Height Text",
                onClick = {
                    onLineHeightClick()
                },isHighlighted = false)

            Spacer(modifier = Modifier.width(8.dp))

            ToolBoxIcon(icon = Icons.Filled.SortByAlpha, contentDescription = "Line Spacing Text",
                onClick = {
                    onLetterSpacingClick()
                },isHighlighted = false)

            Spacer(modifier = Modifier.width(8.dp))

            ToolBoxIcon(icon = Icons.Filled.FormatAlignLeft,
                contentDescription = "Align left Text",
                onClick = {
                    onTextLeftClick()
                },isHighlighted = when (state.currentParagraphStyle.textAlign) {
                    TextAlign.Left -> true
                    else -> false
                })

            Spacer(modifier = Modifier.width(8.dp))

            ToolBoxIcon(icon = Icons.Filled.FormatAlignCenter,
                contentDescription = "Align center Text",
                onClick = {
                    onTextCenterClick()
                },isHighlighted = when (state.currentParagraphStyle.textAlign) {
                    TextAlign.Center -> true
                    else -> false
                })

            Spacer(modifier = Modifier.width(8.dp))

            ToolBoxIcon(icon = Icons.Filled.FormatAlignRight,
                contentDescription = "Align right Text",
                onClick = {
                    onTextRightClick()
                },isHighlighted = when (state.currentParagraphStyle.textAlign) {
                    TextAlign.Right -> true
                    else -> false
                })

            Spacer(modifier = Modifier.width(8.dp))

            Divider(
                color = textColorSecondary,
                modifier = Modifier
                    .height(22.dp)
                    .width(1.4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            ToolBoxIcon(icon = Icons.Filled.FormatListBulleted,
                contentDescription = "Unordered list",
                onClick = {
                    onUnOrderedListClick()
                },isHighlighted = state.isUnorderedList)

            Spacer(modifier = Modifier.width(8.dp))

            ToolBoxIcon(icon = Icons.Filled.FormatListNumbered,
                contentDescription = "Ordered list",
                onClick = {
                    onOrderedListClick()
                },isHighlighted = state.isOrderedList)


            Spacer(modifier = Modifier.width(8.dp))

            Divider(
                color = textColorSecondary,
                modifier = Modifier
                    .height(22.dp)
                    .width(1.4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            ToolBoxIcon(icon = Icons.Filled.Code,
                contentDescription = "Code block",
                onClick = {
                    onCodeClick()
                },isHighlighted = state.isCodeSpan)
        }
    }

}