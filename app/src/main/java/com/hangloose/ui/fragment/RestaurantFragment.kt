package com.hangloose.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import co.ceryle.radiorealbutton.RadioRealButtonGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.hangloose.R
import com.hangloose.ui.activities.SwipeableCardView
import com.hangloose.ui.activities.TabsActivity
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.KEY_DATA
import com.hangloose.utils.dpToPx
import com.hangloose.utils.getDisplaySize
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.SwipeViewBuilder
import kotlinx.android.synthetic.main.fragment_restaurant.editLocation
import kotlinx.android.synthetic.main.fragment_restaurant.view.editLocation

class RestaurantFragment : Fragment() {

    private var TAG = "RestaurantFragment"
    private var mSwipePlaceHolderView: SwipePlaceHolderView? = null
    private var mBtFilter: ImageButton? = null
    private var mBtRadioRealGroup: RadioRealButtonGroup? = null
    private var mEditLocation: EditText? = null
    private var mRestaurantData: ArrayList<RestaurantData>? = ArrayList()
    private val REQUEST_LOCATION_MAPS = 9
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mAddress: String? = null
    private var mContext: TabsActivity? = null
    private val LOCATION_REQUEST_CODE = 109

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRestaurantData = arguments!!.getParcelableArrayList(KEY_DATA)
        mAddress = arguments!!.getString("abc")
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        //checkLocationPermission()
        Log.i(TAG, "Restaurant data : $mRestaurantData")
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.i(TAG, "onAttach : ")
        if (context is TabsActivity) {
            mContext = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_restaurant, null)
        mSwipePlaceHolderView = rootView!!.findViewById(R.id.swipeView) as SwipePlaceHolderView
        mBtFilter = rootView.findViewById(R.id.ibFilter) as ImageButton
        mBtRadioRealGroup = rootView.findViewById(R.id.segmentedButtonGroup) as RadioRealButtonGroup
        mEditLocation = rootView.findViewById(R.id.editLocation) as EditText
        mEditLocation!!.setText(mAddress)
        setSwipeableView()
        setLocationSearch(rootView)
        return rootView
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    private fun setLocationSearch(rootView: View) {
        rootView.editLocation.setOnTouchListener(View.OnTouchListener { _, event ->
            val DRAWABLE_END = 2
            if (event.action == MotionEvent.ACTION_UP) {
                checkLocationPermission()
                if (event.rawX >= (rootView.editLocation.right - rootView.editLocation.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    //Open Maps
                    val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .build(activity)
                    startActivityForResult(intent, REQUEST_LOCATION_MAPS)
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION_MAPS -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val place = PlaceAutocomplete.getPlace(mContext!!, data)
                        Log.i(TAG, place.toString())
                        Log.i(
                            TAG + "1",
                            "${place.latLng.latitude} && ${place.latLng.longitude} && ${place.address}"
                        )
                        editLocation.setText(place.address.toString())
                    }
                    PlaceAutocomplete.RESULT_ERROR -> {
                        val status = PlaceAutocomplete.getStatus(context, data)
                        Log.i(TAG, status.statusMessage)
                    }
                    Activity.RESULT_CANCELED -> Log.i(TAG, "RESULT_CANCELED")
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                checkLocationPermission()
            }
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                mContext!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                mContext!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        } else {
            mFusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->
                if (location != null) {
                    Log.i(TAG, "${location.latitude} && ${location.longitude}")
                    val geoCoder = Geocoder(context)
                    val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (address.size > 0) {
                        editLocation.setText(address[0].getAddressLine(0), TextView.BufferType.EDITABLE)
                    }
                }
            }
        }
    }

    private fun setSwipeableView() {
        val sideMargin = dpToPx(220)
        val bottomMargin = dpToPx(180)
        val windowSize = getDisplaySize(activity!!.windowManager)
        Log.i(TAG, "Window Size : $windowSize")
        Log.i(TAG, "Bottom Margin : $bottomMargin")
        mSwipePlaceHolderView!!.getBuilder<SwipePlaceHolderView, SwipeViewBuilder<SwipePlaceHolderView>>()
            .setDisplayViewCount(3)
            .setHeightSwipeDistFactor(10f)
            .setWidthSwipeDistFactor(5f)
            .setSwipeDecor(
                SwipeDecor()
                    .setViewWidth(windowSize.x - bottomMargin / 4)
                    .setViewHeight(windowSize.y - bottomMargin)
                    .setViewGravity(Gravity.CENTER)
                    .setRelativeScale(0.01f)
                    .setSwipeMaxChangeAngle(2f)
            )
        mSwipePlaceHolderView!!.removeAllViews()
        for (data in mRestaurantData!!) {
            if (data.restaurantType.equals("VEGETERIAN")) {
                mSwipePlaceHolderView!!.addView(SwipeableCardView(mContext!!, data, mSwipePlaceHolderView!!))
            }
        }

        //mBtFilter!!.setOnClickListener { (activity as TabsActivity).replaceFragment(FilterFragment()) }

        mBtRadioRealGroup!!.setOnPositionChangedListener { _, currentPosition, _ ->
            when (currentPosition) {
                0 -> {
                    var one = mRestaurantData!!.map { it.restaurantType }.filter { it.equals("VEGETARIAN") }
                    Log.i(TAG, "VEG : $one")
                    mSwipePlaceHolderView!!.removeAllViews()
                    for (data in mRestaurantData!!) {
                        if (data.restaurantType.equals("VEGETERIAN")) {
                            mSwipePlaceHolderView!!.addView(
                                SwipeableCardView(
                                    mContext!!,
                                    data,
                                    mSwipePlaceHolderView!!
                                )
                            )
                        }
                    }
                }
                1 -> {
                    mSwipePlaceHolderView!!.removeAllViews()
                    for (data in mRestaurantData!!) {
                        if (data.restaurantType.equals("NON_VEGETERIAN")) {
                            mSwipePlaceHolderView!!.addView(
                                SwipeableCardView(
                                    context!!,
                                    data,
                                    mSwipePlaceHolderView!!
                                )
                            )
                        }
                    }
                }
                else -> {
                    mSwipePlaceHolderView!!.removeAllViews()
                    for (data in mRestaurantData!!) {
                        if (data.restaurantType.equals("BAR")) {
                            mSwipePlaceHolderView!!.addView(
                                SwipeableCardView(
                                    mContext!!,
                                    data,
                                    mSwipePlaceHolderView!!
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/*mBtSegmentedGroup!!.setOnClickedButtonListener {
    when (it) {
        0 -> {
            var one = mRestaurantData!!.map { it.restaurantType }.filter { it.equals("VEGETARIAN") }
            Log.i(TAG, "VEG : $one")
            mSwipePlaceHolderView!!.removeAllViews()
            for (data in mRestaurantData!!) {
                if (data.restaurantType.equals("VEGETERIAN")) {
                    mSwipePlaceHolderView!!.addView(SwipeableCardView(context!!, data, mSwipePlaceHolderView!!))
                }
            }
        }
        1 -> {
            mSwipePlaceHolderView!!.removeAllViews()
            for (data in mRestaurantData!!) {
                if (data.restaurantType.equals("NON_VEGETERIAN")) {
                    mSwipePlaceHolderView!!.addView(SwipeableCardView(context!!, data, mSwipePlaceHolderView!!))
                }
            }
        }
        else -> {
            mSwipePlaceHolderView!!.removeAllViews()
            for (data in mRestaurantData!!) {
                if (data.restaurantType.equals("BAR")) {
                    mSwipePlaceHolderView!!.addView(SwipeableCardView(context!!, data, mSwipePlaceHolderView!!))
                }
            }
        }
    }
}*/