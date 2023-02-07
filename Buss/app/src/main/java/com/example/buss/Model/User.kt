package com.example.buss.Model

class User {
    private var name: String = ""
        private var bio: String = ""
    private var image: String = ""
        private var uid: String = ""

    constructor()

    constructor(name: String, bio: String, image: String, uid: String)
    {
        this.name = name
        this.bio = bio
        this.image = image
        this.uid = uid
    }

    fun getName(): String
    {
        return name
    }
    fun setName(name: String)
    {
    this.name = name
        }

    fun getBio(): String
    {
        return bio
    }
    fun setBio(bio: String)
    {
        this.bio = bio
    }

    fun getImage(): String
    {
        return image
    }
    fun setImage(image: String)
    {
        this.image = image
    }

    fun getUID(): String
    {
        return uid
    }
    fun setUID(uid: String)
    {
        this.uid = uid
    }
}