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

class BottomSheetFilterListDialogFragment(
    private val listener: OnItemClickListener,
    private val filterList: ArrayList<String>
) :
    BottomSheetDialogFragment() {

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

        binding.recyclerViewFilter.layoutManager = LinearLayoutManager(context)
        filterList.add(0, getString(R.string.all))
        val adapter = ItemAdapter(filterList.distinct()) { item ->
            listener.onFilterItemClick(item)
            dismiss()
        }
        binding.recyclerViewFilter.adapter = adapter
    }
}
