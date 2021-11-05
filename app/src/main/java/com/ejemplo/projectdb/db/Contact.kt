package com.ejemplo.projectdb.db

import android.app.Application
import androidx.room.Room

class Contact : Application() {
    companion object{
        lateinit var dbcontact: ContactDB
    }

    override fun onCreate() {
        super.onCreate()
        dbcontact =  Room.databaseBuilder(this, ContactDB::class.java, "contact.db").build()
    }
}