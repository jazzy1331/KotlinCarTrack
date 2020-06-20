package com.bawa.kotlincartrack

import android.graphics.ColorSpace
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    var makes:ArrayList<String> = ArrayList()
    var models:ArrayList<String> = ArrayList()
    var cars:ArrayList<Car> = ArrayList()
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        runMakes("https://vpic.nhtsa.dot.gov/api/vehicles/getmakesforvehicletype/car?format=json")
        
        //runModels("https://vpic.nhtsa.dot.gov/api/vehicles/getmodelsformmake/MAKE_NAME?format=json")
    }

    fun addCar(view: View) {}

    fun runMakes(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val response = response.body()!!.string()
                var json_contact: JSONObject = JSONObject(response)
                var results: JSONArray = json_contact.getJSONArray("Results")
                var size: Int = results.length()
                makes = ArrayList();
                for (i in 0..size - 1) {
                    var json_objectdetail: JSONObject = results.getJSONObject(i)
                    makes.add(json_objectdetail.get("MakeName").toString().trim())
                }
                makes.sort()
                //Log.v("RESPONSE RESULTS", makes.toString())
            }
        })
    }
    fun runModels(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val response = response.body()!!.string()
                var json_contact: JSONObject = JSONObject(response)
                var results: JSONArray = json_contact.getJSONArray("Results")
                var size: Int = results.length()
                models = ArrayList();
                for (i in 0..size - 1) {
                    var json_objectdetail: JSONObject = results.getJSONObject(i)
                    models.add(json_objectdetail.get("Model_Name").toString().trim())
                }
                models.sort()
                //Log.v("RESPONSE RESULTS", makes.toString())
            }
        })
    }
}