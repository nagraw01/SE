package com.example.mg156.smarttraveler

import android.graphics.Color
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList

class VenuesOnMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var planName: String = ""
    var planStartTime: String = ""
    var planEndTime: String = ""
    var planPreference: String = ""
    var totalTime: String = "2"
    var lat: String = ""
    var lon: String = ""
    var venueDetailsRouteOne = ArrayList<VenueDetailsClass>()
    var venueDetailsRouteTwo = ArrayList<VenueDetailsClass>()
    var venueDetailsRouteThree = ArrayList<VenueDetailsClass>()
    var fourSquareUrl = "https://api.foursquare.com/v2/venues/explore?client_id=HGBEEF2DXMBJ0FYV4THS0N5Q0Q0ASNC2FBT0ELZB5FDUGXMS&client_secret=Q30RTCD3OKAQP5YQ3WLF43DTT0UZ4JSEQ4VLBFX3LAZLJDCS&v=20181124"
    var selectedPlan = 1

    lateinit var btnRoute1:Button
    lateinit var btnRoute2:Button
    lateinit var btnRoute3:Button
    lateinit var btnToSavePlan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venues_on_map)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Travel Plan"

        btnRoute1 = findViewById(R.id.btn_route1)
        btnRoute2 = findViewById(R.id.btn_route2)
        btnRoute3 = findViewById(R.id.btn_route3)
        btnToSavePlan = findViewById(R.id.btnSubmitToSavePlan)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun centerMapOnLocation(venueList: ArrayList<VenueDetailsClass>) {
        val userLocation = LatLng(lat.toDouble(), lon.toDouble())
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))
        mMap.clear()
        val polyLine = PolylineOptions().add(LatLng(lat.toDouble(), lon.toDouble()))
        val currentMarker = mMap.addMarker(MarkerOptions().position(userLocation).title("Current Location").snippet("This is your current Location"))
        for (data in venueList) {
            val markerLocation = LatLng(data.lat, data.lng)
            polyLine.add(markerLocation)
            var snippet = "Address: "
            for (snippetData in data.formattedAddress) {
                snippet = snippet + snippetData + "\n"
            }
            val marker = mMap.addMarker(MarkerOptions().position(markerLocation).title(data.venueName).snippet(snippet))
            //marker.tag = data.venueId

        }
        polyLine.width(5f).color(Color.RED)

        mMap.addPolyline(polyLine)
        currentMarker.showInfoWindow()

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14f))
    }

    fun onFirstRoute(view: View) {
        btnRoute2.alpha = 0.5f
        btnRoute3.alpha = 0.5f
        btnRoute1.alpha = 1f
        selectedPlan = 1
        centerMapOnLocation(venueDetailsRouteOne)
    }

    fun onSecondRoute(view: View) {
        btnRoute1.alpha = 0.5f
        btnRoute3.alpha = 0.5f
        btnRoute2.alpha = 1f
        selectedPlan = 2
        centerMapOnLocation(venueDetailsRouteTwo)
    }

    fun onThirdRoute(view: View) {
        btnRoute2.alpha = 0.5f
        btnRoute1.alpha = 0.5f
        btnRoute3.alpha = 1f
        selectedPlan = 3
        centerMapOnLocation(venueDetailsRouteThree)
    }

    fun onSavePlan(view: View) {
        if(selectedPlan == 1){
            saveToFirebase(venueDetailsRouteOne)
        }
        else if(selectedPlan == 2){
            saveToFirebase(venueDetailsRouteTwo)
        }
        else{
            saveToFirebase(venueDetailsRouteThree)
        }

    }

    fun saveToFirebase(venueList: ArrayList<VenueDetailsClass>){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/venues/$uid/$planName")

        ref.setValue(venueList).addOnSuccessListener {
            Toast.makeText(baseContext, "Plan Details Added!",
                    Toast.LENGTH_LONG).show()
            saveMetaData()
        }
                .addOnFailureListener {
                    Toast.makeText(baseContext, "Error while saving plan!",
                            Toast.LENGTH_LONG).show()
                }
    }

    fun saveMetaData(){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/venues/$uid/$planName/planDetails")
        val planMetaData = planMetaDetails(planName,planStartTime,planEndTime,planPreference)

        ref.setValue(planMetaData).addOnSuccessListener {
            Toast.makeText(baseContext, "Meta Data Added!",
                    Toast.LENGTH_LONG).show()

        }
                .addOnFailureListener {
                    Toast.makeText(baseContext, "Error while saving plan!",
                            Toast.LENGTH_LONG).show()
                }
    }

    inner class getRecommendations() : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String? {
            val result = MyUtility.downloadJSONusingHTTPGetRequest(params[0]!!)
            return result!!
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val recommendationData = Gson().fromJson(result, Recommendation::class.java)
            val dataFromAPI = recommendationData.response?.groups?.get(0)?.items
            val userId = FirebaseAuth.getInstance().uid ?: ""
            var index = 0
            var venuesPerRoute = totalTime.toInt() / 2 as Int
            for (data in dataFromAPI!!) {
                if ((venuesPerRoute * 1) > index) {
                    val venueDetails = VenueDetailsClass(userId, planName, data!!.venue!!.id!!, data.venue!!.name!!, data.venue!!.location!!.lat!!, data.venue!!.location!!.lng!!, data.venue!!.location!!.formattedAddress!!)
                    venueDetailsRouteOne.add(venueDetails)
                } else if ((venuesPerRoute * 2) > index) {
                    val venueDetails = VenueDetailsClass(userId, planName, data!!.venue!!.id!!, data.venue!!.name!!, data.venue!!.location!!.lat!!, data.venue!!.location!!.lng!!, data.venue!!.location!!.formattedAddress!!)
                    venueDetailsRouteTwo.add(venueDetails)
                } else if ((venuesPerRoute * 3) > index) {
                    val venueDetails = VenueDetailsClass(userId, planName, data!!.venue!!.id!!, data.venue!!.name!!, data.venue!!.location!!.lat!!, data.venue!!.location!!.lng!!, data.venue!!.location!!.formattedAddress!!)
                    venueDetailsRouteThree.add(venueDetails)
                }
                index++
            }
            btnRoute2.alpha = 0.5f
            btnRoute3.alpha = 0.5f
            btnRoute1.alpha = 1f
            selectedPlan = 1
            centerMapOnLocation(venueDetailsRouteOne)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.getUiSettings().setZoomControlsEnabled(true);

        val intent = intent
        planName = intent.getStringExtra("plan_name")
        planStartTime = intent.getStringExtra("plan_start_time")
        planEndTime = intent.getStringExtra("plan_end_time")
        planPreference = intent.getStringExtra("plan_preference")
        totalTime = intent.getStringExtra("total_time")
        lat = intent.getStringExtra("latitude")
        lon = intent.getStringExtra("longitude")

        if (TextUtils.isEmpty(planPreference)) {
            fourSquareUrl = fourSquareUrl + "&ll=$lat" + ",$lon" + "&section=sights"
        } else {
            fourSquareUrl = fourSquareUrl + "&ll=$lat" + ",$lon" + "&query=$planPreference"
        }

        val backTask = getRecommendations()
        backTask.execute(fourSquareUrl)
    }
}
