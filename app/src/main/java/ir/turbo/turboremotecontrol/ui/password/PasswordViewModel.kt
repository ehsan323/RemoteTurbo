package ir.turbo.turboremotecontrol.ui.password

import android.telephony.SmsManager
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import ir.turbo.turboremotecontrol.base.BaseViewModel
import ir.turbo.turboremotecontrol.helper.SingleLiveEvent
import ir.turbo.turboremotecontrol.util.RemoteUtils
import ir.turbo.turboremotecontrol.util.SharedPrefs
import javax.inject.Inject

class PasswordViewModel @Inject constructor(private val sharedPrefs: SharedPrefs) : BaseViewModel() {

    val password = MutableLiveData<String>()
   // val oldPassword = MutableLiveData<String>()
    val currentPassword = MutableLiveData<String>()
    val closePassPage = SingleLiveEvent<Void>()

    val checkPermission1 = SingleLiveEvent<Void>()
    val checkPermission2 = SingleLiveEvent<Void>()

    var status = 0
    var pass = ""

    init {
        password.postValue("")
      //  oldPassword.postValue("")
        currentPassword.postValue(sharedPrefs.loadRemotePassword())
    }


    fun changeAppDevicePasswordClick() {
        checkPermission2.call()
    }

    fun handleSMS(){
        if (!TextUtils.isEmpty(password.value)) {
            if (password.value.toString().length > 3) {
                sendMessagetoRemote("password", password.value.toString(), currentPassword.value.toString(),"درخواست تغییر رمز ارسال شد")
                status = 1
                pass = password.value.toString()
            } else {
                showToast("طول پسورد انتخابی کوتاه است.")
            }

        } else {
            showToast("لطفا پسورد جدید را وارد کنید.")
        }
    }

    fun changeDevicePasswordClick() {
       // checkPermission1.call()
       val loginPass = sharedPrefs.loadRemotePassword()
        if (loginPass.equals(currentPassword.value)){
            if (!TextUtils.isEmpty(password.value.toString())){
                sharedPrefs.saveRemotePassword(password.value.toString().trim())
                showToast("پسورد ریموت با موفقیت آپدیت شد")
                currentPassword.postValue(password.value.toString())
            } else{
                showToast("فیلد رمز جدید خالی است")
            }
        } else{
            showToast("رمز قدیمی اشتباه است.")
        }
    }

    fun handleSMS1(){
        if (!TextUtils.isEmpty(password.value)) {
            sendMessagetoRemote("password", password.value.toString(), currentPassword.value.toString(),"درخواست تغییر رمز ارسال شد")
            status = 2
            pass = password.value.toString()
        } else {
            showToast("لطفا پسورد جدید را وارد کنید.")
        }
    }


    fun closeAdminPageClick() {
        closePassPage.call()
    }

    fun sendMessagetoRemote(keyWord: String, value: String, passOld: String, toast:String): Boolean {
        var success = false
        val phone = sharedPrefs.loadRemotePhoneNumber()
        if (RemoteUtils.checkPhoneNumberValidation(phone)) {
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(passOld) && !TextUtils.isEmpty(keyWord)) {
               if (keyWord == "password") {
                    phone?.let { sendSMS(it, keyWord + passOld + " " + value) }
                    System.out.println("8896523: " + keyWord + passOld + " " + value)
                }
                showToast(toast)
                success = true
            } else {
                success = false
                showToast("خطا در ارسال پیامک")
            }
        } else {
            showToast("شماره تلفن ریموت نامعتبر است.")
            success = false
        }

        return success
    }

    fun sendSMS(phone: String, code: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phone, null, code, null, null)
    }


    fun showSMSResult(message: String) {
        if (message.equals("password ok!") || message.equals("password ok")) {
            showToast("پسورد با موفقیت آپدیت شد.")
            currentPassword.postValue(pass)
            if (status == 1 && pass.length > 3) {
             //   sharedPrefs.saveLoginPassword(pass)
                sharedPrefs.saveRemotePassword(pass)
            } else if (status == 2 && pass.length > 3) {
                sharedPrefs.saveRemotePassword(pass)
            } else {
                showToast("خطا در تغییر پسورد!")
            }
        } else{
            showToast("خطا در تغییر پسورد!")
        }
    }
}