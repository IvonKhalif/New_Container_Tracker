package com.example.containertracker.ui.home.salesordernumber

import androidx.lifecycle.MutableLiveData
import com.example.containertracker.base.BaseViewModel

class SalesOrderNumberViewModel: BaseViewModel() {
    val searchKeyword = MutableLiveData<String>()
}