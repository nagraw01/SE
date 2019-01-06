package com.example.mg156.smarttraveler

import java.lang.reflect.Constructor


class VenueDetailsClass {
    lateinit var userId: String
    lateinit var planName: String
    lateinit var venueId: String
    lateinit var venueName: String
    var lat: Double = 0.0
    var lng: Double = 0.0
    lateinit var formattedAddress: List<String?>

    constructor()

    constructor(userId: String, planName: String,venueId: String, venueName: String, lat: Double, lng: Double, formattedAddress: List<String?>) {
        this.userId = userId
        this.planName = planName
        this.venueId = venueId
        this.venueName = venueName
        this.lat = lat
        this.lng = lng
        this.formattedAddress = formattedAddress
    }
}