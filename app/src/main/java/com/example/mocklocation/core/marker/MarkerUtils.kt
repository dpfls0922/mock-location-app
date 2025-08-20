package com.example.mocklocation.core.marker

import android.content.Context
import android.graphics.*
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.example.mocklocation.core.helper.MarkerType
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt

fun createEndpointMarker(
    context: Context,
    type: MarkerType,
    text: String
): Bitmap {
    val (width, height) = type.iconSize
    val bitmapWidth = width.toInt()
    val bitmapHeight = height.toInt() + 30

    val bitmap = createBitmap(bitmapWidth, bitmapHeight)
    val canvas = Canvas(bitmap)

    val markerRect = computeMarker(context, type.resId, bitmapWidth, bitmapHeight)
    drawMarkerPinImage(context, canvas, type.resId, markerRect)

    drawCenteredText(
        canvas = canvas,
        rect = markerRect,
        text = text,
        bitmapWidth = bitmapWidth,
    )
    return bitmap
}

private fun computeMarker(
    context: Context,
    @DrawableRes markerResId: Int,
    bitmapWidth: Int,
    bitmapHeight: Int
): Rect {
    val drawable = ContextCompat.getDrawable(context, markerResId)
    val iconWidth = drawable?.intrinsicWidth ?: bitmapWidth
    val iconHeight = drawable?.intrinsicHeight ?: (bitmapHeight / 2)
    val maxWidth = bitmapWidth - 12
    val maxHeight = bitmapHeight - 20
    val drawWidth = minOf(iconWidth, maxWidth)
    val drawHeight = minOf(iconHeight, maxHeight)
    val left = (bitmapWidth - drawWidth) / 2
    val top = bitmapHeight - drawHeight - 4
    return Rect(left, top, left + drawWidth, top + drawHeight)
}

private fun drawMarkerPinImage(
    context: Context,
    canvas: Canvas,
    @DrawableRes markerResId: Int,
    bounds: Rect
) {
    val drawable = ContextCompat.getDrawable(context, markerResId)
    drawable?.setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom)
    drawable?.draw(canvas)
}

private fun drawCenteredText(
    canvas: Canvas,
    rect: Rect,
    text: String,
    bitmapWidth: Int,
) {
    val displayedText = text.take(4)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        this.textSize = textSize
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        setShadowLayer(3f, 0f, 2f, "#80000000".toColorInt())
    }

    val centerX = bitmapWidth / 2f
    val centerY = rect.top + rect.height() / 2f
    val textY = centerY - (paint.descent() + paint.ascent()) / 2 - 5f
    canvas.drawText(displayedText, centerX, textY, paint)
}

fun createDefaultMarker(
    context: Context,
    type: MarkerType
): Bitmap? {
    val (width, height) = type.iconSize
    val bitmapWidth = width.toInt()
    val bitmapHeight = height.toInt()

    val drawable = ContextCompat.getDrawable(context, type.resId) ?: return null
    val bitmap = createBitmap(bitmapWidth, bitmapHeight)
    val canvas = Canvas(bitmap)

    drawable.setBounds(0, 0, bitmapWidth, bitmapHeight)
    drawable.draw(canvas)
    return bitmap
}