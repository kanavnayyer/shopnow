package com.awesome.shopnowuser.UI

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class PaymentDialogFragment : DialogFragment() {

    interface PaymentDialogListener {
        fun onPaymentDialogDismissed()
    }

    private var listener: PaymentDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            listener = targetFragment as PaymentDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement PaymentDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Payment Complete")
                .setMessage("Order Confirmed")
                .setPositiveButton("OK") { dialog, which ->
                    listener?.onPaymentDialogDismissed()
                    dialog.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
