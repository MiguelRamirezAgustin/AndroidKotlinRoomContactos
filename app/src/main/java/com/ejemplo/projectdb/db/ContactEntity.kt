package com.ejemplo.projectdb.db

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contactEntity")
class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id:Int,
    @ColumnInfo(name = "name")
    var name:String,
    @ColumnInfo(name = "lastname")
    var lastname:String,
    @ColumnInfo(name = "motherLastName")
    var motherLastName:String,
    @ColumnInfo(name = "phone")
    var phone:String,
    @ColumnInfo(name = "imagen")
    var imagen:String

    ){
}