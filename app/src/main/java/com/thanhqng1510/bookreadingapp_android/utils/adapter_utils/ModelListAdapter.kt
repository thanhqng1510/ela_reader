package com.thanhqng1510.bookreadingapp_android.utils.adapter_utils

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thanhqng1510.bookreadingapp_android.models.ModelDiffCallback
import com.thanhqng1510.bookreadingapp_android.models.UIModel
import com.thanhqng1510.bookreadingapp_android.utils.listener_utils.IOnItemClickAdapterPositionListener

/**
 * A list adapter with DiffUtil support and onClick callback with item position
 */
abstract class ModelListAdapter<T : UIModel<T>, VH : RecyclerView.ViewHolder> : ListAdapter<T, VH>(
    AsyncDifferConfig.Builder(ModelDiffCallback<T>()).build()
) {
    var longClickedPos: Int? = null
        protected set

    lateinit var onItemClickListener: IOnItemClickAdapterPositionListener
}