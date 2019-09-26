package com.textaddict.app.ui.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.textaddict.app.R


class ErrorDialogFragment(var message: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(context!!)
        builder.setMessage(message)
            .setPositiveButton(R.string.ok) { dialog, id ->
                dismiss()
            }
        /*.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, id ->
            // User cancelled the dialog
        })*/
        // Create the AlertDialog object and return it
        return builder.create()

        //return super.onCreateDialog(savedInstanceState)
    }
}