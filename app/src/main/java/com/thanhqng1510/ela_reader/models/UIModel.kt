package com.thanhqng1510.ela_reader.models

/**
 * Interface for model that support rendering with DiffUtil on UI
 *
 * @param T Type of model
 */
interface UIModel<T> {
    fun areItemsTheSame(other: T): Boolean

    fun areContentsTheSame(other: T): Boolean
}