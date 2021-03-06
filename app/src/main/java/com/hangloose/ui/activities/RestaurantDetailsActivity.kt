package com.hangloose.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.hangloose.R
import com.hangloose.ui.model.RestaurantData
import com.hangloose.utils.EXTRA_RESTAURANT_DETAILS_DATA
import kotlinx.android.synthetic.main.activity_restaurant_details.expand_text_view
import kotlinx.android.synthetic.main.activity_restaurant_details.textName
import kotlinx.android.synthetic.main.activity_restaurant_details.textPlace
import kotlinx.android.synthetic.main.activity_restaurant_details.textRatingValue

class RestaurantDetailsActivity : AppCompatActivity() {

    private val TAG = "RestaurantDetails"
    private var restaurantData: RestaurantData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)
        restaurantData = intent.getParcelableExtra<RestaurantData>(EXTRA_RESTAURANT_DETAILS_DATA)
        Log.i(TAG, restaurantData.toString())
        setUpViews()
        val al = ArrayList<String>(4)
        //Added 4 elements
        al.add("Hi")
        al.add("Hello")
        al.add("Bye")
        al.add("GM")
    }

    private fun setUpViews() {
        if (restaurantData != null) {
            textName.text = restaurantData!!.name
            textPlace.text = restaurantData!!.address
            textRatingValue.text = restaurantData!!.ratings
            expand_text_view.text = getString(R.string.about_dummy)
        }
    }
}
