package kr.mash_up.seoulmaps.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.PlaceBuffer
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.sa90.materialarcmenu.StateChangeListener
import kotlinx.android.synthetic.main.activity_main.*
import kr.mash_up.seoulmaps.R
import kr.mash_up.seoulmaps.R.drawable.search
import kr.mash_up.seoulmaps.SeoulMapApplication
import kr.mash_up.seoulmaps.adapter.PlaceAutocompleteAdapter
import kr.mash_up.seoulmaps.base.BaseActivity
import kr.mash_up.seoulmaps.data.PublicToiletItem
import kr.mash_up.seoulmaps.data.model.PublicInfoDataSource
import kr.mash_up.seoulmaps.present.MainContract
import kr.mash_up.seoulmaps.present.MainPresenter

/**
 * Created by wooyoungki on 2017. 7. 24..
 */

class MainActivity : BaseActivity(), MainContract.View,
        OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private var mCameraPosition: CameraPosition? = null
    private var mLastKnownLocation: Location? = null
    private var pressedTime: Long = 0
    private var dialogIsVisible = false

    private val mDefaultLocationSeoul = LatLng(37.56, 126.97)  //서울
    private var mLocationPermissionGranted: Boolean = false

    private var category: String = ""

    // Used for location search
    private val mAdapter: PlaceAutocompleteAdapter by lazy {
        PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null)
    }
    private val mGoogleApiClient: GoogleApiClient by lazy {
        GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build()
    }

    protected lateinit var presenter: MainContract.Presenter

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = MainPresenter()
        presenter.view = this
        presenter.toiletInfo = PublicInfoDataSource
        presenter.smokeInfo = PublicInfoDataSource

        savedInstanceState?.let {
            mLastKnownLocation = it.getParcelable<Location>(KEY_LOCATION)
            mCameraPosition = it.getParcelable<CameraPosition>(KEY_CAMERA_POSITION)
            isErrorProcessing = it.getBoolean(FIELD_ERROR_PROCESSING)
        }
    }

    @Override
    public override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect()
        presenter.adapterView = mAdapter
        search_recycler_view.adapter = mAdapter
    }

    @Override
    public override fun onStop() {
        super.onStop()
        mGoogleApiClient.disconnect()
    }

    override fun getLayoutId() = R.layout.activity_main

    override fun initView() {
        setGoogleMap()
        setUpToolbar()
        setArcMenu()
        setSearchRecyclerView()
        setSearchPlaceTextView()
        showCategoryDialog()
    }

    private fun setSearchPlaceTextView() =
            autocomplete_places.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString() != "" && mGoogleApiClient.isConnected) {
                        mAdapter.let {
                            it.filter.filter(s.toString())
                            search_recycler_view.adapter = it
                        }
                    } else
                        Log.e(TAG, "inot connected")
                }
            })

    private fun setSearchRecyclerView() =
            search_recycler_view.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@MainActivity)
            }

    private fun setArcMenu() =
            arcMenu.apply {
                setRadius(resources.getDimension(R.dimen.radius))
                setStateChangeListener(object : StateChangeListener {
                    override fun onMenuClosed() {

                    }

                    override fun onMenuOpened() {

                    }

                })
            }

    private fun setGoogleMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @UiThread
    private fun setUpToolbar() =
            toolbar?.apply {
                setNavigationIcon(search)
                setOnClickListener { showSearchLayout() }
            }

    private fun showSearchLayout() {
        dialogIsVisible = true
        container?.visibility = View.VISIBLE
        container?.bringToFront()   //우선순위 가장 위
        search_recycler_view?.bringToFront()
        toolbar?.visibility = View.GONE
        search_layout.visibility = View.VISIBLE
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override // Return null here, so that getInfoContents() is called next.
            fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                // Inflate the layouts for the info window, title and snippet.
                val infoWindow = layoutInflater.inflate(R.layout.custom_info_contents, findViewById(R.id.map) as FrameLayout, false)

                val title = infoWindow.findViewById(R.id.title) as TextView
                title.text = marker.title

                val snippet = infoWindow.findViewById(R.id.snippet) as TextView
                snippet.text = marker.snippet

                return infoWindow
            }
        })

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()
        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
    }

    private fun getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
        /*
     * Before getting the device location, you must check location
     * permission, as described earlier in the tutorial. Then:
     * Get the best and most recent location of the device, which may be
     * null in rare cases when a location is not available.
     */
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition))
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(mLastKnownLocation!!.latitude,
                            mLastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
        } else {
            Log.d(TAG, "Current location is null. Using defaults.")
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocationSeoul, DEFAULT_ZOOM.toFloat()))
            mMap.uiSettings.isMyLocationButtonEnabled = false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }

        updateLocationUI()
    }

    private fun updateLocationUI() {
        /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }

        if (mLocationPermissionGranted) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
        } else {
            mMap.isMyLocationEnabled = false
            mMap.uiSettings.isMyLocationButtonEnabled = false
            mLastKnownLocation = null
        }
    }

    private var isErrorProcessing = false
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (isErrorProcessing) return
        isErrorProcessing = true
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RC_API_CLIENT)
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
                mGoogleApiClient.connect()
            }

        } else {
            val dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.errorCode, RC_API_CLIENT)
            dialog.setCancelable(false)
            dialog.show()
        }
    }

    override fun onConnected(bundle: Bundle?) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun showCategoryDialog() {
        val fm = MainFragment.newInstance()
        fm.setOnClickListener { item ->
            category = item
            mLastKnownLocation?.let {
                val lat = it.latitude
                val lng = it.longitude
                Log.d("latlngaa", lat.toString() + "," + lng.toString())

                callPublicService(lat, lng)
            }
        }
        fm.show(supportFragmentManager, MainFragment.TAG)
    }

    override fun onConnectionSuspended(i: Int) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode != RC_API_CLIENT) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }
        isErrorProcessing = false
        if (resultCode == Activity.RESULT_OK) {
            mGoogleApiClient.connect()
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState?.putParcelable(KEY_CAMERA_POSITION, mMap.cameraPosition)
        savedInstanceState?.putParcelable(KEY_LOCATION, mLastKnownLocation)
        savedInstanceState?.putBoolean(FIELD_ERROR_PROCESSING, isErrorProcessing)
    }

    override fun onBackPressed() {
        if (dialogIsVisible) {
            //전면에 나와있는 탐색 창을 닫는다.
            container?.visibility = View.GONE
            toolbar?.visibility = View.VISIBLE
            search_layout?.visibility = View.GONE
            dialogIsVisible = false
        } else if (arcMenu.isMenuOpened)
            arcMenu?.toggleMenu()
        else if (pressedTime == 0.toLong()) {
            Toast.makeText(this@MainActivity, " 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show()
            pressedTime = System.currentTimeMillis()
        } else {
            val seconds = (System.currentTimeMillis() - pressedTime).toInt()

            if (seconds > 2000) {
                Toast.makeText(this@MainActivity, " 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show()
                pressedTime = 0
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun getPlaceInfo(placeItem: AutocompletePrediction?) {
        val placeId = placeItem?.placeId
        val primaryText = placeItem?.getPrimaryText(null)

        Toast.makeText(applicationContext, "Clicked: " + primaryText, Toast.LENGTH_SHORT).show()
        Log.i(TAG, "Called getPlaceById to get Place details for " + placeId)

        //전면에 나와있는 탐색 창을 닫는다.
        container?.visibility = View.GONE
        toolbar?.visibility = View.VISIBLE
        search_layout.visibility = View.GONE
        dialogIsVisible = false

        val placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
        placeResult.setResultCallback(mUpdatePlaceDetailsCallback)
    }

    private val mUpdatePlaceDetailsCallback = ResultCallback<PlaceBuffer> { places ->
        if (!places.status.isSuccess) {
            Log.e(TAG, "Place query did not complete. Error: " + places.status.toString())
            places.release()
            return@ResultCallback
        }
        // Get the Place object from the buffer.
        val place = places.get(0)
        val lat = place.latLng.latitude
        val lng = place.latLng.longitude
        callPublicService(lat, lng)

        //줌 이동
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), DEFAULT_ZOOM.toFloat()))

        places.release()
    }

    private fun callPublicService(lat: Double, lng: Double) {
        when(category) {
            "toilet" -> presenter.getPublicToiletInfo(202652.047 ,444755.038)
            "smoke" -> presenter.getPublicSmokeInfo(202652.047, 444755.038)
        }
    }

    override fun showToiletInfo(publicToiletItem: List<PublicToiletItem>?) {
        publicToiletItem?.let {
            for (item in publicToiletItem) {
                val lng = item.location[0]
                val lat = item.location[1]

                //마커 찍음
                mMap.addMarker(MarkerOptions().position(LatLng(lng, lat)).title("Marker in Sydney"))
            }
        }
    }
    override fun showSmokeInfo() {

    }
    override fun showLoadFail() {
        Toast.makeText(SeoulMapApplication.context, "데이터를 가져오지 못하였습니다.", Toast.LENGTH_SHORT).show()
    }


    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val DEFAULT_ZOOM = 15
        private val KEY_CAMERA_POSITION = "camera_position"
        private val KEY_LOCATION = "location"

        private val RC_PERMISSION = 1
        private val RC_API_CLIENT = 2
        private val FIELD_ERROR_PROCESSING = "errorProcessing"

        private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private val BOUNDS_GREATER_SYDNEY = LatLngBounds(LatLng(37.56, 126.97), LatLng(37.56, 126.97))
    }
}
