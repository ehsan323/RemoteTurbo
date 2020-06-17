package ir.turbo.turboremotecontrol.ui.setting

import android.telephony.SmsManager
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import ir.turbo.turboremotecontrol.base.BaseViewModel
import ir.turbo.turboremotecontrol.helper.SingleLiveEvent
import ir.turbo.turboremotecontrol.util.RemoteUtils
import ir.turbo.turboremotecontrol.util.SharedPrefs
import ir.turbo.turboremotecontrol.util.TotalCarCondition
import javax.inject.Inject

class SettingViewModel @Inject constructor(private val sharedPrefs: SharedPrefs) : BaseViewModel() {

    val carConDialog = SingleLiveEvent<Void>()
    val carSpeedLimit = SingleLiveEvent<Void>()
    val exitApp = SingleLiveEvent<Void>()
    val showRequestDialog = SingleLiveEvent<String>()
    val checkPermission = SingleLiveEvent<String>()
    val registerAdmin = SingleLiveEvent<Void>()
    val changePassword = SingleLiveEvent<Void>()
    val showSensitivityDialog = SingleLiveEvent<Void>()
    val adminPhoneNumber = MutableLiveData<String>()
    val remotePassword = MutableLiveData<String>()
    val switchStatus = MutableLiveData<Boolean>()

    var toastMessage: String = ""
    var keyWord: String = ""
    var speed: String = ""
    var sen: String = ""

    init {
        adminPhoneNumber.postValue(sharedPrefs.loadRemotePhoneNumber())
        remotePassword.postValue(sharedPrefs.loadRemotePassword())
        switchStatus.postValue(sharedPrefs.loadRequestDialogStatus())
    }

    fun updatePass() {
        remotePassword.postValue(sharedPrefs.loadRemotePassword())
    }

    fun exitAPPclick() {
        sharedPrefs.clearSharedPrefs()
        sharedPrefs.saveLauncherPage("startup")
        exitApp.call()
    }

    fun registerAdminClick() {
        registerAdmin.call()
    }

    fun changePasswordClick() {
        changePassword.call()
    }

    fun carSensivityClick() {
        checkPermission.postValue("sensitivity")
    }

    // onClick: Check Car Status
    fun showCarTotalConditionDialog() {
        checkPermission.postValue("car_condition")
    }

    fun showCarSpeedLimitDialog() {
        //  carSpeedLimit.call()
        checkPermission.postValue("speed")
    }

    fun cancelHelpClick() {
        checkPermission.postValue("help")
    }

    fun sendSpeedLimitSms(speed1: String) {
        toastMessage = "درخواست محدودست سرعت ارسال شد"
        keyWord = "speed"
        speed = speed1
        sen = ""
        sendMessagetoRemote()
    }

    fun sendcancelHelpSms() {
        if (sharedPrefs.loadRequestDialogStatus()) {
            showRequestDialog.postValue("help me!")
        } else {
            toastMessage = "دریافت پیام کمک لغو شد"
            keyWord = "help me!"
            speed = ""
            sen = ""
            sendMessagetoRemote()
        }
    }

    fun sendMessageProcessType(type: String, sen1: String) {
        if (type.equals("sensitivity")) {
            toastMessage = "درخواست تنظیم حساسیت ارسال شد"
            keyWord = "sensitivity"
            speed = ""
            sen = sen1
            sendMessagetoRemote()
        } else {
            toastMessage = "دریافت پیام کمک لغو شد"
            keyWord = "help me!"
            speed = ""
            sen = sen1
            sendMessagetoRemote()
        }
    }

    fun loadSpeedLimit(): String? {
        return sharedPrefs.loadSpeedLimit()
    }


    fun loadCarCon(): TotalCarCondition? {
        return sharedPrefs.loadTotalCarCondition()
    }

    fun loadCarConDefault(): TotalCarCondition {
        val carConDefault = TotalCarCondition()
        carConDefault.batteryPercent = "0 %"
        carConDefault.batteryStatus = "نامشخص"
        carConDefault.carDoor = "نامشخص"
        carConDefault.signalstatus = "نامشخص"
        carConDefault.switchStatus = "نامشخص"
        carConDefault.thiefCatcher = "نامشخص"
        return carConDefault
    }

    fun requestCarCondition() {
        toastMessage = "درخواست وضعیت خودرو ارسال شد"
        keyWord = "check"
        speed = ""
        sen = ""
        sendMessagetoRemote()
        //  checkPermission.postValue("car_condition")
    }


     fun sendMessagetoRemote(): Boolean {
        var success = false
        val phone = sharedPrefs.loadRemotePhoneNumber()
        val remotePass = sharedPrefs.loadRemotePassword()
        if (RemoteUtils.checkPhoneNumberValidation(phone)) {
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(remotePass) && !TextUtils.isEmpty(keyWord)) {
                if (keyWord == "sensitivity") {
                    phone?.let { sendSMS(it, keyWord + remotePass + " " + sen) }
                } else if (keyWord == "speed") {
                    phone?.let { sendSMS(it, keyWord + remotePass + " " + speed) }
                    sharedPrefs.saveSpeedLimit(speed)
                } else {
                    phone?.let { sendSMS(it, keyWord + remotePass) }
                }

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


    fun sendSMS(phone: String, code: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phone, null, code, null, null)
    }

    fun saveNewCarCondition(totalCarCondition: TotalCarCondition) {
        sharedPrefs.saveTotalCarCondition(totalCarCondition)
    }

    fun onCheckedChanged(checked: Boolean) {
        sharedPrefs.saveRequestDialogStatus(checked)
    }

    fun checkDialogStatus() {
        if (sharedPrefs.loadRequestDialogStatus()) {
            showRequestDialog.call()
        } else {
            sendMessagetoRemote()
        }
    }
}