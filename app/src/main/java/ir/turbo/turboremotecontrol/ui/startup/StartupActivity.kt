package ir.turbo.turboremotecontrol.ui.startup

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import ir.turbo.turboremotecontrol.R
import ir.turbo.turboremotecontrol.base.BaseActivity
import ir.turbo.turboremotecontrol.databinding.ActivityStartupBinding
import ir.turbo.turboremotecontrol.ui.login.LoginActivity
import ir.turbo.turboremotecontrol.ui.main.MainActivity
import ir.turbo.turboremotecontrol.ui.map.MapFragment
import ir.turbo.turboremotecontrol.ui.remote.RemoteFragment
import ir.turbo.turboremotecontrol.ui.setting.SettingFragment
import ir.turbo.turboremotecontrol.util.RemoteUtils




class StartupActivity : BaseActivity<StartupViewModel, ActivityStartupBinding>() {

    override fun layoutResId(): Int {
        return R.layout.activity_startup
    }

    override fun getViewModel(): Class<StartupViewModel> {
       return StartupViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding?.setLifecycleOwner(this)
        dataBinding?.viewmodel=viewModel

        viewModel?.entranceButton?.observe(this, Observer {
            if (RemoteUtils.checkPhoneNumberValidation(viewModel!!.getPhoneNumber())){
                viewModel!!.getPhoneNumber()?.let { it1 -> viewModel!!.savePhoneNum(it1) }
            } else{
                showMessage("لطفا شماره تلفن دستگاه را وارد کنید.")
            }
        })

        viewModel?.goNextPage?.observe(this, Observer {
            val intent = Intent(this@StartupActivity, LoginActivity::class.java)
            startActivity(intent)
        })

        viewModel?.turbowebsite?.observe(this,  Observer {
            val openURL = Intent(android.content.Intent.ACTION_VIEW)
            openURL.data = Uri.parse("http://radyabturbo.ir/")
            startActivity(openURL)
        })

        viewModel?.instagram?.observe(this,  Observer {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/radyabturbo.ir")
                )
            )
        })

//        viewModel?.telegram?.observe(this,  Observer {
//
//        })

        viewModel?.support?.observe(this,  Observer {
            if (hasPermission(Manifest.permission.CALL_PHONE)) {

                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.setData(Uri.parse("tel:" + "02144832215"))
                startActivity(callIntent)

            } else {
                requestPermissionsSafely(
                    arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_PERMISSION_CALL_SUPPORT
                )
            }
        })
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_CALL_SUPPORT ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.setData(Uri.parse("tel:" + "02144832215"))
                    startActivity(callIntent)
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // (fragment1 as CurrentFragment).showHideLocationIcon(false)
                }
        }
    }

    companion object {
        const val REQUEST_PERMISSION_CALL_SUPPORT = 230
    }
}
