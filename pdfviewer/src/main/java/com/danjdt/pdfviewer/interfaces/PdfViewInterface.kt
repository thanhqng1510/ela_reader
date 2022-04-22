package com.danjdt.pdfviewer.interfaces

import java.io.File

interface PdfViewInterface {
    fun setup(file: File)

    fun setZoomEnabled(isZoomEnabled : Boolean)

    fun setMaxZoom(maxZoom : Float)

    fun setQuality(quality: Int)

    fun setOnPageChangedListener(onPageChangedListener: OnPageChangedListener?)
}