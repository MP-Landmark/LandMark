package com.example.lendmark.ui.my

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.lendmark.databinding.DialogConfirmCancelBinding

class ConfirmCancelDialog(private val onConfirm: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogConfirmCancelBinding.inflate(LayoutInflater.from(context))

        // "Yes" button confirms the cancellation
        binding.btnConfirm.setOnClickListener {
            onConfirm()
            dismiss()
        }

        // "No" button just closes the dialog
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }
}
