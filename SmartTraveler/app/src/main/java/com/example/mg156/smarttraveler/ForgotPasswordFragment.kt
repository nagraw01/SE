package com.example.mg156.smarttraveler

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ForgotPasswordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListenerForgotPassword? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var forgotPasswordView = inflater.inflate(R.layout.fragment_forgot_password, container, false)

        val btnBack = forgotPasswordView.findViewById(R.id.forgot_password_page_btn_back) as Button
        val btnResetPassword = forgotPasswordView.findViewById(R.id.forgot_password_page_btn_reset_password) as Button

        btnBack.setOnClickListener(View.OnClickListener { v -> listener?.onFragmentInteractionForgotPassword(v) })
        btnResetPassword.setOnClickListener(View.OnClickListener { v -> listener?.onFragmentInteractionForgotPassword(v) })

        return forgotPasswordView
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListenerForgotPassword) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListenerForgotPassword {
        // TODO: Update argument type and name
        fun onFragmentInteractionForgotPassword(view: View)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ForgotPasswordFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
