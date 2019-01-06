package com.example.mg156.smarttraveler

class FeedbackList {
    lateinit var feedback_time: String
    lateinit var user_email: String
    lateinit var user_feedback: String
    lateinit var user_id: String
    lateinit var user_name: String

    constructor()

    constructor(feedbackTime: String, user_email: String, user_feedback: String, user_id: String, user_name : String) {
        this.feedback_time = feedbackTime
        this.user_email = user_email
        this.user_feedback = user_feedback
        this.user_id = user_id
        this.user_name = user_name
    }
}
