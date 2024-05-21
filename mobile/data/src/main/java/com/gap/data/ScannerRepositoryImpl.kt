package com.gap.data

import com.gap.data.network.RetrofitInstance
import com.gap.domain.ScannerRepository
import com.gap.domain.entities.ReportItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MultipartBody
import java.io.File

object ScannerRepositoryImpl: ScannerRepository {

    private const val TITLE_TAG = "Отчет от 24.05"
    private val scannerApi = RetrofitInstance.api

    private val listReports = mutableListOf<ReportItem>().apply {
        repeat(10) {
            this.add(ReportItem(request_time = "$TITLE_TAG $it"))
        }
    }

    override fun getListReports(): List<ReportItem> {
        return listReports
    }

    override suspend fun exchangeFiles(files: List<File>): List<ReportItem> {
        val parts = files.map { file ->
            val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image_files", file.name, requestFile)
        }
        return scannerApi.exchangeFiles(parts)
    }
}
