package com.textaddict.app.ui.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.textaddict.app.R


class ErrorDialogFragment(var message: String?) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(context!!)
        if (message == null) {
            builder.setMessage("Something wrong. PLease try later")
        } else {
            builder.setMessage(message)
        }
        builder.setPositiveButton(R.string.ok) { dialog, id ->
            dismiss()
        }
        return builder.create()
    }
}

class ConfirmDialogFragment(
    var message: String,
    private var onInteractionDialog: OnInteractionDialog
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)

        builder.setMessage(message)

        builder.setPositiveButton(R.string.ok) { dialog, id ->
            onInteractionDialog.positiveInteraction()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, id ->
            onInteractionDialog.negativeInteraction()
            dismiss()
        }
        return builder.create()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onInteractionDialog.negativeInteraction()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onInteractionDialog.negativeInteraction()
    }
}

interface OnInteractionDialog {
    fun positiveInteraction()
    fun negativeInteraction()
}
