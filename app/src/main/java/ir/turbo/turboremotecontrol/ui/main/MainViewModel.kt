package ir.turbo.turboremotecontrol.ui.main

import androidx.lifecycle.MutableLiveData
import ir.turbo.turboremotecontrol.base.BaseViewModel
import ir.turbo.turboremotecontrol.helper.SingleLiveEvent
import ir.turbo.turboremotecontrol.ui.setting.SettingFragment
import ir.turbo.turboremotecontrol.util.LocationResponse
import ir.turbo.turboremotecontrol.util.RemoteUtils
import ir.turbo.turboremotecontrol.util.SharedPrefs
import ir.turbo.turboremotecontrol.util.TotalCarCondition
import javax.inject.Inject

class MainViewModel @Inject constructor(private val sharedPrefs: SharedPrefs) : BaseViewModel() {

    val parseCheck = MutableLiveData<TotalCarCondition>()
    val location = MutableLiveData<LocationResponse>()
    val callRemote = MutableLiveData<String>()
    val warning = MutableLiveData<String>()
 //   val callRemote = SingleLiveEvent<Void>()

    fun showSMSResult(message : String){
        println("5236666: 0: "+message)
        if (!message.equals("password ok!") || message.equals("password ok")){
            if (message.contains("Power") && message.contains("Bat")) {
                parseCheck.postValue(RemoteUtils.parseCheckResponse(message))
                println("5236666: 1")
            } else if (message.equals("tracker ok!") || message.equals("monitor ok!")) {
               callRemote.postValue(sharedPrefs.loadRemotePhoneNumber())
                println("5236666: 2")
            } else if (!message.contains("alarm") && !message.contains("help me!") && (message.contains("lat") ||
                        message.contains("lon")) ){
                location.postValue(RemoteUtils.parseLocationResponse(message))
                println("5236666: 3")
            } else if (message.contains("alarm!")|| message.contains("alarm") || message.contains("help me!")){
                warning.postValue(RemoteUtils.parseWarning(message))
                println("5236666: 4")
            } else{
                showToast(RemoteUtils.translateRemoteResponse(message))
            }
        }
    }

    fun loadRemotePhoneNumber() : String{
       return sharedPrefs.loadRemotePhoneNumber().toString()
    }

}