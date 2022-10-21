package com.mosamir.messengerchat.model

data class User (val name:String,
                    val profileImage:String,
                    val uid:String){

    constructor():this("","","")
}
