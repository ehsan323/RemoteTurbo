package ir.turbo.turboremotecontrol.ui.admin

import android.telephony.SmsManager
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import ir.turbo.turboremotecontrol.base.BaseViewModel
import ir.turbo.turboremotecontrol.helper.SingleLiveEvent
import ir.turbo.turboremotecontrol.util.RemoteUtils
import ir.turbo.turboremotecontrol.util.SharedPrefs
import ir.turbo.turboremotecontrol.util.TotalCarCondition
import java.lang.Exception
import javax.inject.Inject

class AdminViewModel @Inject constructor(private val sharedPrefs: SharedPrefs) : BaseViewModel() {

    val phoneNumber = MutableLiveData<String>()
    val closeAdminPage = SingleLiveEvent<Void>()
    val checkPermission = SingleLiveEvent<Void>()

    val parseCheck = MutableLiveData<TotalCarCondition>()

    var type:String = ""

    init {
        phoneNumber.value = ""
    }

    fun correctPhoneNum(phone: String): String {
        if (phone.startsWith("0")) {
            return phone.replaceFirst("0", "+98")
        } else{
            return phone
        }
    }

    fun sendSMS(phone: String, code: String) {
        try{
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phone, null, code, null, null)
        } catch (e:Exception){
            checkPermission.call()
        }
    }

    fun sendMessagetoRemote(keyWord: String, newAdminPhoneNumber: String, toast: String): Boolean {
        var success = false
        val remotePass = sharedPrefs.loadRemotePassword()
        val remotePhone = sharedPrefs.loadRemotePhoneNumber()
        if (RemoteUtils.checkPhoneNumberValidation(newAdminPhoneNumber)) {
            if (!TextUtils.isEmpty(remotePass) && !TextUtils.isEmpty(keyWord)) {
                if (remotePhone != null && remotePhone.length>0) {
                    sendSMS(remotePhone, keyWord + remotePass + " " + correctPhoneNum(newAdminPhoneNumber))
                }
             //   sharedPrefs.saveRemotePhoneNumber(newAdminPhoneNumber)
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

    fun registerAdminClick() {
        if (RemoteUtils.checkPhoneNumberValidation(phoneNumber.value)) {
            type="admin"
            phoneNumber.value?.let { sendMessagetoRemote("admin", it,"درخواست ثبت ادمین ارسال شد") }
        }  else{
            showToast("لطفا شماره موبایل معتبر وارد کنید.")
        }
    }

    fun deleteAdminClick() {
        if (RemoteUtils.checkPhoneNumberValidation(phoneNumber.value)) {
            type="noadmin"
            phoneNumber.value?.let { sendMessagetoRemote("noadmin", it,"درخواست حذف ادمین ارسال شد") }
         //   sharedPrefs.clearAdminPhoneNumber()
        } else{
            showToast("لطفا شماره موبایل معتبر وارد کنید.")
        }
    }

    fun sendSmsAfterPermission(){
        if (type.equals("admin")){
            registerAdminClick()
        } else{
            deleteAdminClick()
        }
    }

    fun closeAdminPageClick() {
        closeAdminPage.call()
    }

    fun showSMSResult(message : String){
        if (!message.equals("password ok!") || message.equals("password ok")){
            if (message.contains("admin ok!") || message.contains("admin ok") ||
                message.contains("noadmin ok!") || message.contains("noadmin ok")) {
              //  parseCheck.postValue(RemoteUtils.parseCheckResponse(message))
                showToast(RemoteUtils.translateRemoteResponse(message))
            } else if (message.contains("This number has been authorized!") ) {
                showToast("این شماره قبلا به عنوان ادمین ثبت شده است.")
            }
        }
    }

}