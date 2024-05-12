package com.gap.presentation.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gap.data.ScannerRepositoryImpl
import com.gap.domain.useCases.GetListReportsUseCase
import com.gap.presentation.databinding.ItemReportBinding

class HomeAdapter : RecyclerView.Adapter<HomeViewHolder>() {
    private val repository = ScannerRepositoryImpl
    private val getListReports = GetListReportsUseCase(repository)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemReportBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HomeViewHolder(binding)
    }


    override fun onBindViewHolder(viewHolder: HomeViewHolder, position: Int) {
        val reportsList = getListReports.invoke()
        val report = reportsList[position]
        viewHolder.binding.tvDate.text = report.title
    }

    override fun getItemCount(): Int = getListReports.invoke().size
}