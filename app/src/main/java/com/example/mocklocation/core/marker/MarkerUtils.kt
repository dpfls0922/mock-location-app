package com.example.mocklocation.core.marker

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import com.example.mocklocation.core.helper.MarkerType
import androidx.core.graphics.createBitmap

fun createEndpointMarker(
    context: Context,
    type: MarkerType,
    text: String
): Bitmap {
    val baseWidth = 90
    val baseHeight = 90
    val scale = 3f
    val w = (baseWidth * scale).toInt()
    val h = (baseHeight * scale).toInt()

    val bitmap = createBitmap(w, h)
    val canvas = Canvas(bitmap)

    val drawable = ContextCompat.getDrawable(context, type.resId)
    drawable?.setBounds(0, 0, w, h)
    drawable?.draw(canvas)

    val textSize = 26f * scale
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        this.textSize = textSize
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    val circleCY = h / 2f
    val textY = circleCY - (paint.descent() + paint.ascent()) / 2 - 5f
    canvas.drawText(text, w / 2f, textY, paint)

    return Bitmap.createScaledBitmap(bitmap, baseWidth, baseHeight, true)
}

fun createDefaultMarker(
    context: Context,
    type: MarkerType
): Bitmap? {
    val (width, height) = type.iconSize
    val density = context.resources.displayMetrics.density
    val bitmapWidthPx = (width * density).toInt()
    val bitmapHeightPx = (height * density).toInt()

    val bitmap = createBitmap(bitmapWidthPx, bitmapHeightPx)
    val canvas = Canvas(bitmap)
    val drawable = ContextCompat.getDrawable(context, type.resId) ?: return null

    drawable.setBounds(0, 0, bitmapWidthPx, bitmapHeightPx)
    drawable.draw(canvas)
    return bitmap
}