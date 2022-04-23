package com.thanhqng1510.bookreadingapp_android.utils.file_utils

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File

/**
 * Represents a file system path of a valid PDF file
 */
class PDFFilePath(rawPath: String) : FilePath(rawPath) {
    /**
     * Get the total page of a PDF file
     *
     * @return total page of a PDF file
     */
    fun getPdfPageCount(): Int {
        val fileDescriptor = ParcelFileDescriptor.open(
            File(rawPath),
            ParcelFileDescriptor.MODE_READ_ONLY
        )
        val renderer = PdfRenderer(fileDescriptor)

        return renderer.pageCount
    }

    /**
     * Extract the first page of the PDF and render as bitmap image
     *
     * @return bitmap image of the first page
     */
    fun getPdfThumbnail(): Bitmap {
        val fileDescriptor = ParcelFileDescriptor.open(
            File(rawPath),
            ParcelFileDescriptor.MODE_READ_ONLY
        )
        val renderer = PdfRenderer(fileDescriptor)
        val page = renderer.openPage(0)

        val thumbnailBitmap =
            Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(thumbnailBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        page.close()
        renderer.close()

        return thumbnailBitmap
    }
}