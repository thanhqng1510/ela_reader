package com.thanhqng1510.bookreadingapp_android.activities.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.thanhqng1510.bookreadingapp_android.datastore.DataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val dataStore: DataStore) : ViewModel() {
    private val reloadTrigger = MutableLiveData<Boolean>()

    // All data loaded from DB
    val bookListData = reloadTrigger.switchMap { dataStore.getAllBooks() }

    fun refreshBookListData() {
        reloadTrigger.value = true
    }

    init {
        refreshBookListData()
    }
}