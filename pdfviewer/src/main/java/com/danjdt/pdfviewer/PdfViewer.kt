package com.danjdt.pdfviewer

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.danjdt.pdfviewer.decoder.FileLoader
import com.danjdt.pdfviewer.interfaces.OnLoadFileListener
import com.danjdt.pdfviewer.interfaces.OnErrorListener
import com.danjdt.pdfviewer.interfaces.OnPageChangedListener
import com.danjdt.pdfviewer.interfaces.PdfViewInterface
import com.danjdt.pdfviewer.utils.PdfPageQuality
import com.danjdt.pdfviewer.view.PdfViewerRecyclerView
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class PdfViewer private constructor(private val rootView: ViewGroup) : OnLoadFileListener {
    private var context = rootView.context

    private var onErrorListener: OnErrorListener? = null

    private lateinit var mainView: PdfViewInterface

    private var startPage by Delegates.notNull<Int>()

    fun load(file: File, startPage: Int) {
        this.startPage = startPage
        display(file)
    }

    fun load(@RawRes resId: Int, startPage: Int) {
        this.startPage = startPage
        FileLoader.loadFile(context, this, resId)
    }

    fun load(input: InputStream, startPage: Int) {
        this.startPage = startPage
        FileLoader.loadFile(context, this, input)
    }

    fun load(uri: Uri, startPage: Int) {
        this.startPage = startPage
        FileLoader.loadFile(context, this, uri)
    }

    fun load(url: String) {
        this.startPage = startPage
        FileLoader.loadFile(context, this, url)
    }

    override fun onFileLoaded(file: File) {
        (mainView as RecyclerView).layoutManager?.scrollToPosition((startPage - 1).coerceAtLeast(0))
        // Minus 1 since page starts at 1 and adapter position starts at 0

        (context as Activity).runOnUiThread {
            display(file)
        }
    }

    override fun onFileLoadError(e: Exception) {
        onErrorListener?.onFileLoadError(e)
    }

    @MainThread
    private fun display(file: File) {
        try {
            rootView.addView(mainView as View)
            mainView.setup(file)

        } catch (e: IOException) {
            onErrorListener?.onPdfRendererError(e)

        } catch (e: Exception) {
            onErrorListener?.onAttachViewError(e)
        }
    }

    class ConfigBuilder(private val rootView: View) {
        private var pdfView: PdfViewInterface = PdfViewerRecyclerView(rootView.context)

        private var pageQuality: PdfPageQuality = PdfPageQuality.QUALITY_1080

        private var isZoomEnabled: Boolean = false

        private var maxZoom: Float = 3f

        private var onPageChangedListener: OnPageChangedListener? = null

        private var onErrorListener: OnErrorListener? = null

        fun setPdfView(pdfView: PdfViewInterface): ConfigBuilder {
            this.pdfView = pdfView
            return this
        }

        fun setPageQuality(pageQuality: PdfPageQuality): ConfigBuilder {
            this.pageQuality = pageQuality
            return this
        }

        fun setZoomEnabled(isEnabled: Boolean): ConfigBuilder {
            this.isZoomEnabled = isEnabled
            return this
        }

        fun setMaxZoom(maxZoom: Float): ConfigBuilder {
            this.maxZoom = maxZoom
            return this
        }

        fun setOnPageChangedListener(onPageChangedListener: OnPageChangedListener): ConfigBuilder {
            this.onPageChangedListener = onPageChangedListener
            return this
        }

        fun setOnErrorListener(onErrorListener: OnErrorListener): ConfigBuilder {
            this.onErrorListener = onErrorListener
            return this
        }

        fun build(): PdfViewer {
            pdfView.setQuality(pageQuality.value)
            pdfView.setZoomEnabled(isZoomEnabled)
            pdfView.setMaxZoom(maxZoom)
            pdfView.setOnPageChangedListener(onPageChangedListener)

            val pdfViewer = PdfViewer(rootView as ViewGroup)
            pdfViewer.onErrorListener = onErrorListener
            pdfViewer.mainView = pdfView
            return pdfViewer
        }
    }
}