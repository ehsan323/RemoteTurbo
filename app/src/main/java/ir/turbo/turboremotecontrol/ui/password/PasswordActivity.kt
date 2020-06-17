package ir.turbo.turboremotecontrol.ui.password

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import ir.turbo.turboremotecontrol.R
import ir.turbo.turboremotecontrol.base.BaseActivity
import ir.turbo.turboremotecontrol.databinding.ActivityPasswordBinding
import ir.turbo.turboremotecontrol.ui.main.MainActivity
import ir.turbo.turboremotecontrol.ui.remote.RemoteFragment

class PasswordActivity : BaseActivity<PasswordViewModel, ActivityPasswordBinding>() {

    lateinit var smsVerifyCatcher: SmsVerifyCatcher

    companion object {
        const val REQUEST_PERMISSION_SMS_2 = 501
        const val REQUEST_PERMISSION_SMS_1 = 502
    }

    override fun layoutResId(): Int {
        return R.layout.activity_password
    }

    override fun getViewModel(): Class<PasswordViewModel> {
        return PasswordViewModel::class.java
    }

    override fun onStart() {
        super.onStart()
        smsVerifyCatcher.onStart()
    }

    override fun onStop() {
        super.onStop()
        smsVerifyCatcher.onStop()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding?.setLifecycleOwner(this)
        dataBinding?.viewmodel=viewModel

        smsVerifyCatcher = SmsVerifyCatcher(this,
            OnSmsCatchListener { message ->
                viewModel?.showSMSResult(message)
            })

        viewModel?.closePassPage?.observe(this, Observer {
            finish()
        })

        viewModel?.checkPermission2?.observe(this, Observer {
            if (hasPermission(Manifest.permission.SEND_SMS) && hasPermission(Manifest.permission.RECEIVE_SMS)) {
              //  (fragmentRemote as RemoteFragment).sendSMS()
                viewModel?.handleSMS()
            } else {
                requestPermissionsSafely(
                    arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS),
                    REQUEST_PERMISSION_SMS_2
                )
            }
        })

        viewModel?.checkPermission1?.observe(this, Observer {
            if (hasPermission(Manifest.permission.SEND_SMS) && hasPermission(Manifest.permission.RECEIVE_SMS)) {
                //  (fragmentRemote as RemoteFragment).sendSMS()
                viewModel?.handleSMS1()
            } else {
                requestPermissionsSafely(
                    arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS),
                    REQUEST_PERMISSION_SMS_2
                )
            }
        })

    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_SMS_2 ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel?.handleSMS()
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                }

            REQUEST_PERMISSION_SMS_1 ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel?.handleSMS1()
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
