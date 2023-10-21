package com.cns.mytaskmanager.utils.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.cns.mytaskmanager.R
import com.cns.mytaskmanager.databinding.BottomSheetListDialogFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFilterListDialogFragment(private val listener: OnItemClickListener) :
    BottomSheetDialogFragment() {

    private val items = listOf("All", "General", "Personal", "Family", "Fun", "Games", "Tour")

    private lateinit var binding: BottomSheetListDialogFilterBinding

    interface OnItemClickListener {
        fun onFilterItemClick(item: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.bottom_sheet_list_dialog_filter, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = ItemAdapter(items) { item ->
            listener.onFilterItemClick(item)
            dismiss()
        }
        binding.recyclerView.adapter = adapter
    }
}
