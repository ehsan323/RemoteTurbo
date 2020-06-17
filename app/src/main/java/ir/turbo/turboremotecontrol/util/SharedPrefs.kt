package ir.turbo.turboremotecontrol.util

import android.content.SharedPreferences
import com.google.gson.Gson
import javax.inject.Inject

class SharedPrefs @Inject
constructor(mPreferences: SharedPreferences) {

    companion object {
        const val REMOTE_PHONE_NUMBER = "remote_phone_number"
        const val LOGIN_PASSWORD = "login_password"
        const val REMOTE_PASSWORD = "remote_password"
        const val TOTAL_CAR_CON = "total_car_condition"
        const val SPEED_LIMIT = "speed_limit"
        const val DIALOG_STATUS = "dialog_status"
        const val LAUNCHER_PAGE = "launcher_page"
    }

    private var mPreferences: SharedPreferences = mPreferences

    fun clearSharedPrefs() {
        mPreferences?.edit()?.remove(REMOTE_PHONE_NUMBER)?.commit()
        mPreferences?.edit()?.remove(REMOTE_PASSWORD)?.commit()
        mPreferences?.edit()?.remove(TOTAL_CAR_CON)?.commit()
        mPreferences?.edit()?.remove(SPEED_LIMIT)?.commit()
        mPreferences?.edit()?.remove(DIALOG_STATUS)?.commit()
        mPreferences?.edit()?.remove(LAUNCHER_PAGE)?.commit()
    }

    fun clearAdminPhoneNumber() {
        mPreferences?.edit()?.remove(REMOTE_PHONE_NUMBER)?.commit()
    }

    fun saveSpeedLimit(type: String) {
        mPreferences?.edit()?.putString(SPEED_LIMIT, type)?.commit()
    }

    fun loadSpeedLimit(): String? {
        return mPreferences?.getString(SPEED_LIMIT, "10")
    }


    fun saveTotalCarCondition(totalCarCondition: TotalCarCondition) {
        mPreferences?.edit()?.putString(TOTAL_CAR_CON, Gson().toJson(totalCarCondition))?.commit()
    }

    fun loadTotalCarCondition(): TotalCarCondition? {
        return Gson().fromJson(mPreferences?.getString(TOTAL_CAR_CON, ""), TotalCarCondition::class.java)
    }


    fun saveRemotePhoneNumber(type: String) {
        mPreferences?.edit()?.putString(REMOTE_PHONE_NUMBER, type)?.commit()
    }

    fun loadRemotePhoneNumber(): String? {
        return mPreferences?.getString(REMOTE_PHONE_NUMBER, "")
    }

//    fun saveLoginPassword(type: String) {
//        mPreferences?.edit()?.putString(LOGIN_PASSWORD, type)?.commit()
//    }
//
//    fun loadLoginPassword(): String? {
//        return mPreferences?.getString(LOGIN_PASSWORD, "")
//    }

    fun saveRemotePassword(type: String) {
        mPreferences?.edit()?.putString(REMOTE_PASSWORD, type)?.commit()
    }

    fun loadRemotePassword(): String? {
        return mPreferences?.getString(REMOTE_PASSWORD, "")
    }

    fun saveRequestDialogStatus(checked: Boolean) {
        mPreferences?.edit()?.putBoolean(DIALOG_STATUS, checked)?.commit()
    }

    fun loadRequestDialogStatus(): Boolean {
        return mPreferences.getBoolean(DIALOG_STATUS, false)
    }

    fun saveLauncherPage(string: String) {
        mPreferences?.edit()?.putString(LAUNCHER_PAGE, string)?.commit()
    }

    fun loadLauncherPage(): String {
        return mPreferences.getString(LAUNCHER_PAGE, "").toString()
    }

}
