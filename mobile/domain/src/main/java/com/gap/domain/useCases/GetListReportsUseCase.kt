package com.gap.domain.useCases

import com.gap.domain.ScannerRepository
import com.gap.domain.entitys.Report

class GetListReportsUseCase(
    private val repository: ScannerRepository
) {
    operator fun invoke(): List<Report> {
        return repository.getListReports()
    }
}