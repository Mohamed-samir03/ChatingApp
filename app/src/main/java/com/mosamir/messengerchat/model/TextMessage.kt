package com.mosamir.messengerchat.model

import java.util.*

data class TextMessage(val text:String,var senderId:String,val receiverId:String,var date:Date) {

    constructor():this("","","",Calendar.getInstance().time)

}