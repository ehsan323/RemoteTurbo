package ir.turbo.turboremotecontrol.ui.remote

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import ir.turbo.turboremotecontrol.R
import ir.turbo.turboremotecontrol.base.BaseFragment
import ir.turbo.turboremotecontrol.databinding.FragmentRemoteBinding
import ir.turbo.turboremotecontrol.ui.setting.RequestDialog


class RemoteFragment : BaseFragment<RemoteViewModel, FragmentRemoteBinding>(),
    RequestDialog.RequestDialogListener {

    lateinit var reqDialog: DialogFragment
    private var onCheckSMSPermission: OnCheckSMSPermission? = null

    override fun clickOkButton() {
        viewModel.sendMessagetoRemote()
        reqDialog.dismissAllowingStateLoss()
    }

    override fun clickCancelButton() {
        reqDialog.dismissAllowingStateLoss()
    }

    override fun layoutResId(): Int {
        return R.layout.fragment_remote
    }

    override fun getViewModel(): Class<RemoteViewModel> {
        return RemoteViewModel::class.java
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

        viewModel.checkPermission.observe(this, Observer {
            onCheckSMSPermission?.onSMSPermission()
        })

        viewModel.showRequestDialog.observe(this, Observer {
            reqDialog.setTargetFragment(this@RemoteFragment,2)
            fragmentManager?.let { reqDialog.show(it, "req_dialog") }
        })


        viewModel.changeTab.observe(this, Observer {
            onCheckSMSPermission?.onChangeTab()
        })

        viewModel.showAnimation.observe(this, Observer {
            when(it){
                "remotePowerOn" ->{
                    YoYo.with(Techniques.Tada)
                        .duration(700)
                        .repeat(5)
                        .playOn(dataBinding.remotePowerOn)
                }

                "remotePowerOff" ->{
                    YoYo.with(Techniques.Tada)
                        .duration(700)
                        .repeat(5)
                        .playOn(dataBinding.remotePowerOff)
                }

                "remoteLockOn" ->{
                    YoYo.with(Techniques.Tada)
                        .duration(700)
                        .repeat(5)
                        .playOn(dataBinding.lockRemote)
                }

                "remoteLockOff" ->{
                    YoYo.with(Techniques.Tada)
                        .duration(700)
                        .repeat(5)
                        .playOn(dataBinding.unlockRemote)
                }

                "remoteHearing" ->{
                    YoYo.with(Techniques.Tada)
                        .duration(700)
                        .repeat(5)
                        .playOn(dataBinding.imageView)
                }
            }
        })


    }

    companion object {
        fun newInstance(): RemoteFragment {

            val args = Bundle()

            val fragment = RemoteFragment()
            fragment.arguments = args
            return fragment
        }
    }

    fun sendSMS() {
        viewModel.checkDialogStatus()
      //  viewModel.sendMessagetoRemote()
    }

    fun denySMSPermission() {
        showMessage("ارتباط با دستگاه امکان پذیر نمی باشد.")
    }


    interface OnCheckSMSPermission {
        fun onSMSPermission()
        fun onChangeTab()
    }

    fun setOnCheckSMSPermission(smsPermission: OnCheckSMSPermission) {
        this.onCheckSMSPermission = smsPermission
    }

}