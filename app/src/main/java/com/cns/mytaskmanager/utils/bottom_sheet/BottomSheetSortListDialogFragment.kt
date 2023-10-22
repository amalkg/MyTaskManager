package com.cns.mytaskmanager.utils.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.cns.mytaskmanager.R
import com.cns.mytaskmanager.databinding.BottomSheetListDialogSortBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetSortListDialogFragment(private val listener: OnItemClickListener) :
    BottomSheetDialogFragment() {

    private val items = listOf("Date", "Priority - High to low", "Priority - Low to high")

    private lateinit var binding: BottomSheetListDialogSortBinding

    interface OnItemClickListener {
        fun onSortItemClick(item: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.bottom_sheet_list_dialog_sort,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewSort.layoutManager = LinearLayoutManager(context)
        val adapter = ItemAdapter(items) { item ->
            listener.onSortItemClick(item)
            dismiss()
        }
        binding.recyclerViewSort.adapter = adapter
    }
}
