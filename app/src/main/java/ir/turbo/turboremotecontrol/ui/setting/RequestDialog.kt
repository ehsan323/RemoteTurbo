package ir.turbo.turboremotecontrol.ui.setting

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ir.turbo.turboremotecontrol.R
import kotlinx.android.synthetic.main.dialog_request.*

class RequestDialog : DialogFragment()  {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_request, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listener = targetFragment as RequestDialogListener

        ok_btn_click.setOnClickListener{
            listener.clickOkButton()
        }

        cancel_btn_click.setOnClickListener{
            listener.clickCancelButton()
        }
    }

    companion object{
        fun newInstance(): RequestDialog {

            val args = Bundle()

            val fragment = RequestDialog()
            fragment.arguments = args
            return fragment
        }
    }

    interface RequestDialogListener {
        fun clickOkButton()
        fun clickCancelButton()
    }
}