package ir.turbo.turboremotecontrol.ui.remote

import android.telephony.SmsManager
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import ir.turbo.turboremotecontrol.R
import ir.turbo.turboremotecontrol.base.BaseViewModel
import ir.turbo.turboremotecontrol.helper.SingleLiveEvent
import ir.turbo.turboremotecontrol.util.RemoteUtils
import ir.turbo.turboremotecontrol.util.SharedPrefs
import javax.inject.Inject

class RemoteViewModel @Inject constructor(private val sharedPrefs: SharedPrefs) : BaseViewModel() {

    var keyWord: String = ""
    var toastMessage: String = ""

    val showRequestDialog = SingleLiveEvent<Void>()
    val checkPermission = SingleLiveEvent<Void>()
    val changeTab = SingleLiveEvent<Void>()
    val showAnimation = MutableLiveData<String>()

    fun sendMessagetoRemote(): Boolean {
        var success = false
        val phone = sharedPrefs.loadRemotePhoneNumber()
        val remotePass = sharedPrefs.loadRemotePassword()
        if (RemoteUtils.checkPhoneNumberValidation(phone)) {
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(remotePass) && !TextUtils.isEmpty(keyWord)) {
                phone?.let { sendSMS(it, keyWord + remotePass) }
                showToast(toastMessage)
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

    fun remotePowerOnClick() {
        keyWord = "Resume"
        toastMessage="درخواست لغو خاموشی خودرو ارسال شد"
        checkPermission.call()
        showAnimation.postValue("remotePowerOn")
    }

    fun checkDialogStatus() {
        if (sharedPrefs.loadRequestDialogStatus()) {
            showRequestDialog.call()
        } else {
            sendMessagetoRemote()
        }
    }

    fun remotePowerOffClick() {
        toastMessage="دستور خاموش شدن خودرو ارسال شد"
        keyWord = "Stop"
        checkPermission.call()
        showAnimation.postValue("remotePowerOff")
    }

    fun remoteLockOnClick() {
        toastMessage="دستور فعال شدن ارسال شد"
        keyWord = "Arm"
        checkPermission.call()
        showAnimation.postValue("remoteLockOn")
    }

    fun remoteLockOffClick() {
        toastMessage="دستور لغو هشدارها ارسال شد"
        keyWord = "DisArm"
        checkPermission.call()
        showAnimation.postValue("remoteLockOff")
    }

    fun remoteLocationClick() {
        //   keyWord = "Resume"
        changeTab.call()
    }

    fun remoteHearingClick() {
        toastMessage="دستور فعال شدن حالت شنود ارسال شد"
        keyWord = "monitor"
        checkPermission.call()
        showAnimation.postValue("remoteHearing")
    }


    fun sendSMS(phone: String, code: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phone, null, code, null, null)
    }
}