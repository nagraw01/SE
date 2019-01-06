package com.example.mg156.smarttraveler

import android.app.TimePickerDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.*
import java.util.*
import java.text.ParseException
import java.text.SimpleDateFormat


class TravelPlanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_plan)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Travel Plan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        var getStartTime = findViewById(R.id.get_start_time) as ImageView
        var getEndTime = findViewById(R.id.get_end_time) as ImageView

        var setStartTime = findViewById(R.id.txtStartTime) as EditText
        var setEndTime = findViewById(R.id.txtEndTime) as EditText

        val btn_create_plan = findViewById<Button>(R.id.create_plan)

        getStartTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                setStartTime.setText(SimpleDateFormat("HH:mm").format(cal.time)).toString()
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        getEndTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                setEndTime.setText(SimpleDateFormat("HH:mm").format(cal.time)).toString()
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        btn_create_plan.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                createPlan()
            }
        })
    }

    private fun createPlan() {
        var plan_name = findViewById(R.id.txtPlanName) as EditText
        var start_time = findViewById(R.id.txtStartTime) as EditText
        var end_time = findViewById(R.id.txtEndTime) as EditText
        var plan_preference = findViewById<EditText>(R.id.txtPreference)

        var planName = plan_name.text.toString()
        var startTime = start_time.text.toString()
        var endTime = end_time.text.toString()
        var preference = plan_preference.text.toString()


        if (TextUtils.isEmpty(planName)) {
            plan_name.setError("Enter Plan Name!")
            plan_name.requestFocus()
            return
        }

        if (TextUtils.isEmpty(startTime)) {
            start_time.setError("Select Start Time!")
            start_time.requestFocus()
            return
        }

        if (TextUtils.isEmpty(endTime)) {
            end_time.setError("Select End Time!")
            end_time.requestFocus()
            return
        }

        val timeFormat = SimpleDateFormat("HH:mm")
        var hours: Long = 2
        try {
            val parsedStartTime = timeFormat.parse(startTime)
            val parsedEndTime = timeFormat.parse(endTime)
            val difference = parsedEndTime.getTime() - parsedStartTime.getTime()
            var days = (difference / (1000 * 60 * 60 * 24)).toInt()
            hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)) as Long
            var min = (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) as Long / (1000 * 60)
            if (hours < 0) {
                hours = hours + 24
                if (hours < 2) {
                    end_time.setError("Time difference should be minimum of 2 hours")
                    end_time.requestFocus()
                    return
                }
            }
            if (hours < 2 && hours >= 0) {
                end_time.setError("Time difference should be minimum of 2 hours")
                end_time.requestFocus()
                return
            }
        } catch (e: ParseException) {
            Toast.makeText(applicationContext, "Error While Parsing Time", Toast.LENGTH_LONG).show()
        }


        val intent = Intent(applicationContext, MapsActivity::class.java)
        intent.putExtra("plan_name", planName)
        intent.putExtra("plan_start_time", startTime)
        intent.putExtra("plan_end_time", endTime)
        intent.putExtra("plan_preference", preference)
        intent.putExtra("total_time",hours.toString())
        startActivity(intent)
    }


}

