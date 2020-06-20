package com.bawa.kotlincartrack

import android.content.Context
import android.graphics.Color
import android.icu.util.ULocale
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    var makes:ArrayList<String> = ArrayList()
    var models:ArrayList<String> = ArrayList()
    var years:ArrayList<Int> = ArrayList()
    var cars:ArrayList<Car> = ArrayList()
    var selectedMake:String = ""
    var selectedModel:String = ""
    var selectedYear:Int = 0
    var context:Context = this
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for(i in 2022 downTo 1995){
            years.add(i)
        }
        val spinner: Spinner = findViewById(R.id.year_spinner)
        val arrayAdapter: ArrayAdapter<Int> =
            ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item, years)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(arrayAdapter)

    }

    fun addCar(view: View) {
        var newCar:Car = Car(selectedYear, selectedMake, selectedModel)
        selectedModel = findViewById<Spinner>(R.id.model_spinner).selectedItem.toString()
        Log.v("CHECK", selectedModel)

        var lLayout:LinearLayout = findViewById(R.id.dynamicLayout)
        var newCarText:TextView = TextView(this)
        newCarText.setText("Tracking a ${newCar.carYear} ${newCar.carMake} ${newCar.carModel}")
        newCarText.setTextColor(Color.BLACK)
        newCarText.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10F)

        lLayout.addView(newCarText)

        cars.add(newCar)
        findViewById<Spinner>(R.id.model_spinner).adapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item)
        findViewById<Spinner>(R.id.make_spinner).adapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item)
    }

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

                runOnUiThread {
                    val spinner: Spinner = findViewById(R.id.make_spinner)
                    val arrayAdapter: ArrayAdapter<String> =
                        ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, makes)
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.setAdapter(arrayAdapter)
                }

                Log.v("RESULT MAKES", makes.toString())

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
                Log.v("RESPONSE RESULTS", models.toString())

                runOnUiThread {
                    val spinner: Spinner = findViewById(R.id.model_spinner)
                    val arrayAdapter: ArrayAdapter<String> =
                        ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, models)
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.setAdapter(arrayAdapter)
                }

            }
        })
    }

    fun goMake(view: View) {
        selectedYear = findViewById<Spinner>(R.id.year_spinner).selectedItem.toString().toInt()
        Log.v("CHECK", selectedYear.toString())
        runMakes("https://vpic.nhtsa.dot.gov/api/vehicles/getmakesforvehicletype/car?format=json")

    }
    fun goModel(view: View) {
        selectedMake = findViewById<Spinner>(R.id.make_spinner).selectedItem.toString()
        Log.v("CHECK", selectedMake)
        runModels("https://vpic.nhtsa.dot.gov/api/vehicles/GetModelsForMakeYear/make/${selectedMake}/modelyear/${selectedYear}?format=json")
    }
}