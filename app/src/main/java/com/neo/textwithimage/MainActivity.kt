package com.neo.textwithimage

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.neo.textwithimage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.text.setTextWithImage("Essa é a {motinha} rodando no {android}.")
    }
}

enum class Image(
    val flag: String,
    @DrawableRes val resource: Int
) {
    MOTORCYCLE(
        flag = "motinha",
        resource = R.drawable.motorbike
    ),
    ANDROID(
        flag = "android",
        resource = R.drawable.android
    );

    companion object {
        val pattern = Regex("\\{\\w+\\}")
    }
}

private fun TextView.setTextWithImage(text: String) {
    setText(
        SpannableString(text).apply {
            Image.pattern.findAll(input = text).forEach { result ->

                val flag = result.value.removeSurrounding(
                    prefix = "{",
                    suffix = "}"
                )

                val image = Image.entries.find {
                    it.flag == flag
                } ?: Image.ANDROID

                val drawable = AppCompatResources.getDrawable(
                    context,
                    image.resource
                )?.apply {
                    val height = textSize.toInt()
                    val width = (height * intrinsicWidth / intrinsicHeight)
                    setBounds(0, 0, width, height)
                }

                setSpan(
                    ImageSpan(checkNotNull(drawable)),
                    result.range.first,
                    result.range.endExclusive,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    )
}