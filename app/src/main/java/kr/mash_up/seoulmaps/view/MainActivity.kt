package kr.mash_up.seoulmaps.view

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
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
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
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
import kr.mash_up.seoulmaps.adapter.PlaceAutocompleteAdapter
import kr.mash_up.seoulmaps.base.BaseActivity
import kr.mash_up.seoulmaps.present.MainContract
import kr.mash_up.seoulmaps.present.MainPresenter
import kr.mash_up.seoulmaps.util.SharedPreferencesUtil

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

    // Used for selecting the current place.
    private val mMaxEntries = 5
    private var mLikelyPlaceNames = arrayOfNulls<String>(mMaxEntries)
    private var mLikelyPlaceAddresses = arrayOfNulls<String>(mMaxEntries)
    private var mLikelyPlaceAttributions = arrayOfNulls<String>(mMaxEntries)
    private var mLikelyPlaceLatLngs = arrayOfNulls<LatLng>(mMaxEntries)

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
    protected lateinit var p: MainContract.Presenter

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        p = MainPresenter(this)

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
        setCategoryDialog()
        setArcMenu()
        setSearchRecyclerView()
        setSearchPlaceTextView()
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

    private fun setCategoryDialog() {
        val fm = MainFragment.newInstance()
        fm.setOnClickListener { category ->
            if (category == "toilet") {
                Log.d(TAG, "toilet")
            } else {
                Log.d(TAG, "smoke")
            }
        }
        fm.show(supportFragmentManager, MainFragment.TAG)
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

    //    /**
    //     * Listener that handles selections from suggestions from the AutoCompleteTextView that
    //     * displays Place suggestions.
    //     * Gets the place id of the selected item and issues a request to the Places Geo Data API
    //     * to retrieve more details about the place.
    //     */
    //    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
    //        @Override
    //        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    //            /*
    //             Retrieve the place ID of the selected item from the Adapter.
    //             The adapter stores each Place suggestion in a AutocompletePrediction from which we
    //             read the place ID and title.
    //              */
    //            final AutocompletePrediction item = mAdapter.getItem(position);
    //            final String placeId = item.getPlaceId();
    //            final CharSequence primaryText = item.getPrimaryText(null);
    //
    //            Log.i(TAG, "Autocomplete item selected: " + primaryText);
    //
    //            /*
    //             Issue a request to the Places Geo Data API to retrieve a Place object with additional
    //             details about the place.
    //              */
    //            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
    //            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
    //
    //            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText, Toast.LENGTH_SHORT).show();
    //            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
    //        }
    //    };
    //    /**
    //     * Callback for results from a Places Geo Data API query that shows the first place result in
    //     * the details view on screen.
    //     */
    //    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
    //        @Override
    //        public void onResult(PlaceBuffer places) {
    //            if (!places.getStatus().isSuccess()) {
    //                // Request did not complete successfully
    //                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
    //                places.release();
    //                return;
    //            }
    //            // Get the Place object from the buffer.
    //            final Place place = places.get(0);
    //
    //            // Format details of the place for display and show it in a TextView.
    //            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
    //                    place.getId(), place.getAddress(), place.getPhoneNumber(),
    //                    place.getWebsiteUri()));
    //
    //            // Display the third party attributions if set.
    //            final CharSequence thirdPartyAttribution = places.getAttributions();
    //            if (thirdPartyAttribution == null) {
    //                mPlaceDetailsAttribution.setVisibility(View.GONE);
    //            } else {
    //                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
    //                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
    //            }
    //
    //            Log.i(TAG, "Place details received: " + place.getName());
    //
    //            places.release();
    //        }
    //    };
    //    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
    //                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
    //        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
    //                websiteUri));
    //        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
    //                websiteUri));
    //    }


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
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
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

        //Fused Location Provider
        if (requestCode != RC_PERMISSION) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.isNotEmpty()) {
            for (code in grantResults) {
                if (code == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                    return
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
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
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

    private fun showCurrentPlace() {
        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            val result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null)
            result.setResultCallback { likelyPlaces ->
                var i = 0
                mLikelyPlaceNames = arrayOfNulls<String>(mMaxEntries)
                mLikelyPlaceAddresses = arrayOfNulls<String>(mMaxEntries)
                mLikelyPlaceAttributions = arrayOfNulls<String>(mMaxEntries)
                mLikelyPlaceLatLngs = arrayOfNulls<LatLng>(mMaxEntries)
                for (placeLikelihood in likelyPlaces) {
                    // Build a list of likely places to show the user. Max 5.
                    mLikelyPlaceNames[i] = placeLikelihood.place.name as String
                    mLikelyPlaceAddresses[i] = placeLikelihood.place.address as String
                    mLikelyPlaceAttributions[i] = placeLikelihood.place
                            .attributions as String
                    mLikelyPlaceLatLngs[i] = placeLikelihood.place.latLng

                    i++
                    if (i > mMaxEntries - 1) {
                        break
                    }
                }
                // Release the place likelihood buffer, to avoid memory leaks.
                likelyPlaces.release()

                // Show a dialog offering the user the list of likely places, and add a
                // marker at the selected place.
                openPlacesDialog()
            }
        } else {
            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocationSeoul)
                    .snippet(getString(R.string.default_info_snippet)))
        }
    }

    private fun openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        val listener = DialogInterface.OnClickListener { dialog, which ->
            // The "which" argument contains the position of the selected item.
            val markerLatLng = mLikelyPlaceLatLngs[which]
            var markerSnippet = mLikelyPlaceAddresses[which]
            if (mLikelyPlaceAttributions[which] != null) {
                markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which]
            }
            // Add a marker for the selected place, with an info window
            // showing information about that place.
            mMap.addMarker(MarkerOptions()
                    .title(mLikelyPlaceNames[which])
                    .position(markerLatLng!!)
                    .snippet(markerSnippet))

            // Position the map's camera at the location of the marker.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                    DEFAULT_ZOOM.toFloat()))
        }

        // Display the dialog.
        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show()
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
        getLocation()   // 현재 내 GPS정보 가져옴
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), RC_PERMISSION)
            }
            Snackbar.make(window.decorView.rootView, "Location Permission", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK") { ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), RC_PERMISSION) }.show()
            return
        }

        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
                .let { displayLocation(it) }

        //업데이트 요청을 설정
        val request = LocationRequest()
        //최소업데이트 시간(5초)
        request.fastestInterval = 5000
        //실제 업데이트 시간
        request.interval = 7000
        //최소 업데이트 거리(meter)
        request.smallestDisplacement = 3f
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        //리스너 등록
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, mListener)
    }

    var mListener: LocationListener = LocationListener { it -> displayLocation(it) }

    private fun displayLocation(location: Location) {
        SharedPreferencesUtil.getInstances()?.userLat = location.latitude.toFloat()
        SharedPreferencesUtil.getInstances()?.userLong = location.longitude.toFloat()

        val userLatitue = SharedPreferencesUtil.getInstances()?.userLat
        Log.d(TAG, userLatitue.toString())
    }
    override fun onConnectionSuspended(i: Int) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mListener)
    }


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

    /**
     * Saves the state of the map when the activity is paused.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_CAMERA_POSITION, mMap.cameraPosition)
        outState.putParcelable(KEY_LOCATION, mLastKnownLocation)
        outState.putBoolean(FIELD_ERROR_PROCESSING, isErrorProcessing)
    }

    override fun onBackPressed() {
        if (dialogIsVisible) {
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

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val DEFAULT_ZOOM = 15
        private val KEY_CAMERA_POSITION = "camera_position"
        private val KEY_LOCATION = "location"

        private val RC_PERMISSION = 1
        private val RC_API_CLIENT = 2
        private val FIELD_ERROR_PROCESSING = "errorProcessing"

        private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private val BOUNDS_GREATER_SYDNEY = LatLngBounds(
                LatLng(-34.041458, 150.790100), LatLng(-33.682247, 151.383362))
    }
}
