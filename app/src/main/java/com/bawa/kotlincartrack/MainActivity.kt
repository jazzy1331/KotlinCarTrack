package com.bawa.kotlincartrack

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    // Properties for the rest of the activity functions
    var makes: ArrayList<String> = ArrayList()
    var models: ArrayList<String> = ArrayList()
    var years: ArrayList<Int> = ArrayList()
    var cars: ArrayList<Car> = ArrayList()
    var selectedMake: String = ""
    var selectedModel: String = ""
    var selectedYear: Int = 0
    var context: Context = this
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Creates an array of years and populates the dropdown spinner
        for (i in 2022 downTo 1995) {
            years.add(i)
        }
        val spinner: Spinner = findViewById(R.id.year_spinner)
        val arrayAdapter: ArrayAdapter<Int> =
            ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item, years)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

    }

    // OnClick method for Add Car button, creates a new car object and uses it to display a sentence on UI
    fun addCar(view: View) {
        // Sets up new Car
        selectedModel = findViewById<Spinner>(R.id.model_spinner).selectedItem.toString()
        var newCar: Car = Car(selectedYear, selectedMake, selectedModel)

//        Log.v("CHECK", selectedModel)
        // Accesses the layout to the be edited and creates a TextView to be placed as a child
        var lLayout: LinearLayout = findViewById(R.id.dynamicLayout)
        var newCarText: TextView = TextView(this)
        newCarText.text = "Tracking a ${newCar.carYear} ${newCar.carMake} ${newCar.carModel}"
        newCarText.setTextColor(Color.BLACK)
        newCarText.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10F)
        lLayout.addView(newCarText)

        // Adds Car to an array and resets Make and Model Spinners
        cars.add(newCar)
        findViewById<Spinner>(R.id.model_spinner).adapter =
            ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item)
        findViewById<Spinner>(R.id.make_spinner).adapter =
            ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item)
    }

    // Calls API for Makes and populates the appropriate spinner with information
    fun runMakes(url: String) {

        // Builds an HTTP request with the given URL
        val request = Request.Builder()
            .url(url)
            .build()

        // Performs the HTTP request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            // The response received is used to instantiate a JSON Object that can be manipulated for information
            override fun onResponse(call: Call, response: Response) {
                val response = response.body()!!.string()
                var jsonObject: JSONObject = JSONObject(response)
                var results: JSONArray = jsonObject.getJSONArray("Results")
                var size: Int = results.length()
                makes = ArrayList()
                for (i in 0 until size) {
                    var json_objectdetail: JSONObject = results.getJSONObject(i)
                    makes.add(json_objectdetail.get("MakeName").toString().trim())
                }

                // Ends up with an ArrayList of all Makes given in the response, and they are sorted
                makes.sort()
                //Log.v("RESPONSE RESULTS", makes.toString())

                // The Array List is used to populate the spinner
                // Needs to call runOnUiThread because this is happening asynchronously and the thread can't work with the UI
                runOnUiThread {
                    val spinner: Spinner = findViewById(R.id.make_spinner)
                    val arrayAdapter: ArrayAdapter<String> =
                        ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, makes)
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = arrayAdapter
                }

                //Log.v("RESULT MAKES", makes.toString())

            }
        })
    }
    // Calls API for models and populates the appropriate spinner with information
    fun runModels(url: String) {

        // Builds an HTTP request with the given URL
        val request = Request.Builder()
            .url(url)
            .build()

        // Performs the HTTP request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            // The response received is used to instantiate a JSON Object that can be manipulated for information
            override fun onResponse(call: Call, response: Response) {
                val response = response.body()!!.string()
                var jsonObject: JSONObject = JSONObject(response)
                var results: JSONArray = jsonObject.getJSONArray("Results")
                var size: Int = results.length()
                models = ArrayList()
                for (i in 0 until size) {
                    var objectDetail: JSONObject = results.getJSONObject(i)
                    models.add(objectDetail.get("Model_Name").toString().trim())
                }

                // Ends up with an ArrayList of all Makes given in the response, and they are sorted
                models.sort()
                //Log.v("RESPONSE RESULTS", models.toString())

                // The Array List is used to populate the spinner
                // Needs to call runOnUiThread because this is happening asynchronously and the thread can't work with the UI
                runOnUiThread {
                    val spinner: Spinner = findViewById(R.id.model_spinner)
                    val arrayAdapter: ArrayAdapter<String> =
                        ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, models)
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = arrayAdapter
                }
            }
        })
    }

    // OnClick function for when the Next button is clicked below the year, it starts the process of populating the makes
    fun goMake(view: View) {

        // Saves the attribute
        selectedYear = findViewById<Spinner>(R.id.year_spinner).selectedItem.toString().toInt()
        //Log.v("CHECK", selectedYear.toString())
        runMakes("https://vpic.nhtsa.dot.gov/api/vehicles/getmakesforvehicletype/car?format=json")

    }

    // OnClick function for when the Next button is clicked below the make, it starts the process of populating the models
    fun goModel(view: View) {

        //Saves the Attribute
        selectedMake = findViewById<Spinner>(R.id.make_spinner).selectedItem.toString()
        Log.v("CHECK", selectedMake)
        runModels("https://vpic.nhtsa.dot.gov/api/vehicles/GetModelsForMakeYear/make/${selectedMake}/modelyear/${selectedYear}?format=json")
    }
}