package com.thanhqng1510.ela_reader.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.thanhqng1510.ela_reader.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    // App bar's title, children fragments can modify this variable to change label of app bar
    val appBarTitle = MutableStateFlow(application.resources.getString(R.string.app_name))
}
