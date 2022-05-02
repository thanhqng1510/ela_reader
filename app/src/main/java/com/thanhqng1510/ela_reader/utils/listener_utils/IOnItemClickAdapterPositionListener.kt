package com.thanhqng1510.ela_reader.utils.listener_utils

import android.view.View

/**
 * Item click callback that also provide position on adapter
 */
interface IOnItemClickAdapterPositionListener {
    fun onItemClick(view: View, position: Int)
}