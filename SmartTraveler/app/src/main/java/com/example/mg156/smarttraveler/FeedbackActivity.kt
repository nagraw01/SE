package com.example.mg156.smarttraveler

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*



class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.title ="User Feedback"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val btn_submit_feedback = findViewById(R.id.send_feedback) as Button

        btn_submit_feedback.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                saveFeedback()
            }
        })


    }

    private fun saveFeedback(){
        val uid = FirebaseAuth.getInstance().uid?: ""
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/feedbacks/$filename")

        val edit_feedback_full_name = findViewById(R.id.feedback_user_name) as EditText
        val edit_feedback_user_email_id = findViewById(R.id.feedback_user_email) as EditText
        val edit_feedback_feedback = findViewById(R.id.feedback_feedback) as EditText

        val fullname = edit_feedback_full_name.text.toString()
        val email = edit_feedback_user_email_id.text.toString()
        val feedback = edit_feedback_feedback.text.toString()

        val today = Date()
        val format = SimpleDateFormat("MM-dd-yyyy hh:mm:ss a")
        val feedback_time = format.format(today)

        if (TextUtils.isEmpty(fullname)) {
            edit_feedback_full_name.setError("Enter your name!")
            edit_feedback_full_name.requestFocus()
            return
        }

        if (TextUtils.isEmpty(email)) {
            edit_feedback_user_email_id.setError("Enter your email id!")
            edit_feedback_user_email_id.requestFocus()
            return
        }

        if(!isValidEmail(email)){
            edit_feedback_user_email_id.setError("Email format is incorrect!")
            edit_feedback_user_email_id.requestFocus()
            return
        }

        if (TextUtils.isEmpty(feedback)) {
            edit_feedback_feedback.setError("Enter Feedback!")
            edit_feedback_feedback.requestFocus()
            return
        }

        val feedbackObj = FeedbackList(feedback_time,email,feedback, uid, fullname)
        ref.setValue(feedbackObj).addOnSuccessListener {
            Toast.makeText(baseContext, "Feedback sent!",
                    Toast.LENGTH_LONG).show()
            val intent = Intent(applicationContext,FeedbackActivity::class.java)
            startActivity(intent)
        }
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
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
}
