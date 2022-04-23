package com.thanhqng1510.bookreadingapp_android.models

/**
 * Interface for model that support rendering with DiffUtil on UI
 *
 * @param T Type of model
 */
interface UIModel<T> {
    fun areItemsTheSame(other: T): Boolean

    fun areContentsTheSame(other: T): Boolean
}