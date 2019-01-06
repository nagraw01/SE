package com.example.mg156.smarttraveler

import java.lang.reflect.Constructor


class planMetaDetails {
    lateinit var planName: String
    lateinit var planStartTime: String
    lateinit var planEndTime: String
    lateinit var planPreference: String

    constructor()

    constructor(planName: String,planStartTime: String, planEndTime: String, planPreference:String) {
        this.planName = planName
        this.planStartTime = planStartTime
        this.planEndTime = planEndTime
        this.planPreference = planPreference
    }
}