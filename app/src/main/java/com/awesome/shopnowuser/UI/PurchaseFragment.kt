package com.awesome.shopnowuser.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.awesome.shopnowuser.R
import com.awesome.shopnowuser.databinding.FragmentPurchaseBinding


class PurchaseFragment : Fragment(), PaymentDialogFragment.PaymentDialogListener {

    private var _binding: FragmentPurchaseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPurchaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBuy.setOnClickListener {
            showPaymentDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPaymentDialog() {
        val dialog = PaymentDialogFragment()
        dialog.setTargetFragment(this, 0)
        dialog.show(parentFragmentManager, "PaymentDialogFragment")
    }

    override fun onPaymentDialogDismissed() {
        openNewFragment()
    }

    private fun openNewFragment() {
        parentFragmentManager.commit {
            replace(R.id.fragment_container, ProductsScreenFragment()) // Replace with your new fragment
            addToBackStack(null) // Optional: Add to back stack for navigation
        }
    }
}
