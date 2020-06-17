package ir.turbo.turboremotecontrol.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ir.turbo.turboremotecontrol.R
import kotlinx.android.synthetic.main.dialog_warnings.*

class WarningsDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_warnings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listener = activity as WarningsDialogListener
        val warning = arguments?.get("messageWarnings")

        warning_message.text= warning.toString()

        btn_warning.setOnClickListener{
            listener.cancelwarning()
            dismissAllowingStateLoss()
        }
    }


    companion object {
        fun newInstance(message: String): WarningsDialog {

            val args = Bundle()

            val fragment = WarningsDialog()
            args.putString("messageWarnings", message)
            fragment.arguments = args
            return fragment
        }
    }

    interface WarningsDialogListener {
        fun cancelwarning()
    }

}