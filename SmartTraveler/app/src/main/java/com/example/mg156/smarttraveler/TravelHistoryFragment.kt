package com.example.mg156.smarttraveler

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.io.Serializable


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"

class TravelHistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var param3: String? = null
    private var param4: String? = null


    lateinit var plan: planMetaDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            param3 = it.getString(ARG_PARAM3)
            param4 = it.getString(ARG_PARAM4)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val historyView = inflater.inflate(R.layout.fragment_travel_history, container, false)

        val txtPlanName = historyView.findViewById<TextView>(R.id.history_fragment_plan_name)

        val txtPlanStartTime = historyView.findViewById<TextView>(R.id.history_fragment_plan_start_time)

        val txtPlanEndTime = historyView.findViewById<TextView>(R.id.history_fragment_plan_end_time)

        val txtPlanPreference = historyView.findViewById<TextView>(R.id.history_fragment_plan_preference)

        txtPlanName.setText("Plan Name : " + param1)
        txtPlanStartTime.setText("Plan Start Time : " + param2)
        txtPlanEndTime.setText("Plan End Time : " + param3)
        txtPlanPreference.setText("Plan Prefernce : " + param4)

        return historyView
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: String, param4: String) =
                TravelHistoryFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                        putString(ARG_PARAM3, param3)
                        putString(ARG_PARAM4, param4)
                    }
                }
    }
}
