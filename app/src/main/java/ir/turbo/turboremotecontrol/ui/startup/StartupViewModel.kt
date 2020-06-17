package ir.turbo.turboremotecontrol.ui.startup

import androidx.lifecycle.MutableLiveData
import ir.turbo.turboremotecontrol.base.BaseViewModel
import ir.turbo.turboremotecontrol.helper.SingleLiveEvent
import ir.turbo.turboremotecontrol.util.SharedPrefs
import javax.inject.Inject

class StartupViewModel @Inject constructor(private val sharedPrefs: SharedPrefs) : BaseViewModel() {

    val entranceButton = SingleLiveEvent<Void>()
    val turbowebsite = SingleLiveEvent<Void>()
    val instagram = SingleLiveEvent<Void>()
    val telegram = SingleLiveEvent<Void>()
    val support = SingleLiveEvent<Void>()
    val goNextPage = SingleLiveEvent<Void>()

    val remotePhoneNumber = MutableLiveData<String>()

    init {
        remotePhoneNumber.postValue(sharedPrefs.loadRemotePhoneNumber())
    }

    fun supportClick(){
        support.call()
    }

    fun turbowebsiteClick(){
        turbowebsite.call()
    }

    fun instagramClick(){
        instagram.call()
    }

    fun telegramClick(){
        telegram.call()
    }

    fun entranceButtonClick(){
        entranceButton.call()
    }

    fun getPhoneNumber() : String?{
        return remotePhoneNumber.value
    }

    fun savePhoneNum(phone : String){
        sharedPrefs.saveRemotePhoneNumber(phone)
        goNextPage.call()
    }

}