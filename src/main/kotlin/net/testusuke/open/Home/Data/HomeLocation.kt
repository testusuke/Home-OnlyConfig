package net.testusuke.open.Home.Data

import org.bukkit.Location

data class HomeLocation(private val name:String,private var location: Location) {

    fun getLocation():Location{
        return location
    }
    fun setLocation(location: Location){
        this.location = location
    }
    fun getName():String{
        return name
    }
}