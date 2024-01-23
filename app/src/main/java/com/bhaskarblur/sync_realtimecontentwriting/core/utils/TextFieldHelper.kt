package com.bhaskarblur.sync_realtimecontentwriting.core.utils

import android.text.Spannable
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import com.bhaskarblur.sync_realtimecontentwriting.domain.model.UserModelCursor
import java.lang.IndexOutOfBoundsException
import java.lang.StringBuilder
import java.util.regex.Pattern

fun buildAnnotatedStringWithColors(items : List<UserModelCursor>, text : String): AnnotatedString{
    val builder = AnnotatedString.Builder()
    builder.append(text)
    try {
        for ((count, cursor) in items.withIndex()) {
            cursor.position?.let {
                if (cursor.position >= 0) {
                    if (text.length >= cursor.position) {
                        try {
                            builder.withStyle(style = SpanStyle(color = cursor.color)) {
                                if (text.length >= cursor.position) {
                                    if(cursor.position-1 >= 0) {
                                        Log.d("Span", "1")
                                        addStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 17.sp,
                                                background = cursor.color,
                                            ),
                                            cursor.position - 1,
                                            cursor.position
                                        )
                                    }
                                    else {
                                        addStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 17.sp,
                                                background = cursor.color,
                                            ),
                                            cursor.position,
                                            cursor.position +1
                                        )
                                    }
                                } else {
                                    Log.d("Span", "2")

                                    if(cursor.position-1 >= 0) {
                                        addStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 17.sp,
                                                background = cursor.color,
                                            ),
                                            cursor.position - 1,
                                            cursor.position
                                        )
                                    }
                                    else {
                                        addStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 17.sp,
                                                background = cursor.color,
                                            ),
                                            cursor.position,
                                            cursor.position +1
                                        )
                                    }
                                }

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            builder.withStyle(style = SpanStyle(color = cursor.color)) {
                                Log.d("Span", "3")
                                addStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 17.sp,
                                        background = cursor.color,
                                    ),
                                    text.length,
                                    text.length
                                )

                            }
                        } catch (e: IndexOutOfBoundsException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return builder.toAnnotatedString()
}

fun findFirstDifferenceIndex(str1: String, str2: String): Int {
    val minLength = minOf(str1.length, str2.length)

    for (i in 0 until minLength) {
        if (str1[i] != str2[i]) {
            return i
        }
    }

    // If the common prefix is the same for both strings, but one string is shorter
    // than the other, return the length of the shorter string.
    return if (str1.length < str2.length) str1.length else str2.length
}