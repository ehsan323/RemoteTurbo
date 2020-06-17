package ir.turbo.turboremotecontrol.ui.setting

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import ir.turbo.turboremotecontrol.R
import ir.turbo.turboremotecontrol.base.BaseFragment
import ir.turbo.turboremotecontrol.databinding.FragmentSettingBinding
import ir.turbo.turboremotecontrol.ui.admin.AdminActivity
import ir.turbo.turboremotecontrol.ui.map.MapFragment
import ir.turbo.turboremotecontrol.ui.password.PasswordActivity
import ir.turbo.turboremotecontrol.ui.remote.RemoteFragment
import ir.turbo.turboremotecontrol.util.TotalCarCondition


class SettingFragment : BaseFragment<SettingViewModel, FragmentSettingBinding>(),
    SpeedLimitDialog.CarSpeedLimitDialogListener,
    RequestDialog.RequestDialogListener,
    SensitivityDialog.SensitivityDialogListener,
    CarConditionDialog.CarConditionDialogListener {

    lateinit var reqDialog: DialogFragment
    lateinit var senDialog: DialogFragment
    private lateinit var processType: String
    lateinit var conDialog: CarConditionDialog
    lateinit var speedLimitDialog: SpeedLimitDialog
    private var onSettingSMSPermission: OnSettingSMSPermission? = null

    override fun onErrorMessage(item: String) {
        showMessage(item)
    }

    override fun onResume() {
        super.onResume()

        viewModel.updatePass()
    }

    override fun onSensitivityChoose(item: String) {
        viewModel.sendMessageProcessType("sensitivity", item)
        senDialog.dismissAllowingStateLoss()
    }

    override fun onCancelChoose() {
        senDialog.dismissAllowingStateLoss()
    }

    override fun clickOkButton() {
        viewModel.sendMessageProcessType(processType, "")
        reqDialog.dismissAllowingStateLoss()
    }

    override fun clickCancelButton() {
        reqDialog.dismissAllowingStateLoss()
    }


    // check permission first!
    override fun updateSpeedLimit(speed: String) {
        viewModel.sendSpeedLimitSms(speed)
        speedLimitDialog.dismissAllowingStateLoss()
    }

    override fun cancelSpeedLimitDialog() {
        speedLimitDialog.dismissAllowingStateLoss()
    }

    override fun updateTotalCarCon() {
        viewModel.requestCarCondition()
    }

    override fun cancelCarConDialog() {
        conDialog.dismissAllowingStateLoss()
    }

    override fun layoutResId(): Int {
        return R.layout.fragment_setting
    }

    override fun getViewModel(): Class<SettingViewModel> {
        return SettingViewModel::class.java
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        dataBinding.lifecycleOwner = this
        dataBinding.viewmodel = viewModel
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reqDialog = RequestDialog.newInstance()
        senDialog = SensitivityDialog.newInstance()

        viewModel.checkPermission.observe(this, Observer {
            onSettingSMSPermission?.onSettingSMSPermission(it)
//            if (hasPermission(Manifest.permission.SEND_SMS) && hasPermission(Manifest.permission.RECEIVE_SMS)) {
//                sendSMS(it)
//            } else {
//                onSettingSMSPermission?.onSettingSMSPermission(it)
//            }
        })

        viewModel.showSensitivityDialog.observe(this, Observer {
            senDialog.setTargetFragment(this@SettingFragment, 7)
            fragmentManager?.let { it1 -> senDialog.show(it1, "dialog_sensitivity") }
        })

        viewModel.exitApp.observe(this, Observer {
            activity?.finishAffinity()
        })

        viewModel.carConDialog.observe(this, Observer {
            val carConSaved = viewModel.loadCarCon()
            if (carConSaved != null) {
                conDialog = CarConditionDialog.newInstance(carConSaved)
                conDialog.setTargetFragment(this@SettingFragment, 0)
                fragmentManager?.let { it1 -> conDialog.show(it1, "dialog_total_car_condition") }
            } else {
                conDialog = CarConditionDialog.newInstance(viewModel.loadCarConDefault())
                conDialog.setTargetFragment(this@SettingFragment, 0)
                fragmentManager?.let { it1 -> conDialog.show(it1, "dialog_total_car_condition") }
            }

        })

        viewModel.carSpeedLimit.observe(this, Observer {
            speedLimitDialog = SpeedLimitDialog.newInstance(viewModel.loadSpeedLimit()!!)
            speedLimitDialog.setTargetFragment(this@SettingFragment, 1)
            fragmentManager?.let { it1 -> speedLimitDialog.show(it1, "dialog_speed_limit") }
        })

        viewModel.registerAdmin.observe(this, Observer {
            val intent = Intent(activity, AdminActivity::class.java)
            activity?.startActivity(intent)
        })

        viewModel.changePassword.observe(this, Observer {
            val intent = Intent(activity, PasswordActivity::class.java)
            activity?.startActivity(intent)
        })

        viewModel.showRequestDialog.observe(this, Observer {
            processType = it
            reqDialog.setTargetFragment(this@SettingFragment, 3)
            fragmentManager?.let { reqDialog.show(it, "req_dialog") }
        })
    }

    companion object {
        fun newInstance(): SettingFragment {

            val args = Bundle()

            val fragment = SettingFragment()
            fragment.arguments = args
            return fragment
        }
    }

    fun getCarconditionResponse(totalCarCondition: TotalCarCondition) {
        viewModel.saveNewCarCondition(totalCarCondition)
        if (conDialog.getDialog() != null && conDialog.getDialog()!!.isShowing() && !conDialog.isRemoving()
        ) {
            //dialog is showing so do something
            conDialog.showNewCarCondition(totalCarCondition)
        } else {
            //dialog is not showing
            conDialog.setTargetFragment(this@SettingFragment, 0)
            fragmentManager?.let { it1 -> conDialog.show(it1, "dialog_total_car_condition") }
            conDialog.showNewCarCondition(totalCarCondition)
        }
    }


    interface OnSettingSMSPermission {
        fun onSettingSMSPermission(type:String)
    }

    fun setOnSettingSMSPermission(smsPermission: OnSettingSMSPermission) {
        this.onSettingSMSPermission = smsPermission
    }

    fun sendSMS(type:String) {
        when(type){
            "sensitivity"->{
                senDialog.setTargetFragment(this@SettingFragment, 7)
                fragmentManager?.let { it1 -> senDialog.show(it1, "dialog_sensitivity") }
            }

            "speed"->{
                speedLimitDialog = SpeedLimitDialog.newInstance(viewModel.loadSpeedLimit()!!)
                speedLimitDialog.setTargetFragment(this@SettingFragment, 1)
                fragmentManager?.let { it1 -> speedLimitDialog.show(it1, "dialog_speed_limit") }
            }

            "help"->{
                viewModel.sendcancelHelpSms()
            }

            "car_condition"->{
                val carConSaved = viewModel.loadCarCon()
                if (carConSaved != null) {
                    conDialog = CarConditionDialog.newInstance(carConSaved)
                    conDialog.setTargetFragment(this@SettingFragment, 0)
                    fragmentManager?.let { it1 -> conDialog.show(it1, "dialog_total_car_condition") }
                } else {
                    conDialog = CarConditionDialog.newInstance(viewModel.loadCarConDefault())
                    conDialog.setTargetFragment(this@SettingFragment, 0)
                    fragmentManager?.let { it1 -> conDialog.show(it1, "dialog_total_car_condition") }
                }
            }
        }

    }
}