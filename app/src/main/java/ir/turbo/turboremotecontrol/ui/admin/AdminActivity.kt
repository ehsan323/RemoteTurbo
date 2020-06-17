package ir.turbo.turboremotecontrol.ui.admin

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.Observer
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import ir.turbo.turboremotecontrol.R
import ir.turbo.turboremotecontrol.base.BaseActivity
import ir.turbo.turboremotecontrol.databinding.ActivityAdminBinding
import ir.turbo.turboremotecontrol.ui.password.PasswordActivity

class AdminActivity : BaseActivity<AdminViewModel, ActivityAdminBinding>() {

    lateinit var smsVerifyCatcher: SmsVerifyCatcher

    override fun layoutResId(): Int {
        return R.layout.activity_admin
    }

    override fun getViewModel(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun onStart() {
        super.onStart()
        smsVerifyCatcher.onStart()
    }

    override fun onStop() {
        super.onStop()
        smsVerifyCatcher.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding?.setLifecycleOwner(this)
        dataBinding?.viewmodel=viewModel

        smsVerifyCatcher = SmsVerifyCatcher(this,
            OnSmsCatchListener { message ->
                viewModel?.showSMSResult(message)
            })

        viewModel?.closeAdminPage?.observe(this, Observer {
            finish()
        })

        viewModel?.checkPermission?.observe(this, Observer {
            if (hasPermission(Manifest.permission.SEND_SMS) && hasPermission(Manifest.permission.RECEIVE_SMS)) {
                //  (fragmentRemote as RemoteFragment).sendSMS()
                viewModel?.sendSmsAfterPermission()
            } else {
                requestPermissionsSafely(
                    arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS),
                    REQUEST_PERMISSION_SMS_8
                )
            }
        })
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_SMS_8 ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel?.sendSmsAfterPermission()
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                }
        }
    }

    companion object {
        const val REQUEST_PERMISSION_SMS_8 = 501
    }
}
