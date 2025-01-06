package com.example.containertracker.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.InputType
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.lifecycleScope
import com.example.containertracker.R
import com.example.containertracker.databinding.SearchWidgetBinding
import com.jakewharton.rxbinding4.widget.textChangeEvents
import kotlinx.coroutines.launch


class SearchWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    var binding: SearchWidgetBinding

    private var isInputFilled: Boolean? = false
    private var isBarcodeEnable: Boolean? = false
    private var isEnableDrawableStart: Boolean = true

    //    private var searchListener: AppBarSearchListener? = null

    private var searchBarcodeListener: () -> Unit = {}

    init {
        val layoutInflater = LayoutInflater.from(context)
        binding = SearchWidgetBinding.inflate(layoutInflater, this, false)
        addView(binding.root)
        val typeArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.SearchWidget,
            0,
            0
        )

        val inputHint = typeArray.getString(R.styleable.SearchWidget_searchHint)
        val inputTypeFlag = typeArray.getInt(R.styleable.SearchWidget_searchInputType, 0)
        isBarcodeEnable =
            typeArray.getBoolean(R.styleable.SearchWidget_searchDrawableEndIconBarcode, false)

        typeArray.recycle()

        setAttribute(inputHint, inputTypeFlag)
    }

    private fun setAttribute(inputHint: String?, inputTypeFlag: Int) {
        binding.inputSearch.hint = inputHint
        binding.inputSearch.filters = arrayOf<InputFilter>(AllCaps())

        setInputType(inputTypeFlag)
        drawableEndClick("")
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun drawableEndClick(text: String?) {
        binding.inputSearch.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2
                if (binding.inputSearch.compoundDrawables[DRAWABLE_RIGHT] != null) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        if (event.rawX >= binding.inputSearch.right - binding.inputSearch.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                            if (text.isNullOrBlank())
                                searchBarcodeListener()
                            else
                                binding.inputSearch.text!!.clear()

                            return true
                        }
                    }
                }
                return false
            }
        })
    }

    private fun setInputType(inputTypeFlag: Int) {
        when (inputTypeFlag) {
            0 -> binding.inputSearch.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
            1 -> binding.inputSearch.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            2 -> binding.inputSearch.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
            3 -> binding.inputSearch.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
    }

    fun setEditorAction() {
        binding.inputSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(
                text: TextView?,
                actionId: Int,
                keyEvent: KeyEvent?
            ): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard()

                    return true
                }
                return false
            }

        })
    }

    fun getTextInputSearch(): String {
        return binding.inputSearch.text.toString()
    }

    fun setTextInputSearch(label: String?) {
        binding.inputSearch.setText(label)
    }

    fun setDrawable(drawableStartIcon: Int = 0, drawableEndIcon: Int = 0) {
        binding.inputSearch.setCompoundDrawablesWithIntrinsicBounds(
            drawableStartIcon,
            0,
            drawableEndIcon,
            0
        )
    }

    fun setInputFilledSearch(isInputFilled: Boolean?) {
        this.isInputFilled = isInputFilled
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.inputSearch.windowToken, 0)
    }

    fun enableDrawableLeft(isEnable: Boolean = true) {
        isEnableDrawableStart = isEnable
    }

    fun onBarcodeListener(listener: () -> Unit) {
        this.searchBarcodeListener = listener
    }

    object CustomBinding {
        @InverseBindingAdapter(attribute = "searchWidgetInputText")
        @JvmStatic
        fun getInputFieldValue(view: SearchWidget): String {
            return view.getTextInputSearch()
        }

        @BindingAdapter("app:searchWidgetInputText")
        @JvmStatic
        fun setTextInputField(view: SearchWidget, text: String?) {
            if (text != view.getTextInputSearch()) {
                view.setTextInputSearch(text)
            }
            view.drawableEndClick(text)
            view.setEditorAction()
            val startDrawable =
                if (view.isEnableDrawableStart) R.drawable.ic_rounded_search_grey else 0
            if (text.isNullOrBlank()) {
                if (view.isBarcodeEnable == true)
                    view.setDrawable(startDrawable, R.drawable.ic_barcode_scanner)
                else
                    view.setDrawable(startDrawable, 0)
            } else {
                view.setDrawable(startDrawable, R.drawable.ic_clear_button)
            }
        }

        @BindingAdapter("searchWidgetInputTextAttrChanged")
        @JvmStatic
        fun setListenerCustomTextField(
            customField: SearchWidget,
            listener: InverseBindingListener
        ) {
            customField.binding.inputSearch.textChangeEvents()
                .skipInitialValue()
                .subscribe {
                    listener.onChange()
                    customField.setInputFilledSearch(true)
                }
        }
    }
}