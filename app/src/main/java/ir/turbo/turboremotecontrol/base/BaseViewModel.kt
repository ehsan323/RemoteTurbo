package ir.turbo.turboremotecontrol.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

   // abstract fun getSharedPrefs(): SharedPrefs?

    public val toast = MutableLiveData<String>()

    fun showToast(message : String){
        toast.postValue(message)
    }

}