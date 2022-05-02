package com.danjdt.pdfviewer.interfaces

import java.io.IOException

/**
 * Created by daniel.teixeira on 23/01/19
 */
interface OnErrorListener {

    fun onFileLoadError(e: Exception)

    fun onAttachViewError(e: Exception)

    fun onPdfRendererError(e: IOException)
}