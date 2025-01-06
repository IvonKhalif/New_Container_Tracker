package com.example.containertracker.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.containertracker.R
import com.example.containertracker.databinding.DropDownWidgetBinding

class DropDownWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    var binding: DropDownWidgetBinding

    init {
        val layoutInflater = LayoutInflater.from(context)
        binding = DropDownWidgetBinding.inflate(layoutInflater, this, false)
        addView(binding.root)
    }

    fun setTitle(value: String): DropDownWidget {
        binding.dropSelectBasicTextCaption.text = value
        return this
    }

    fun setText(value: String): DropDownWidget {
        binding.dropSelectBasicText.text = value
        binding.dropSelectBasicText.setTextColor(
            ContextCompat.getColorStateList(
                context,
                R.color.black_484848
            )
        )
        return this
    }

    fun setTextForHint(hint: String): DropDownWidget {
        binding.dropSelectBasicText.text = hint
        binding.dropSelectBasicText.setTextColor(
            ContextCompat.getColorStateList(
                context,
                R.color.gray_ADABAB
            )
        )
        return this
    }

    fun setEnabledClick(enabled: Boolean): DropDownWidget {
        binding.root.isEnabled = enabled
        return this
    }

    fun onClickDropDownListener(onClickListener: () -> Unit): DropDownWidget {
        binding.root.setOnClickListener { onClickListener() }
        return this
    }

    fun setDrawableEnd(drawableId: Int): DropDownWidget {
        binding.dropSelectBasicTextCaption.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            drawableId,
            0
        )
        return this
    }

    fun setRequired(isRequired: Boolean): DropDownWidget {
        binding.dropSelectBasicTextMandatoryCaption.isVisible = isRequired
        return this
    }
}