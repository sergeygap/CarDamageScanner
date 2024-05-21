package com.gap.presentation.main.createReport.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gap.data.ScannerRepositoryImpl
import com.gap.domain.entities.ReportItem
import com.gap.domain.useCases.ExchangeFilesUseCase
import kotlinx.coroutines.launch
import java.io.File

class SendPhotosViewModel : ViewModel() {
    private val repository = ScannerRepositoryImpl
    private val exchangeFilesUseCase = ExchangeFilesUseCase(repository)
    private val _reportLD = MutableLiveData<List<ReportItem>>()
    val reportLD
        get() = _reportLD

    fun exchangeFiles(files: List<File>) {
        viewModelScope.launch {
            _reportLD.postValue(exchangeFilesUseCase.invoke(files))
        }
    }
}