package com.awesome.shopnowuser.UI

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import com.awesome.shopnowuser.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomScreenFragmentFragment : BottomSheetDialogFragment() {

    interface FilterListener {
        fun onFilterApplied(priceFilter: String, categoryFilter: String)
    }

    private var listener: FilterListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = if (targetFragment is FilterListener) {
            targetFragment as FilterListener
        } else {
            context as? FilterListener
        }
        Log.d("FilterBottomScreen", "Listener attached: $listener")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filter_bottom_screen, container, false)

        val priceRadioGroup: RadioGroup = view.findViewById(R.id.priceRadioGroup)
        val categoryRadioGroup: RadioGroup = view.findViewById(R.id.categoryRadioGroup)
        val applyFilterButton: Button = view.findViewById(R.id.applyFilterButton)

        applyFilterButton.setOnClickListener {
            val selectedPriceId = priceRadioGroup.checkedRadioButtonId
            val selectedCategoryId = categoryRadioGroup.checkedRadioButtonId

            val priceFilter = when (selectedPriceId) {
                R.id.priceLow -> "Low to High"
                R.id.priceHigh -> "High to Low"
                else -> ""
            }

            val categoryFilter = when (selectedCategoryId) {
                R.id.categoryAll -> "All"
                R.id.categoryElectronics -> "Electronics"
                R.id.categoryClothing -> "Clothing"
                else -> ""
            }

            Log.d("FilterBottomScreen", "Filter button clicked: Price = $priceFilter, Category = $categoryFilter")
            listener?.onFilterApplied(priceFilter, categoryFilter)
            dismiss()
        }

        return view
    }
}
