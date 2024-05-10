package com.neo.textwithimage

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.neo.textwithimage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.text.setTextWithImage("Essa {motinha} é um recurso, e o {ninja} é dinâmico.")
    }
}

private val images = listOf(
    ImageFlag(
        flag = "motinha",
        image = ImageFlag.Image.Resource(R.drawable.motorbike)
    ),
    ImageFlag(
        flag = "android",
        image = ImageFlag.Image.Resource(R.drawable.android)
    ),
    ImageFlag(
        flag = "ninja",
        image = ImageFlag.Image.Download(
            url = "https://theme.zdassets.com/theme_assets/1588918/de57ba20259fdae2150de364248a120040d34a9c.png"
        )
    )
)

data class ImageFlag(
    val flag: String,
    val image: Image
) {
    sealed class Image {
        data class Resource(
            @DrawableRes val resource: Int
        ) : Image()

        data class Download(
            val url: String
        ) : Image()
    }

    companion object {
        val pattern = Regex("\\{\\w+\\}")
    }
}

private fun TextView.setTextWithImage(text: String) {

    val spannable = SpannableString(text)

    ImageFlag.pattern.findAll(input = text).forEach { result ->

        val flag = result.value.removeSurrounding(
            prefix = "{",
            suffix = "}"
        )

        val image = images.find { it.flag == flag }?.image
            ?: ImageFlag.Image.Resource(R.drawable.android)

        when (image) {
            is ImageFlag.Image.Download -> {

                Glide.with(context)
                    .load(image.url)
                    .into(
                        object : CustomTarget<Drawable>() {

                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?
                            ) {
                                resource.resize(textSize.toInt())

                                spannable.setSpan(
                                    ImageSpan(resource),
                                    result.range.first,
                                    result.range.endExclusive,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )

                                setText(spannable, TextView.BufferType.SPANNABLE)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) = Unit
                        }
                    )
            }

            is ImageFlag.Image.Resource -> {

                val drawable = AppCompatResources.getDrawable(
                    context,
                    image.resource
                )

                drawable?.resize(textSize.toInt())

                spannable.setSpan(
                    ImageSpan(checkNotNull(drawable)),
                    result.range.first,
                    result.range.endExclusive,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                setText(spannable, TextView.BufferType.SPANNABLE)
            }
        }
    }
}

private fun Drawable.resize(textSize: Int) {
    val width = (textSize * intrinsicWidth / intrinsicHeight)
    setBounds(0, 0, width, textSize)
}
