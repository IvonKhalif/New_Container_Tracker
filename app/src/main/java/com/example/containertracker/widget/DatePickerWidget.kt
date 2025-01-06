package com.example.containertracker.widget

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.containertracker.R
import com.example.containertracker.databinding.DatePickerWidgetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import java.util.*

class DatePickerWidget(
//    private val minimumDate: LocalDate = StaticData.startDate,
//    private val isUsedMinimumDate: Boolean = true,
//    private val title : String = app().context.getString(R.string.select_date)
) : BottomSheetDialogFragment(), DatePicker.OnDateChangedListener {

    interface OnSelectedListener {
        fun onSelected(value: LocalDate): Boolean
    }

    fun setListener(onSelectedListener: OnSelectedListener) {
        this.onSelectedListener = onSelectedListener
    }

    private var selectedYear: Int = 0
    private var selectedMonthOfYear: Int = 0
    private var selectedDayOfMonth: Int = 0
    private var onSelectedListener: OnSelectedListener? = null

    private lateinit var binding: DatePickerWidgetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.date_picker_widget,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener()
    }

    private fun listener() {
        binding.buttonCancel.setOnClickListener {
            dialog?.dismiss()
        }

        binding.buttonSave.setOnClickListener {
            val listener = onSelectedListener
            if (listener == null) {
                dialog?.dismiss()
            } else {
                val selected =
                    LocalDate.of(selectedYear, selectedMonthOfYear + 1, selectedDayOfMonth)
                if (listener.onSelected(selected)) {
                    dialog?.dismiss()
                }
            }
        }

        binding.datePicker.init(selectedYear, selectedMonthOfYear, selectedDayOfMonth, this)
    }

    override fun onStart() {
        super.onStart()

        dialog?.setCancelable(false)
//        setWrapContent(dialog!!, activity)
    }

    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        this.selectedYear = year
        this.selectedMonthOfYear = monthOfYear
        this.selectedDayOfMonth = dayOfMonth
    }

    fun init(year: Int, monthOfYear: Int, dayOfMonth: Int, onSelectedListener: OnSelectedListener) {
        this.selectedYear = year
        this.selectedMonthOfYear = monthOfYear
        this.selectedDayOfMonth = dayOfMonth
        this.onSelectedListener = onSelectedListener
    }

    fun init(date: LocalDate, onSelectedListener: OnSelectedListener) {
        val yy = date.year
        val mm = date.monthValue - 1
        val dd = date.dayOfMonth
        init(yy, mm, dd, onSelectedListener)
    }

    fun bind(liveData: MutableLiveData<LocalDate>) {
        val date = liveData.value ?: LocalDate.now()
        init(date, object : OnSelectedListener {

            override fun onSelected(value: LocalDate): Boolean {
                liveData.postValue(value)
                return true
            }

        })
    }

//    @Deprecated("Prefer using bind with LocalDate data type")
//    fun bindString(liveData: MutableLiveData<String>) {
//        val date = Utils.stringToLocalDate(liveData.value) ?: LocalDate.now()
//        init(date, object : OnSelectedListener {
//
//            override fun onSelected(value: LocalDate): Boolean {
//                liveData.postValue(Utils.localDateToString(value))
//                return true
//            }
//
//        })
//    }
}