package com.bawa.kotlincartrack

class Car(year: Int, make: String, model: String) {

    var carYear: Int = -1
    var carMake: String = "N/A"
    var carModel: String = "N/A"

    init{
        carYear = year
        carMake = make
        carModel = model
    }

}