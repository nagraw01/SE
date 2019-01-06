package com.example.mg156.smarttraveler


class Users {

    lateinit var full_name: String
    lateinit var photo_url: String
    lateinit var user_email: String
    lateinit var user_id: String
    lateinit var type: String

    constructor()


    constructor(full_name: String, photo_url: String, user_email: String, user_id: String, type : String) {
        this.full_name = full_name
        this.photo_url = photo_url
        this.user_email = user_email
        this.user_id = user_id
        this.type = type
    }
}