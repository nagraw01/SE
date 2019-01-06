package com.example.mg156.smarttraveler

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener




class FeedbackListActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var feedbackAdapter: FeedbackListAdapter
    private val list = ArrayList<FeedbackList>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_list)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Feedback List"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerViewFeedbackId)

        feedbackAdapter = FeedbackListAdapter(list)
        recyclerView.setAdapter(feedbackAdapter)
        recyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setLayoutManager(mLayoutManager)


        getFeedbackData()
    }

    private fun getFeedbackData() {
        val uid = FirebaseAuth.getInstance().uid
        val userTypeRef = FirebaseDatabase.getInstance().reference.child("users/$uid")
        var user: Users? = null


        userTypeRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    user = dataSnapshot.getValue(Users::class.java)!!
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("loadPost:onCancelled ${error.toException()}")
            }
        })


        val feedbackRef = FirebaseDatabase.getInstance().reference.child("feedbacks")

        feedbackRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for (data in dataSnapshot.getChildren()) {
                        if(user!!.type == "user"){
                            var feedback:FeedbackList
                            feedback = data.getValue(FeedbackList::class.java)!!
                            if(feedback.user_id == uid){
                                list.add(feedback)
                            }
                        }
                        else{
                            list.add(data.getValue(FeedbackList::class.java)!!)
                        }
                    }
                    feedbackAdapter.notifyDataSetChanged();
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("loadPost:onCancelled ${error.toException()}")
            }
        })
    }
}
