package com.example.mg156.smarttraveler

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_travel_history.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class TravelHistoryActivity : AppCompatActivity() {

    var planDetailsList = ArrayList<planMetaDetails>()
    var planTitleList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_history)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.title ="Travel History"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setVenueDetails()
    }

    fun setVenueDetails() {
        val uid = FirebaseAuth.getInstance().uid
        val profileRef = FirebaseDatabase.getInstance().reference.child("venues/$uid")
        profileRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for(data in dataSnapshot.children){
                        planTitleList.add(data.key!!)
                        for(details in data.children){
                            if(details.key == "planDetails"){
                                planDetailsList.add(details.getValue(planMetaDetails::class.java)!!)
                            }
                        }
                    }
                }
                activity_view_pager.adapter = VenuePagerAdapter(supportFragmentManager)
                activity_view_pager.currentItem = 1

                activity_view_pager.setPageTransformer (false , MyPageTransformer())

                tabs.setupWithViewPager(activity_view_pager)
            }
            override fun onCancelled(error: DatabaseError) {
                println("loadPost:onCancelled ${error.toException()}")
                activity_view_pager.adapter = VenuePagerAdapter(supportFragmentManager)
                activity_view_pager.currentItem = 1

                activity_view_pager.setPageTransformer (false , MyPageTransformer())

                tabs.setupWithViewPager(activity_view_pager)
            }
        })
    }


    private class MyPageTransformer : ViewPager.PageTransformer {
        private val MIN_SCALE = 0.85F
        private val MIN_ALPHA = 0.5F

        override fun transformPage (p0: View, p1: Float ) {
            val pageW = p0.width
            val pageH = p0.height

            if (p1 < -1) { // way off - screen to the left !
                p0.alpha = 0f
            }
            else if (p1 <= 0) { // [-1 0]
                val scaleFactor = Math . max ( MIN_SCALE , 1
                        -Math . abs (p1))
                val verMargin = pageH * (1- scaleFactor )/2
                val horMargin = pageW * (1- scaleFactor )/2

                if (p1 < 0)
                    p0.translationX = horMargin - verMargin /2
                else
                    p0.translationX = verMargin /2 - horMargin

                p0.scaleX = scaleFactor
                p0.scaleY = scaleFactor
                p0. alpha = MIN_ALPHA + ( scaleFactor - MIN_SCALE ) /(1
                        - MIN_SCALE ) *(1 - MIN_ALPHA )
            }
            else { // (1, + infinity
                p0.alpha = 0F
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id == R.id.home){
            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
        }

        if (id == R.id.log_out) {
            signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


    inner class VenuePagerAdapter ( fragmentManager : FragmentManager) : FragmentStatePagerAdapter( fragmentManager ) {

        override fun getItem (position: Int ): Fragment {
            return TravelHistoryFragment.newInstance(planDetailsList[position].planName,planDetailsList[position].planStartTime
            ,planDetailsList[position].planEndTime,planDetailsList[position].planPreference)
        }
        override fun getCount (): Int {
            return planTitleList.size
        }
        override fun getPageTitle ( position : Int ): CharSequence ? {
            val l = Locale.getDefault()
            val title = planTitleList[position] as String
            return title.toUpperCase(l)
        }
    }
}
