package com.hallam.examreview

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.ContextCompat.getString
import androidx.core.content.edit

class sharedPrefHelper(private val ctx:Context) {
    private var context = ctx
    private val defaultID = "4a2e428c-dd25-484c-bbc8-2d6ce10ef42c" // Black Lotus - Second Edition Version
    private val cardKey = getString(context,R.string.cardKey)
    private  val sharedPref = context.getSharedPreferences(cardKey,MODE_PRIVATE)
    private var cardString:String

    init{
        cardString = sharedPref.getString(cardKey,defaultID).toString()
    }

    fun getCardId():String{
        return cardString
    }

    fun setCardId(newID:String){
        cardString = newID
        sharedPref.edit() {
            putString(cardKey, newID)
        }
    }

}

