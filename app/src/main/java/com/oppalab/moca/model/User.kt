package com.oppalab.moca.model

class User
{
    private var username: String = ""
    private var email: String = ""
    private var category: String = ""
    private var image: String = ""
    private var uid: String = ""


    constructor()


    constructor(username: String, email: String, category: String, image: String, uid: String)
    {
        this.username = username
        this.email = email
        this.category = category
        this.image = image
        this.uid = uid
    }


    fun getUsername(): String
    {
        return  username
    }

    fun setUsername(username: String)
    {
        this.username = username
    }

    fun setEmail(email : String)
    {
        this.email= email
    }

    fun getEmail(): String
    {
        return  email
    }

    fun getCategory(): String
    {
        return category
    }

    fun setCategory(category: String)
    {
        this.category = category
    }


    fun getImage(): String
    {
        return  image
    }

    fun setImage(image: String)
    {
        this.image = image
    }


    fun getUID(): String
    {
        return  uid
    }

    fun setUID(uid: String)
    {
        this.uid = uid
    }
}