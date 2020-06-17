package ir.turbo.turboremotecontrol.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import ir.turbo.turboremotecontrol.R
import ir.turbo.turboremotecontrol.base.BaseActivity
import ir.turbo.turboremotecontrol.databinding.ActivityLoginBinding
import ir.turbo.turboremotecontrol.ui.main.MainActivity

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    override fun layoutResId(): Int {
        return R.layout.activity_login
    }

    override fun getViewModel(): Class<LoginViewModel> {
        return LoginViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding?.setLifecycleOwner(this)
        dataBinding?.viewmodel=viewModel

        viewModel?.login?.observe(this, Observer {
           if (viewModel!!.checkPasswordValidation(viewModel!!.loginPass.value!!)){
               viewModel!!.saveLauncherActivity("main")
//               if (viewModel!!.loginPass.value!!.equals("123456")){
//                   viewModel!!.saveLoginPass("123456")
//               }
               val intent = Intent(this@LoginActivity, MainActivity::class.java)
               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
               startActivity(intent)
               finishAffinity()
           } else{
               showMessage("پسورد نامعتبر است.")
           }
        })

    }
}
