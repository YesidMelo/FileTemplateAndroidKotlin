package ${PACKAGE_NAME}

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

class ${NAME}(
        val context: Context,
        val deviceWithOutGPSSensor: () -> Unit,
        val reloadFragment : (()->Unit) ?= null
) {

    companion object {
        private var beforeLocation:Location ?= null
        private var listenerLocationManager :action ?= null
        private var  newPosition:Boolean ? = false
        /**
         * llamar en el ondestroy
         */
        fun clear(){
            beforeLocation = null
            listenerLocationManager = null
            newPosition = null
        }
        fun stopVeryfication(){
            listenerLocationManager?.stop()
        }

        /**
         * llamar si se conoce la ultima posicion
         */
        fun setLastPosition(location: Location?){
            beforeLocation = location
        }
    }

    private var hasGPSSensor = false
    private var manager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var timePeriodic :Long = 500
    private var minDistance : Float = 0.toFloat()
    private val seconds : Long = 10

    init {
        verifyGPSSensor()
    }

    private fun verifyGPSSensor() {
        val pm: PackageManager = context.packageManager
        if (!pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            deviceWithOutGPSSensor()
            return
        }
        hasGPSSensor = true
        showAlertEnableGPS()
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(funLocation: (Location?) -> Unit,timeOut:()->Unit) {
        if (!hasGPSSensor) {
            return
        }
        if(!isEnabledPermissions()){
            showAlertEnabledPermissions(funLocation)
            return
        }

        if (manager.getProviders(true).isEmpty()) {
            showAlertEnableGPS(funLocation)
            return
        }
        launch {
            try {
                verifyLocation(funLocation)
            }catch (e:TimeoutException){
                timeOut.invoke()
                listenerLocationManager?.stop()
            }catch (e:Exception){
                Log.e("Error",Log.getStackTraceString(e))
                listenerLocationManager?.stop()
                timeOut.invoke()
            }
        }

    }

    private suspend fun verifyLocation(funLocation: (Location?) -> Unit)= withTimeout(1000*seconds) {
        var myLocation :Location? = null
        var stopLocation :(()->Unit) ? = null
        (this@${NAME}.context as Activity).runOnUiThread {
            showAlertEnableGPS(funLocation)
        }
        newPosition = false
        reportLocation@fun listenReportLocation(location: Location?,stop:(()->Unit)?= null) {
            beforeLocation = location
            myLocation =location
            newPosition = true
            stopLocation= stop
            funLocation(myLocation)
            stop?.invoke()
        }
        (this@${NAME}.context as Activity).runOnUiThread {
            monitorLocation(::listenReportLocation)
        }
        while (!newPosition!!){
            delay(500)
        }
    }

    private fun isEquals(loc1:Location,loc2:Location):Boolean{
        if(loc1.latitude == loc2.latitude && loc1.longitude == loc2.longitude){
            return true
        }
        return false
    }

    @SuppressLint("MissingPermission")
    private fun monitorLocation(listenNewLocation : ((Location?,(()->Unit)?)->Unit)?= null){
        var myProvider =""
        fun hasInternet():Boolean{
            try {
                val connectivityManager : ConnectivityManager? = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if(connectivityManager == null){
                    return false
                }
                val networkInfo : NetworkInfo = connectivityManager.activeNetworkInfo ?: return false
                return networkInfo?.isConnected!!
            } catch (e: Exception) {
                return false
            }
        }
        manager.getProviders(true).forEach listProvider@{
            if(hasInternet()){
                myProvider = LocationManager.NETWORK_PROVIDER
                return@listProvider
            }
            if (it == LocationManager.PASSIVE_PROVIDER) {
                myProvider = it
                return@listProvider
            }
            if (it == LocationManager.GPS_PROVIDER) {
                myProvider = it
                return@listProvider
            }
        }
        if(listenerLocationManager == null){
            listenerLocationManager = action(listenNewLocation)
        }
        manager.requestLocationUpdates(myProvider,timePeriodic, minDistance, listenerLocationManager!!)
    }

    private fun showAlertEnableGPS(funLocation: ((Location?) -> Unit) ? = null) {
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return
        }
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.gps_sensor))
        builder.setMessage(context.getString(R.string.enable_gps))

        builder.setPositiveButton(context.getString(R.string.accept), object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                funLocation?.invoke(null)
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        })

        builder.setNegativeButton(context.getString(R.string.cancel), object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
            }
        })
        builder.create().show()
    }

    fun isEnabledPermissions(funLocation: ((Location?) -> Unit) ? = null):Boolean{
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            return false
        }
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED){
            return false
        }
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED){
            return false
        }
        return true
    }

    private fun showAlertEnabledPermissions(funLocation:(Location?)->Unit){
        funLocation.invoke(null)
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.gps_sensor))
        builder.setMessage(context.getString(R.string.enable_permission_gps))

        builder.setPositiveButton(context.getString(R.string.accept), object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri= Uri.fromParts("package",context.packageName,null)
                intent.data = uri
                context.startActivity(intent)
            }
        })

        builder.setNegativeButton(context.getString(R.string.cancel), object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
            }
        })
        builder.create().show()
    }

    //Listeners
    private inner class action(val listenNewLocation: ((Location?,(()->Unit)?)->Unit)?= null):LocationListener{
        fun stop(){
            manager.removeUpdates(this)
        }
        override fun onLocationChanged(p0: Location?) {
            listenNewLocation?.invoke(p0,::stop)
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            Log.e("","")
        }

        override fun onProviderEnabled(p0: String?) {
            Log.e("","")
        }

        override fun onProviderDisabled(p0: String?) {
            Log.e("","")
        }

    }

}