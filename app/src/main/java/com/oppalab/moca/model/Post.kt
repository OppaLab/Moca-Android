package com.oppalab.moca.model

class Post {
    private var postId: String = ""
    private var title: String = ""
    private var content: String = ""
    private var publisher: String = ""
    private var thumbnail: String = ""

    constructor()

    constructor(postId: String, title: String, content: String, publisher: String, thumbnail: String) {
        this.postId = postId
        this.title = title
        this.content = content
        this.publisher = publisher
        this.thumbnail = thumbnail
    }

    fun getPostId(): String
    {
        return  postId
    }

    fun setPostId(postId: String)
    {
        this.postId = postId
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