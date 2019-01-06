package com.example.mg156.smarttraveler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class FeedbackListAdapter(list:ArrayList<FeedbackList>) : RecyclerView.Adapter<FeedbackListAdapter.FeedbackViewHolder>() {

    private var dataList: ArrayList<FeedbackList>? = null
    var lastPosition = -1
    init {
        this.dataList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val v: View
        v = LayoutInflater.from(parent.context).inflate(R.layout.feedback_item, parent, false)
        return FeedbackViewHolder(v)
    }

    override fun getItemCount () : Int {
        return dataList!!.size
    }

    override fun onBindViewHolder ( holder : FeedbackViewHolder , position : Int ) {
        val feedback = dataList!![position]
        holder.feedbackUserName.text = feedback.user_name
        holder.feedBackUserEmail.text = feedback.user_email
        holder.feedbackUserFeedback.text = feedback.user_feedback
        holder.feedbackTime.text = feedback.feedback_time
        setProfileDetails(feedback.user_id,holder.feedbackUserImage)

        setAnimation(holder.itemView , position )

    }

    private fun setProfileDetails(userId: String,imageHolder: ImageView) {
        val imageRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)

        imageRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    Picasso.get().load(dataSnapshot.child("photo_url").value.toString()).fit().into(imageHolder)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("loadPost:onCancelled ${error.toException()}")
            }
        })
    }

    fun setAnimation ( view : View , position : Int ) {
        if ( position != lastPosition ) {
            var animation =  AnimationUtils.loadAnimation( view.context, android.R.anim.slide_in_left);
            animation.setDuration(1000);
            view.startAnimation(animation) ;
            lastPosition = position
        }
    }

    inner class FeedbackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val feedBackUserEmail = view.findViewById<TextView>(R.id.feedback_item_user_email)
        val feedbackUserFeedback = view.findViewById<TextView>(R.id.feedback_item_user_feedback)
        val feedbackUserName = view.findViewById<TextView>(R.id.feedback_item_user_name)
        val feedbackTime = view.findViewById<TextView>(R.id.feedback_item_time)
        val feedbackUserImage = view.findViewById<ImageView>(R.id.feedback_item_image)

    }
}