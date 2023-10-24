package com.cns.mytaskmanager.ui.add_category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.cns.mytaskmanager.R
import com.cns.mytaskmanager.core.BaseFragment
import com.cns.mytaskmanager.databinding.FragmentAddCategoryBinding
import com.cns.mytaskmanager.utils.hideKeyboard
import com.cns.mytaskmanager.utils.jsonToList
import com.cns.mytaskmanager.utils.listToJson
import com.cns.mytaskmanager.utils.setCustomClickListener
import com.cns.mytaskmanager.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCategoryFragment : BaseFragment<FragmentAddCategoryBinding, AddCategoryViewModel>() {

    var categoryListFromDb: ArrayList<String> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservables()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backArrow.setCustomClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
        binding.btnSubmit.setCustomClickListener {
            viewModel.validateForm()
        }
    }

    private fun setupObservables() {
        viewModel.categoryList.observe(viewLifecycleOwner) {
            categoryListFromDb = jsonToList(it.toString())
        }

        viewModel.isValidLiveData.observe(viewLifecycleOwner) { isValid ->
            if (isValid) {
                categoryListFromDb.add(viewModel.categoryLiveData.value.toString())
                viewModel.saveCategoryList(listToJson(categoryListFromDb))
            } else {
                requireContext().showToast(getString(R.string.please_enter_category_name))
            }
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddCategoryBinding {
        return FragmentAddCategoryBinding.inflate(inflater, container, false)
    }

    override fun getViewModelClass(): Class<AddCategoryViewModel> {
        return AddCategoryViewModel::class.java
    }

    override fun setupViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

}