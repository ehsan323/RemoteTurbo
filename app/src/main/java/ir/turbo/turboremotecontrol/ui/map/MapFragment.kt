package ir.turbo.turboremotecontrol.ui.map

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ir.turbo.turboremotecontrol.location.LocationParams
import ir.turbo.turboremotecontrol.R
import ir.turbo.turboremotecontrol.base.BaseFragment
import ir.turbo.turboremotecontrol.databinding.FragmentMapBinding
import ir.turbo.turboremotecontrol.ui.setting.RequestDialog
import ir.turbo.turboremotecontrol.util.LocationResponse

class MapFragment : BaseFragment<MapViewModel, FragmentMapBinding>(),
    RequestDialog.RequestDialogListener {

    private var onMapSMSPermission: OnMapSMSPermission? = null

    var lateLocationCall: String = "0"

    override fun clickOkButton() {
        viewModel.sendMessagetoRemote("درخواست مکان یابی خودرو ارسال شد")
        reqDialog.dismissAllowingStateLoss()
    }

    override fun clickCancelButton() {
        reqDialog.dismissAllowingStateLoss()
    }

    private var onFindCurrentLocation: OnFindCurrentLocation? = null
    private var gMap: GoogleMap? = null
    private var userMarker: Marker? = null
    private var latLng: LatLng? = null
    lateinit var reqDialog: DialogFragment
    lateinit var mapFragment: SupportMapFragment

    override fun layoutResId(): Int {
        return R.layout.fragment_map
    }

    override fun getViewModel(): Class<MapViewModel> {
        return MapViewModel::class.java
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // dataBinding.lifecycleOwner = this
        dataBinding.mviewmodel = viewModel
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reqDialog = RequestDialog.newInstance()

        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!

        viewModel.checkPermission.observe(this, Observer {
            if (hasPermission(Manifest.permission.SEND_SMS) && hasPermission(Manifest.permission.RECEIVE_SMS)) {
                // viewModel.sendMessagetoRemote("درخواست مکان یابی خودرو ارسال شد")
                viewModel.checkDialogStatus("درخواست مکان یابی خودرو ارسال شد")
            } else {
                onMapSMSPermission?.onMapSMSPermission()
            }
        })

        viewModel.showRequestDialog.observe(this, Observer {
            reqDialog.setTargetFragment(this@MapFragment, 4)
            fragmentManager?.let { reqDialog.show(it, "req_dialog") }
        })

        viewModel.driverLatLon.observe(this, Observer {
            showMap(it?.latitude!!.toDouble(), it.longitude!!.toDouble())
        })

        viewModel.driverFab.observe(this, Observer {
            Log.i("5233369", "driverFab clicked")
            onFindCurrentLocation?.onLocationRequest()
        })

        viewModel.carFab.observe(this, Observer {

        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    private fun showMap(lat: Double, lon: Double) {
        mapFragment.getMapAsync { googleMap ->
            latLng = LatLng(lat, lon)
            gMap = googleMap
//            if (userMarker != null) {
//                userMarker!!.remove()
//            }
            userMarker = googleMap.addMarker(
                MarkerOptions().position(latLng!!).title("")
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_pin_marker))
            )
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            googleMap.animateCamera(cameraUpdate)
            viewModel.bigProgress.postValue(false)
        }
    }

    companion object {
        fun newInstance(): MapFragment {

            val args = Bundle()

            val fragment = MapFragment()
            fragment.arguments = args
            return fragment
        }
    }

    fun showLocationOnMap(location: LocationResponse) {
        mapFragment.getMapAsync(fun(googleMap: GoogleMap) {
            if (!TextUtils.isEmpty(location.lat)) {
                latLng = LatLng(location.lat!!.toDouble(), location.lon!!.toDouble())
                gMap = googleMap
                userMarker = googleMap.addMarker(
                    MarkerOptions().position(latLng!!).title("")
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi2))
                )
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                googleMap.animateCamera(cameraUpdate)
                viewModel.bigProgress.postValue(false)
            } else {
                showMessage("لوکیشن در دسترس نیست")
            }
        })
    }

    fun sendSMS() {
        viewModel.checkDialogStatus("درخواست مکان یابی خودرو ارسال شد")
        // viewModel.sendMessagetoRemote("درخواست مکان یابی خودرو ارسال شد")
    }


    interface OnMapSMSPermission {
        fun onMapSMSPermission()
    }

    fun setOnMapSMSPermission(smsPermission: OnMapSMSPermission) {
        this.onMapSMSPermission = smsPermission
    }

    interface OnFindCurrentLocation {
        fun onLocationRequest()
    }

    fun setOnFindCurrentLocation(locationListener: OnFindCurrentLocation) {
        this.onFindCurrentLocation = locationListener
    }

    fun showHideBigProgress(show: Boolean) {
        viewModel.bigProgress.postValue(show)
    }

    fun showLocationError(str: String) {
        viewModel.locationError.postValue(str)
        viewModel.bigProgress.postValue(false)
    }

    fun showLocationSuccess(location: Location) {
        val locationParams = LocationParams()
        locationParams.latitude = location.latitude.toString()
        locationParams.longitude = location.longitude.toString()
        viewModel.driverLatLon.postValue(locationParams)
    }

    fun checkPerm() {
        if (hasPermission(Manifest.permission.SEND_SMS) && hasPermission(Manifest.permission.RECEIVE_SMS)) {
            viewModel.keyWord = "tracker"
            viewModel.checkDialogStatus("درخواست مکان یابی خودرو ارسال شد")
        } else {
            onMapSMSPermission?.onMapSMSPermission()
        }
    }

}