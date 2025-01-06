package com.example.containertracker.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.containertracker.R
import com.example.containertracker.data.history.model.HistoryModel
import com.example.containertracker.databinding.ItemHistoryBinding
import com.example.containertracker.utils.DateUtil
import com.example.containertracker.utils.DateUtil.convertLocalDateTimeToLocalDate
import com.example.containertracker.utils.DateUtil.convertPattern
import com.example.containertracker.utils.DateUtil.formatLocalDateTimeToString
import com.example.containertracker.utils.DateUtil.formatLocalDateToString
import com.example.containertracker.utils.DateUtil.formatStringToDateTime
import com.example.containertracker.utils.enums.ConditionEnum
import org.threeten.bp.LocalDateTime

class HistoryViewHolder(
    val binding: ItemHistoryBinding,
    private val isHistoryDetail: Boolean = false,
    private val onItemClick: (HistoryModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: HistoryModel) = with(binding) {
        val localDateTime = formatStringToDateTime(item.dateTime) ?: LocalDateTime.now()
        val date = convertLocalDateTimeToLocalDate(localDateTime)
        val dateFormat = if (isHistoryDetail) {
            val formatter = formatLocalDateTimeToString(localDateTime)
            convertPattern(formatter, DateUtil.LOCAL_DATE_TIME_PATTERN, "dd MMM yyyy HH:mm")
        } else {
            formatLocalDateToString(date, "dd MMM yyyy")
        }

        if (isHistoryDetail) {
            containerNameHistory.text = item.nameAccount
            imageContainer.setImageDrawable(ContextCompat.getDrawable(root.context, R.drawable.ic_account_circle))
        } else {
            containerNameHistory.text = item.codeContainer
            imageContainer.setImageDrawable(ContextCompat.getDrawable(root.context, R.drawable.container_truck))
        }

        postNameHistory.text = item.location
        dateHistory.text = dateFormat
        textSealNumber.text = if (item.sealId.isNullOrBlank()) "-" else item.sealId
        doorSideValue.text = item.frontDoorSideCondition
        backSideValue.text = item.backDoorSideCondition
        rightSideValue.text = item.rightSideCondition
        leftSideValue.text = item.leftSideCondition
        roofSideValue.text = item.roofSideCondition
        floorSideValue.text = item.floorSideCondition

        setGoodOrNot(item = item)
        setTextDrawable(item = item)

        root.setOnClickListener {
            onItemClick.invoke(item)
        }
    }

    private fun ItemHistoryBinding.setGoodOrNot(item: HistoryModel) {
        val (good, damage) = calculateGoodOrNot(item = item)

        tvGoodCount.text = good.toString()
        tvDamageCount.text = damage.toString()
    }

    private fun calculateGoodOrNot(item: HistoryModel): Pair<Int, Int> {
        var good = 0

        if (item.frontDoorSideCondition == ConditionEnum.GOOD.type) good++
        if (item.backDoorSideCondition == ConditionEnum.GOOD.type) good++
        if (item.rightSideCondition == ConditionEnum.GOOD.type) good++
        if (item.leftSideCondition == ConditionEnum.GOOD.type) good++
        if (item.roofSideCondition == ConditionEnum.GOOD.type) good++
        if (item.floorSideCondition == ConditionEnum.GOOD.type) good++

        return Pair(good, 6 - good)
    }

    private fun ItemHistoryBinding.setTextDrawable(item: HistoryModel) {
        doorSideValue.setDrawableWithCondition(item.frontDoorSideCondition.orEmpty())
        backSideValue.setDrawableWithCondition(item.backDoorSideCondition.orEmpty())
        rightSideValue.setDrawableWithCondition(item.rightSideCondition.orEmpty())
        leftSideValue.setDrawableWithCondition(item.leftSideCondition.orEmpty())
        roofSideValue.setDrawableWithCondition(item.roofSideCondition.orEmpty())
        floorSideValue.setDrawableWithCondition(item.floorSideCondition.orEmpty())
    }

    private fun AppCompatTextView.setDrawableWithCondition(condition: String) {
        val drawableId = if (condition == ConditionEnum.GOOD.type) {
            R.drawable.ic_check_18dp
        } else {
            R.drawable.ic_warning_18dp
        }
        this.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            drawableId,
            0
        )
    }

    companion object {

        fun create(
            parent: ViewGroup,
            isHistoryDetail: Boolean = false,
            onItemClicked: (HistoryModel) -> Unit
        ): HistoryViewHolder {
            val view = DataBindingUtil
                .inflate<ItemHistoryBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_history,
                    parent,
                    false
                )
            return HistoryViewHolder(view, isHistoryDetail, onItemClicked)
        }
    }
}