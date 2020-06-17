package ir.turbo.turboremotecontrol.base

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import ir.turbo.turboremotecontrol.R
import javax.inject.Inject


abstract class BaseFragment<VM: ViewModel, DB : ViewDataBinding> : DaggerFragment(){

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: VM
    lateinit var dataBinding: DB

    @LayoutRes
    protected abstract fun layoutResId(): Int

    abstract fun getViewModel(): Class<VM>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(getViewModel())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, layoutResId(), container, false) as DB
        dataBinding.setLifecycleOwner(this)
        return dataBinding.root
    }

    fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frame, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (viewModel as BaseViewModel).toast.observe(this, Observer {
            showMessage(it)
        })

    }


//    private fun showSnackBar(message: String) {
//        val snackbar = Snackbar.make(
//            activity?.findViewById(android.R.id.content),
//            message, Snackbar.LENGTH_SHORT
//        )
//        val sbView = snackbar.view
//        val textView = sbView
//            .findViewById<View>(androidx.design.R.id.snackbar_text) as TextView
//        textView.setTextColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
//        snackbar.show()
//    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || activity?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }


    fun onError(resId: Int) {
        onError(getString(resId))
    }


    fun onError(message: String?) {
        if (message != null) {
         //  showSnackBar(message)
        }
    }


    fun showMessage(message: String?) {
        if (message != null) {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun showMessage(resId: Int) {
        showMessage(getString(resId))
    }
}