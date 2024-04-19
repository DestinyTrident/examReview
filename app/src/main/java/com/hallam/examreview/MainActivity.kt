package com.hallam.examreview

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.hallam.examreview.databinding.ActivityMainBinding
import com.hallam.examreview.model.scryfallCardModel
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var cardImage:ImageView
    private lateinit var cardName:TextView
    private lateinit var loadButton:Button
    private lateinit var helper:sharedPrefHelper
    private lateinit var jsonConvert:jsonConverter
    private lateinit var randomizeButton:Button
    private var connected = true
    private var cardID = ""
    private var cardJsonString = ""
    override fun onCreate(savedInstanceState: Bundle?) {

        binding= ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        helper= sharedPrefHelper(this)
        jsonConvert = jsonConverter(this)

        cardImage = binding.imgCardImage
        cardName = binding.txtLoadedCardName
        loadButton = binding.btnLoadPrefs
        randomizeButton = binding.btnRandomize
        binding.btnReconnect.setOnClickListener{
            /* Check Connection to for the Internet */
            connected = false
        }
        setOnClicks(connected)
    }

    private fun loadCardInfo(cardModel: scryfallCardModel,randomized:Boolean) {


    }

    private fun fetchStartCard(id:String):Thread{
        return Thread{
            //Loading Card to a model then update UI with the data from model
            val url = URL("https://api.scryfall.com/cards/${id}?format=json&pretty=true")
            val connection = url.openConnection() as HttpURLConnection
            if(connection.responseCode == 200){
                // Success
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem,"UTF-8")
                var jsonStr =""
// inputStreamReader is a json format currently

               val list = inputStreamReader.readLines()
                for(index in list){
                    jsonStr+=index
                    Log.i("Tester",index)
                }
                Log.i("Tester",(jsonStr =="").toString())

                updateCardJsonString(jsonStr,false)

                inputStreamReader.close()
                inputSystem.close()
            }else{
                //Failure
                binding.txtThreadfailure.text = getString(R.string.fail)
            }
        }
    }

    // Running API collecting Thread
    private fun fetchRandomCard():Thread{
        return Thread{
            val url = URL("https://api.scryfall.com/cards/random")
            val connection = url.openConnection() as HttpURLConnection
            if(connection.responseCode == 200){
                // Success
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem,"UTF-8")
                var jsonStr =""

                val list = inputStreamReader.readLines()
                for(index in list){
                    jsonStr+=index
                    Log.i("Tester",index)
                }
                Log.i("Tester",(jsonStr =="").toString())

                updateCardJsonString(jsonStr,true)

                inputStreamReader.close()
                inputSystem.close()

            }else{
                //Failure
                binding.txtThreadfailure.text = getString(R.string.fail)
            }

        }
    }

    private fun updateCardJsonString(json:String,randomized: Boolean){
        runOnUiThread{
            kotlin.run {
                Log.i("Tester",(json !="").toString())
                cardJsonString = json
                Log.i("Tester",(cardJsonString !="").toString())
                if(cardJsonString !="") {

                    val model = jsonConvert.generateCardModel(cardJsonString)
                    Log.i("Tester", model.toString())

                    //cardImage// UNKNOWN IF YOU CAN PUT IMAGE RESOURCE BY URI
                    if(randomized || model.getCardID() == "4a2e428c-dd25-484c-bbc8-2d6ce10ef42c"){
                        helper.setCardId(model.getCardID())
                    }
                    cardName.text = model.getCardName()
                    //CURRENT URI IS NOT LOCAL HAVE TO USE THE GLIDE API
                    // cardImage.setImageURI(model!!.getImageURI())
                    //GLIDE API
                    //Link to the documentation of Glide: https://bumptech.github.io/glide/
                    Glide.with(this).load(model.getImageURI()).into(cardImage)
                    //END OF GLIDE API

                    Log.i("Tester", model.getImageURI().toString())
                }
            }
        }
    }
private fun setOnClicks(bool:Boolean){
    if(bool){
        randomizeButton.setOnClickListener {
            fetchRandomCard().start()
        }
        loadButton.setOnClickListener {

            cardID = helper.getCardId()
            fetchStartCard(cardID).start()
        }
        binding.button.setOnClickListener {
            fetchStartCard("4a2e428c-dd25-484c-bbc8-2d6ce10ef42c").start()
        }
    }else{
        randomizeButton.setOnClickListener {
            notConnected()
        }
        loadButton.setOnClickListener {
            notConnected()
        }
        binding.button.setOnClickListener {
            notConnected()
        }
    }
}
    private fun notConnected(){
        val text = "Please Connect to the Internet if you did click on the xyz button"
        Toast.makeText(this,text,Toast.LENGTH_LONG).show()
        binding.txtThreadfailure.textSize=25F
        binding.txtThreadfailure.text = text
    }

}