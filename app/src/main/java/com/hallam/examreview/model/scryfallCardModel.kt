package com.hallam.examreview.model

import android.net.Uri




class scryfallCardModel(cardName:String, cardID:String, oracleText:String, imageUri: Uri) {
    private var CardName = cardName
    private var CardID = cardID
    private var oracle_text = oracleText
    private var image = imageUri

    override fun toString(): String {
        val str = " Name = ${CardName}\n ID = ${CardID}\n oracle_text = ${oracle_text}\n imageUri = ${image}"
        return str
    }

    fun getCardID():String{return CardID}
    fun getCardName():String{return CardName}
    fun getOracleText():String{return oracle_text}
    fun getImageURI():Uri {return image}
}