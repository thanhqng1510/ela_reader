package com.thanhqng1510.ela_reader.models

import androidx.recyclerview.widget.DiffUtil

/**
 * DiffUtil callback that expects UIModel interface
 *
 * By using this instead of DiffUtil.ItemCallback,
 * we enforce each model type to implement its own diff callback
 *
 * @param T Model type that support rendering with DiffUtil on UI
 */
class ModelDiffCallback<T : UIModel<T>> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) =
        oldItem.areItemsTheSame(newItem)

    override fun areContentsTheSame(oldItem: T, newItem: T) =
        oldItem.areContentsTheSame(newItem)
}