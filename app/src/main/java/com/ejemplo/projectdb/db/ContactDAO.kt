package com.ejemplo.projectdb.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ContactDAO {
    /**
     * Consultas db
     * */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contac:ContactEntity):Long

    /**Obtener el listado total de los contactos**/
    @Query("SELECT * FROM contactEntity ")
    fun getContact():List<ContactEntity>

    /**Obtener el listado total de los contactos por order de nombre**/
    @Query("SELECT * FROM contactEntity ORDER BY name ASC")
    fun getContactOrder():List<ContactEntity>


    /**Actualizar el contacto seleccionado por id**/
    @Query("UPDATE  contactEntity SET name=:parameterName, lastname=:parameterLastname, motherLastName=:parameterMotherLastName, phone=:parameterPhone, imagen=:parameterImagen WHERE id=:parameterId")
    fun updateContact(parameterName:String,
                      parameterLastname:String,
                      parameterMotherLastName:String,
                      parameterPhone:String,
                      parameterImagen:String,
                      parameterId:Int)

    /**Eliminar item contact**/
    @Query("DELETE FROM contactEntity WHERE id=:parameterId")
    fun deleteItemContact(parameterId: Int)
}