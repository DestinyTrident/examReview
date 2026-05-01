package com.hallam.examreview

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.hallam.examreview.databinding.ActivityMainBinding
import com.hallam.examreview.model.viewModel
import kotlinx.coroutines.launch
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Creation of the  ViewModel
        val viewModel= ViewModelProvider(this)[viewModel::class.java]
        //Observing any changes to the viewModel
        viewModel.connectionStatus.observe(this){
            x-> connected =x; setOnClicks(x)
            Toast.makeText(applicationContext,"Connection State $x",Toast.LENGTH_SHORT).show()
            //binding.txtThreadfailure.text="Connection State $x"
        }

        helper = sharedPrefHelper(applicationContext)
        jsonConvert = jsonConverter(applicationContext)
        cardImage = binding.imgCardImage
        cardName = binding.txtLoadedCardName
        loadButton = binding.btnLoadPrefs
        randomizeButton = binding.btnRandomize
        lifecycleScope.launch { viewModel.testConnection(applicationContext) }

        if(connected){
            fetchCard("4a2e428c-dd25-484c-bbc8-2d6ce10ef42c").start()
        }

        binding.btnReconnect.setOnClickListener {
            lifecycleScope.launch { viewModel.testConnection(applicationContext) }
            Toast.makeText(applicationContext,"Testing Connection",Toast.LENGTH_SHORT).show()
        }
    }
//Uses the Scryfall API to fetch a Magic Card from the inputted cardID
// like 4a2e428c-dd25-484c-bbc8-2d6ce10ef42c, and it will retrieve a Black Lotus card
    private fun fetchCard(card:String):Thread{
        return Thread{
            try{
                val url = URL("https://api.scryfall.com/cards/${card}?format=json&pretty=true")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod="GET"
                connection.setRequestProperty("UserAgent","ExamReviewApp/1.0")//App Identification
                connection.setRequestProperty("Accept","application/json")

                if(connection.responseCode==200){
                    val jsonString=connection.inputStream.bufferedReader().use{it.readText()}
                    updateCardJsonString(jsonString,false)
                } else {
                    runOnUiThread{
                        binding.txtThreadfailure.text=getString(R.string.connectionFailure)
                    }
                }
                connection.disconnect()
            }catch(e: Exception){
                Log.e("Tester","Connection Failed",e)
            }
        }
    }

//Uses the Scryfall API to fetch a random Magic Card
    private fun fetchRandomCard():Thread {
       return Thread{
            try{
           val url = URL("https://api.scryfall.com/cards/random?format=json&pretty=true")
           val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod="GET"
                connection.setRequestProperty("UserAgent","ExamReviewApp/1.0")//App Identification
                connection.setRequestProperty("Accept","application/json")

           //Log.i("Tester","Has Internet Connection: $connected")
           //Log.i("Tester","responseCode of Connection: ${connection.responseCode}")
            if(connection.responseCode==200){
                val jsonString=connection.inputStream.bufferedReader().use{it.readText()}
                updateCardJsonString(jsonString,true)
            } else {
                runOnUiThread{
                    binding.txtThreadfailure.text=getString(R.string.connectionFailure)
                }
            }
                connection.disconnect()
            }catch(e: Exception){
                Log.e("Tester","Connection Failed",e)
            }
       }
    }
//This Function is to update the JSON inbound by the function
    private fun updateCardJsonString(json:String,randomized: Boolean){
        runOnUiThread{
            kotlin.run {
                cardJsonString = json
                if(cardJsonString !="") {

                    val model = jsonConvert.generateCardModel(cardJsonString)

                    //cardImage// UNKNOWN IF YOU CAN PUT IMAGE RESOURCE BY URI
                    if(randomized){
                        helper.setCardId(model.getCardID())
                    }
                    cardName.text = model.getCardName()
                    //CURRENT URI IS NOT LOCAL HAVE TO USE THE GLIDE API
                    // cardImage.setImageURI(model!!.getImageURI())
                    //GLIDE API
                    //Link to the documentation of Glide: https://bumptech.github.io/glide/
                    Glide.with(this).load(model.getImageURI()).into(cardImage)
                    //END OF GLIDE API
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
            fetchCard(cardID).start()
        }
    }else{
        randomizeButton.setOnClickListener {
            notConnected()
        }
        loadButton.setOnClickListener {
            notConnected()
        }
    }
}
    private fun notConnected()
    {
        val text = "Please Connect to the Internet if you did click on the Reconnect button"
        Toast.makeText(this,text,Toast.LENGTH_LONG).show()
        binding.txtThreadfailure.textSize=25F
        binding.txtThreadfailure.text = text
    }
}