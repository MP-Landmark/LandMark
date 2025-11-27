package com.example.lendmark.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide

class PhotoPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    init {
        orientation = HORIZONTAL
    }

    fun setImages(urls: List<String>) {
        removeAllViews()

        urls.forEach { url ->
            val img = ImageView(context).apply {
                layoutParams = LayoutParams(180, 180).apply {
                    setMargins(10, 0, 10, 0)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            Glide.with(context)
                .load(url)
                .into(img)

            addView(img)
        }
    }
}
