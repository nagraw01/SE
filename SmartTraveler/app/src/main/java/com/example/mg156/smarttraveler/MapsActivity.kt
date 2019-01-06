package com.example.mg156.smarttraveler

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.location.*
import android.os.AsyncTask
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.widget.Toast
import android.view.View
import android.widget.EditText
import com.google.gson.Gson
import java.io.IOException
import android.content.Intent
import java.io.DataOutput


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var planName: String = ""
    var planStartTime: String = ""
    var planEndTime: String = ""
    var planPreference: String = ""
    var totalTime: String = "2"
    var lat: String = ""
    var lon: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Select Location"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun onCreatePlan(view: View){
        if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lon)) {
            Toast.makeText(applicationContext,"Please select location first!",Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(applicationContext, VenuesOnMapActivity::class.java)
        intent.putExtra("plan_name", planName)
        intent.putExtra("plan_start_time", planStartTime)
        intent.putExtra("plan_end_time", planEndTime)
        intent.putExtra("plan_preference", planPreference)
        intent.putExtra("total_time",totalTime)
        intent.putExtra("latitude",lat)
        intent.putExtra("longitude",lon)
        startActivity(intent)
    }

    fun onSearch(view: View) {
        val location_tf = findViewById(R.id.txtMapSearch) as EditText
        val location = location_tf.text.toString()

        if (TextUtils.isEmpty(location)) {
            location_tf.setError("Please enter address!")
            location_tf.requestFocus()
            return
        }

        var addressList: List<Address>? = null
        if (location != null || location != "") {
            val geocoder = Geocoder(this)
            try {
                addressList = geocoder.getFromLocationName(location, 1)

            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                val address = addressList!![0]
                val temp = Location(LocationManager.GPS_PROVIDER)
                lat = address.latitude.toString()
                lon = address.longitude.toString()
                temp.setLatitude(address.getLatitude())
                temp.setLongitude(address.getLongitude())
                centerMapOnLocation(temp, location)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Can't find the address", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun centerMapOnLocation(location: Location?, title: String) {
        if (location != null) {
            val userLocation = LatLng(location.latitude, location.longitude)
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(userLocation).title(title))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val intent = intent
        planName = intent.getStringExtra("plan_name")
        planStartTime = intent.getStringExtra("plan_start_time")
        planEndTime = intent.getStringExtra("plan_end_time")
        planPreference = intent.getStringExtra("plan_preference")
        totalTime = intent.getStringExtra("total_time")

    }
}
