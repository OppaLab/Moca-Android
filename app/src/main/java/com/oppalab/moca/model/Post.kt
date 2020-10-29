package com.oppalab.moca.model

class Post {
    private var postid: String = ""
    private var title: String = ""
    private var content: String = ""
    private var publisher: String = ""
    private var thumbnail: String = ""

    constructor()

    constructor(postid: String, title: String, content: String, publisher: String, thumbnail: String) {
        this.postid = postid
        this.title = title
        this.content = content
        this.publisher = publisher
        this.thumbnail = thumbnail
    }

    fun getPostid(): String
    {
        return  postid
    }

    fun setPostid(postid: String)
    {
        this.postid = postid
    }

    fun getTitle(): String
    {
        return  title
    }

    fun setTitle(title: String)
    {
        this.title = title
    }

    fun getContent(): String
    {
        return  content
    }

    fun setContent(content: String)
    {
        this.content = content
    }

    fun getPublisher(): String
    {
        return  publisher
    }

    fun setPublisher(publisher: String)
    {
        this.publisher = publisher
    }

    fun getThumbnail(): String
    {
        return  thumbnail
    }

    fun setUsername(username: String)
    {
        this.thumbnail = thumbnail
    }

}