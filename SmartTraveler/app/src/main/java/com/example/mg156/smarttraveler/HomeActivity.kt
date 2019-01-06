package com.example.mg156.smarttraveler

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.example.mg156.smarttraveler.R.id.nav_view
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    private var authListener: FirebaseAuth.AuthStateListener? = null
    private var auth: FirebaseAuth? = null
    private var firedb: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        isLoggedIn()
        //get firebase auth instance
        auth = FirebaseAuth.getInstance()
        firedb = FirebaseDatabase.getInstance()

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)



        val headerView = nav_view.getHeaderView(0)

        val profileFullName = headerView.findViewById<TextView>(R.id.nav_full_name)
        val profileEmail = headerView.findViewById<TextView>(R.id.nav_email_id)
        val profileImage = headerView.findViewById<CircleImageView>(R.id.nav_profile_image)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val profileRef = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        val toggle = object : ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)

            }
        }
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.itemIconTintList = null


        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        profileRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println("loadPost:onCancelled ${p0.toException()}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot != null){
                    profileEmail.text = dataSnapshot.child("user_email").value.toString()
                    profileFullName.text = dataSnapshot.child("full_name").value.toString()
                    Picasso.get().load(dataSnapshot.child("photo_url").value.toString()).fit().into(profileImage)
                }
            }
        })
    }

    private fun isLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid

        if(uid == null){
            val intent = Intent(this,MainActivity::class.java )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.user_profile) {
            startActivity(Intent(this, ProfilePageActivity::class.java))
        }
        if (id == R.id.user_feedback) {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }
        if (id == R.id.feedback_list) {
            startActivity(Intent(this, FeedbackListActivity::class.java))
        }
        if (id == R.id.travel_plan) {
            startActivity(Intent(this, TravelPlanActivity::class.java))
        }
        if (id == R.id.travel_history) {
            startActivity(Intent(this, TravelHistoryActivity::class.java))
        }
        if(id == R.id.log_out){
            signOut()
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    public override fun onStart() {
        super.onStart()
        auth?.addAuthStateListener(authListener!!)
    }

    public override fun onStop() {
        super.onStop()
        if (authListener != null) {
            auth?.removeAuthStateListener(authListener!!)
        }
    }
}
