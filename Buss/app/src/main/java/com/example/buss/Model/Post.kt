package com.example.buss.Model

class Post {

    private var postid : String = ""
    private var postimage : String = ""
    private var publisher : String = ""
    private var description : String = ""
    private var date : String = ""


constructor()
    constructor(postid: String, postimage: String, publisher: String, description: String, date: String) {
        this.postid = postid
        this.postimage = postimage
        this.publisher = publisher
        this.description = description
        this.date = date

    }

    fun getPostid():String{
        return postid
    }
    fun getPostimage():String{
        return postimage
    }
    fun getPublisher():String{
        return publisher
    }
    fun getDescription():String{
        return description
    }
    fun getDate():String{
        return date
    }


    fun setPostid(postid: String)
    {
        this.postid = postid
    }
    fun setPostimage(postimage: String)
    {
        this.postimage = postimage
    }
    fun setPublisher(publisher: String)
    {
        this.publisher = publisher
    }
    fun setDescription(description: String)
    {
        this.description = description
    }
    fun setDate(date: String){
        this.date = date
    }



}