package ir.turbo.turboremotecontrol.ui.setting

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ir.turbo.turboremotecontrol.R
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.adapters.SeekBarBindingAdapter.setOnSeekBarChangeListener
import kotlinx.android.synthetic.main.dialog_car_speed_dialog.*


class SpeedLimitDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_car_speed_dialog, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listener = targetFragment as CarSpeedLimitDialogListener
        val speed = arguments?.get("speedLimit")

        car_speed_limit_button.setOnClickListener{
            listener.updateSpeedLimit(car_speed_limit_value.text.toString() )
        }

        cancel_speed_limit_button.setOnClickListener{
            listener.cancelSpeedLimitDialog()
        }

        car_speed_limit_value.text = speed.toString()+"Km/s"
        val intSpeed = speed.toString().replace("Km/s","").trim()
        speed_limit_seekbar.setProgress(intSpeed.toInt())

        val step = 1
        val max = 260
        val min = 10

        speed_limit_seekbar.setMax(max)

        speed_limit_seekbar.setOnSeekBarChangeListener(
            object : OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar) {}

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int,
                    fromUser: Boolean
                ) {
                    val value = (min + progress * step).toInt()
                    car_speed_limit_value.text = value.toString() + " Km/s"
                }
            }
        )

    }

    companion object{
        fun newInstance(speed : String): SpeedLimitDialog {

            val args = Bundle()

            val fragment = SpeedLimitDialog()
            args.putString("speedLimit", speed)
            fragment.arguments = args
            return fragment
        }
    }

    interface CarSpeedLimitDialogListener {
        fun updateSpeedLimit(speed : String)
        fun cancelSpeedLimitDialog()
    }
}