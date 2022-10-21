package com.mosamir.messengerchat.model

data class Friends (var image:String,
                    var name:String,
                    var lastMessage:String,
                    var time:String){

    constructor():this("","","","")
}