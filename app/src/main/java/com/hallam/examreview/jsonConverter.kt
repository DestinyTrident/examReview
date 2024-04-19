package com.hallam.examreview

import android.content.Context
import android.net.Uri
import android.util.Log
import com.hallam.examreview.model.scryfallCardModel
import org.json.JSONObject
import java.net.URL

class jsonConverter(ctx:Context) {
    private val context = ctx
    private lateinit var model:scryfallCardModel

    fun generateCardModel(jsonString:String):scryfallCardModel{
        val jsonConverter = JSONObject(jsonString)
        val name = jsonConverter.getString("name")
        val cardID = jsonConverter.getString("id")
        val oracleText = jsonConverter.getString("oracle_text")
        val jsonStrImages=jsonConverter.getString("image_uris")
        val imageJsonObj = JSONObject(jsonStrImages)
        val imgURL = Uri.parse(imageJsonObj.getString("png"))
        model = scryfallCardModel(name,cardID,oracleText,imgURL)

        return model
    }
}