package ir.turbo.turboremotecontrol.ui.map

import android.telephony.SmsManager
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import ir.turbo.turboremotecontrol.location.LocationParams
import ir.turbo.turboremotecontrol.base.BaseViewModel
import ir.turbo.turboremotecontrol.helper.SingleLiveEvent
import ir.turbo.turboremotecontrol.util.RemoteUtils
import ir.turbo.turboremotecontrol.util.SharedPrefs
import javax.inject.Inject

class MapViewModel @Inject constructor(private val sharedPrefs: SharedPrefs) : BaseViewModel() {

    val bigProgress = SingleLiveEvent<Boolean>()
    val driverFab = SingleLiveEvent<Void>()
    val carFab = SingleLiveEvent<Void>()
    val carLatLon = SingleLiveEvent<LocationParams>()
    val driverLatLon = SingleLiveEvent<LocationParams>()
    val locationError = MutableLiveData<String>()

    val checkPermission = SingleLiveEvent<Void>()
    val showRequestDialog = SingleLiveEvent<Void>()

    var keyWord: String = ""

    fun driverFabClick(){
        driverFab.call()
    }

    fun carFabClick(){
       // carFab.call()
      //  checkPermission.call()
        keyWord="tracker"
        checkPermission.call()
    }


    fun checkDialogStatus(toast: String) {
        if (sharedPrefs.loadRequestDialogStatus()) {
        //    keyWord="tracker"
            showRequestDialog.call()
        } else {
            sendMessagetoRemote(toast)
        }
    }



    fun sendMessagetoRemote(toast: String): Boolean {
        var success = false
        val phone = sharedPrefs.loadRemotePhoneNumber()
        val remotePass = sharedPrefs.loadRemotePassword()
        if (RemoteUtils.checkPhoneNumberValidation(phone)) {
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(remotePass) && !TextUtils.isEmpty(keyWord)) {
                phone?.let { sendSMS(it, keyWord + remotePass) }
                System.out.println("29998: send: " + keyWord + remotePass)
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

}