package ir.turbo.turboremotecontrol.ui.login

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import ir.turbo.turboremotecontrol.base.BaseViewModel
import ir.turbo.turboremotecontrol.helper.SingleLiveEvent
import ir.turbo.turboremotecontrol.util.SharedPrefs
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val sharedPrefs: SharedPrefs) : BaseViewModel() {

    val loginPass = MutableLiveData<String>()
    val login = SingleLiveEvent<Void>()

    init {
        if (sharedPrefs.loadRemotePassword() == "" || sharedPrefs.loadRemotePassword() == "123456" ) {
            loginPass.postValue("123456")
        } else {
            loginPass.postValue("")
        }

       // System.out.println("8896523: login pass: " + sharedPrefs.loadLoginPassword())
        System.out.println("8896523: remote pass: " + sharedPrefs.loadRemotePassword())
    }

    fun loginBtnClick() {
        login.call()
    }

    fun saveLoginPass(pass: String) {
        sharedPrefs.saveRemotePassword(pass)
    }

    fun saveLauncherActivity(launcher: String) {
        sharedPrefs.saveLauncherPage(launcher)
    }

    fun checkPasswordValidation(pass: String): Boolean {
        val check: Boolean
        val passWord = sharedPrefs.loadRemotePassword()
        if (TextUtils.isEmpty(passWord)) {
           // check = pass.equals("123456")
            saveLoginPass(pass)
            check = true
        } else {
            check = passWord.equals(pass)
        }
        return check
    }


}