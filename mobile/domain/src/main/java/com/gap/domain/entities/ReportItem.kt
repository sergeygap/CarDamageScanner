package com.gap.domain.entities

data class ReportItem(
    val labels: Labels = Labels(),
    val part: String = "",
    val request_time: String = "",
    val urls: String = ""
)