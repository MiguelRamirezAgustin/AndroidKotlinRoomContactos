package com.ejemplo.projectdb.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(ContactEntity::class), version = 1)
abstract class ContactDB: RoomDatabase() {
    abstract fun contacDao():ContactDAO
}