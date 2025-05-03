package com.example.containertracker.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.text.InputFilter
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.example.containertracker.R
import com.example.containertracker.databinding.InputTextViewBinding
import com.example.containertracker.utils.Asterisk
import com.jakewharton.rxbinding4.widget.afterTextChangeEvents
import com.jakewharton.rxbinding4.widget.textChangeEvents

class InputTextViewWidget(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    var binding: InputTextViewBinding
    var label: String? = ""
    var isPasswordVisible = true
    private var isInputFilled: Boolean? = false

    init {
        val layoutInflater = LayoutInflater.from(context)
        binding = InputTextViewBinding.inflate(layoutInflater, this, false)
        addView(binding.root)

        val typeArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.InputTextViewWidget,
            0,
            0
        )

        var hint = typeArray.getText(R.styleable.InputTextViewWidget_inputTextViewHint)
        var label = typeArray.getText(R.styleable.InputTextViewWidget_inputTextViewLabel)
        var isMandatoryLabel =
            typeArray.getBoolean(R.styleable.InputTextViewWidget_inputTextViewMandatoryLabel, false)
        val inputTypeFlag = typeArray.getInt(R.styleable.InputTextViewWidget_inputTextViewInputType, 0)
        val isShowHidePassword = typeArray.getBoolean(R.styleable.InputTextViewWidget_inputTextViewIsShowHidePassword, true)
        val textAllCaps = typeArray.getBoolean(R.styleable.InputTextViewWidget_inputTextViewInfoAllCaps, false)
        val maxLength = typeArray.getInt(R.styleable.InputTextViewWidget_maxLength, -1)
        val isEditable = typeArray.getBoolean(R.styleable.InputTextViewWidget_isEditable, true)

        if (hint.isNullOrEmpty()) {
            hint = "Fill your hint here"
        } else if (label.isNullOrEmpty()) {
            label = "Label"
        }

        typeArray.recycle()

        setAttribute(
            label.toString(),
            isMandatoryLabel,
            hint.toString(),
            inputTypeFlag,
            isShowHidePassword,
            textAllCaps,
            maxLength,
            isEditable
        )

    }

    private fun setAttribute(
        label: String?,
        isMandatoryLabel: Boolean,
        hint: String?,
        inputTypeFlag: Int?,
        isShowHidePassword: Boolean,
        textAllCaps: Boolean,
        maxLength: Int,
        isEditable: Boolean
    ) {
        val inputFilter = mutableListOf<InputFilter>()
        if (maxLength > -1) {
            inputFilter.add(InputFilter.LengthFilter(maxLength))
        }
        if (textAllCaps) {
            inputFilter.add(InputFilter.AllCaps())
        }

        binding.inputfieldBasicInputBasic.filters = inputFilter.toTypedArray()
        binding.inputfieldBasicInputBasic.hint = hint
        binding.inputfieldBasicTextLabel.text = label
        binding.inputfieldBasicInputBasic.compoundDrawablePadding = 24
        setInputType(inputTypeFlag, isShowHidePassword)

        setRequired(isMandatoryLabel)
        setEditable(isEditable)

        binding.inputfieldBasicInputBasic.afterTextChangeEvents()
            .skipInitialValue()
            .subscribe {
                if (it.editable?.length!! >= 1) {
                    binding.inputfieldBasicInputBasic.setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.black_484848
                        )
                    )
                } else if (it.editable.isNullOrEmpty()) {
                    binding.inputfieldBasicInputBasic.setTextColor(
                        ContextCompat.getColorStateList(
                            context,
                            R.color.gray_ADABAB
                        )
                    )
                }
            }

        binding.inputfieldBasicInputBasic.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                binding.inputfieldBasicInputBasic.background.mutate()
                    .setColorFilter(ContextCompat.getColor(context, R.color.blue_lite_2196F3), PorterDuff.Mode.SRC_ATOP)

            } else {
                binding.inputfieldBasicInputBasic.background.mutate()
                    .setColorFilter(ContextCompat.getColor(context, R.color.stroke_EDEDED), PorterDuff.Mode.SRC_ATOP)

            }
        }
    }

    fun setRequired(isMandatoryLabel: Boolean) {
        if (isMandatoryLabel) {
            binding.inputfieldBasicTextMandatoryCaption.visibility = VISIBLE
        } else {
            binding.inputfieldBasicTextMandatoryCaption.visibility = GONE
        }
    }

    fun setEditable(isEditable: Boolean) {
        if (!isEditable) {
            binding.inputfieldBasicInputBasic.isFocusable = false
            binding.inputfieldBasicInputBasic.isFocusableInTouchMode = false
            binding.inputfieldBasicInputBasic.isClickable = false
            binding.inputfieldBasicInputBasic.isCursorVisible = false
        }
    }

    private fun setInputType(inputType: Int?, isShowHidePassword: Boolean) {
        when (inputType) {
            0 -> binding.inputfieldBasicInputBasic.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
            1 -> binding.inputfieldBasicInputBasic.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            2 -> binding.inputfieldBasicInputBasic.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
            3 -> binding.inputfieldBasicInputBasic.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            4 -> setInputPassword(isShowHidePassword)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setInputPassword(isShowHidePassword: Boolean) {
        binding.inputfieldBasicInputBasic.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        binding.inputfieldBasicInputBasic.transformationMethod = Asterisk()

        if (!isShowHidePassword) {
            binding.inputfieldBasicInputBasic.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                0,
                0
            )
            binding.inputfieldBasicInputBasic.setTextAppearance(context, R.style.LargePassword)
            return
        }

        binding.inputfieldBasicInputBasic.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_visibility_off,
            0
        )
        binding.inputfieldBasicInputBasic.setOnTouchListener { view, motionEvent ->
            val DRAWABLE_START = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_END = 2
            val DRAWABLE_BOTTOM = 3

            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >=
                    (binding.inputfieldBasicInputBasic.right - binding.inputfieldBasicInputBasic.compoundDrawables[DRAWABLE_END].bounds.width())
                ) {
                    if (isPasswordVisible) {
                        setVisibleOrInvisiblePassword(false, null, R.drawable.ic_visibility_on)
                    } else {
                        setVisibleOrInvisiblePassword(
                            true,
                            PasswordTransformationMethod.getInstance(),
                            R.drawable.ic_visibility_off
                        )
                    }
                    return@setOnTouchListener true
                }
            }

            return@setOnTouchListener false
        }
        binding.inputfieldBasicInputBasic.setTextAppearance(context, R.style.LargePassword)
    }

    private fun setVisibleOrInvisiblePassword(
        isPasswordVisible: Boolean,
        transformationMethod: PasswordTransformationMethod?,
        @DrawableRes iconVisibility: Int
    ) {
        this.isPasswordVisible = isPasswordVisible
        binding.inputfieldBasicInputBasic.transformationMethod = transformationMethod
        binding.inputfieldBasicInputBasic.text?.length?.let { length ->
            binding.inputfieldBasicInputBasic.setSelection(length)
        }
        binding.inputfieldBasicInputBasic.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onActionItemClicked(
                actionMode: ActionMode?,
                menuItem: MenuItem?
            ): Boolean {
                return false
            }

            override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(actionMode: ActionMode?) {

            }
        }

        binding.inputfieldBasicInputBasic.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconVisibility, 0)
    }

    fun setTextInput(label: String?) {
        binding.inputfieldBasicInputBasic.setText(label)
    }

    fun getTextInput(): String? {
        return binding.inputfieldBasicInputBasic.text.toString()
    }

    fun setError() {
        binding.inputfieldBasicInputBasic.requestFocus()
        binding.inputfieldBasicInputBasic.background.mutate()
            .setColorFilter(ContextCompat.getColor(context, R.color.red_F44336), PorterDuff.Mode.SRC_ATOP)
    }

    fun resetError() {
        binding.inputfieldBasicInputBasic.background.mutate()
            .setColorFilter(ContextCompat.getColor(context, R.color.stroke_EDEDED), PorterDuff.Mode.SRC_ATOP)
    }

    fun resetErrorBlueLine() {
        binding.inputfieldBasicInputBasic.background.mutate()
            .setColorFilter(ContextCompat.getColor(context, R.color.blue_lite_2196F3), PorterDuff.Mode.SRC_ATOP)
    }

    fun hideKeyboard() {
        binding.inputfieldBasicInputBasic.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(text: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(binding.inputfieldBasicInputBasic.windowToken, 0)

                    return true
                }
                return false
            }

        })
    }

    fun putCursorEndOfText(length: Int) {
        binding.inputfieldBasicInputBasic.setSelection(length)
    }

    fun setRequestFocus() {
        binding.inputfieldBasicInputBasic.requestFocus()
        binding.inputfieldBasicInputBasic.text?.length?.let { binding.inputfieldBasicInputBasic.setSelection(it) }
    }

    fun setImeOption(typeImeOptions: Int) {
        binding.inputfieldBasicInputBasic.imeOptions = typeImeOptions
    }

    fun setInputFilled(isInputFilled: Boolean?) {
        this.isInputFilled = isInputFilled
    }

    fun isInputFilled(): Boolean? {
        return isInputFilled
    }

    object BindingCustom {
        @InverseBindingAdapter(attribute = "inputTextValue")
        @JvmStatic
        fun getCustomTextFieldValue(view: InputTextViewWidget): String? {
            return view.getTextInput()
        }

        @BindingAdapter("app:inputTextValue")
        @JvmStatic
        fun setTextInputFieldCombination(view: InputTextViewWidget, text: String?) {
            if (text != view.getTextInput()) {
                view.setTextInput(text)
            }
        }

        @SuppressLint("CheckResult")
        @BindingAdapter("inputTextValueAttrChanged")
        @JvmStatic
        fun setListenerCustomTextField(
            customField: InputTextViewWidget,
            listener: InverseBindingListener
        ) {
            customField.binding.inputfieldBasicInputBasic.textChangeEvents()
                .skipInitialValue()
                .subscribe {
                    listener.onChange()
                    customField.setInputFilled(true)
                }

        }
    }
}