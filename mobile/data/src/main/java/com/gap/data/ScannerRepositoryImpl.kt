package com.gap.data

import com.gap.domain.ScannerRepository
import com.gap.domain.entitys.Report

object ScannerRepositoryImpl: ScannerRepository {

    private const val TITLE_TAG = "Отчет от 24.05"

    private val listReports = mutableListOf<Report>().apply {
        repeat(0) {
            this.add(Report("$TITLE_TAG $it"))
        }
    }
    override fun getListReports(): List<Report> {
        return listReports
    }

}