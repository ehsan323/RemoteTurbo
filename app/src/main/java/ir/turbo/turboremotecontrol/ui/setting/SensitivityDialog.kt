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
import kotlinx.android.synthetic.main.dialog_request.cancel_btn_click
import kotlinx.android.synthetic.main.dialog_request.ok_btn_click
import kotlinx.android.synthetic.main.dialog_sensitivity.*

class SensitivityDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_sensitivity, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val listener = targetFragment as SensitivityDialogListener

        high_sensitivity.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
            mid_sensitivity.isChecked=false
            low_sensitivity.isChecked=false
            }
        }

        mid_sensitivity.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                high_sensitivity.isChecked=false
                low_sensitivity.isChecked=false
            }
        }

        low_sensitivity.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mid_sensitivity.isChecked=false
                high_sensitivity.isChecked=false
            }
        }

        ok_btn_click.setOnClickListener{

            if (high_sensitivity.isChecked || mid_sensitivity.isChecked || low_sensitivity.isChecked){
                if (high_sensitivity.isChecked){
                    listener.onSensitivityChoose("1")
                } else if (mid_sensitivity.isChecked){
                    listener.onSensitivityChoose("2")
                } else{
                    listener.onSensitivityChoose("3")
                }
            } else{
                listener.onErrorMessage("لطفا حساسیت مورد نظر خود را انتخاب کنید.")
            }
        }

        cancel_btn_click.setOnClickListener{
            listener.onCancelChoose()
        }
    }

    companion object{
        fun newInstance(): SensitivityDialog {

            val args = Bundle()

            val fragment = SensitivityDialog()

            fragment.arguments = args
            return fragment
        }
    }


    interface SensitivityDialogListener {
        fun onSensitivityChoose(item: String)
        fun onErrorMessage(item: String)
        fun onCancelChoose()
    }
}