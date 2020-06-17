package ir.turbo.turboremotecontrol.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import ir.turbo.turboremotecontrol.R
import ir.turbo.turboremotecontrol.base.BaseActivity
import ir.turbo.turboremotecontrol.databinding.ActivityMainBinding
import ir.turbo.turboremotecontrol.ui.map.MapFragment
import ir.turbo.turboremotecontrol.ui.remote.RemoteFragment
import ir.turbo.turboremotecontrol.ui.setting.SettingFragment
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import androidx.lifecycle.Observer
import ir.turbo.turboremotecontrol.util.RemoteUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(),
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    MapFragment.OnFindCurrentLocation,
    RemoteFragment.OnCheckSMSPermission,
    SettingFragment.OnSettingSMSPermission,
    WarningsDialog.WarningsDialogListener,
    MapFragment.OnMapSMSPermission {

    lateinit var mGoogleApiClient: GoogleApiClient

    //  lateinit var smsVerifyCatcher: SmsVerifyCatcher
    private var isLocationAvailable: Boolean = false
    private lateinit var handler: android.os.Handler
    lateinit var mExpiredRunnable: Runnable
    lateinit var mCheckRunnable: Runnable
    internal val fm = supportFragmentManager
    var fragmentMap: Fragment? = null
    var fragmentRemote: Fragment? = null
    var fragmentSetting: Fragment? = null
    var active: Fragment? = null

    //  lateinit var warningDialog: DialogFragment
    var media: MediaPlayer? = null
    private var requestType = ""

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        media = MediaPlayer.create(this, R.raw.alarm)
        handler = android.os.Handler()

//        smsVerifyCatcher = SmsVerifyCatcher(this,
//            OnSmsCatchListener { message ->
//                viewModel?.showSMSResult(message)
//            })

//        btn_test.setOnClickListener {
//            viewModel?.showSMSResult("Power: ON\n" +
//                    "Bat: 100%\n" +
//                    "GPRS: ON\n" +
//                    "GPS: ON\n" +
//                    "ACC: ON\n" +
//                    "Door: ON\n" +
//                    "GSM: 31\n" +
//                    "Oil: 0.00%\n" +
//                    "ODO: 0.00\n" +
//                    "APN: mtnirancell,,;\n" +
//                    "IP: 193.193.165.37:28961\n" +
//                    "Arm: OFF")
//        }

        fragmentRemote = RemoteFragment.newInstance()
        (fragmentRemote as RemoteFragment).setOnCheckSMSPermission(this)
        fm.beginTransaction().add(R.id.frame, fragmentRemote!!, "remote").show(fragmentRemote!!)
            .commit()
        active = fragmentRemote


        viewModel?.parseCheck?.observe(this, Observer {
            if (fragmentSetting != null) {
                (fragmentSetting as SettingFragment).getCarconditionResponse(it)
            }
        })

        viewModel?.location?.observe(this, Observer {
            if (fragmentMap != null) {
                (fragmentMap as MapFragment).showLocationOnMap(it)
            }
        })

        viewModel?.callRemote?.observe(this, Observer {
            if (hasPermission(Manifest.permission.CALL_PHONE)) {

                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.setData(Uri.parse("tel:" + it))
                startActivity(callIntent)

            } else {
                requestPermissionsSafely(
                    arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_PERMISSION_CALL
                )
            }

        })

//        viewModel?.warning?.observe(this, Observer {
//            showWarningDialog(it)
//        })

        initBottomNavigation()


        val smsIntent = intent
        if (smsIntent != null) {
            val sms = smsIntent.getStringExtra("turbo_sms")
            if (sms != null) {
                if (sms.contains("alarm!") || sms.contains("alarm") || sms.contains("help me!")) {
                    showWarningDialog(sms)
                } else {
                    viewModel?.showSMSResult(sms)
                }
            } else {
                showMessage("null")
                //    viewModel?.showSMSResult("Door alarms!")
            }
        }
    }

    override fun cancelwarning() {
        if (media != null) {
            try {
                media!!.stop()
                media!!.release()
                media = null
            } catch (e: Exception) {
            }
        }
        //  warningDialog.dismissAllowingStateLoss()
    }

    private fun showWarningDialog(message: String) {

        val warningDialog = WarningsDialog.newInstance(message)
        warningDialog.show(supportFragmentManager, "warning_fragment")

        if (media != null) {
            try {
                media!!.start()
            } catch (e: Exception) {
            }
        } else {
            media = MediaPlayer.create(this, R.raw.alarm)
            media!!.start()
        }
    }


    override fun onSettingSMSPermission(type: String) {
        requestType = type
        if (hasPermission(Manifest.permission.SEND_SMS) && hasPermission(Manifest.permission.RECEIVE_SMS)) {
            (fragmentSetting as SettingFragment).sendSMS(type)
        } else {
            requestPermissionsSafely(
                arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS),
                REQUEST_PERMISSION_SMS3
            )
        }
    }


    override fun onChangeTab() {
        dataBinding?.bottomNavigation?.selectedItemId = R.id.map
        if (fragmentMap != null) {
            fm.beginTransaction().hide(active!!).show(fragmentMap!!).commit()
            active = fragmentMap
            (fragmentMap as MapFragment).lateLocationCall = "1"
            Handler().postDelayed({
                (fragmentMap as MapFragment).checkPerm()
            }, 1000)
        } else {
            fragmentMap = MapFragment.newInstance()
            (fragmentMap as MapFragment).setOnFindCurrentLocation(this)
            (fragmentMap as MapFragment).setOnMapSMSPermission(this)
            fm.beginTransaction().add(R.id.frame, fragmentMap!!, "map").hide(active!!)
                .show(fragmentMap!!)
                .commit()
            active = fragmentMap
            (fragmentMap as MapFragment).lateLocationCall = "1"
            Handler().postDelayed({
                (fragmentMap as MapFragment).checkPerm()
            }, 1000)
        }
    }

    override fun onMapSMSPermission() {
        if (hasPermission(Manifest.permission.SEND_SMS) && hasPermission(Manifest.permission.RECEIVE_SMS)) {
            (fragmentRemote as RemoteFragment).sendSMS()
        } else {
            requestPermissionsSafely(
                arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS),
                REQUEST_PERMISSION_SMS3
            )
        }
    }

    override fun onSMSPermission() {
        if (hasPermission(Manifest.permission.SEND_SMS) && hasPermission(Manifest.permission.RECEIVE_SMS)) {
            (fragmentRemote as RemoteFragment).sendSMS()
        } else {
            requestPermissionsSafely(
                arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS),
                REQUEST_PERMISSION_SMS
            )
        }
    }

    override fun onLocationRequest() {
        buildGoogleApiClient()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onConnected(p0: Bundle?) {
        setupLocationManager()
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun layoutResId(): Int {
        return R.layout.activity_main
    }

    override fun getViewModel(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun onStart() {
        super.onStart()
        //  smsVerifyCatcher.onStart()
    }

//    override fun onStop() {
//        super.onStop()
//        // smsVerifyCatcher.onStop()
//        smsVerifyCatcher = SmsVerifyCatcher(this,
//            OnSmsCatchListener { message ->
//                viewModel?.showSMSResult(message)
//            })
//    }


    private fun initBottomNavigation() {

        if (fragmentMap != null) {
            fm.beginTransaction().add(R.id.frame, fragmentMap!!, "map").hide(fragmentMap!!).commit()
        }
        if (fragmentSetting != null) {
            fm.beginTransaction().add(R.id.frame, fragmentSetting!!, "setting")
                .hide(fragmentSetting!!).commit()
        }

        dataBinding?.bottomNavigation?.selectedItemId = R.id.remote
        dataBinding?.bottomNavigation?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.remote -> {
                    if (fragmentRemote != null) {
                        fm.beginTransaction().hide(active!!).show(fragmentRemote!!).commit()
                        active = fragmentRemote
                    } else {
                        fragmentRemote = RemoteFragment.newInstance()
                        fm.beginTransaction().add(R.id.frame, fragmentRemote!!, "remote")
                            .hide(active!!)
                            .show(fragmentRemote!!)
                            .commit()
                        active = fragmentRemote
                    }
                    true
                }

                R.id.map -> {
                    if (fragmentMap != null) {
                        fm.beginTransaction().hide(active!!).show(fragmentMap!!).commit()
                        active = fragmentMap
                    } else {
                        fragmentMap = MapFragment.newInstance()
                        (fragmentMap as MapFragment).setOnFindCurrentLocation(this)
                        (fragmentMap as MapFragment).setOnMapSMSPermission(this)
                        fm.beginTransaction().add(R.id.frame, fragmentMap!!, "map").hide(active!!)
                            .show(fragmentMap!!)
                            .commit()
                        active = fragmentMap
                    }

                    true


                }

                R.id.settings -> {
                    if (fragmentSetting != null) {
                        fm.beginTransaction().hide(active!!).show(fragmentSetting!!).commit()
                        active = fragmentSetting
                    } else {
                        fragmentSetting = SettingFragment.newInstance()
                        (fragmentSetting as SettingFragment).setOnSettingSMSPermission(this)
                        fm.beginTransaction().add(R.id.frame, fragmentSetting!!, "settings")
                            .hide(active!!)
                            .show(fragmentSetting!!)
                            .commit()
                        active = fragmentSetting
                    }
                    true
                }
                else -> {
                    false
                }

            }

        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION_CODE ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient()
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // (fragment1 as CurrentFragment).showHideLocationIcon(false)
                }

            REQUEST_PERMISSION_SMS ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    (fragmentRemote as RemoteFragment).sendSMS()
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    (fragmentRemote as RemoteFragment).denySMSPermission()
                }

            REQUEST_PERMISSION_SMS2 ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    (fragmentMap as MapFragment).sendSMS()
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //  (fragmentRemote as RemoteFragment).denySMSPermission()
                }

            REQUEST_PERMISSION_SMS3 ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (fragmentSetting != null) {
                        (fragmentSetting as SettingFragment).sendSMS(requestType)
                    }
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //  (fragmentRemote as RemoteFragment).denySMSPermission()
                }

            REQUEST_PERMISSION_CALL ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.setData(Uri.parse("tel:" + viewModel?.loadRemotePhoneNumber()))
                    startActivity(callIntent)
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //  (fragmentRemote as RemoteFragment).denySMSPermission()
                }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //  smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                buildGoogleApiClient()
                (fragmentMap as MapFragment).showHideBigProgress(true)
            } else {
                removeHandlers()
                (fragmentMap as MapFragment).showHideBigProgress(false)
            }

        }
    }

    fun setupLocationManager() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) + ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val mLocationRequest = LocationRequest()
            mLocationRequest.interval = 3000 //5 seconds
            mLocationRequest.fastestInterval = 1000 //3 seconds
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationRequest.expirationTime = 20000

            val builder = LocationSettingsRequest.Builder()
            builder.setAlwaysShow(true)
            builder.addLocationRequest(mLocationRequest)

            val result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

            result.addOnCompleteListener {

                try {
                    Log.i("2003236", "LocationSettingsRequest")
                    //  val response = task2.getResult(ApiException::class.java)
                    // val status = response?.locationSettingsStates
                    val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                    mFusedLocationClient.locationAvailability.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful && task.result != null) {
                            val lastLocation = task.result
                            isLocationAvailable = lastLocation!!.isLocationAvailable
                            Log.i("2003236", "isLocationAvailable: $isLocationAvailable")
                        } else {
                            Log.i("2003236", "getLocationAvailability: not")
                        }
                    }

                    mFusedLocationClient.lastLocation
                        .addOnSuccessListener(this) { location ->
                            if (isLocationAvailable) {
                                storeLocation(location)
                                Log.i("2003236", "setupLocationManager11: " + location.latitude)
                                Log.i("2003236", "setupLocationManager12: " + location.longitude)
                            } else {
                                Log.i("2003236", "setupLocationManager11: not")
                            }

                        }

                    val mLocationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            for (location in locationResult.locations) {
                                storeLocation(location)
                            }
                        }
                    }

                    mFusedLocationClient.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback,
                        null
                    )

                    mCheckRunnable = Runnable {
                        setupLocationManager()
                        Log.i("2003236", "mCheckRunnable")
                    }
                    handler.postDelayed(mCheckRunnable, 6000)

                    mExpiredRunnable = Runnable {
                        showErrorMessage()
                        Log.i("2003236", "mExpiredRunnable")
                    }
                    handler.postDelayed(mExpiredRunnable, 12000)

                } catch (exception: ApiException) {
                    when (exception.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            val resolvable = exception as ResolvableApiException
                            resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                            Log.i("2003236", "RESOLUTION_REQUIRED")
                        } catch (e: IntentSender.SendIntentException) {
                            showErrorMessage()
                            Log.i("2003236", "SendIntentException")
                        }

                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            //   onLocationUpdateListener!!.onDialogSettingDenied()
                            Log.i("2003236", "SETTINGS_CHANGE_UNAVAILABLE")
                        }
                    }
                }
            }


        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_PERMISSION_CODE
            )
            Log.i("6632", "requestPermissions: ")
        }


    }


    private fun storeLocation(location: Location) {
        if (mGoogleApiClient.isConnected) {
            if (fragmentMap != null) {
                (fragmentMap as MapFragment).showLocationSuccess(location)
                removeHandlers()
            }

        } else {
            showErrorMessage()
            removeHandlers()
        }
    }

    fun removeHandlers() {
        handler.removeCallbacksAndMessages(null);
    }

    fun showErrorMessage() {
        (fragmentMap as MapFragment).showLocationError("موقعیت مکانی شما در دسترس نیست. لطفا مجددا اقدام کنید.")
    }


    @Synchronized
    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(ActivityRecognition.API)
            .addApi(LocationServices.API)
            .build()
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect()
        }

        mGoogleApiClient.isConnectionFailedListenerRegistered {
            showErrorMessage()
        }
    }


    override fun onBackPressed() {
        if (dataBinding!!.bottomNavigation.selectedItemId == R.id.remote) {
            //   finishAffinity()
            System.exit(0)
        } else {
            dataBinding!!.bottomNavigation.selectedItemId = R.id.remote
        }

        if (media != null) {
            media!!.stop()
            media!!.release()
        }

    }

    companion object {
        const val REQUEST_PERMISSION_CODE = 200
        const val REQUEST_PERMISSION_SMS = 201
        const val REQUEST_PERMISSION_SMS2 = 207
        const val REQUEST_PERMISSION_SMS3 = 205
        const val REQUEST_PERMISSION_CALL = 209
        const val REQUEST_CHECK_SETTINGS = 23
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (media != null) {
                media!!.stop()
                media!!.release()
                media = null
            }
        } catch (e: Exception) {
        }
    }

//    override fun onResume() {
//        super.onResume()
//        smsVerifyCatcher = SmsVerifyCatcher(this,
//            OnSmsCatchListener { message ->
//                viewModel?.showSMSResult(message)
//            })
//    }

}
