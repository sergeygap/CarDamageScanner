package com.gap.domain

import com.gap.domain.entitys.Report

interface ScannerRepository {
    fun getListReports() : List<Report>
}