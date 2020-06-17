package ir.turbo.turboremotecontrol.ui.setting

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ir.turbo.turboremotecontrol.R
import java.util.*
import android.widget.Button
import ir.turbo.turboremotecontrol.util.TotalCarCondition
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_car_condition.*


class CarConditionDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(ir.turbo.turboremotecontrol.R.layout.dialog_car_condition, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listener = targetFragment as CarConditionDialogListener?
        val totalCarCondition = arguments!!.getSerializable("totalCarCondition") as TotalCarCondition

        car_switch_condition_value?.text = totalCarCondition.switchStatus
        catch_thief_value?.text = totalCarCondition.thiefCatcher
        car_battery_condition_value?.text = totalCarCondition.batteryStatus
        remote_signal_value?.text = totalCarCondition.signalstatus
        remote_internet_value?.text = totalCarCondition.carDoor
        remote_inner_battery_value?.text = totalCarCondition.batteryPercent

        car_condition_update_button?.setOnClickListener {
            listener?.updateTotalCarCon()
        }

        car_con_cancel?.setOnClickListener {
            listener?.cancelCarConDialog()
        }

    }


    fun showNewCarCondition(totalCarCondition: TotalCarCondition) {
        car_switch_condition_value?.text = totalCarCondition.switchStatus
        catch_thief_value?.text = totalCarCondition.thiefCatcher
        car_battery_condition_value?.text = totalCarCondition.batteryStatus
        remote_signal_value?.text = totalCarCondition.signalstatus
        remote_internet_value?.text = totalCarCondition.carDoor
        remote_inner_battery_value?.text = totalCarCondition.batteryPercent
    }

    companion object {
        fun newInstance(totalCarCondition: TotalCarCondition): CarConditionDialog {

            val args = Bundle()

            val fragment = CarConditionDialog()
            args.putSerializable("totalCarCondition", totalCarCondition)
            fragment.arguments = args
            return fragment
        }
    }

    interface CarConditionDialogListener {
        fun updateTotalCarCon()
        fun cancelCarConDialog()
    }

}