package com.oppalab.moca.model

class Comment {
    private var comment: String = ""
    private var publisher: String = ""
    private lateinit var replyData: ArrayList<Reply>
    constructor()

    constructor(comment: String, publisher: String, replyData: ArrayList<Reply>) {
        this.comment = comment
        this.publisher = publisher
        this.replyData = replyData
    }

    fun getComment(): String{
        return comment
    }

    fun getPublisher(): String{
        return publisher
    }

    fun getReplyData(): ArrayList<Reply>{
        return replyData
    }

    fun setComment(comment: String){
        this.comment = comment
    }

    fun setPublisher(publisher: String){
        this.publisher = publisher
    }

    fun setReplyData(replyData: ArrayList<Reply>){
        this.replyData = replyData
    }

}